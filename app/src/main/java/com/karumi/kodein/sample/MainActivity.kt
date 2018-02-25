package com.karumi.kodein.sample

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import kotlinx.android.synthetic.main.activity_main.tv_name_activity_scope
import kotlinx.android.synthetic.main.activity_main.tv_name_app_scope

class MainActivity : KodeinAppCompatActivity() {

    private val applicationScopeClass: ApplicationScopeClass by injector.instance()
    private val activityScopeClass: ActivityScopeClass by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.asApp().addModule(activityModules)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextFromController()
    }

    private fun setTextFromController() {
        tv_name_app_scope.text = applicationScopeClass.getText()
        tv_name_activity_scope.text = activityScopeClass.getText()
    }

    private val activityModules = Module(allowSilentOverride = true) {
        bind<ActivityScopeClass>() with provider {
            ActivityScopeClass()
        }
    }
}
