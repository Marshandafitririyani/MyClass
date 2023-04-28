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

            //TODO: jika fotonya null maka akan menampilkan berikut
            if (imageUrl.isNullOrEmpty()) {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_loading)
                    .apply(RequestOptions.centerCropTransform())
                    .error(R.drawable.img_edit_poto)
                    .into(view)

            } else {
                //TODO:jika fotonya tidak null atau ada fotonya akan menampilkan foto yang ada pada user masing-masing
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