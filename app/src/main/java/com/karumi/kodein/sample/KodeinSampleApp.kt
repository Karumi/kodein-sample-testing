package com.karumi.kodein.sample

import android.app.Application
import android.content.Context
import org.kodein.di.Kodein.Module
import org.kodein.di.KodeinAware
import org.kodein.di.conf.ConfigurableKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class KodeinSampleApp : Application(), KodeinAware {
    override val kodein = ConfigurableKodein(mutable = true)
    var overrideModule: Module? = null

    override fun onCreate() {
        super.onCreate()
        resetInjection()
    }

    fun addModule(activityModules: Module) {
        kodein.addImport(activityModules, true)
        if (overrideModule != null) {
            kodein.addImport(overrideModule!!, true)
        }
    }

    fun resetInjection() {
        kodein.clear()
        kodein.addImport(appDependencies(), true)
    }

    private fun appDependencies(): Module {
        return Module(allowSilentOverride = true) {
            bind<ApplicationScopeClass>() with singleton {
                ApplicationScopeClass()
            }
        }
    }
}

fun Context.asApp() = this.applicationContext as KodeinSampleApp