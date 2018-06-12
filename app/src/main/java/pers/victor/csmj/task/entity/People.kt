package pers.victor.csmj.task.entity

import java.io.Serializable

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
data class People(val name: String,
                  var points: Int) : Serializable {
    override fun toString(): String {
        return "$name : $points"
    }
}