package com.karumi.kodein.sample

class MainActivityController(val nameDataSource: NameDataSource) {
    fun getName(): String = nameDataSource.getName()
}
