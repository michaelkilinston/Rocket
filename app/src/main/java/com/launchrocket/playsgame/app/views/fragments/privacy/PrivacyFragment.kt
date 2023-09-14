package com.launchrocket.playsgame.app.views.fragments.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.launchrocket.playsgame.app.R
import com.launchrocket.playsgame.app.navigation.MainNavigation
import org.kodein.di.DI
import org.kodein.di.instance

class PrivacyFragment(private val di: DI): Fragment() {
    private val navigation: MainNavigation by di.instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.privacy_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatImageButton>(R.id.homeButton).setOnClickListener {
            navigation.popBackStack()
        }
    }
}