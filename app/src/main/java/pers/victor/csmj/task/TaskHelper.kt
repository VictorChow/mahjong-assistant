package pers.victor.csmj.task

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pers.victor.csmj.task.entity.Hu
import pers.victor.csmj.task.entity.PayResult
import pers.victor.csmj.task.entity.People
import pers.victor.ext.spGetString
import pers.victor.ext.spRemove
import pers.victor.ext.spSetString
import kotlin.math.abs

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
class TaskHelper {
    companion object {
        private const val KEY_PEOPLE = "TaskHelperPeople"
        private const val KEY_HISTORY = "TaskHelperHistory"
    }

    private val people = arrayListOf<People>()
    private val history = arrayListOf<String>()
    private val gson = Gson()

    fun start(names: List<String>) {
        if (names.size != 4) {
            throw IllegalStateException()
        }
        people.apply {
            clear()
            addAll(names.map { People(it, 0) })
        }

        save()
    }

    fun isStarted() = people.isNotEmpty()

    fun hu(hu: Hu) {
        history.add(gson.toJson(people))

        if (hu.loser == hu.winner) {
            //自摸
            people.filter { it.name != hu.winner }
                    .forEach {
                        var points = if (hu.type == 0) 2 else 8
                        if (hu.winner == hu.dealer) {
                            points = points shl 1
                        }
                        if (it.name == hu.dealer) {
                            points = points shl 1
                        }
                        if (hu.birds[0] == hu.winner || hu.birds[0] == it.name) {
                            points = points shl 1
                        }
                        if (hu.birds[1] == hu.winner || hu.birds[1] == it.name) {
                            points = points shl 1
                        }
                        people.first { it.name == hu.winner }.points += points
                        it.points -= points
                    }
        } else {
            var points = if (hu.type == 0) 1 else 7
            if (hu.winner == hu.dealer) {
                points = points shl 1
            }
            if (hu.loser == hu.dealer) {
                points = points shl 1
            }
            if (hu.birds[0] == hu.winner || hu.birds[0] == hu.loser) {
                points = points shl 1
            }
            if (hu.birds[1] == hu.winner || hu.birds[1] == hu.loser) {
                points = points shl 1
            }
            people.first { it.name == hu.winner }.points += points
            people.first { it.name == hu.loser }.points -= points
        }

        save()
    }

    private fun save() {
        spSetString(KEY_PEOPLE, gson.toJson(people))
        spSetString(KEY_HISTORY, gson.toJson(history))
    }

    fun restore() {
        people.apply {
            clear()
            addAll(parse(spGetString(KEY_PEOPLE)))
        }
        history.apply {
            clear()
            addAll(parse(spGetString(KEY_HISTORY)))
        }
    }

    fun rollback(): Boolean {
        if (history.isEmpty()) {
            return false
        }
        val lastPeople = history[history.lastIndex]
        people.apply {
            clear()
            addAll(parse(lastPeople))
        }
        history.removeAt(history.lastIndex)
        save()
        return true
    }

    fun checkRestore(): Boolean {
        if (spGetString(KEY_PEOPLE).isEmpty()) {
            return false
        }
        return try {
            parse<List<People>>(spGetString(KEY_PEOPLE))
            parse<List<String>>(spGetString(KEY_HISTORY))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun end(): String {
        spRemove(KEY_PEOPLE)
        spRemove(KEY_HISTORY)

        if (people.all { it.points == 0 }) {
            return "所有人不输不赢，浪费时间！"
        }

        val list = people.filter { it.points != 0 }.partition { it.points > 0 }
        val winners = list.first
        val losers = list.second
        if (winners.size == 1) {
            return losers.map { PayResult(it, winners[0], abs(it.points)) }.joinToString("\n") { it.toString() }
        }
        if (losers.size == 1) {
            return winners.map { PayResult(losers[0], it, it.points) }.joinToString("\n") { it.toString() }
        }
        val results = arrayListOf<PayResult>()
        val maxWinner = winners.maxBy { it.points }!!
        val minLoser = losers.minBy { abs(it.points) }!!
        results.add(PayResult(minLoser, maxWinner, abs(minLoser.points)))
        val maxWinRemaining = maxWinner.points - abs(minLoser.points)
        val anotherLoser = losers.filter { it != minLoser }[0]
        val anotherWinner = winners.filter { it != maxWinner }[0]
        if (maxWinRemaining != 0) {
            results.add(PayResult(anotherLoser, maxWinner, maxWinRemaining))
        }
        results.add(PayResult(anotherLoser, anotherWinner, abs(anotherLoser.points) - maxWinRemaining))
        return results.joinToString("\n") { it.toString() }
    }

    fun getPeople(): List<People> = people

    private inline fun <reified T> parse(json: String): T = gson.fromJson(json, object : TypeToken<T>() {}.type)
}