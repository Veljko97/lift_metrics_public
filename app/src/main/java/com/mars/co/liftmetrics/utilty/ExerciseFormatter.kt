package com.mars.co.liftmetrics.utilty

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Exercise

class ExerciseFormatter(private val _exercise: Exercise, private val context: Context) {

    //Implementation done based on this answer: https://stackoverflow.com/a/15488321
    val name
        get(): String {
            val resId =
                context.resources.getIdentifier(
                    _exercise.name,
                    "string",
                    context.packageName
                )
            if (resId != 0) {
                return context.getString(resId)
            }
            return _exercise.name
        }

    val description
        get(): String {
            val resId =
                context.resources.getIdentifier(
                    _exercise.description,
                    "string",
                    context.packageName
                )
            if (resId != 0) {
                return context.getString(resId)
            }
            return _exercise.description
        }

    val videoLink
        get(): String? {
            if (exercise.videoLink != null &&
                !exercise.videoLink!!.startsWith("https://") &&
                !exercise.videoLink!!.startsWith("http://")
            ) {
                return null
            }
            return exercise.videoLink
        }

    fun insertGraphic(imageView: ImageView) {

        if (_exercise.imageDepiction == null) {
            Glide.with(context)
                .load(R.drawable.ic_image_search_24dp)
                .into(imageView)
            return
        }

        val resId =
            context.resources.getIdentifier(
                _exercise.imageDepiction,
                "drawable",
                context.packageName
            )
        if (resId != 0) {
            Glide.with(context)
                .load(resId)
                .into(imageView)
            return
        }
        Glide.with(context)
            .load(_exercise.imageDepiction)
            .into(imageView)
    }

    val exercise get() = _exercise

    override fun toString(): String {
        return name
    }
}