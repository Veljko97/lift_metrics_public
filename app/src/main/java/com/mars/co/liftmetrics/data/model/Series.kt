package com.mars.co.liftmetrics.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Series(
    @ColumnInfo val repeats: Long,
    @ColumnInfo val weight: Double
) {
    @PrimaryKey(autoGenerate = true)
    var seriesId: Long? = null

    fun copyWith(
        repeats: Long?,
        weight: Double?,
        seriesId: Long?
    ): Series {
        val series = Series(repeats ?: this.repeats, weight ?: this.weight)
        series.seriesId = seriesId ?: this.seriesId
        return series
    }
}