package com.karumi.kodein.sample

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import kotlinx.android.synthetic.main.activity_main.tv_hello_world

class MainActivity : KodeinAppCompatActivity() {

    private val controller: MainActivityController by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.asApp().addModule(activityModules)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextFromController()
    }

    private fun setTextFromController() {
        tv_hello_world.text = controller.getName()
    }

    val activityModules = Module(allowSilentOverride = true) {
        bind<MainActivityController>() with provider {
            MainActivityController(instance())
        }
    }
}