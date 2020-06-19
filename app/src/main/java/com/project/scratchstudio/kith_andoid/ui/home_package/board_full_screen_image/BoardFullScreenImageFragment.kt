package com.project.scratchstudio.kith_andoid.ui.home_package.board_full_screen_image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardFullScreenBinding
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class BoardFullScreenImageFragment : BaseFragment() {

    private lateinit var binding: FragmentBoardFullScreenBinding
    private lateinit var photoModelView: PhotoModelView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBoardFullScreenBinding.inflate(inflater, container, false)
        photoModelView = BoardFullScreenImageFragmentArgs.fromBundle(arguments!!).photo
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (photoModelView.photoInthernetPath.isNotEmpty()) {
            Picasso.with(context).load(Const.BASE_URL + photoModelView.photoInthernetPath)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.boardFullScreenPhoto)
        } else {
            binding.boardFullScreenPhoto.setImageBitmap(photoModelView.photoBitmap)
        }

        binding.boardFullScreenPhoto.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

}
