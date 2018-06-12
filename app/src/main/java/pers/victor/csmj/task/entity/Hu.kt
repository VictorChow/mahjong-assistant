package pers.victor.csmj.task.entity

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
data class Hu(var winner: People,
              var loser: People,
              var dealer: People,
              var type: Int /* 小胡:0, 大胡:1 */,
              var birds: ArrayList<People>)

