package com.project.scratchstudio.kith_andoid.ui.home_package.board_info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BoardFragmentType
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class ImageViewPageAdapter(private val context: Context, private val type: BoardFragmentType
                           , private val listener: OnItemClickListener?, private val clickImageListener: OnItemClickListener?) : PagerAdapter() {

    val imagePathList = ArrayList<PhotoModelView>()
    private lateinit var layoutInflater: LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return imagePathList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.layout_image_slider, null)
        val imageView = view.findViewById(R.id.layout_image_slider_photo) as ImageView
        val delete = view.findViewById(R.id.layout_image_slider_delete) as ImageView

        with(imagePathList[position]) {
            if (this.photoInthernetPath != "" && this.photoInthernetPath != "null") {
                Picasso.with(context).load(Const.BASE_URL + this.photoInthernetPath)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(imageView)
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            } else if (this.photoBitmap != null) {
                imageView.setImageBitmap(this.photoBitmap)
            }
        }

        imageView.setOnClickListener { clickImageListener?.onItemClick(imagePathList[position]) }

        val vp = container as ViewPager
        vp.addView(view, 0)

        if (type == BoardFragmentType.BOARD_EDIT) {
            delete.setOnClickListener { listener?.onItemClick(imagePathList[position]) }
        } else {
            delete.visibility = View.GONE
        }

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

    interface OnItemClickListener {
        fun onItemClick(item: PhotoModelView)
    }

}