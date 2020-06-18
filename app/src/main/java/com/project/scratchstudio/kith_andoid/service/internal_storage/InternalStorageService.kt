package com.project.scratchstudio.kith_andoid.service.internal_storage

import android.app.Activity
import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import com.project.scratchstudio.kith_andoid.model.Cache
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.service.internal_storage.get_internal_data.IGetInternalData
import com.project.scratchstudio.kith_andoid.service.internal_storage.set_internal_data.ISetInternalData

class InternalStorageService(private val context: Activity, private val callback: PostExecuteCallback?) : AsyncTask<String, Void, Int>() {

    private var iGetInternalData: IGetInternalData? = null
    private var iSetInternalData: ISetInternalData? = null

    private var id: Int = 0
    private var token: String? = ""
    private var password: String? = ""
    fun setUserData(idq: Int, tokenq: String, passwordq: String) {
        id = idq
        token = tokenq
        password = passwordq
    }

    companion object {
        var entryStatus = false
        var user = UserModelView()
    }

    override fun onPostExecute(i: Int?) {
        super.onPostExecute(i)
        callback?.doAfter()
    }

    interface PostExecuteCallback {
        fun doAfter()
    }

    override fun doInBackground(vararg strings: String): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        if (!sp.contains("cur_user_id")) {
            initializingStorage(sp)
        }
        return if (iGetInternalData != null) {
            iGetInternalData!![sp]
            0
        } else {
            iSetInternalData!!.set(sp, id, token, password)
            1
        }
    }

    fun setIGetInternalData(iGetInternalData: IGetInternalData) {
        this.iGetInternalData = iGetInternalData
    }

    fun setISetInternalData(iSetInternalData: ISetInternalData) {
        this.iSetInternalData = iSetInternalData
    }

    private fun initializingStorage(sp: SharedPreferences) {
        val ed: SharedPreferences.Editor = sp.edit()
//        ed.putInt("count_users", 0)
        ed.putInt("cur_user_id", -1)
        ed.putString("user_token", "")
        ed.apply()

        Cache.allUsers = 0
    }

}
