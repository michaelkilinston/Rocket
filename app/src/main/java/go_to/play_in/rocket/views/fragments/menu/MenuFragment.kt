package go_to.play_in.rocket.views.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import go_to.play_in.rocket.R
import go_to.play_in.rocket.navigation.MainNavigation
import org.kodein.di.DI
import org.kodein.di.instance

class MenuFragment(private val di: DI): Fragment() {
    private val navigation: MainNavigation by di.instance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.menu_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatImageButton>(R.id.playInRouletteButton).setOnClickListener {
            navigation.navigate(navigation.rouletteDest)
        }
        view.findViewById<AppCompatImageButton>(R.id.playInSlotsButton).setOnClickListener {
            navigation.navigate(navigation.slotsDest)
        }
        view.findViewById<AppCompatImageButton>(R.id.privacyButton).setOnClickListener {
            navigation.navigate(navigation.privacyDest)
        }
    }
}