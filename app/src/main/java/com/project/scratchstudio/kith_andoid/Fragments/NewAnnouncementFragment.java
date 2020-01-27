package com.project.scratchstudio.kith_andoid.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.ImageFilePath;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import br.com.sapereaude.maskedEditText.MaskedEditText;

import static android.app.Activity.RESULT_OK;

public class NewAnnouncementFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private Bitmap photo;
    private boolean correctDate = false;

    private AnnouncementInfo board;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static NewAnnouncementFragment newInstance(Bundle bundle) {
        NewAnnouncementFragment fragment = new NewAnnouncementFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        return inflater.inflate(R.layout.fragment_new_announcement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        verifyStoragePermissions(getActivity());
        setButtonsListener();

        MaskedEditText endDate = getActivity().findViewById(R.id.date_text);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/intro_regular.ttf");
        endDate.setTypeface(face);
        endDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Calendar cal = Calendar.getInstance();
                String clean = s.toString().replaceAll("[^\\d.]|-.", "");

                if (clean.length() == 8){

                    int day  = Integer.parseInt(clean.substring(6,8));
                    int mon  = Integer.parseInt(clean.substring(4,6));
                    int year = Integer.parseInt(clean.substring(0,4));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",year, mon, day);
                    clean = String.format("%s-%s-%s", clean.substring(0, 4),
                        clean.substring(4, 6),
                        clean.substring(6, 8));
                    Log.i("FIRST DATE", String.valueOf(s));
                    Log.i("SECOND DATE", clean);
                    if(String.valueOf(s).equals(clean))
                        correctDate = true;
                    else
                        correctDate = false;
                } else correctDate = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")){
            int id =  bundle.getInt("board_list_id");
            board = AnnouncementFragment.getListAnn().get(id);
            fillFields();
        }
    }

    private void fillFields(){
        CustomFontEditText title = getActivity().findViewById(R.id.title_text);
        CustomFontEditText description = getActivity().findViewById(R.id.description);
        MaskedEditText endDate = getActivity().findViewById(R.id.date_text);
        CustomFontEditText need = getActivity().findViewById(R.id.need_text);
        ImageView photo = getActivity().findViewById(R.id.photo);

        title.setText(board.title);
        description.setText(board.description);
        endDate.setText(board.endDate);
        need.setText(board.needParticipants);
        if(board.url != null && !board.url.equals("null") && !board.url.equals("")) {
            Picasso.with(getActivity()).load(board.url.replaceAll("@[0-9]*", ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setButtonsListener(){
        CustomFontTextView cancel = getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(this::onClickCancel);
        Button load = getActivity().findViewById(R.id.loadPhoto);
        load.setOnClickListener(this::onClickLoadPhoto);
        CustomFontTextView done = getActivity().findViewById(R.id.done);
        done.setOnClickListener(this::onClickDone);
    }

    public void onClickCancel(View view){
        if(bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")){
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(AnnouncementInfoFragment.newInstance(bundle));
        } else {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
        }
    }

    public boolean onBackPressed() {
        if(bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")){
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(AnnouncementInfoFragment.newInstance(bundle));
        } else {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
        }
        return true;
    }

    public void onClickDone(View view){
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        CustomFontTextView done = getActivity().findViewById(R.id.done);
        done.setEnabled(false);

        CustomFontEditText title = getActivity().findViewById(R.id.title_text);
        CustomFontEditText description = getActivity().findViewById(R.id.description);
        if(title.getText() == null || title.getText().toString().equals("") || description.getText() == null || description.getText().toString().equals("") ){
            Toast.makeText(getContext(),"Не заполнены все поля обьявления", Toast.LENGTH_SHORT).show();
            done.setEnabled(true);
        } else{
            if(correctDate) {
                if (isNetworkConnected()) {
                    AnnouncementInfo info = createAnnouncementInfo();
                    HttpService httpService = new HttpService();

                    if(board == null)
                        httpService.addAnnouncement(getActivity(), HomeActivity.getMainUser(), this, info, photo);
                    else
                        httpService.editAnnouncement(getActivity(), HomeActivity.getMainUser(), this, board, info, photo );

                } else {
                    done.setEnabled(true);
                    Toast.makeText(getContext(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Неправильный формат даты", Toast.LENGTH_SHORT).show();
                done.setEnabled(true);
            }
        }
    }

    public void close(){
        CustomFontTextView done = getActivity().findViewById(R.id.done);
        done.setEnabled(true);
        if(bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")){
            editBoard();
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(AnnouncementInfoFragment.newInstance(bundle));
        } else {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
        }
    }

    private void editBoard(){
        CustomFontEditText title = getActivity().findViewById(R.id.title_text);
        CustomFontEditText description = getActivity().findViewById(R.id.description);
        MaskedEditText endDate = getActivity().findViewById(R.id.date_text);
        CustomFontEditText need = getActivity().findViewById(R.id.need_text);

        board.title = title.getText().toString();
        board.description = description.getText().toString();
        if(endDate.getText() != null)
            board.endDate = endDate.getText().toString();
        try {
            if(need.getText().equals(""))
                board.needParticipants = "";
            else
                board.needParticipants = need.getText().toString();
        } catch (NullPointerException ex){
            board.needParticipants = "";
        }
    }

    private AnnouncementInfo createAnnouncementInfo() {
        CustomFontEditText title = getActivity().findViewById(R.id.title_text);
        MaskedEditText endDate = getActivity().findViewById(R.id.date_text);
        CustomFontEditText need = getActivity().findViewById(R.id.need_text);
        CustomFontEditText description = getActivity().findViewById(R.id.description);

        AnnouncementInfo info = new AnnouncementInfo();
        info.title = title.getText().toString();

        if(endDate.getText() == null)
            info.endDate = null;
        else
            info.endDate = endDate.getText().toString() + " 12:00:00";

        if(need.getText() == null)
            info.needParticipants = "-";
        else
            info.needParticipants = need.getText().toString();

        info.description = description.getText().toString();
        info.organizerId = HomeActivity.getMainUser().getId();

        return info;
    }

    public void onClickLoadPhoto(View view){
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
        else
            startGallery(view);
    }

    private void startGallery(View view){
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return;

        view.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //super method removed
        Button load = getActivity().findViewById(R.id.loadPhoto);
        if (requestCode == 0 && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            ImageView image = getActivity().findViewById(R.id.photo);
            Uri imageUri = intent.getData();
            String path = ImageFilePath.getPath(getContext(), intent.getData());
            InputStream imageStream;
            PhotoService photoService = new PhotoService(getActivity());
            try {
                imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                photo = BitmapFactory.decodeStream(imageStream);
                photo = photoService.changePhoto(photo, imageUri);
                photo = photoService.resizeBitmap(photo, image.getWidth(), image.getHeight());
                photo = photoService.compressPhoto(photo, path);
                image.setImageBitmap(photo);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            load.setEnabled(true);
        } else if (requestCode == 0) {
            load.setEnabled(true);
        }
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }
}
