package com.project.scratchstudio.kith_andoid.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.utils.internal_storage.InternalStorageService
import com.project.scratchstudio.kith_andoid.utils.internal_storage.set_internal_data.ClearUserIdAndToken
import com.project.scratchstudio.kith_andoid.utils.internal_storage.set_internal_data.SetUserIdAndToken
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class HomeActivity : BaseActivity() {

    companion object {
        lateinit var userApi: UserApi
        lateinit var boardApi: BoardApi
        val disposable = CompositeDisposable()

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboard(activity: Activity, editTextView: View) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editTextView, InputMethodManager.SHOW_IMPLICIT)
        }

        fun exit(activity: Activity) {
            val clearUser = InternalStorageService(activity, object : InternalStorageService.PostExecuteCallback {
                override fun doAfter() {
                    Const.isEntry = false
                    val intent = Intent(activity, EntryActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
            })
            clearUser.setISetInternalData(ClearUserIdAndToken())
            clearUser.execute()
        }
    }

    private val mainUserViewModel: MainUserViewModel by viewModels()
    private lateinit var userPresenter: UserPresenter
    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (intent.getSerializableExtra("user") != null) {
            mainUserViewModel.setMainUser(intent.getSerializableExtra("user") as UserModelView)
        }

        Const.isEntry = true

        userApi = ApiClient.getClient(applicationContext).create<UserApi>(UserApi::class.java)
        boardApi = ApiClient.getClient(applicationContext).create<BoardApi>(BoardApi::class.java)
        userPresenter = UserPresenter(this)

        val navController = findNavController(R.id.nav_host_fragment_home)
        NavigationUI.setupActionBarWithNavController(this, navController)

        navigationView = findViewById(R.id.bottom_navigation_home)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReSelectedListener)

        initializeUserHomePage()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_home)
        return navController.navigateUp()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_tree -> {
                findNavController(R.id.nav_host_fragment_home).navigate(R.id.homeFragment)
                item.isChecked = true
            }
            R.id.navigation_announcements -> {
                findNavController(R.id.nav_host_fragment_home).navigate(R.id.boardsFragment)
                item.isChecked = true
            }
        }
        false
    }

    private val mOnNavigationItemReSelectedListener = BottomNavigationView.OnNavigationItemReselectedListener {
        supportFragmentManager.popBackStackImmediate()
    }

    private fun initializeUserHomePage() {
        disposable.add(
                userApi.getUser(mainUserViewModel.getMainUser().value!!.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<User>() {
                            override fun onSuccess(response: User) {
                                mainUserViewModel.setMainUser(userPresenter.userParser(response, mainUserViewModel.getMainUser().value!!))
                                saveUserOnPhoneStorage()
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
        )
    }

    private fun saveUserOnPhoneStorage() {
        val internalStorageService = InternalStorageService(this, null)
        with(mainUserViewModel.getMainUser().value!!) {
            internalStorageService.setUserData(id, token, password)
        }
        internalStorageService.setISetInternalData(SetUserIdAndToken())
        internalStorageService.execute()
    }

}
