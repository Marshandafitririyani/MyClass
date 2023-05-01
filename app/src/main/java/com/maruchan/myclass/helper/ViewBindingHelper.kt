package com.maruchan.myclass.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.maruchan.myclass.R

class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            if (imageUrl.isNullOrEmpty()) {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_loading)
                    .apply(RequestOptions.centerCropTransform())
                    .error(R.drawable.img_edit_poto)
                    .into(view)

            } else {
                imageUrl.let {
                    Glide
                        .with(view.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.img_loading)
                        .into(view)

                }

            }

        }

    }

}