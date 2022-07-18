package com.example.videoplayer.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.videoplayer.R

class CommonUtils {

    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    ) ?: false
                } else {
                    (@Suppress("Deprecation")
                    return this.activeNetworkInfo?.isConnected ?: false)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun setImageUrl(imageView: ImageView, imageUrl: String?) {
            if (imageUrl == null) {
                imageView.setImageDrawable(null)
            } else {
                Glide.with(imageView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_baseline_broken_image_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)
            }
        }

        fun getBooleanSharedPreference(
            context: Context,
            key: String?,
            defaultValue: Boolean
        ): Boolean {
            val preferences = context.applicationContext.getSharedPreferences(
                Constants.APP_SHARED_PREFERENCE,
                Context.MODE_PRIVATE
            )
            return if (preferences.contains(key)) {
                preferences.getBoolean(key, defaultValue)
            } else {
                defaultValue
            }
        }

        fun setBooleanSharedPreference(context: Context, key: String?, value: Boolean) {
            val preferences = context.applicationContext.getSharedPreferences(
                Constants.APP_SHARED_PREFERENCE,
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit()
            if (key != null && !key.isEmpty()) {
                editor.putBoolean(key, value)
                editor.apply()
            }
        }

    }

}