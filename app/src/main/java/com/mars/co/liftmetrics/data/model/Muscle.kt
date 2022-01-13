package com.mars.co.liftmetrics.data.model

import androidx.room.TypeConverter
import com.mars.co.liftmetrics.R
import java.lang.StringBuilder

enum class Muscle(val value: Int) {
    BICEPS(1),
    TRICEPS(2),
    CHEST(3),
    SHOULDERS(4),
    LEGS(5),
    BACK(6)
}

class MusclesConverter {
    @TypeConverter
    fun fromMuscles(values: Set<Muscle>): String {
        val stringBuilder = StringBuilder()
        for ( muscle in values ) {
            stringBuilder.append(muscle.name)
            stringBuilder.append(", ")
        }
        stringBuilder.setLength(stringBuilder.length - 2)
        return stringBuilder.toString()
    }

    @TypeConverter
    fun toMuscles(value: String): Set<Muscle> {
        val muscles: MutableSet<Muscle> = mutableSetOf()
        val values = value.split(',')
        for ( name in values ) {
            muscles.add(Muscle.valueOf(name.trim()))
        }
        return muscles
    }


    @TypeConverter
    fun fromMuscle(value: Muscle): String {
        return value.name
    }

    @TypeConverter
    fun toMuscle(value: String): Muscle = Muscle.valueOf(value)

    companion object {
        fun toStringRes(muscle: Muscle): Int {
            return when(muscle){
                Muscle.BICEPS -> R.string.biceps
                Muscle.TRICEPS -> R.string.triceps
                Muscle.CHEST -> R.string.chest
                Muscle.SHOULDERS -> R.string.shoulders
                Muscle.LEGS -> R.string.legs
                Muscle.BACK -> R.string.back
            }
        }
    }
}