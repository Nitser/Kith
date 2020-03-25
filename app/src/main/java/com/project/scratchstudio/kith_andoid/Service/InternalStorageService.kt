package com.project.scratchstudio.kith_andoid.Service

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Handler
import android.preference.PreferenceManager

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.Activities.MainActivity
import com.project.scratchstudio.kith_andoid.GetInternalData.IGetInternalData
import com.project.scratchstudio.kith_andoid.model.Cache
import com.project.scratchstudio.kith_andoid.SetInternalData.ISetInternalData

class InternalStorageService(private val context: Activity) : AsyncTask<String, Void, Int>() {

    private val SPLASH_DISPLAY_LENGTH = 2000
    private var iGetInternalData: IGetInternalData? = null
    private var iSetInternalData: ISetInternalData? = null

    override fun onPostExecute(i: Int?) {
        super.onPostExecute(i)
        when (i) {
            1 -> Handler().postDelayed({
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                context.finish()
            }, SPLASH_DISPLAY_LENGTH.toLong())
            2 -> Handler().postDelayed({
                val intent2 = Intent(context, HomeActivity::class.java)
                intent2.putExtra("another_user", false)
                context.startActivity(intent2)
                context.finish()
            }, SPLASH_DISPLAY_LENGTH.toLong())
        }
    }

    override fun doInBackground(vararg strings: String): Int? {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        if (!sp.contains("count_users")) {
            inizializingStorage(sp)
        }
        return if (iGetInternalData != null) {
            iGetInternalData!!.get(sp)
        } else
            iSetInternalData!!.set(sp)
    }

    //    public String

    fun setiGetInternalData(iGetInternalData: IGetInternalData) {
        this.iGetInternalData = iGetInternalData
    }

    fun setiSetInternalData(iSetInternalData: ISetInternalData) {
        this.iSetInternalData = iSetInternalData
    }

    private fun inizializingStorage(sp: SharedPreferences) {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("count_users", 0)
        ed.putInt("cur_user_id", -1)
        ed.putString("user_token", "")
        ed.apply()

        Cache.allUsers = 0
    }

}
