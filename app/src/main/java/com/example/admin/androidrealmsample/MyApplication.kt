package com.example.admin.androidrealmsample

import android.app.Application
import io.realm.Realm

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Realm （Applicationクラスのオーバーライドしたらマニュフェストにも追記が必要）android:name=".MyApplication"
        Realm.init(this)
    }
}