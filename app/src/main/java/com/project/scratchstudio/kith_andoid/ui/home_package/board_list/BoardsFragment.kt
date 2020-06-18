package com.project.scratchstudio.kith_andoid.ui.home_package.board_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.UserType
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardListBinding
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.list.BoardAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.list.BoardHolder
import com.project.scratchstudio.kith_andoid.view_model.BoardListViewModel
import com.project.scratchstudio.kith_andoid.view_model.CurrentBoardViewModel
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class BoardsFragment : BaseFragment() {

    private lateinit var adapter: BoardAdapter
    private lateinit var boardPresenter: BoardPresenter
    private val mainUserViewModel: MainUserViewModel by activityViewModels()
    private val boardListViewModel: BoardListViewModel by activityViewModels()
    private lateinit var binding: FragmentBoardListBinding

    var btnBottomSheet: Button? = null
    var layoutBottomSheet: LinearLayout? = null

    var sheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        boardPresenter = BoardPresenter(context!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBoardListBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_board_list_create_board -> {
                activity!!.findNavController(R.id.nav_host_fragment_home)
                        .navigate(BoardsFragmentDirections.actionBoardsFragmentToNewEditBoardFragment(UserType.MAIN_NEW_USER))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleBottomSheet() {
        if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            btnBottomSheet?.text = "Close sheet"
        } else {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            btnBottomSheet?.text = "Expand sheet"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        btnBottomSheet = activity?.findViewById(R.id.bottom_sheet_button_done)
//        layoutBottomSheet = activity?.findViewById(R.id.bottom_sheet)
//        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)

//        binding.boardListButtonFilterList.setOnClickListener { toggleBottomSheet() }

        binding.boardListButtonFilterList.setOnClickListener(this::chooseCategory)
        binding.boardListButtonSearchBack.setOnClickListener {
            HomeActivity.hideKeyboard(activity!!)
            closeSearch()
        }
        binding.boardListSearchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                openSearch()
            else
                closeSearch()
        }
        binding.boardListSearchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.i("FILTER BOARD", "text changed. $s ")
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        val llm = GridLayoutManager(context, 2)
        binding.boardListBoards.layoutManager = llm
        setAdapter()
        boardListViewModel.getBoardList().observe(viewLifecycleOwner, Observer<ArrayList<BoardModelView>> { item ->
            adapter.annList.clear()
            adapter.annList.addAll(item)
            adapter.notifyDataSetChanged()
        })
        filterList(binding.boardListSearchField.text.toString())
    }

    private fun chooseCategory(view: View) {

    }

    private fun setAdapter() {
        adapter = BoardAdapter(activity!!, object : BoardAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardModelView, id: Int, boardHolder: BoardHolder) {
                val model: CurrentBoardViewModel by activityViewModels()
                model.setCurrentBoard(item)
                activity!!.findNavController(R.id.nav_host_fragment_home)
                        .navigate(BoardsFragmentDirections.actionBoardsFragmentToBoardInfoFragment(
                                if (mainUserViewModel.getMainUser().value!!.id == item.organizerId)
                                    UserType.MAIN_USER
                                else UserType.ANOTHER_USER
                        ))
            }
        }, boardPresenter)
        binding.boardListBoards.adapter = adapter
    }

    private fun filterList(message: String) {
        Log.i("FILTER BOARD", message + " ")
        HomeActivity.disposable.add(
                HomeActivity.boardApi.searchBoards(message, "", 80, 0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BoardsResponse>() {
                            override fun onSuccess(response: BoardsResponse) {
                                boardListViewModel.setBoardList(ArrayList(response.boards.map { boardPresenter.boardParser(it) }))
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
        )
    }

    private fun openSearch() {
        binding.boardListIcSearch.visibility = View.INVISIBLE
        binding.boardListButtonSearchBack.visibility = View.VISIBLE
        binding.boardListSearchField.requestFocus()
        HomeActivity.showKeyboard(activity!!, binding.boardListSearchField)
    }

    private fun closeSearch() {
        binding.boardListIcSearch.visibility = View.VISIBLE
        binding.boardListButtonSearchBack.visibility = View.GONE
        binding.boardListSearchField.clearFocus()
    }

    companion object {

        fun newInstance(): BoardsFragment {
            return BoardsFragment()
        }
    }
}
