package com.project.scratchstudio.kith_andoid.ui.home_package.board_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.UserType
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardListBinding
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.model.SearchOptions
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.bottom_sheet.BottomSheetFragment
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.list.BoardAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.list.BoardHolder
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.paid_list.PaidBoardAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.paid_list.PaidBoardHolder
import com.project.scratchstudio.kith_andoid.view_model.BoardListViewModel
import com.project.scratchstudio.kith_andoid.view_model.CurrentBoardViewModel
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import com.project.scratchstudio.kith_andoid.view_model.SearchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class BoardsFragment : BaseFragment() {

    private lateinit var adapter: BoardAdapter
    private lateinit var paidAdapter: PaidBoardAdapter
    private lateinit var boardPresenter: BoardPresenter
    private val mainUserViewModel: MainUserViewModel by activityViewModels()
    private val boardListViewModel: BoardListViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private lateinit var binding: FragmentBoardListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        boardPresenter = BoardPresenter(context!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(false)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        binding = FragmentBoardListBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board_list, menu)
        searchViewModel.setSearchOptions(SearchOptions())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_board_list_create_board -> {
                val model: CurrentBoardViewModel by activityViewModels()
                model.setCurrentBoard(BoardModelView())
                activity!!.findNavController(R.id.nav_host_fragment_home)
                        .navigate(BoardsFragmentDirections.actionBoardsFragmentToNewEditBoardFragment(UserType.MAIN_NEW_USER))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        val llm = GridLayoutManager(context, 2)
        binding.boardListBoards.layoutManager = llm
        val llmPaid = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.boardListPaidBoards.layoutManager = llmPaid
        setAdapter()
        boardListViewModel.getBoardList().observe(viewLifecycleOwner, Observer<ArrayList<BoardModelView>> { item ->
            adapter.annList.clear()
            paidAdapter.annList.clear()
            val paidItem = ArrayList(item.filter { it.isPaid })
            val notPaidItem = ArrayList(item.filter { !it.isPaid })
            adapter.annList.addAll(notPaidItem)
            paidAdapter.annList.addAll(paidItem)
            adapter.notifyDataSetChanged()
            paidAdapter.notifyDataSetChanged()
        })
        searchViewModel.getSearchOptions().observe(viewLifecycleOwner, Observer<SearchOptions> {
            filterList(binding.boardListSearchField.text.toString())
        })
        filterList(binding.boardListSearchField.text.toString())
    }

    private fun chooseCategory(view: View) {
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun setAdapter() {
        adapter = BoardAdapter(requireActivity(), object : BoardAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardModelView, id: Int, boardHolder: BoardHolder) {
                val model: CurrentBoardViewModel by activityViewModels()
                model.setCurrentBoard(item)
                requireActivity().findNavController(R.id.nav_host_fragment_home)
                        .navigate(BoardsFragmentDirections.actionBoardsFragmentToBoardInfoFragment(
                                if (mainUserViewModel.getMainUser().value!!.id == item.organizerId)
                                    UserType.MAIN_USER
                                else UserType.ANOTHER_USER
                        ))
            }
        }, boardPresenter)
        paidAdapter = PaidBoardAdapter(requireActivity(), object : PaidBoardAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardModelView, id: Int, boardHolder: PaidBoardHolder) {
                val model: CurrentBoardViewModel by activityViewModels()
                model.setCurrentBoard(item)
                requireActivity().findNavController(R.id.nav_host_fragment_home)
                        .navigate(BoardsFragmentDirections.actionBoardsFragmentToBoardInfoFragment(
                                if (mainUserViewModel.getMainUser().value!!.id == item.organizerId)
                                    UserType.MAIN_USER
                                else UserType.ANOTHER_USER
                        ))
            }
        }, boardPresenter)
        binding.boardListBoards.adapter = adapter
        binding.boardListPaidBoards.adapter = paidAdapter
    }

    private fun filterList(message: String) {
        if (searchViewModel.getSearchOptions().value != null)
            with(searchViewModel.getSearchOptions().value!!) {
                val categoryId = if (this.categoryId != -1)
                    this.categoryId.toString()
                else
                    ""

                val countryId = if (this.countryId != -1)
                    this.countryId.toString()
                else
                    ""

                val regionId = if (this.regionId != -1)
                    this.regionId.toString()
                else
                    ""

                val cityId = if (this.cityId != -1)
                    this.cityId.toString()
                else
                    ""

                HomeActivity.disposable.add(
                        HomeActivity.boardApi.searchBoards(message, categoryId, countryId, regionId, cityId, 80, 0)
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

}
