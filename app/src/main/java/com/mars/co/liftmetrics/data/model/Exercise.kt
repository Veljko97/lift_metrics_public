package com.mars.co.liftmetrics.data.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
class Exercise(
    @ColumnInfo(name = "muscles") val muscles: Set<Muscle>,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image_depiction") val imageDepiction: String? = null,
    @ColumnInfo(name = "video_link") val videoLink: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exercise

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}