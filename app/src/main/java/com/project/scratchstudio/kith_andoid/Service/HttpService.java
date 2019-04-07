package com.project.scratchstudio.kith_andoid.Service;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.CheckInActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Activities.MainActivity;
import com.project.scratchstudio.kith_andoid.Activities.SmsActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.Cache;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpService {

    public void count(Activity activity, CustomFontTextView textView) {
        String key = "users_count";

        HttpGetRequest httpGetRequest = new HttpGetRequest((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                JSONObject response;
                try {
                    response = new JSONObject(resultJSON);
                    textView.setText(response.get(key).toString());
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("all_users", response.getInt(key));
                    editor.commit();
                } catch (JSONException e) {
                    if(code != 200)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else {
                textView.setText(Cache.getAllUsers());
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        });
        httpGetRequest.execute("http://kith.ml/api/users/count");

    }

    public void singin(View view, String login, String password){
        String[] result_keys = {"status", "user_id", "user_token"};
        String[] input_keys = {"login", "password"};
        String[] data = { login, password };

        HttpPostRequest httpPostRequest = new HttpPostRequest(input_keys, data, (output, resultJSON, code) -> {
            if( output && resultJSON != null ){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[0])){
                        HomeActivity.createMainUser();
                        HomeActivity.getMainUser().setId(response.getInt(result_keys[1]));
                        HomeActivity.getMainUser().setToken(response.getString(result_keys[2]));

                        Intent intent = new Intent(view.getContext(), HomeActivity.class);
                        intent.putExtra("another_user", false);
                        view.getContext().startActivity(intent);
                        ((Activity)view.getContext()).finish();
                    } else if( code == 405 ){
                        response = new JSONObject(resultJSON);
                        HomeActivity.createMainUser();
                        HomeActivity.getMainUser().setId(response.getInt(result_keys[1]));
//                        Log.i("We set id: ", response.getString(result_keys[1]));
                        Intent intent = new Intent(view.getContext(), SmsActivity.class);
                        view.getContext().startActivity(intent);
                    } else{
                        Toast.makeText(view.getContext(), getErrorMessage(code), Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                } catch (JSONException e) {
                    Toast.makeText(view.getContext(), getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(view.getContext(), getErrorMessage(code), Toast.LENGTH_SHORT).show();
                view.setEnabled(true);

            }
        });
        httpPostRequest.execute("http://kith.ml/api/singin");
    }

    public void singup(Activity activity, Button button, Bitmap photo, String parent_id, Map<String, CustomFontEditText> fields) throws UnsupportedEncodingException {
        String[] result_keys = {"status", "user_id", "user_token"};
        String[] body_keys = {"user_firstname", "user_lastname", "user_middlename", "user_phone", "user_login", "user_password", "invitation_user_id", "user_position", "user_description"};
        String[] body_data  = { fields.get(body_keys[0]).getText().toString().trim(), fields.get(body_keys[1]).getText().toString().trim(), fields.get(body_keys[2]).getText().toString().trim(),
                fields.get(body_keys[3]).getText().toString().trim(), fields.get(body_keys[4]).getText().toString().trim(), fields.get(body_keys[5]).getText().toString().trim(),
                parent_id, fields.get(body_keys[7]).getText().toString().trim(),  fields.get(body_keys[8]).getText().toString().trim()};

        PhotoService photoService = new PhotoService(activity);
        String res = photoService.base64Photo(photo);

        HttpPostRequest httpPostRequest = new HttpPostRequest(body_keys, body_data, res, (output, resultJSON, code) -> {
            if( output && resultJSON!= null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[0])){
                        HomeActivity.createMainUser();
                        HomeActivity.getMainUser().setId(response.getInt(result_keys[1]));
                        HomeActivity.getMainUser().setToken(response.getString(result_keys[2]));
                        HomeActivity.getMainUser().setImage(photo);
                        Intent intent = new Intent(activity, SmsActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                        button.setEnabled(true);
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            }
        });
        httpPostRequest.execute("http://kith.ml/api/singup");
    }

    public void getUser(Activity context){
        String[] input_keys = {"user-token"};

        HttpGetRequest httpGetRequest = new HttpGetRequest(input_keys, (output, resultJSON, code) -> {
            if(output && resultJSON != null){
                JSONObject response;
                try {
                    response = new JSONObject(resultJSON);
                    if(response.getBoolean("status")){
                        JSONObject user = response.getJSONObject("user");
                        HomeActivity.getMainUser().setFirstName(user.getString("user_firstname"));
                        HomeActivity.getMainUser().setLastName(user.getString("user_lastname"));
                        HomeActivity.getMainUser().setMiddleName(user.getString("user_middlename"));
                        HomeActivity.getMainUser().setLogin(user.getString("user_login"));
                        HomeActivity.getMainUser().setPhone(user.getString("user_phone"));
                        HomeActivity.getMainUser().setDescription(user.getString("user_description"));
                        HomeActivity.getMainUser().setPosition(user.getString("user_position"));

                            try {
                                HomeActivity.getMainUser().setUrl(user.getString("photo"));
                            } catch (Exception e){
//                                Log.i("DECODE ERR: ", e.getMessage());
                            }

                        ImageView photo = context.findViewById(R.id.photo);
                        Picasso.with(context).load(user.getString("photo"))
                                .placeholder(R.mipmap.person)
                                .error(R.mipmap.person)
                                .transform(new PicassoCircleTransformation())
                                .into(photo);

                        CustomFontTextView name = context.findViewById(R.id.name);
                        String userName = HomeActivity.getMainUser().getFirstName() + " " + HomeActivity.getMainUser().getLastName();
                        name.setText(userName);
                    }
                    else {
                        Toast.makeText(context, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                        HomeActivity.cleanMainUser();
                        InternalStorageService internalStorageService = new InternalStorageService(context);
                        internalStorageService.setiSetInternalData(new ClearUserIdAndToken());
                        internalStorageService.execute();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        context.finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
//
                }
            }
            else
                Toast.makeText(context, getErrorMessage(code), Toast.LENGTH_SHORT).show();
        });
        httpGetRequest.execute("http://kith.ml/api/users/" + HomeActivity.getMainUser().getId());
    }

    public void referralCount(Activity activity, Boolean onlyMine, int id){
        String[] result_keys = {"referral_count"};
        String[] header_keys = {"user-token"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_keys;
        String[] body_data;

        if(onlyMine){
            body_keys = new String[]{"user_id", "only_mine"};
            body_data = new String[]{String.valueOf(id), "1"};
        } else {
            body_keys = new String[]{"user_id"};
            body_data = new String[]{String.valueOf(id)};
        }

        HttpPostRequest httpPostRequest = new HttpPostRequest(header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(!response.has("status")){
                        if(onlyMine){
                            Cache.setMyUsers(response.getInt(result_keys[0]));
                        } else {
                            CustomFontTextView textView = activity.findViewById(R.id.yourPeople);
                            textView.setText(activity.getResources().getQuantityString(R.plurals.people, response.getInt(result_keys[0]), response.getInt(result_keys[0])));
                        }
                    } else {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
        }));

        httpPostRequest.execute("http://kith.ml/api/users/referral_count");

    }

    public void getReferral(Activity activity){
        String[] result_keys = {"user_referral"};
        String[] header_keys = {"user-token"};
        String[] body_keys = {"user_id"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_data = { String.valueOf(HomeActivity.getMainUser().getId()) };

        HttpPostRequest httpPostRequest = new HttpPostRequest(header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(!response.has("status")){
                        CustomFontTextView textView = activity.findViewById(R.id.customFontTextView6);
                        textView.setText(response.getString(result_keys[0]));
                    } else {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://kith.ml/api/users/referral");
    }

    public void getInvitedUsers(Activity activity, int id, boolean owner){
        String[] result_keys = {"status", "user_id", "user_firstname", "user_lastname", "photo"};
        String[] header_keys = {"user-token"};
        String[] body_keys = {"user_id", "page", "size"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_data = { String.valueOf(id) , "0", "50" };

        HttpPostRequest httpPostRequest = new HttpPostRequest(header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONArray response = new JSONArray(resultJSON);
                    List<User> list = new ArrayList<>();
                    if(response.length()!= 0 && !response.getJSONObject(0).has(result_keys[0])){
                        for(int i=0; i<response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            User user = new User();
                            user.setId(obj.getInt(result_keys[1]));
                            user.setFirstName(obj.getString(result_keys[2]));
                            user.setLastName(obj.getString(result_keys[3]));
                            user.setUrl(obj.getString(result_keys[4]));

                            list.add(user);
                        }
                        HomeActivity homeActivity = (HomeActivity)activity;
                        homeActivity.setMainUser(list);

                    }
                    LinearLayout parent = activity.findViewById(R.id.paper);
                    TreeService treeService = new TreeService();
                    if (owner)
                        treeService.makeOwnTree(parent);
                    else
                        treeService.makeAlienTree(parent);
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://kith.ml/api/users/referral_users");
    }

    public void checkReferral(CheckInActivity activity, String referralCode, View view){
        String[] result_keys = {"user_id", "status"};
        String[] body_keys = {"user_referral"};
        String[] body_data = { referralCode };

        HttpPostRequest httpPostRequest = new HttpPostRequest(body_keys, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[1])){
                        activity.checkFields();
                        String user_id = response.getString(result_keys[0]);
                        activity.checkIn(user_id);

                    } else {
                        activity.checkFields();
                        CustomFontEditText referralEditText = activity.findViewById(R.id.editText9);
                        referralEditText.setBackgroundResource(R.drawable.entri_field_error_check_in);
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                } catch (JSONException e) {
                    activity.checkFields();
                    CustomFontEditText referralEditText = activity.findViewById(R.id.editText9);
                    referralEditText.setBackgroundResource(R.drawable.entri_field_error_check_in);
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                activity.checkFields();
                CustomFontEditText referralEditText = activity.findViewById(R.id.editText9);
                referralEditText.setBackgroundResource(R.drawable.entri_field_error_check_in);
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }));
        httpPostRequest.execute("http://kith.ml/api/check_referral");
    }

    public void sendSms(Activity activity, int id){
        String[] result_keys = {"status"};
        String[] body_keys = {"user_id"};
        String[] body_data = { String.valueOf(id) };

        HttpPostRequest httpPostRequest = new HttpPostRequest(body_keys, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
//                    Log.i("Data from sms: ", response.toString());
                    if(!response.getBoolean(result_keys[0])) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://kith.ml/api/users/confirm");
    }

    public void checkSms(Activity activity, View view, int id, String smsCode){
        String[] result_keys = {"status"};
        String[] body_keys = {"user_id", "sms_code"};
        String[] body_data = { String.valueOf(id), smsCode };

        HttpPostRequest httpPostRequest = new HttpPostRequest(body_keys, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[0])) {
                        Intent intent = new Intent(activity, HomeActivity.class);
                        intent.putExtra("another_user", false);
                        activity.startActivity(intent);
                        Toast.makeText(activity, "СМС код подтвержден", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Неверный СМС код", Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }));
        httpPostRequest.execute("http://kith.ml/api/users/checkcode");
    }

    public void refreshInvitedUsers(Activity activity, int id, boolean owner, SwipeRefreshLayout refreshLayout){
        String[] result_keys = {"status", "user_id", "user_firstname", "user_lastname"};
        String[] header_keys = {"user-token"};
        String[] body_keys = {"user_id", "page", "size"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_data = { String.valueOf(id) , "0", "50" };

        HttpPostRequest httpPostRequest = new HttpPostRequest(header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONArray response = new JSONArray(resultJSON);
                    List<User> list = new ArrayList<>();
                    if(response.length()!= 0 && !response.getJSONObject(0).has(result_keys[0])){
                        for(int i=0; i<response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            User user = new User();
                            user.setId(obj.getInt(result_keys[1]));
                            user.setFirstName(obj.getString(result_keys[2]));
                            user.setLastName(obj.getString(result_keys[3]));

                            BitmapDrawable bitmapDrawable = ((BitmapDrawable) activity.getResources().getDrawable(R.mipmap.person));
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            user.setImage(bitmap);

                            list.add(user);
                        }
                        HomeActivity homeActivity = (HomeActivity)activity;
                        homeActivity.setMainUser(list);

                    }
                    LinearLayout parent = activity.findViewById(R.id.paper);
                    try{
                        parent.removeViews(5, parent.getChildCount()-5);
                    } catch (Exception e){ }

                    TreeService treeService = new TreeService();
                    if (owner)
                        treeService.makeOwnTree(parent);
                    else
                        treeService.makeAlienTree(parent);
                    refreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://kith.ml/api/users/referral_users");
    }

    private String getErrorMessage(int code){
        switch (code){
            case 400: return "Неверный формат данных";
            case 401: return "Неверный логин или пароль";
            case 402: return "Ошибка сервера";
            case 403: return "Неверный реферальный код";
            case 404: return "Пользователь не существует";
            case 405: return "Пользователь не подтвержден";
            case 406: return "Логин уже занят";
            default:
//                Log.i("RESPONSE ERROR", String.valueOf(code));
                return "Ошибка сервера";
        }
    }
}
