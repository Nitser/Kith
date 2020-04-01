package com.project.scratchstudio.kith_andoid.ui.home_package.BoardList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast

import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.fragments.TreeFragment
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.ui.home_package.BoardInfo.BoardInfoFragment
import com.project.scratchstudio.kith_andoid.ui.home_package.BoardList.list.BoardAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.neweditboard.NewEditBoardFragment
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi
import com.project.scratchstudio.kith_andoid.network.model.board.Board
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

import java.util.ArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.ui.home_package.BoardList.list.BoardHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class BoardsFragment : BaseFragment() {

    private var type = BoardType.ALL
    private var bundle: Bundle? = null
    private var container: RecyclerView? = null
    private var adapter: BoardAdapter? = null

    private var boardApi: BoardApi? = null
    private val disposable = CompositeDisposable()

    private var clickPhoto: ImageView? = null
    private var clickUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bundle = arguments
        return inflater.inflate(R.layout.fragment_announcement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        boardApi = ApiClient.getClient(context!!).create<BoardApi>(BoardApi::class.java!!)

        if (bundle != null && bundle!!.containsKey("type")) {
            type = bundle!!.getSerializable("type") as BoardType
        }

        setButtonsListener()
        loadBoards(type)

        container = activity!!.findViewById(R.id.listCards)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        container!!.layoutManager = llm
        setAdapter()
    }

    fun subscribeAnnouncement(user_id: Int, board: Board, favorite: CheckBox) {
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

    fun unsubscribeAnnouncement(user_id: Int, board: Board, favorite: CheckBox) {
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

    fun reloadPhotoById() {
        Log.i("RELOAD", "IN BOARD OK")
        Picasso.with(context).load(clickUrl)
                .error(R.drawable.newspaper)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(clickPhoto)
    }

    private fun setAdapter() {
        adapter = BoardAdapter(activity!!, object : BoardAdapter.OnItemClickListener {
            override fun onItemClick(item: Board, id: Int, boardHolder: BoardHolder) {
                bundle!!.putSerializable("board", item)
                bundle!!.putSerializable("type", type)
                clickPhoto = boardHolder.image
                clickUrl = item.url
                (context as HomeActivity).addFragment(BoardInfoFragment.newInstance(bundle!!), FragmentType.BOARD_INFO.name)
            }
        }, this)
        container!!.adapter = adapter
    }

    private fun setButtonsListener() {
        val back = activity!!.findViewById<ImageButton>(R.id.plus)
        back.setOnClickListener { this.onClickAdd(it) }

        val all = activity!!.findViewById<Button>(R.id.all)
        val sub = activity!!.findViewById<Button>(R.id.sub)
        val my = activity!!.findViewById<Button>(R.id.my)

        all.setOnClickListener { loadBoards(BoardType.ALL) }
        sub.setOnClickListener { loadBoards(BoardType.SUB) }
        my.setOnClickListener { loadBoards(BoardType.MY) }
    }

    private fun changeSelectedButton() {
        val all = activity!!.findViewById<Button>(R.id.all)
        val sub = activity!!.findViewById<Button>(R.id.sub)
        val my = activity!!.findViewById<Button>(R.id.my)
        when (type) {
            BoardType.SUB -> {
                sub.isSelected = true
                all.isSelected = false
                my.isSelected = false
            }
            BoardType.ALL -> {
                sub.isSelected = false
                all.isSelected = true
                my.isSelected = false
            }
            BoardType.MY -> {
                sub.isSelected = false
                all.isSelected = false
                my.isSelected = true
            }
        }
    }

    private fun loadBoards(newType: BoardType) {
        val dis: Disposable
        when (newType) {
            BoardType.ALL -> {
                dis = boardApi!!.getBoards(HomeActivity.mainUser.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BoardsResponse>() {
                            override fun onSuccess(response: BoardsResponse) {
                                changeBoardDate(newType, response.boards)
                            }

                            override fun onError(e: Throwable) {
                                Log.e("GetAllBoard Resp", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
            }
            BoardType.SUB -> {
                dis = boardApi!!.getFavoriteBoards(HomeActivity.mainUser.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BoardsResponse>() {
                            override fun onSuccess(response: BoardsResponse) {
                                changeBoardDate(newType, response.boards)
                            }

                            override fun onError(e: Throwable) {
                                Log.e("GetAllBoard Resp", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
            }
            else -> {
                dis = boardApi!!.getMyBoards(HomeActivity.mainUser.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BoardsResponse>() {
                            override fun onSuccess(response: BoardsResponse) {
                                changeBoardDate(newType, response.boards)
                            }

                            override fun onError(e: Throwable) {
                                Log.e("GetAllBoard Resp", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
            }
        }

        disposable.add(dis)
    }

    private fun changeBoardDate(boardType: BoardType, boards: ArrayList<Board>) {
        for (board in boards) {
            Log.i("BOARDLIST", "cost = " + board.cost)
            board.organizerName = board.organizerFirstName + " " + board.organizerLastName
            if (board.url != "") {
                board.url = board.url.replace("\\/".toRegex(), "/")
            }
        }
        type = boardType
        changeSelectedButton()
        adapter!!.annList = boards
        adapter!!.notifyDataSetChanged()
    }

    private fun onClickAdd(view: View) {
        val homeActivity = activity as HomeActivity?
        bundle!!.putSerializable("type", type)
        homeActivity!!.addFragment(NewEditBoardFragment.newInstance(bundle!!), FragmentType.BOARD_NEW_EDIT.name)
    }

    fun reloadBoards() {
        loadBoards(type)
    }

    override fun onBackPressed(): Boolean {
        if ((activity as HomeActivity).getStackBundles().size == 1) {
            val homeActivity = activity as HomeActivity?
            homeActivity!!.replaceFragment(TreeFragment.newInstance(bundle!!), FragmentType.TREE.name)
            homeActivity.setTreeNavigation()
        } else {
            val bundle = (activity as HomeActivity).getStackBundles()[(activity as HomeActivity).getStackBundles().size - 1]
            val homeActivity = activity as HomeActivity?
            homeActivity!!.replaceFragment(TreeFragment.newInstance(bundle), FragmentType.TREE.name)
            homeActivity.setTreeNavigation()
        }
        return true
    }

    companion object {

        fun newInstance(bundle: Bundle, title: String): BoardsFragment {
            val fragment = BoardsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
