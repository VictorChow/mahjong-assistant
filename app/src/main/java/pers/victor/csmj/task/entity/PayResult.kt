package pers.victor.csmj.task.entity

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
data class PayResult(val from: People,
                     val to: People,
                     val points: Int) {
    override fun toString(): String {
        return "${from.name}  →  ${to.name}  :  $points"
    }
}