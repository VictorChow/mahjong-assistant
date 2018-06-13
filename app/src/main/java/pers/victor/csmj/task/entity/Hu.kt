package pers.victor.csmj.task.entity

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
data class Hu(var winner: String,
              var loser: String,
              var dealer: String,
              var type: Int /* 小胡:0, 大胡:1 */,
              var birds: ArrayList<String>)

