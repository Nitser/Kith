package com.project.scratchstudio.kith_andoid.Service

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.project.scratchstudio.kith_andoid.Activities.ChangePasswordActivity
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.network.model.user.User
import org.json.JSONException
import org.json.JSONObject

class HttpService {

//    fun count(activity: Activity?, textView: CustomFontTextView) {
//        val key = "users_count"
//
//        val httpGetRequest = HttpGetRequest(activity) { output, resultJSON, code ->
//            if (output!! && resultJSON != null) {
//                val response: JSONObject
//                try {
//                    response = JSONObject(resultJSON)
//                    if (activity != null) {
//                        textView.text = response.get(key).toString()
//                    }
//                    Cache.allUsers = response.getInt(key)
//                    val getCount = InternalStorageService(activity)
//                    getCount.setiSetInternalData(SetCountData())
//                    getCount.execute()
//                } catch (e: JSONException) {
//                    if (code != 200 && activity != null) {
//                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
//                    }
//                    e.printStackTrace()
//                } catch (ignored: NullPointerException) {
//                }
//
//            } else {
//                if (activity != null) {
//                    textView.setText(Cache.allUsers)
//                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//        httpGetRequest.execute("http://$SERVER/api/users/count")
//
//    }


    fun sendSms(activity: Activity?, login: String) {
        val result_keys = arrayOf("status")
        val body_keys = arrayOf("user_login")
        val body_data = arrayOf(login)

        val httpPostRequest = HttpPostRequest(activity, body_keys, body_data) { output, resultJSON, code ->
            if (output!! && resultJSON != null) {
                try {
                    val response = JSONObject(resultJSON)
                    if (!response.getBoolean(result_keys[0]) && activity != null) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    if (activity != null) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                } catch (ignored: NullPointerException) {
                }

            } else if (activity != null) {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
            }
        }
        httpPostRequest.execute("http://$SERVER/api/users/confirm")
    }

    fun checkSms(activity: Activity?, view: View, id: Int, smsCode: String) {
        val result_keys = arrayOf("status", "access_token", "token_type")
        val body_keys = arrayOf("user_login", "sms_code", "user_password")
        val body_data = arrayOf(HomeActivity.mainUser!!.login, smsCode, HomeActivity.mainUser!!.password)

        val httpPostRequest = HttpPostRequest(activity, body_keys, body_data) { output, resultJSON, code ->
            if (output!! && resultJSON != null) {
                try {
                    val response = JSONObject(resultJSON)
                    if (response.getBoolean(result_keys[0]) && activity != null) {
                        HomeActivity.mainUser!!.token = response.getString(result_keys[2]) + " " + response.getString(result_keys[1])
                        val intent = Intent(activity, HomeActivity::class.java)
                        intent.putExtra("another_user", false)
                        activity.startActivity(intent)
                        Toast.makeText(activity, "СМС код подтвержден", Toast.LENGTH_SHORT).show()
                        activity.finish()
                    } else if (activity != null) {
                        Toast.makeText(activity, "Неверный СМС код", Toast.LENGTH_SHORT).show()
                        view.isEnabled = true
                    }
                } catch (e: JSONException) {
                    if (activity != null) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
                        view.isEnabled = true
                    }
                    e.printStackTrace()
                } catch (ignored: NullPointerException) {
                }

            } else if (activity != null) {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
                view.isEnabled = true
            }
        }
        httpPostRequest.execute("http://$SERVER/api/users/checkcode")
    }

    fun changePassword(activity: Activity?, user: User, newPassword: String) {
        val result_keys = arrayOf("status")
        val header_keys = arrayOf("Authorization")
        val body_keys = arrayOf("user_id", "user_new_password", "user_old_password")
        val header_data = arrayOf(user.token)
        val body_data = arrayOf(user.id.toString(), newPassword, user.password)

        val httpPostRequest = HttpPostRequest(activity, header_keys, body_keys, header_data, body_data
        ) { output, resultJSON, code ->
            if (output!! && resultJSON != null) {
                val response: JSONObject
                try {
                    response = JSONObject(resultJSON)
                    if (response.getBoolean(result_keys[0]) && activity != null) {
                        Toast.makeText(activity, "Пароль изменен", Toast.LENGTH_SHORT).show()
                        val passwordActivity = activity as ChangePasswordActivity?
                        passwordActivity!!.saveNewPassword(newPassword)

                    } else if (activity != null) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    if (code != 200 && activity != null) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                } catch (ignored: NullPointerException) {
                }

            } else if (activity != null) {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show()
            }
            //            if(activity != null){
            //                Button button = activity.findViewById(R.id.button3);
            //                button.setEnabled(true);
            //            }
        }
        httpPostRequest.execute("http://$SERVER/api/users/edit/password")
    }

}
