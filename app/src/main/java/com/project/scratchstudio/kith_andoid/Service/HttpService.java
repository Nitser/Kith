package com.project.scratchstudio.kith_andoid.Service;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.ChangePasswordActivity;
import com.project.scratchstudio.kith_andoid.Activities.CheckInActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Activities.MainActivity;
import com.project.scratchstudio.kith_andoid.Activities.SignInActivity;
import com.project.scratchstudio.kith_andoid.Activities.SmsActivity;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Fragments.AnnouncementFragment;
import com.project.scratchstudio.kith_andoid.Fragments.NewAnnouncementFragment;
import com.project.scratchstudio.kith_andoid.Fragments.SearchFragment;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.Model.Cache;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.project.scratchstudio.kith_andoid.SetInternalData.SetCountData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpService {

    private static final String SERVER = "dev.kith.ml";

    public void count(Activity activity, CustomFontTextView textView) {
        String key = "users_count";

        HttpGetRequest httpGetRequest = new HttpGetRequest(activity, (output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                JSONObject response;
                try {
                    response = new JSONObject(resultJSON);
                    textView.setText(response.get(key).toString());
                    Cache.setAllUsers(response.getInt(key));
                    InternalStorageService getCount = new InternalStorageService(activity);
                    getCount.setiSetInternalData(new SetCountData());
                    getCount.execute();
                } catch (JSONException e) {
                    if(code != 200)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (NullPointerException ignored){}
            }
            else {
                textView.setText(Cache.getAllUsers());
                if (activity != null)
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        });
        httpGetRequest.execute("http://" + SERVER + "/api/users/count");

    }

    public void singin(View view, String login, String password){
        String[] result_keys = {"status", "user_id", "access_token", "token_type"};
        String[] input_keys = {"login", "password"};
        String[] data = { login, password };
        SignInActivity activity = (SignInActivity) view.getContext();
        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, input_keys, data, (output, resultJSON, code) -> {
            if( output && resultJSON != null ){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(response.has(result_keys[1])){
                        HomeActivity.createMainUser();
                        HomeActivity.getMainUser().setId(response.getInt(result_keys[1]));
                        HomeActivity.getMainUser().setToken(response.getString(result_keys[3]) + " " + response.getString(result_keys[2]));
                        HomeActivity.getMainUser().setPassword(password);
                        Intent intent = new Intent(view.getContext(), HomeActivity.class);
                        intent.putExtra("another_user", false);
                        view.getContext().startActivity(intent);
                        ((Activity)view.getContext()).finish();
                    } else if( code == 405 ){
                        response = new JSONObject(resultJSON);
                        HomeActivity.createMainUser();
                        HomeActivity.getMainUser().setId(response.getInt(result_keys[1]));
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
                } catch (NullPointerException ignored) {}
            }
            else if(view.getContext() != null){
                Toast.makeText(view.getContext(), getErrorMessage(code), Toast.LENGTH_SHORT).show();
                view.setEnabled(true);

            }
        });
        httpPostRequest.execute("http://" + SERVER + "/api/singin");
    }

    public void singup(Activity activity, Button button, Bitmap photo, String parent_id, Map<String, CustomFontEditText> fields) throws UnsupportedEncodingException {
        String[] result_keys = {"status", "user_id", "access_token", "token_type"};
        String[] body_keys = {"user_firstname", "user_lastname", "user_middlename", "user_phone", "user_login", "user_password", "invitation_user_id", "user_position", "user_description"};
        String[] body_data  = { fields.get(body_keys[0]).getText().toString().trim(), fields.get(body_keys[1]).getText().toString().trim(), fields.get(body_keys[2]).getText().toString().trim(),
                fields.get(body_keys[3]).getText().toString().trim(), fields.get(body_keys[4]).getText().toString().trim(), fields.get(body_keys[5]).getText().toString().trim(),
                parent_id, fields.get(body_keys[7]).getText().toString().trim(),  fields.get(body_keys[8]).getText().toString().trim()};

        PhotoService photoService = new PhotoService(activity);
        String res = photoService.base64Photo(photo);

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, body_keys, body_data, res, (output, resultJSON, code) -> {
            if( output && resultJSON!= null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[0])){
                        HomeActivity.createMainUser();
                        HomeActivity.getMainUser().setId(response.getInt(result_keys[1]));
                        HomeActivity.getMainUser().setToken(response.getString(result_keys[3]) + " " + response.getString(result_keys[2]));
                        HomeActivity.getMainUser().setImage(photo);
                        HomeActivity.getMainUser().setPassword(fields.get(body_keys[5]).getText().toString().trim());
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
                } catch (NullPointerException ignored) {}
            }
            else if(activity != null) {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            }
        });
        httpPostRequest.execute("http://" + SERVER + "/api/singup");
    }

    public void getUser(Activity context){
        String[] input_keys = {"Authorization"};

        HttpGetRequest httpGetRequest = new HttpGetRequest(context, input_keys, (output, resultJSON, code) -> {
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
                        CustomFontTextView position = context.findViewById(R.id.position);
                        String userName = HomeActivity.getMainUser().getFirstName() + " " + HomeActivity.getMainUser().getLastName();
                        name.setText(userName);
                        position.setText(HomeActivity.getMainUser().getPosition());
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
                } catch (NullPointerException ignored) {}
            }
            else if(context != null)
                Toast.makeText(context, getErrorMessage(code), Toast.LENGTH_SHORT).show();
        });
        httpGetRequest.execute("http://" + SERVER + "/api/users/" + HomeActivity.getMainUser().getId());
    }

    public void referralCount(Activity activity, User user, Boolean onlyMine){
        String[] result_keys = {"referral_count"};
        String[] header_keys = {"Authorization"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_keys;
        String[] body_data;

        if(onlyMine){
            body_keys = new String[]{"user_id", "only_mine"};
            body_data = new String[]{String.valueOf(user.getId()), "1"};
        } else {
            body_keys = new String[]{"user_id"};
            body_data = new String[]{String.valueOf(user.getId())};
        }

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(!response.has("status")){
                        if(onlyMine){
                            user.setUsersCount(response.getInt(result_keys[0]));
                            if(HomeActivity.getMainUser().getId() == user.getId())
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
                } catch (NullPointerException ignored) {}
            }
            else if(activity != null)
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
        }));

        httpPostRequest.execute("http://" + SERVER + "/api/users/referral_count");

    }

    public void getReferral(Activity activity){
        String[] result_keys = {"user_referral"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_data = { String.valueOf(HomeActivity.getMainUser().getId()) };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
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
                } catch (NullPointerException ignored) {}
            } else if(activity != null) {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/referral");
    }

    public void getInvitedUsers(Activity activity, User user, boolean owner){

        String[] result_keys = {"status", "user_id", "user_firstname", "user_lastname", "photo", "user_middlename", "user_position", "user_description"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id", "page", "size"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_data = { String.valueOf(user.getId()) , "0", "50" };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONArray response = new JSONArray(resultJSON);
                    List<User> list = new ArrayList<>();
                    if(response.length()!= 0 && !response.getJSONObject(0).has(result_keys[0])){
                        for(int i=0; i<response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            User invitedUser = new User();
                            invitedUser.setId(obj.getInt(result_keys[1]));
                            invitedUser.setFirstName(obj.getString(result_keys[2]));
                            invitedUser.setLastName(obj.getString(result_keys[3]));
                            invitedUser.setMiddleName(obj.getString(result_keys[5]));
                            invitedUser.setUrl(obj.getString(result_keys[4]));
                            invitedUser.setPosition(obj.getString(result_keys[6]));
                            invitedUser.setDescription(obj.getString(result_keys[7]));

                            list.add(invitedUser);
                        }
                    }

                    try {
                        if(user.getId() == HomeActivity.getMainUser().getId()){
                            HomeActivity homeActivity = (HomeActivity)activity;
                            homeActivity.setInvitedUsers(list);
                        }

                        LinearLayout parent = activity.findViewById(R.id.paper);
                        TreeService treeService = new TreeService();
                        treeService.makeTree(parent, list, owner);
                    } catch (NullPointerException ignored){}

                } catch (JSONException e) {
                    if(activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER +"/api/users/referral_users");
    }

    public void checkReferral(CheckInActivity activity, String referralCode, View view){
        String[] result_keys = {"user_id", "status"};
        String[] body_keys = {"user_referral"};
        String[] body_data = { referralCode };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, body_keys, body_data, ((output, resultJSON, code) -> {
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
                } catch (NullPointerException ignored) {}
            } else if(activity != null){
                activity.checkFields();
                CustomFontEditText referralEditText = activity.findViewById(R.id.editText9);
                referralEditText.setBackgroundResource(R.drawable.entri_field_error_check_in);
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/check_referral");
    }

    public void sendSms(Activity activity, int id){
        String[] result_keys = {"status"};
        String[] body_keys = {"user_id"};
        String[] body_data = { String.valueOf(id) };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, body_keys, body_data, ((output, resultJSON, code) -> {
            if( output && resultJSON != null){
                try {
                    JSONObject response = new JSONObject(resultJSON);
                    if(!response.getBoolean(result_keys[0])) {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (NullPointerException ignored) {}
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/confirm");
    }

    public void checkSms(Activity activity, View view, int id, String smsCode){
        String[] result_keys = {"status"};
        String[] body_keys = {"user_id", "sms_code"};
        String[] body_data = { String.valueOf(id), smsCode };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, body_keys, body_data, ((output, resultJSON, code) -> {
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
                } catch (NullPointerException ignored) {}
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/checkcode");
    }

    public void refreshInvitedUsers(Activity activity, int id, boolean owner, SwipeRefreshLayout refreshLayout){
        String[] result_keys = {"status", "user_id", "user_firstname", "user_lastname", "photo", "user_middlename", "user_position", "user_phone", "user_description"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id", "page", "size"};
        String[] header_data = { HomeActivity.getMainUser().getToken() };
        String[] body_data = { String.valueOf(id) , "0", "50" };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
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
                            user.setMiddleName(obj.getString(result_keys[5]));
                            user.setPosition(obj.getString(result_keys[6]));
                            user.setPhone(obj.getString(result_keys[7]));
                            user.setDescription(obj.getString(result_keys[8]));

                            list.add(user);
                        }
                        HomeActivity homeActivity = (HomeActivity)activity;
                        homeActivity.setInvitedUsers(list);

                    }
                    LinearLayout parent = activity.findViewById(R.id.paper);
                    try{
                        parent.removeViews(5, parent.getChildCount()-5);
                    } catch (Exception e){ }

                    TreeService treeService = new TreeService();
                    treeService.makeTree(parent, list, owner);

                    refreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (NullPointerException ignored) {}
            } else if(activity != null) {
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/referral_users");
    }

    public void refreshUser(Activity activity, User user, Bitmap photo) throws UnsupportedEncodingException {
        PhotoService photoService = new PhotoService(activity);
        String res = photoService.base64Photo(photo);

        String[] result_keys = {"status"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id", "user_firstname", "user_lastname", "user_middlename", "user_phone", "user_email", "user_position", "user_description", "user_photo"};
        String[] header_data = { user.getToken() };
        String[] body_data = { String.valueOf(user.getId()) , user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getPhone(), user.getEmail(), user.getPosition(), user.getDescription(), res};

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                JSONObject response;
                try {
                    response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[0])){
                        Toast.makeText(activity, "Данные обновлены", Toast.LENGTH_SHORT).show();
                        User mainUser = HomeActivity.getMainUser();
                        mainUser.setFirstName(user.getFirstName());
                        mainUser.setLastName(user.getLastName());
                        mainUser.setMiddleName(user.getMiddleName());
                        mainUser.setPhone(user.getPhone());
                        mainUser.setEmail(user.getEmail());
                        mainUser.setPosition(user.getPosition());
                        mainUser.setDescription(user.getDescription());
                        if(user.getUrl() != null && !user.getUrl().equals("null"))
                            mainUser.setUrl(user.getUrl());
                    } else {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    if(code != 200 && activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (NullPointerException ignored){}
            }
            else if (activity != null)
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            if(activity != null){
                Button button = activity.findViewById(R.id.button3);
                button.setEnabled(true);
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/edit");
    }

    public void searchUsers(Activity activity, User user, SearchFragment searchFragment) {
        String[] result_keys = {"status", "user_id", "user_firstname", "user_lastname", "user_photo", "user_middlename", "user_position", "user_description"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id", "search", "page", "size"};
        String[] header_data = { user.getToken() };
        String[] body_data = { String.valueOf(user.getId()) ,"",  "0", "50" };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                try {
                    JSONObject json = new JSONObject(resultJSON);
                    JSONArray response = json.getJSONArray("users");
                    List<SearchInfo> list = new ArrayList<>();
                    if(response.length()!= 0 && !response.getJSONObject(0).has(result_keys[0])){
                        for(int i=0; i<response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            SearchInfo newInfo = new SearchInfo();
                            newInfo.id = obj.getInt(result_keys[1]);
                            newInfo.firstName = obj.getString(result_keys[2]);
                            newInfo.lastName = obj.getString(result_keys[3]);
                            newInfo.photo = obj.getString(result_keys[4]);
                            newInfo.position = obj.getString(result_keys[6]);

                            list.add(newInfo);
//                            Log.i("LIST", newInfo.firstName);
                        }
                        SearchFragment.setListPersons(list);
                        searchFragment.setAdapter();

                    }
                } catch (JSONException e) {
                    if(activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/search");
    }

    public void changePassword(Activity activity, User user, String newPassword){
        String[] result_keys = {"status"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id", "user_new_password", "user_old_password"};
        String[] header_data = { user.getToken() };
        String[] body_data = { String.valueOf(user.getId()) , newPassword, user.getPassword()};

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                JSONObject response;
                try {
                    response = new JSONObject(resultJSON);
                    if(response.getBoolean(result_keys[0])){
                        Toast.makeText(activity, "Пароль изменен", Toast.LENGTH_SHORT).show();
                        ChangePasswordActivity passwordActivity = (ChangePasswordActivity) activity;
                        passwordActivity.saveNewPassword(newPassword);

                    } else {
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    if(code != 200 && activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (NullPointerException ignored){}
            }
            else if (activity != null)
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
//            if(activity != null){
//                Button button = activity.findViewById(R.id.button3);
//                button.setEnabled(true);
//            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/users/edit/password");
    }

    public void getAnnouncements(Activity activity, User user, AnnouncementFragment fragment){
        String[] result_keys = {"status", "user_id", "user_firstname", "user_lastname", "user_photo", "user_middlename", "user_position", "user_description"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"user_id"};
        String[] header_data = { user.getToken() };
        String[] body_data = { String.valueOf(user.getId()) };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                try {
                    JSONObject json = new JSONObject(resultJSON);
                    JSONArray response = json.getJSONArray("boards");
                    List<AnnouncementInfo> list = new ArrayList<>();
                    if(response.length()!= 0 && json.getBoolean(result_keys[0]) ){
                        for(int i=0; i<response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            AnnouncementInfo newInfo = new AnnouncementInfo();

                            newInfo.id = obj.getInt("board_id");
                            newInfo.title = obj.getString("board_title");
                            newInfo.enabled = obj.getInt("board_enabled");
                            newInfo.organizerName = obj.getString("owner_firstname") + " " + obj.getString("owner_lastname");
                            newInfo.url = obj.getString("user_photo");
                            newInfo.description = obj.getString("board_description");
                            newInfo.startDate = obj.getString("board_date_created");
                            newInfo.endDate = obj.getString("board_date_end");
                            newInfo.needParticipants = obj.getString("board_needs_subscriptions");
                            newInfo.participants = obj.getString("board_current_subscriptions");
                            newInfo.organizerId = obj.getInt("board_user_id");

                            list.add(newInfo);
                            Log.i("New AnInf", newInfo.title);
                        }
                        Log.i("GET ANN", "done");
                        AnnouncementFragment.setListAnn(list);
                        fragment.setAdapter();

                    }
                } catch (JSONException e) {
                    if(activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/boards/list");
    }

    public void joinAnnouncement(Activity activity, User user, int boardId){
        String[] result_keys = {"status"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"subscription_user_id", "subscription_board_id"};
        String[] header_data = { user.getToken() };
        String[] body_data = { String.valueOf(user.getId()), String.valueOf(boardId) };

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                try {
                    JSONObject json = new JSONObject(resultJSON);
                    if( json.getBoolean(result_keys[0]) ){
                       Toast.makeText(activity, "Вы подписаны", Toast.LENGTH_SHORT).show();
                       CustomFontTextView have = activity.findViewById(R.id.have);
                       String result = have.getText().toString();
                       int res = Integer.parseInt(result) + 1;
                       have.setText(String.valueOf(res));
                    }
                } catch (JSONException e) {
                    if(activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/boards/subscribe");
    }

    public void addAnnouncement(Activity activity, User user, NewAnnouncementFragment nFragment, AnnouncementInfo info){
        String[] result_keys = {"status"};
        String[] header_keys = {"Authorization"};
        String[] body_keys = {"board_user_id", "board_title", "board_description", "board_photo", "board_subscriptions", "board_date_end", "board_enabled", "board_needs_subscriptions"};
        String[] header_data = { user.getToken() };
        String[] body_data = { String.valueOf(user.getId()), info.title, info.description, info.url, String.valueOf(0), info.endDate, String.valueOf(1), info.needParticipants};

        HttpPostRequest httpPostRequest = new HttpPostRequest(activity, header_keys, body_keys, header_data, body_data, ((output, resultJSON, code) -> {
            if(output && resultJSON!=null){
                try {
                    JSONObject json = new JSONObject(resultJSON);
                    if( json.getBoolean(result_keys[0]) ){
                        Toast.makeText(activity, "Объявление создано", Toast.LENGTH_SHORT).show();
                        nFragment.close();
                    }
                } catch (JSONException e) {
                    if(activity != null)
                        Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if(activity != null){
                Toast.makeText(activity, getErrorMessage(code), Toast.LENGTH_SHORT).show();
            }
        }));
        httpPostRequest.execute("http://" + SERVER + "/api/boards/create");
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