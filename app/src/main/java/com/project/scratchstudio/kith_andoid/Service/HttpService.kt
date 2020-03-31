package com.project.scratchstudio.kith_andoid.Service

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

}
