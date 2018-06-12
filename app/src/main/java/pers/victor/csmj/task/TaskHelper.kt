package pers.victor.csmj.task

import pers.victor.csmj.task.entity.Hu
import pers.victor.csmj.task.entity.PayResult
import pers.victor.csmj.task.entity.People
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pers.victor.ext.spGetString
import pers.victor.ext.spRemove
import pers.victor.ext.spSetString
import kotlin.math.abs

/**
 * Created by Victor on 2018/6/11. (ง •̀_•́)ง
 */
class TaskHelper {
    companion object {
        private const val KEY = "TaskHelperPeople"
    }

    private val people = arrayListOf<People>()
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

    fun hu(hu: Hu) {
        if (hu.loser == hu.winner) {
            //自摸
            people.filter { it != hu.winner }
                    .forEach {
                        var points = if (hu.type == 0) 2 else 8
                        if (hu.winner == hu.dealer) {
                            points = points shl 1
                        }
                        if (it == hu.dealer) {
                            points = points shl 1
                        }
                        if (hu.birds[0] == hu.winner || hu.birds[0] == it) {
                            points = points shl 1
                        }
                        if (hu.birds[1] == hu.winner || hu.birds[1] == it) {
                            points = points shl 1
                        }
                        hu.winner.points += points
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
            hu.winner.points += points
            hu.loser.points -= points
        }

        save()
    }

    private fun save() {
        spSetString(KEY, gson.toJson(people))
    }

    fun restore() {
        people.apply {
            clear()
            addAll(gson.fromJson(spGetString(KEY), object : TypeToken<List<People>>() {}.type))
        }
    }

    fun checkRestore(): Boolean {
        if (spGetString(KEY).isEmpty()) {
            return false
        }
        return try {
            gson.fromJson<List<People>>(spGetString(KEY), object : TypeToken<List<People>>() {}.type)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun end(): String {
        spRemove(KEY)

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

    fun log() {

    }

    fun getPeople(): List<People> = people
}