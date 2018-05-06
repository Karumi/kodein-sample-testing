package com.karumi.kodein.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.tv_name_activity_scope
import kotlinx.android.synthetic.main.activity_main.tv_name_app_scope
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private val applicationScopeClass: ApplicationScopeClass by instance()
    private val activityScopeClass: ActivityScopeClass by instance()

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

    private val activityModules = Kodein.Module(allowSilentOverride = true) {
        bind<ActivityScopeClass>() with provider {
            ActivityScopeClass()
        }
    }
}
