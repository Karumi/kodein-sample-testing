package com.karumi.kodein.sample

import android.app.Application
import com.github.tmurakami.dexopener.DexOpenerAndroidJUnitRunner

class KotlinAndroidTestRunner
    : DexOpenerAndroidJUnitRunner() {

    override fun callApplicationOnCreate(app: Application) {
        app.asApp().kodein.mutable = true
        super.callApplicationOnCreate(app)
    }

}