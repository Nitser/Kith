package com.project.scratchstudio.kith_andoid.ui.home_package.BoardInfo

import android.accounts.NetworkErrorException
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.board.Board
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse
import com.project.scratchstudio.kith_andoid.ui.home_package.Comments.CommentListFragment
import com.project.scratchstudio.kith_andoid.ui.home_package.neweditboard.NewEditBoardFragment
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.Date

class BoardInfoFragment : BaseFragment() {

    private var bundle: Bundle? = null
    private var info: Board? = null
    private var boardApi: BoardApi? = null
    private var userApi: UserApi? = null
    private val disposable = CompositeDisposable()
    private var userPresenter: UserPresenter? = null

    private var photo: ImageView? = null

    private fun setIsJoin() {
        val favorite = activity!!.findViewById<CheckBox>(R.id.heart)
        favorite.isChecked = true
        favorite.isEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bundle = arguments
        bundle!!.putBoolean("is_edit", false)
        if (bundle != null) {
            info = bundle!!.getSerializable("board") as Board
        }
        return inflater.inflate(R.layout.fragment_announcement_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        setButtonsListener()

        val title = view.findViewById<TextView>(R.id.title)
        val description = view.findViewById<TextView>(R.id.description)
        val owner = view.findViewById<TextView>(R.id.owner)
        val phone = view.findViewById<TextView>(R.id.phone)
        val creationDate = view.findViewById<TextView>(R.id.creationDate)
        photo = view.findViewById(R.id.photo)

        title.text = info!!.title

        try {
            val inputFormat: DateFormat
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = SimpleDateFormat("yyyy-MM-dd")
                val outputFormat = SimpleDateFormat("dd.MM.yyyy")
                var newCreationDateFormat: Date? = null
                if (info!!.startDate == "null") {
                    creationDate.visibility = View.GONE
                } else {
                    newCreationDateFormat = inputFormat.parse(info!!.startDate!!.replace("\\s.*$".toRegex(), ""))
                }
                creationDate.text = getString(R.string.create_date, outputFormat.format(newCreationDateFormat))
            } else {
                val errDate = "Создано: " + info!!.startDate!!.replace("\\s.*$".toRegex(), "")
                creationDate.text = errDate
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val ownerName = StringBuilder()
        if (info!!.organizerName != null && info!!.organizerName != "null") {
            ownerName.append(info!!.organizerName)
        } else {
            if (info!!.organizerLastName != null && info!!.organizerLastName != "null") {
                ownerName.append(info!!.organizerLastName)
                owner.append(" ")
                if (info!!.organizerFirstName != null && info!!.organizerFirstName != "null") {
                    owner.append(info!!.organizerFirstName)
                }
            } else {
                owner.visibility = View.GONE
            }
        }
        owner.text = getString(R.string.board_info_owner, ownerName)

        if (info!!.organizerPhone != null && info!!.organizerPhone != "null") {
            phone.text = getString(R.string.organizer_phone, info!!.organizerPhone)
        } else {
            phone.visibility = View.GONE
        }

        description.text = info!!.description
        if (info!!.url != null && info!!.url != "null" && info!!.url != "") {
            Picasso.with(activity).load(info!!.url!!.replace("@[0-9]*".toRegex(), ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo)
            photo!!.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        if (info!!.organizerId == HomeActivity.mainUser!!.id) {
            val button = activity!!.findViewById<ImageButton>(R.id.edit)
            button.visibility = View.VISIBLE
            button.setOnClickListener { this.onClickEdit(it) }
        }

        if (info!!.subscriptionOnBoard == 1) {
            setIsJoin()
        }

        userApi = ApiClient.getClient(context!!).create<UserApi>(UserApi::class.java!!)
        boardApi = ApiClient.getClient(context!!).create<BoardApi>(BoardApi::class.java!!)
    }

    fun reloadPhoto() {
        if (info!!.url != "" && info!!.url != "null") {
            Picasso.with(activity).load(info!!.url!!.replace("@[0-9]*".toRegex(), ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo)
            photo!!.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun setButtonsListener() {
        val back = activity!!.findViewById<ImageButton>(R.id.back)
        back.setOnClickListener { this.onClickBack(it) }
        val favorite = activity!!.findViewById<CheckBox>(R.id.heart)
        favorite.setOnClickListener { this.onClickJoin(it) }
        val comments = activity!!.findViewById<Button>(R.id.comments)
        comments.setOnClickListener { this.onClickComments(it) }
        val profile = activity!!.findViewById<Button>(R.id.user_profile)
        profile.setOnClickListener { this.onClickProfile(it) }
        val phone = activity!!.findViewById<TextView>(R.id.phone)
        phone.setOnClickListener { this.onClickPhone(it) }
    }

    private fun onClickPhone(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + info!!.organizerPhone!!)
        startActivity(intent)
    }

    private fun onClickProfile(view: View) {
        view.isEnabled = false
        userPresenter!!.getUser(object : UserPresenter.GetUserCallback {
            override fun onSuccess(userResponse: UserResponse) {
                if (userResponse.status) {
                    Log.i("Get User Resp", userResponse.toString())
                    userResponse.user!!.id = info!!.organizerId
                    if (userResponse.user!!.photo != "") {
                        userResponse.user!!.photo = userResponse.user!!.photo!!.replace("\\/".toRegex(), "/")
                    }
                    userPresenter!!.openProfile(userResponse.user!!)
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
                view.isEnabled = true
            }

            override fun onError(networkError: NetworkErrorException) {
                Log.e("BoardFragmentInfo", "onError: " + networkError.message)
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                view.isEnabled = true
            }
        }, info!!.organizerId)
    }

    private fun onClickEdit(view: View) {
        bundle!!.putBoolean("is_edit", true)
        bundle!!.putSerializable("board", info)
        (activity as HomeActivity).addFragment(NewEditBoardFragment.newInstance(bundle!!), FragmentType.BOARD_NEW_EDIT.name)
    }

    fun onClickBack(view: View) {
        (activity as HomeActivity).backFragment()
    }

    private fun onClickComments(view: View) {
        bundle!!.putInt("board_id", info!!.id)
        bundle!!.putString("board_title", info!!.title)
        (context as HomeActivity).addFragment(CommentListFragment.newInstance(bundle!!), FragmentType.COMMENT_LIST.name)
    }

    private fun onClickJoin(view: View) {
        view.isEnabled = false
        if (info!!.subscriptionOnBoard != 1) {
            subscribeAnnouncement(HomeActivity.mainUser!!.id, info!!, view as CheckBox)
        } else {
            unsubscribeAnnouncement(HomeActivity.mainUser!!.id, info!!, view as CheckBox)
        }
    }

    private fun subscribeAnnouncement(user_id: Int, board: Board, favorite: CheckBox) {
        disposable.add(
                boardApi!!.subscribeAnnouncement(user_id.toString(), board.id.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<FavoriteResponse>() {
                            override fun onSuccess(response: FavoriteResponse) {
                                if (response.status) {
                                    board.subscriptionOnBoard = 1
                                    Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    favorite.isChecked = !favorite.isChecked
                                }
                                favorite.isEnabled = true
                            }

                            override fun onError(e: Throwable) {
                                Log.e("BoardsFragment", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                favorite.isChecked = !favorite.isChecked
                                favorite.isEnabled = true
                            }
                        })
        )
    }

    private fun unsubscribeAnnouncement(user_id: Int, board: Board, favorite: CheckBox) {
        disposable.add(
                boardApi!!.unsubscribeAnnouncement(user_id.toString(), board.id.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<FavoriteResponse>() {
                            override fun onSuccess(response: FavoriteResponse) {
                                if (response.status) {
                                    board.subscriptionOnBoard = 0
                                    Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    favorite.isChecked = !favorite.isChecked
                                }
                                favorite.isEnabled = true
                            }

                            override fun onError(e: Throwable) {
                                Log.e("BoardsFragment", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                favorite.isChecked = !favorite.isChecked
                                favorite.isEnabled = true
                            }
                        })
        )
    }

    override fun onBackPressed(): Boolean {
        return (activity as HomeActivity).backFragment()
    }

    companion object {

        fun newInstance(bundle: Bundle): BoardInfoFragment {
            val fragment = BoardInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
