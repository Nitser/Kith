package com.project.scratchstudio.kith_andoid.Fragments

import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.scratchstudio.kith_andoid.Activities.CodeActivity
import com.project.scratchstudio.kith_andoid.Activities.EntryActivity
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.service.HttpService
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.holders.TreeHolder
import com.project.scratchstudio.kith_andoid.model.Cache
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import java.util.Arrays

class TreeFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var httpService: HttpService
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var list: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var mySwipeRefreshLayout: SwipeRefreshLayout
    private var currentUser = HomeActivity.mainUser

    private lateinit var userApi: UserApi
    private val disposable = CompositeDisposable()
    private lateinit var userPresenter: UserPresenter

    private val isNetworkConnected: Boolean
        @SuppressLint("MissingPermission")
        get() {
            val cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bundle = if (arguments != null) arguments!! else Bundle()
        return inflater.inflate(R.layout.fragment_tree, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        userApi = ApiClient.getClient(context!!).create<UserApi>(UserApi::class.java!!)

        setButtonsListener()
        httpService = HttpService()
        val mainUser = HomeActivity.mainUser
        var userName = ""

        if (mainUser.firstName == "") {
            userPresenter.getUser(object : UserPresenter.GetUserCallback {
                override fun onSuccess(userResponse: UserResponse) {
                    if (userResponse.status) {
                        currentUser.firstName = userResponse.user.firstName
                        currentUser.lastName = userResponse.user.lastName
                        currentUser.middleName = userResponse.user.middleName
                        currentUser.login = userResponse.user.login
                        currentUser.phone = userResponse.user.phone
                        currentUser.description = userResponse.user.description
                        currentUser.position = userResponse.user.position
                        currentUser.email = userResponse.user.email
                        currentUser.photo = userResponse.user.photo.replace("\\/".toRegex(), "/")

                        val photo = view.findViewById<ImageView>(R.id.photo)
                        Picasso.with(context).load(currentUser.photo)
                                .placeholder(R.mipmap.person)
                                .error(R.mipmap.person)
                                .transform(PicassoCircleTransformation())
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .into(photo)

                        val name = view.findViewById<CustomFontTextView>(R.id.name)
                        val position = view.findViewById<CustomFontTextView>(R.id.position)
                        val userName = currentUser.firstName + " " + currentUser.lastName
                        name.text = userName
                        position.text = currentUser.position
                    } else {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        exit()
                    }
                }

                override fun onError(networkError: NetworkErrorException) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    exit()
                }
            }, currentUser.id)
        } else {
            userName = currentUser.firstName + " " + currentUser.lastName
        }
        HomeActivity.createInvitedUsers()

        init(userName)
        initReferralCount()

        userPresenter.getInvitedUsers(object : UserPresenter.GetUserListCallback {
            override fun onSuccess(userResponse: Array<User>) {
                val response = ArrayList(Arrays.asList(*userResponse))
                for (user in response) {
                    if (user.photo == "") {
                        user.photo = user.photo.replace("\\/".toRegex(), "/").replace("@[0-9]*".toRegex(), "")
                        Log.d("UserDebug", "Photo: " + user.photo)
                    }
                }
                setInvitedUsersList(response)
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, mainUser.id)

        mySwipeRefreshLayout = activity!!.findViewById(R.id.swiperefresh)
        mySwipeRefreshLayout.setOnRefreshListener { this.myUpdateOperation() }

    }

    private fun exit() {
        HomeActivity.cleanMainUser()
//        val internalStorageService = InternalStorageService(activity!!)
//        internalStorageService.setISetInternalData(ClearUserIdAndToken())
//        internalStorageService.execute()
        val intent = Intent(context, EntryActivity::class.java)
        startActivity(intent)
    }

    private fun initReferralCount() {
        userPresenter.getReferralCount(object : UserPresenter.ReferralCodeCallback {
            override fun onSuccess(response: ReferralResponse) {
                if (response.status) {
                    HomeActivity.mainUser.usersCount = response.referralCount
                    Cache.myUsers = response.referralCount
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, HomeActivity.mainUser.id, 0)

        userPresenter.getReferralCount(object : UserPresenter.ReferralCodeCallback {
            override fun onSuccess(response: ReferralResponse) {
                if (response.status) {
                    val textView = activity!!.findViewById<CustomFontTextView>(R.id.yourPeople)
                    textView.text = activity!!.resources
                            .getQuantityString(R.plurals.people, response.referralCount, response.referralCount)
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, HomeActivity.mainUser.id, 1)

    }

    private fun init(str_name: String) {
        val name = activity!!.findViewById<CustomFontTextView>(R.id.name)
        val position = activity!!.findViewById<CustomFontTextView>(R.id.position)
        val photo = activity!!.findViewById<ImageView>(R.id.photo)
        if (currentUser.photo == "") {
            Picasso.with(activity).load(currentUser.photo.replace("@[0-9]*".toRegex(), ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo)
        }
        name.text = str_name
        position.text = currentUser.position

        linearLayout = activity!!.findViewById(R.id.list_view)

    }

    private fun setInvitedUsersList(invitedUsers: List<User>) {
        cleanUsers()
        for (i in invitedUsers.indices) {
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val itemView = inflater.inflate(R.layout.list_item_layout, null)
            linearLayout.addView(itemView, i)
            val holder = TreeHolder(itemView)

            val user = invitedUsers[i]
            val name = user.firstName + " " + user.lastName
            holder.name.text = name
            holder.position.text = user.position

            Picasso.with(activity).load(user.photo)
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image)

            itemView.setOnClickListener {
                buttonCount = SystemClock.elapsedRealtime()
                userPresenter.openProfile(user)
            }
        }
    }

    private fun cleanUsers() {
        linearLayout.removeAllViews()
    }

    private fun myUpdateOperation() {
        val activity = activity

        if (isNetworkConnected) {
            val mainUser = HomeActivity.mainUser
            Handler().post {
                if (bundle.containsKey("another_user") && !bundle.getBoolean("another_user")) {
                    initReferralCount()

                    HomeActivity.createInvitedUsers()
                    userPresenter.getInvitedUsers(object : UserPresenter.GetUserListCallback {
                        override fun onSuccess(userResponse: Array<User>) {
                            val response = ArrayList(Arrays.asList(*userResponse))
                            for (user in response) {
                                if (user.photo == "") {
                                    user.photo = user.photo.replace("\\/".toRegex(), "/").replace("@[0-9]*".toRegex(), "")
                                }
                            }
                            setInvitedUsersList(response)
                            mySwipeRefreshLayout.isRefreshing = false
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            mySwipeRefreshLayout.isRefreshing = false
                        }
                    }, mainUser.id)

                    refreshUser()
                }
            }
        } else {
            Toast.makeText(activity, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            mySwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshUser() {
        val photo = activity!!.findViewById<ImageView>(R.id.photo)
        val name = activity!!.findViewById<CustomFontTextView>(R.id.name)
        val position = activity!!.findViewById<CustomFontTextView>(R.id.position)

        val user = HomeActivity.mainUser
        Picasso.with(activity).load(user.photo.replace("@[0-9]*".toRegex(), ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(photo)
        val nameStr = HomeActivity.mainUser.firstName + " " + HomeActivity.mainUser.lastName
        name.text = nameStr
        position.text = user.position
    }

    private fun setButtonsListener() {
        val back = activity!!.findViewById<ImageButton>(R.id.back)
        back.setOnClickListener { this.onClickBack() }
        val search = activity!!.findViewById<ImageButton>(R.id.search)
        search.setOnClickListener { this.onClickSearch(it) }
        val code = activity!!.findViewById<ImageButton>(R.id.plus)
        code.setOnClickListener { this.onClickCode(it) }
        val profile = activity!!.findViewById<ImageButton>(R.id.buttonProfile)
        profile.setOnClickListener { this.onClickProfileButton(it) }
        val searchBack = activity!!.findViewById<ImageButton>(R.id.back_search)
        searchBack.setOnClickListener { this.onClickBackSearch(it) }
    }

    private fun onClickBack() {
        (activity as HomeActivity).getStackBundles().removeAt((activity as HomeActivity).getStackBundles().size - 1)
        val bundle = (activity as HomeActivity).getStackBundles()[(activity as HomeActivity).getStackBundles().size - 1]
        val homeActivity = activity as HomeActivity?
        homeActivity!!.replaceFragment(newInstance(bundle), FragmentType.TREE.name)
    }

    private fun onClickCode(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return
        }
        buttonCount = SystemClock.elapsedRealtime()
        view.isEnabled = false
        val intent = Intent(context, CodeActivity::class.java)
        startActivity(intent)
        view.isEnabled = true
    }

    private fun onClickSearch(view: View) {
        val layout = activity!!.findViewById<RelativeLayout>(R.id.search_header)
        layout.visibility = View.VISIBLE
        val linearLayout = activity!!.findViewById<LinearLayout>(R.id.paper_search)
        val editText = activity!!.findViewById<EditText>(R.id.filter)
        editText.requestFocus()
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        view.isEnabled = false
        disposable.add(
                userApi.searchUsers(HomeActivity.mainUser!!.id, "", "0", "500")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<UserListResponse>() {
                            override fun onSuccess(response: UserListResponse) {
                                if (response.status) {
                                    for (user in response.users) {
                                        if (user.photo == "") {
                                            user.photo = user.photo.replace("\\/".toRegex(), "/").replace("@[0-9]*".toRegex(), "")
                                        }
                                    }
                                    setListPersons(response.users)
                                    setSearchAdapter(view)
                                } else {
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                }
                                view.isEnabled = true
                            }

                            override fun onError(e: Throwable) {
                                Log.e("TreeFragmentInfo", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                view.isEnabled = true
                            }
                        })
        )

        list = activity!!.findViewById(R.id.listPerson)
        val filter = activity!!.findViewById<EditText>(R.id.filter)
        filter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    linearLayout.visibility = View.VISIBLE
                } else {
                    linearLayout.visibility = View.INVISIBLE
                }
                this@TreeFragment.searchAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        list.layoutManager = llm

    }

    private fun onClickBackSearch(view: View) {
        val layout = activity!!.findViewById<RelativeLayout>(R.id.search_header)
        layout.visibility = View.INVISIBLE
        val linearLayout = activity!!.findViewById<LinearLayout>(R.id.paper_search)
        linearLayout.visibility = View.INVISIBLE
        val editText = activity!!.findViewById<EditText>(R.id.filter)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun setSearchAdapter(view: View) {
        searchAdapter = SearchAdapter(activity!!, listPersons, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: User) {
                getProfile(view, item.id)
            }
        })

        list.adapter = searchAdapter
    }

    private fun getProfile(view: View, newUserId: Int) {
        view.isEnabled = false
        val editText = activity!!.findViewById<EditText>(R.id.filter)
        editText.requestFocus()
        userPresenter.getUser(object : UserPresenter.GetUserCallback {
            override fun onSuccess(userResponse: UserResponse) {
                if (userResponse.status) {
                    userResponse.user.id = newUserId
                    if (userResponse.user.photo == "") {
                        userResponse.user.photo = userResponse.user.photo.replace("\\/".toRegex(), "/")
                    }
                    hideKeyboard(activity!!)
                    userPresenter.openProfile(userResponse.user)
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
                view.isEnabled = true
            }

            override fun onError(networkError: NetworkErrorException) {
                Log.e("TreeFragmentInfo", "onError: " + networkError.message)
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                view.isEnabled = true
            }
        }, newUserId)
    }

    private fun onClickProfileButton(view: View) {
        view.isEnabled = false
        userPresenter.openProfile(HomeActivity.mainUser)
        view.isEnabled = true
    }

    companion object {

        private var buttonCount: Long = 0

        private var listPersons: List<User> = ArrayList()

        private fun setListPersons(list: List<User>) {
            listPersons = list
        }

        fun newInstance(bundle: Bundle): TreeFragment {
            val treeFragment = TreeFragment()
            treeFragment.arguments = bundle
            return treeFragment
        }

        private fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
