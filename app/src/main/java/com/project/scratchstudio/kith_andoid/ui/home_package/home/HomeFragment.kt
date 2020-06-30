package com.project.scratchstudio.kith_andoid.ui.home_package.home

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.databinding.FragmentHomeBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse
import com.project.scratchstudio.kith_andoid.utils.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.ui.home_package.home.invited_user_list.InvitedUserAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.home.search_user_list.SearchAdapter
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList

class HomeFragment : Fragment() {

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var userPresenter: UserPresenter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: InvitedUserAdapter
    private val mainUserViewModel: MainUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home_search -> {
                onClickSearch()
                return true
            }
            R.id.menu_home_share -> {
                activity!!.findNavController(R.id.nav_host_fragment_home).navigate(HomeFragmentDirections.actionHomeFragmentToShareCodeFragment(mainUserViewModel.getMainUser().value!!.referralCode))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        binding.homeInvitedPeopleList.layoutManager = LinearLayoutManager(requireContext())
        adapter = InvitedUserAdapter(requireContext(), object : InvitedUserAdapter.OnItemClickListener {
            override fun onItemClick(item: UserModelView) {
//                currentUserViewModel.setCurrentUser(item)
                activity!!.findNavController(R.id.nav_host_fragment_home)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(item))
            }
        })
        binding.homeInvitedPeopleList.adapter = adapter

        binding.homeButtonProfile.setOnClickListener {
            activity!!.findNavController(R.id.nav_host_fragment_home)
                    .navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(mainUserViewModel.getMainUser().value!!))
        }
        binding.homeButtonBackSearch.setOnClickListener {
            closeSearch()
        }
        binding.homeSwipeRefreshLayout.setOnRefreshListener { this.myUpdateOperation() }

        mainUserViewModel.getMainUser().observe(viewLifecycleOwner, Observer<UserModelView> {
            initUserData()
        })

        if (mainUserViewModel.getMainUser().value!!.firstName == "") {
            userPresenter.getUser(object : UserPresenter.GetUserCallback {
                override fun onSuccess(userResponse: User) {
                    mainUserViewModel.setMainUser(userPresenter.userParser(userResponse, mainUserViewModel.getMainUser().value!!))
                }

                override fun onError(networkError: NetworkErrorException) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    HomeActivity.exit(activity!!)
                }
            }, mainUserViewModel.getMainUser().value!!.id)
        } else {
            initUserData()
        }

        userPresenter.getInvitedUsers(object : UserPresenter.GetUserListCallback {
            override fun onSuccess(userResponse: UserListResponse) {
                val response = ArrayList(userResponse.users).map {
                    userPresenter.userParser(it, UserModelView())
                }
                adapter.invitedUsers.clear()
                adapter.invitedUsers.addAll(response)
                adapter.notifyDataSetChanged()
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, mainUserViewModel.getMainUser().value!!.id)
    }

    private fun initUserData() {
        with(mainUserViewModel.getMainUser().value!!) {
            binding.homeUserName.text = "$firstName $lastName"
            binding.homeUserPosition.text = position
            if (photoBitmap != null) {
                binding.homeUserPhoto.setImageBitmap(photoBitmap)
            } else if (photo != "")
                Picasso.with(context).load(Const.BASE_URL + photo)
                        .placeholder(R.mipmap.person)
                        .error(R.mipmap.person)
                        .transform(PicassoCircleTransformation())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(binding.homeUserPhoto)
        }
    }

    private fun myUpdateOperation() {
        Handler().post {
            userPresenter.getInvitedUsers(object : UserPresenter.GetUserListCallback {
                override fun onSuccess(userResponse: UserListResponse) {
                    val response = ArrayList(userResponse.users).map {
                        userPresenter.userParser(it, UserModelView())
                    }
                    adapter.invitedUsers.clear()
                    adapter.invitedUsers.addAll(response)
                    adapter.notifyDataSetChanged()

                    userPresenter.getUser(object : UserPresenter.GetUserCallback {
                        override fun onSuccess(userResponse: User) {
                            mainUserViewModel.setMainUser(userPresenter.userParser(userResponse, mainUserViewModel.getMainUser().value!!))
                            binding.homeSwipeRefreshLayout.isRefreshing = false
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            binding.homeSwipeRefreshLayout.isRefreshing = false
                        }
                    }, mainUserViewModel.getMainUser().value!!.id)
                }

                override fun onError(networkError: NetworkErrorException) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    binding.homeSwipeRefreshLayout.isRefreshing = false
                }
            }, mainUserViewModel.getMainUser().value!!.id)
        }
    }

    private fun openSearch() {
        binding.homeSearchLayout.visibility = View.VISIBLE
        binding.homeSearchPersonList.visibility = View.VISIBLE
        binding.homeSearchField.requestFocus()
        HomeActivity.showKeyboard(requireActivity(), binding.homeSearchField)
    }

    private fun closeSearch() {
        HomeActivity.hideKeyboard(requireActivity())
        binding.homeSearchField.clearFocus()
        binding.homeSearchLayout.visibility = View.GONE
        binding.homeSearchPersonList.visibility = View.GONE
    }

    private fun setSearchAdapter(searchResultList: ArrayList<UserModelView>) {
        searchAdapter = SearchAdapter(activity!!, searchResultList, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: UserModelView) {
                userPresenter.getUser(object : UserPresenter.GetUserCallback {
                    override fun onSuccess(userResponse: User) {
                        closeSearch()
//                        currentUserViewModel.setCurrentUser(item)
                        activity!!.findNavController(R.id.nav_host_fragment_home)
                                .navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                                        userPresenter.userParser(userResponse, UserModelView())
                                ))
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, item.id)
            }
        })
        binding.homeSearchPersonList.adapter = searchAdapter
    }

    private fun onClickSearch() {
        openSearch()
        userPresenter.searchUsers(object : UserPresenter.GetUserListCallback {
            override fun onSuccess(userResponse: UserListResponse) {
                val result = ArrayList(userResponse.users).map {
                    userPresenter.userParser(it, UserModelView())
                }
                setSearchAdapter(ArrayList(result))

                binding.homeSearchField.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        searchAdapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable) {

                    }
                })
                val llm = LinearLayoutManager(context)
                llm.orientation = LinearLayoutManager.VERTICAL
                binding.homeSearchPersonList.layoutManager = llm
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, binding.homeSearchField.text.toString())
    }

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

}
