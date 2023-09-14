package com.launchrocket.playsgame.app.views.fragments.slots

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.launchrocket.playsgame.app.R
import com.launchrocket.playsgame.app.navigation.MainNavigation
import com.launchrocket.playsgame.app.repo.BalanceRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.random.Random

class SlotsFragment(private val di: DI): Fragment() {
    private val navigation: MainNavigation by di.instance()
    private val balanceRepo: BalanceRepo by di.instance()
    private val balance = MutableStateFlow(0)
    private val bet = MutableStateFlow(10)
    private var autospinMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.slots_fragment, container, false)
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
        val betText = view.findViewById<AppCompatTextView>(R.id.betText)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bet.collect {
                    betText.text = getString(R.string.bet, it)
                }
            }
        }
        val slots: List<AppCompatImageView> = view
            .findViewById<LinearLayoutCompat>(R.id.slotsContainer).allViews
            .filter {it is AppCompatImageView}.map{it as AppCompatImageView}.toList()
        val slotsVal = mutableListOf(
            R.drawable.slots_01_image,
            R.drawable.slots_02_image,
            R.drawable.slots_03_image,
            R.drawable.slots_04_image,
            R.drawable.slots_05_image,
            R.drawable.slots_06_image,
            R.drawable.slots_07_image,
            R.drawable.slots_08_image,
            R.drawable.slots_09_image,
        )
        val spinB =  view.findViewById<AppCompatImageButton>(R.id.spinButton)
        spinB.setOnClickListener {
            if(balance.value < 100) return@setOnClickListener
            it.isEnabled = false
            lifecycleScope.launch {
                balance.value -= 100
                var toLeft = true
                repeat(((Random.nextDouble() * (200.0 - 120.0)) + 120.0).toInt()) {
                    if(toLeft) {
                        slotsVal[8] = slotsVal[4]
                        slots[8].setImageResource(slotsVal[8])
                        slotsVal[5] = slotsVal[1]
                        slots[5].setImageResource(slotsVal[5])
                        slotsVal[7] = slotsVal[3]
                        slots[7].setImageResource(slotsVal[7])
                        slotsVal[4] = slotsVal[0]
                        slots[4].setImageResource(slotsVal[4])
                        slotsVal[0] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[1] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[2] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[3] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[6] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slots[0].setImageResource(slotsVal[0])
                        slots[1].setImageResource(slotsVal[1])
                        slots[2].setImageResource(slotsVal[2])
                        slots[3].setImageResource(slotsVal[3])
                        slots[6].setImageResource(slotsVal[6])
                    }
                    else {
                        slotsVal[2] = slotsVal[4]
                        slots[2].setImageResource(slotsVal[2])
                        slotsVal[5] = slotsVal[7]
                        slots[5].setImageResource(slotsVal[5])
                        slotsVal[1] = slotsVal[3]
                        slots[1].setImageResource(slotsVal[1])
                        slotsVal[4] = slotsVal[6]
                        slots[4].setImageResource(slotsVal[4])
                        slotsVal[0] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[3] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[6] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[7] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[8] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slots[0].setImageResource(slotsVal[0])
                        slots[3].setImageResource(slotsVal[3])
                        slots[6].setImageResource(slotsVal[6])
                        slots[7].setImageResource(slotsVal[7])
                        slots[8].setImageResource(slotsVal[8])
                    }
                    toLeft = !toLeft
                    delay(25)
                }
                repeat(10) {
                    if(toLeft) {
                        slotsVal[8] = slotsVal[4]
                        slots[8].setImageResource(slotsVal[8])
                        slotsVal[5] = slotsVal[1]
                        slots[5].setImageResource(slotsVal[5])
                        slotsVal[7] = slotsVal[3]
                        slots[7].setImageResource(slotsVal[7])
                        slotsVal[4] = slotsVal[0]
                        slots[4].setImageResource(slotsVal[4])
                        slotsVal[0] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[1] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[2] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[3] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[6] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slots[0].setImageResource(slotsVal[0])
                        slots[1].setImageResource(slotsVal[1])
                        slots[2].setImageResource(slotsVal[2])
                        slots[3].setImageResource(slotsVal[3])
                        slots[6].setImageResource(slotsVal[6])
                    }
                    else {
                        slotsVal[2] = slotsVal[4]
                        slots[2].setImageResource(slotsVal[2])
                        slotsVal[5] = slotsVal[7]
                        slots[5].setImageResource(slotsVal[5])
                        slotsVal[1] = slotsVal[3]
                        slots[1].setImageResource(slotsVal[1])
                        slotsVal[4] = slotsVal[6]
                        slots[4].setImageResource(slotsVal[4])
                        slotsVal[0] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[3] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[6] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[7] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slotsVal[8] = Random.nextInt(R.drawable.slots_01_image, R.drawable.slots_09_image + 1)
                        slots[0].setImageResource(slotsVal[0])
                        slots[3].setImageResource(slotsVal[3])
                        slots[6].setImageResource(slotsVal[6])
                        slots[7].setImageResource(slotsVal[7])
                        slots[8].setImageResource(slotsVal[8])
                    }
                    toLeft = !toLeft
                    delay(100)
                }
                var win = 0
                for(slot in slotsVal.take(3)) {
                    if(slot == slotsVal[3] || slot == slotsVal[4] || slot == slotsVal[5]) {
                        if(slot == slotsVal[6] || slot == slotsVal[7] || slot == slotsVal[8]) {
                            win += bet.value * (slot - R.drawable.slots_01_image + 1)
                        }
                    }
                }
                if(win != 0) {
                    balance.value += win
                    balanceRepo.saveCallback(balance.value)
                }
                it.isEnabled = true
                if(autospinMode) {
                    spinB.performClick()
                }
            }
        }
        view.findViewById<AppCompatImageButton>(R.id.betMinusButton).setOnClickListener {
            if(bet.value > 10) {
                bet.value -= 10
            }
        }
        view.findViewById<AppCompatImageButton>(R.id.betPlusButton).setOnClickListener {
            if((bet.value + 10) <= balance.value) {
                bet.value += 10
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
    }
}