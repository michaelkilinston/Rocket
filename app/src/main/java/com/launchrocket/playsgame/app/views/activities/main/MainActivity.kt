package com.launchrocket.playsgame.app.views.activities.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.launchrocket.playsgame.app.R
import com.launchrocket.playsgame.app.models.LinkContainer
import com.launchrocket.playsgame.app.navigation.MainNavigation
import com.launchrocket.playsgame.app.repo.BalanceRepo
import com.launchrocket.playsgame.app.views.activities.web.WebActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

class MainActivity : AppCompatActivity() {
    private val  dependencyInjection: DI = DI {
        bind {
            singleton { MainNavigation(this.di) }
        }
        bind {
            singleton { BalanceRepo() }
        }

    }

    private val navigation: MainNavigation by dependencyInjection.instance()
    private val balanceRepo: BalanceRepo by dependencyInjection.instance()
    private lateinit var webIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        webIntent = Intent(this@MainActivity, WebActivity::class.java)
        navigation.getSupportFragmentManager = ::getSupportFragmentManager
        navigation.navigateAdd(navigation.waitingDest)
        balanceRepo.getCallback = {
            getSharedPreferences("shared_preferences", MODE_PRIVATE).getInt("balance", 0)
        }
        balanceRepo.saveCallback = {
            getSharedPreferences("shared_preferences", MODE_PRIVATE).edit().putInt("balance", it).apply()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val data = getData()
            if(data != null) {
                val linkContainer = LinkContainer(data)
                webIntent.putExtra("link", linkContainer.link)
                startActivity(webIntent)
            }
            else {
                navigation.navigateReplace(navigation.menuDest, true)
                onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if(!navigation.popBackStack()) {
                            finish()
                        }
                    }
                })
            }
        }
    }

    private fun getData(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL)
            .build()
        val response = client.newCall(request).execute()
        if(response.isSuccessful) {
            Log.i("Request", "Successful")
            val body = response.body?.string()?.replace("\n", "")?.replace("\r", "")?.replace(" ", "")
            if(body != null && body.substring(body.indexOf("access_code:")+12)[0] == '1') {
                Log.i("Request", "Body ok.")
                val index1 = body.indexOf("link:") +6
                val index2 = body.indexOf("\"}")
                if(index1 < index2 && index1 != -1) {
                    val link = body.substring(index1, index2)
                    if (link.contains("http") && link.contains("/")) {
                        Log.i("Request", "Link ok.")
                        return link
                    }
                }
                else {
                    Log.w("Request", "Index1: $index1. Index2: $index2")
                    Log.i("Request", "Link: ${body.indexOf("link")}. Link:: ${body.indexOf("link:")}")
                }
            }
            else {
                if(body == null) {
                    Log.w("Request", "Body null")
                }
                else if (body.substring(body.indexOf("access_code:")+12)[0] != '1') {
                    Log.w("Request", "Body invalid. ${body.substring(body.indexOf("access_code:")+13)[0]}")
                }
            }
        }
        return null
    }

    companion object {
        private const val BASE_URL = "https://gist.githubusercontent.com/michaelkilinston/5b0014878d80c0655fa1486af566e82e/raw/rocket_gist"
    }
}