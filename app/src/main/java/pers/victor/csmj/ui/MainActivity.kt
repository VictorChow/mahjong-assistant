package pers.victor.csmj.ui

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pers.victor.csmj.R
import pers.victor.csmj.task.TaskHelper
import pers.victor.ext.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var huDialog: HuDialog
    private val helper = TaskHelper()
    private val views by lazy { listOf(tv_1, tv_2, tv_3, tv_4) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar == null) {
            toast("supportActionBar为null，部分功能无法使用")
        } else {
        }

        click(tv_hu, tv_start)

        views.forEach {
            val tv = it
            tv.click {
                if (helper.isStarted()) {
                    return@click
                }

                val et = EditText(this)
                et.value = tv.text.toString()
                et.inputType = InputType.TYPE_CLASS_TEXT
                et.setSelection(et.value.length)
                AlertDialog.Builder(this)
                        .setView(et)
                        .setTitle("叫啥")
                        .setPositiveButton("嗯", { _, _ ->
                            val v = et.value.trim()
                            val isExist = views.any { it.text.trim() == v }
                            if (isExist) {
                                toast("${v}已经有了")
                                return@setPositiveButton
                            }
                            tv.text = v
                        })
                        .show()
            }
        }

        if (helper.checkRestore()) {
            AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("检测到有未完成的对局，是否恢复？")
                    .setCancelable(false)
                    .setPositiveButton("嗯", { _, _ ->
                        tv_hu.visiable()
                        tv_start.invisiable()
                        helper.restore()
                        updatePoints()

                        huDialog = HuDialog(this) {
                            helper.hu(it)
                            updatePoints()
                        }
                        huDialog.setPeople(helper.getPeople())

                    })
                    .setNeutralButton("否", null)
                    .show()
        }
    }

    private fun click(vararg v: View) {
        v.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(v: View) {
        when (v) {
            tv_hu -> huDialog.show()
            tv_start -> start()
        }
    }

    private fun start() {
        val isSetName = views.all { it.text.trim().isNotEmpty() }
        if (!isSetName) {
            toast("先填名字")
            return
        }
        tv_hu.visiable()
        tv_start.invisiable()

        helper.start(views.map { it.text.toString() })
        huDialog = HuDialog(this) {
            helper.hu(it)
            updatePoints()
        }
        huDialog.setPeople(helper.getPeople())

        updatePoints()
    }

    private fun updatePoints() {
        views.zip(helper.getPeople()).forEach { it.first.text = it.second.toString() }
    }

    override fun onBackPressed() {
        toast("不能退")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!helper.isStarted()) {
            toast("先开局")
            return false
        }
        when (item.itemId) {
            R.id.menu_rollback -> {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要回退？")
                        .setPositiveButton("嗯", { _, _ ->
                            val r = helper.rollback()
                            if (r) {
                                toast("回退成功")
                                updatePoints()
                            } else {
                                toast("回退失败")
                            }
                        })
                        .show()
            }
            R.id.menu_finish -> {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要结算？")
                        .setPositiveButton("嗯", { _, _ ->
                            AlertDialog.Builder(this)
                                    .setTitle("结算")
                                    .setMessage(helper.end())
                                    .setCancelable(false)
                                    .show()
                        })
                        .show()
            }
        }
        return true
    }
}
