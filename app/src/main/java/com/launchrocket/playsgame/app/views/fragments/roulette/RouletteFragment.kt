package com.launchrocket.playsgame.app.views.fragments.roulette

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.launchrocket.playsgame.app.R
import com.launchrocket.playsgame.app.helpful.RouletteRotations
import com.launchrocket.playsgame.app.navigation.MainNavigation
import com.launchrocket.playsgame.app.repo.BalanceRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.Random

class RouletteFragment(private val di: DI): Fragment() {
    private val navigation: MainNavigation by di.instance()
    private val balanceRepo: BalanceRepo by di.instance()
    private val balance = MutableStateFlow(0)
    private val stoped = MutableStateFlow(false)
    private var autospinMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.roulette_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balance.value = balanceRepo.getCallback()

        view.findViewById<AppCompatImageButton>(R.id.homeButton).setOnClickListener {
            navigation.popBackStack()
        }
        val balanceText = view.findViewById<AppCompatTextView>(R.id.balanceText)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                balance.collect {
                    var text = it.toString().reversed()
                    var i = 3
                    while(i < text.length) {
                        text = text.substring(0, i) + "." + text.substring(i, text.length)
                        i++
                        i++
                        i++
                        i++
                    }
                    text = text.reversed()
                    Log.i("Roulette", "Balance: ${balance.value}. Balance text: $text")
                    balanceText.text = getString(R.string.balance, text)
                }
            }
        }

        val spinB =  view.findViewById<AppCompatImageButton>(R.id.spinButton)
        spinB.setOnClickListener {
            if(balance.value < 100) return@setOnClickListener
            it.isEnabled = false
            lifecycleScope.launch {
                balance.value -= 100
                stoped.value = false
                var rotation = 10.0f
                val rouletteElements = view.findViewById<AppCompatImageView>(R.id.rouletteElements)
                repeat(((Random().nextDouble() * (1000.0 - 600.0)) + 600.0).toInt()) {
                    if(!stoped.value) {
                        rouletteElements.rotation += 1
                        if (rouletteElements.rotation >= 360f) {
                            rouletteElements.rotation = 0f
                        }
                        rotation = rouletteElements.rotation
                        delay(5)
                    }
                }
                while(!RouletteRotations.rotationToValue.keys.contains(rotation)) {
                    rouletteElements.rotation += 1
                    if(rouletteElements.rotation >= 360f) {
                        rouletteElements.rotation = 0f
                    }
                    rotation = rouletteElements.rotation
                    if(!stoped.value) {
                        delay(25)
                    }
                }
                val win = RouletteRotations.rotationToValue[rotation]
                if(win != null) {
                    Log.w("Roulette", "Not null. $win")
                    if(win != -1) {
                        balance.value += win
                        balanceRepo.saveCallback(balance.value)
                    }
                    else {
                        it.isEnabled = true
                        balance.value += 100
                        spinB.performClick()
                        return@launch
                    }
                }
                else {
                    Log.w("Roulette", "Null")
                }
                it.isEnabled = true
                if(autospinMode) {
                    spinB.performClick()
                }
            }
        }
        view.findViewById<AppCompatImageButton>(R.id.autospinButton).setOnClickListener {
            autospinMode = !autospinMode
            if(autospinMode) {
                spinB.performClick()
                it.foreground = ResourcesCompat.getDrawable(resources, R.drawable.selected_autospin_button_image, null)
            }
            else {
                it.foreground = null
            }
        }
        view.findViewById<AppCompatImageButton>(R.id.privacyButton).setOnClickListener {
            navigation.navigate(navigation.privacyDest)
        }
        view.findViewById<AppCompatImageButton>(R.id.pauseButton).setOnClickListener {
            stoped.value = true
        }
    }
}