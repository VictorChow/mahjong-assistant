package pers.victor.csmj.ui

import android.app.Activity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.window_hu.view.*
import pers.victor.csmj.R
import pers.victor.csmj.task.entity.Hu
import pers.victor.csmj.task.entity.People
import pers.victor.ext.children
import pers.victor.ext.click
import pers.victor.ext.get

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
class HuDialog(activity: Activity, val onClick: (Hu) -> Unit) : AlertDialog(activity) {
    private var v: View = View.inflate(activity, R.layout.window_hu, null)
    private lateinit var hu: Hu

    init {
        setView(v)

        setCancelable(false)

        v.tv_ok.click {
            dismiss()
            onClick(hu)
        }

        v.tv_no.click { dismiss() }
    }

    fun setPeople(people: List<People>) {
        val first = people[0].name
        hu = Hu(first, first, first, 0, arrayListOf(first, first))

        val groups = listOf<RadioGroup>(v.rg_hu, v.rg_way, v.rg_dealer, v.rg_bird1, v.rg_bird2)
        var rb: RadioButton
        groups.forEach {
            val group = it
            people.forEach {
                rb = RadioButton(context)
                rb.textSize = 15f
                rb.text = it.name
                val lp = RadioGroup.LayoutParams(0, -2)
                lp.weight = 1f
                group.addView(rb, lp)
            }
        }


        v.rg_hu.check(v.rg_hu[0].id)
        v.rg_type.check(v.rg_type[0].id)
        v.rg_way.check(v.rg_way[0].id)
        v.rg_dealer.check(v.rg_dealer[0].id)
        v.rg_bird1.check(v.rg_bird1[0].id)
        v.rg_bird2.check(v.rg_bird2[0].id)

        val huIds = v.rg_hu.children.map { it.id }
        v.rg_hu.setOnCheckedChangeListener { _, id -> hu.winner = people[huIds.indexOf(id)].name }

        val typeIds = v.rg_type.children.map { it.id }
        v.rg_type.setOnCheckedChangeListener { _, id -> hu.type = typeIds.indexOf(id) }

        val wayIds = v.rg_way.children.map { it.id }
        v.rg_way.setOnCheckedChangeListener { _, id -> hu.loser = people[wayIds.indexOf(id)].name }

        val bird1Ids = v.rg_bird1.children.map { it.id }
        v.rg_bird1.setOnCheckedChangeListener { _, id -> hu.birds[0] = people[bird1Ids.indexOf(id)].name }

        val bird2Ids = v.rg_bird2.children.map { it.id }
        v.rg_bird2.setOnCheckedChangeListener { _, id -> hu.birds[1] = people[bird2Ids.indexOf(id)].name }

        val dealerIds = v.rg_dealer.children.map { it.id }
        v.rg_dealer.setOnCheckedChangeListener { _, id -> hu.dealer = people[dealerIds.indexOf(id)].name }
    }
}