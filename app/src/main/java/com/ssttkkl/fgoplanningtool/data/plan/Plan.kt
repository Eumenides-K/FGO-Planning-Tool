package com.ssttkkl.fgoplanningtool.data.plan

import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Plan")
@TypeConverters(Converter::class)
@Parcelize
data class Plan(@PrimaryKey var servantId: Int,
                var nowExp: Int,
                var planExp: Int,
                var ascendedOnNowStage: Boolean,
                var ascendedOnPlanStage: Boolean,
                var nowSkill1: Int,
                var nowSkill2: Int,
                var nowSkill3: Int,
                var planSkill1: Int,
                var planSkill2: Int,
                var planSkill3: Int,
                @ColumnInfo(name = "dress") var dressID: Set<Int>) : Parcelable {
    val servant: Servant?
        get() = ResourcesProvider.instance.servants[servantId]

    val nowLevel
        get() = LevelValues.expToLevel(nowExp)

    val planLevel
        get() = LevelValues.expToLevel(planExp)

    val nowStage
        get() = LevelValues.levelToStage(servant!!.star, nowLevel, ascendedOnNowStage)

    val planStage
        get() = LevelValues.levelToStage(servant!!.star, planLevel, ascendedOnPlanStage)

    var dress: Set<Dress>
        get() = dressID.mapNotNull { servant?.dress?.get(it) }.toSet()
        set(value) {
            dressID = value.map { servant!!.dress.indexOf(it) }.toSet()
        }

    @Ignore
    constructor(servantId: Int) : this(
            servantId, 0,
            LevelValues.levelToExp(ResourcesProvider.instance.servants[servantId]!!.stageMapToMaxLevel[4]),
            false, false,
            1, 1, 1,
            10, 10, 10,
            setOf())

    @Ignore
    constructor(plan: Plan) : this(plan.servantId,
            plan.nowExp,
            plan.planExp,
            plan.ascendedOnNowStage,
            plan.ascendedOnPlanStage,
            plan.nowSkill1,
            plan.nowSkill2,
            plan.nowSkill3,
            plan.planSkill1,
            plan.planSkill2,
            plan.planSkill3,
            plan.dressID)
}

private object Converter {
    private val gson = Gson()
    private val type = object : TypeToken<Set<Int>>() {}.type

    @JvmStatic
    @TypeConverter
    fun convertIntSet(set: Set<Int>): String {
        return gson.toJson(set, type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertIntSet(value: String): Set<Int> {
        return gson.fromJson(value, type)
    }
}