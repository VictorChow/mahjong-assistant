package pers.victor.csmj

import android.app.Application
import pers.victor.ext.Ext

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Ext.with(this)
    }
}