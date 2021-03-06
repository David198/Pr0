package com.pr0gramm.app.ui.upload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.pr0gramm.app.R
import com.pr0gramm.app.api.pr0gramm.Api
import com.pr0gramm.app.services.UriHelper
import com.pr0gramm.app.ui.dialogs.PopupPlayerFactory
import com.pr0gramm.app.ui.views.viewer.MediaUri
import com.pr0gramm.app.ui.views.viewer.MediaView
import com.pr0gramm.app.util.AndroidUtility
import com.squareup.picasso.Picasso

/**
 */
class SimilarImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    private val itemAdapter = ItemAdapter()

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = itemAdapter
    }

    var items: List<Api.Posted.SimilarItem> = emptyList()
        set(value) {
            field = value.toList()
            itemAdapter.notifyDataSetChanged()
        }

    private inner class ItemAdapter() : RecyclerView.Adapter<ThumbnailViewHolder>() {
        val picasso: Picasso = appKodein().instance()

        override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
            val item = items[position]

            val imageUri = UriHelper.of(context).thumbnail(item)
            picasso.load(imageUri)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(ColorDrawable(0xff333333.toInt()))
                    .into(holder.image)

            holder.image.setOnClickListener {
                val activity = AndroidUtility.activityFromContext(context)!!

                val uri = UriHelper.of(context).media(item.image)
                val mediaUri = MediaUri.of(item.id(), uri)
                val config = MediaView.Config.of(activity, mediaUri)
                PopupPlayerFactory.newInstance(config).show()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.thumbnail, parent, false)
            return ThumbnailViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    private class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView as ImageView
    }
}
