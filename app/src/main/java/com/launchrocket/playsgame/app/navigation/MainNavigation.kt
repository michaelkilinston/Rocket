package com.launchrocket.playsgame.app.navigation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.launchrocket.playsgame.app.R
import com.launchrocket.playsgame.app.views.fragments.menu.MenuFragment
import com.launchrocket.playsgame.app.views.fragments.privacy.PrivacyFragment
import com.launchrocket.playsgame.app.views.fragments.roulette.RouletteFragment
import com.launchrocket.playsgame.app.views.fragments.slots.SlotsFragment
import com.launchrocket.playsgame.app.views.fragments.waiting.WaitingFragment
import org.kodein.di.DI

class MainNavigation(private val di: DI) {
    private var backStack: MutableList<Fragment> = mutableListOf()

    lateinit var getSupportFragmentManager: () -> FragmentManager

    private val supportFragmentManager
        get() = getSupportFragmentManager()

    val waitingDest = "waiting"
    val menuDest = "menu"
    val rouletteDest = "roulette"
    val slotsDest = "slots"
    val privacyDest = "privacy"

    fun navigateAdd(dest: String, allowed: Boolean = false) {
        backStack.add(getFragmentByDestination(dest))
        Log.i("Navigation", "Add ${backStack.last()}")
        val transaction = supportFragmentManager.beginTransaction()
            .apply { add(R.id.fragmentsContainer, backStack.last()) }
        commitByAllowed(transaction, allowed)
    }
    fun navigate(dest: String, allowed: Boolean = false) {
        backStack.add(getFragmentByDestination(dest))
        Log.i("Navigation", "Replace to ${backStack.last()}")
        val transaction = supportFragmentManager.beginTransaction()
            .apply { replace(R.id.fragmentsContainer, backStack.last()) }
        commitByAllowed(transaction, allowed)
    }

    fun navigateReplace(dest: String, allowed: Boolean = false) {
        backStack.removeLast()
        backStack.add(getFragmentByDestination(dest))
        Log.i("Navigation (replace)", "Replace to ${backStack.last()}")
        val transaction = supportFragmentManager.beginTransaction()
            .apply { replace(R.id.fragmentsContainer, backStack.last()) }
        commitByAllowed(transaction, allowed)
    }

    fun popBackStack(allowed: Boolean = false): Boolean {
        return if(backStack.size > 1) {
            backStack.removeLast()
            Log.i("Navigation", "Pop back stack")
            val transaction = supportFragmentManager.beginTransaction()
                .apply { replace(R.id.fragmentsContainer, backStack.last()) }
            commitByAllowed(transaction, allowed)
            true
        }
        else {
            false
        }
    }

    private fun getFragmentByDestination(dest: String): Fragment {
        return if(dest == waitingDest) {
            WaitingFragment()
        }
        else if (dest == menuDest) {
            MenuFragment(di)
        }
        else if (dest == rouletteDest) {
            RouletteFragment(di)
        }
        else if (dest == slotsDest) {
            SlotsFragment(di)
        }
        else if (dest == privacyDest) {
            PrivacyFragment(di)
        }
        else throw IllegalArgumentException()
    }

    private fun commitByAllowed(transaction: FragmentTransaction, allowed: Boolean) {
        if (allowed) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }
}