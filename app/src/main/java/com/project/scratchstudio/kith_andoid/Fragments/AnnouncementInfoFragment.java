package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;

public class AnnouncementInfoFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    int boardListId;
    private AnnouncementInfo info;
    private boolean is_join = false;

    public void setIsJoin(boolean bol){
        CheckBox favorite = getActivity().findViewById(R.id.heart);
        favorite.setChecked(bol);
        favorite.setEnabled(true);
        is_join = bol;
    }

    public static AnnouncementInfoFragment newInstance(Bundle bundle) {
        AnnouncementInfoFragment fragment = new AnnouncementInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        bundle.putBoolean("is_edit", false);
        if(bundle != null) {
            boardListId = bundle.getInt("board_list_id");
            info = AnnouncementFragment.getListAnn().get(boardListId);
        }
        return inflater.inflate(R.layout.fragment_announcement_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();

        CustomFontTextView title = view.findViewById(R.id.title);
        CustomFontTextView date = view.findViewById(R.id.date);
        CustomFontTextView description = view.findViewById(R.id.description);
        CustomFontTextView owner = view.findViewById(R.id.owner);
        CustomFontTextView creationDate = view.findViewById(R.id.creationDate);
        ImageView photo = view.findViewById(R.id.photo);

        title.setText(info.title);
        if(info.endDate.equals("null"))
            info.endDate = "Неограниченно";

        try {
            DateFormat inputFormat;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date newDateFormat;
                Date newCreationDateFormat = null;
                newDateFormat = inputFormat.parse(info.endDate.replaceAll("\\s.*$", ""));
                if(info.startDate.equals("null"))
                    creationDate.setVisibility(View.INVISIBLE);
                else
                    newCreationDateFormat = inputFormat.parse(info.startDate.replaceAll("\\s.*$", ""));
                String outputDateStr = outputFormat.format(newDateFormat);
                date.setText(outputDateStr);
                outputDateStr = "Создано: " + outputFormat.format(newCreationDateFormat);
                creationDate.setText(outputDateStr);
            } else {
                date.setText(info.endDate.replaceAll("\\s.*$", ""));
                String errDate = "Создано: " + info.startDate.replaceAll("\\s.*$", "");
                creationDate.setText(errDate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String ownerName = "Владелец: ";
        if(info.organizerLastName!= null && !info.organizerLastName.equals("null"))
            ownerName += (info.organizerLastName + " ");
        if(!info.organizerName.equals("null"))
            ownerName += info.organizerName;
        else
            owner.setVisibility(View.INVISIBLE);
        owner.setText(ownerName);

        if(info.needParticipants.equals("null") || info.needParticipants.equals(""))
            info.needParticipants = "-";
        description.setText(info.description);
        if(info.url != null && !info.url.equals("null") && !info.url.equals("")) {
            Picasso.with(getActivity()).load(info.url.replaceAll("@[0-9]*", ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if(info.organizerId == HomeActivity.getMainUser().getId()) {
            ImageButton button = getActivity().findViewById(R.id.edit);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this::onClickEdit);
        }

        if(info.subscriptionOnBoard == 1){
            setIsJoin(true);
        }
    }

    private void setButtonsListener(){
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        CheckBox favorite = getActivity().findViewById(R.id.heart);
        favorite.setOnClickListener(this::onClickJoin);
        Button comments = getActivity().findViewById(R.id.comments);
        comments.setOnClickListener(this::onClickComments);
    }

    public void onClickEdit(View view){
        HomeActivity homeActivity = (HomeActivity) getActivity();
        bundle.putBoolean("is_edit", true);
        bundle.putSerializable("board_list_id", boardListId);
        homeActivity.loadFragment(NewAnnouncementFragment.newInstance(bundle));
    }

    public void onClickBack(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
    }

    public boolean onBackPressed() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
        return true;
    }

    public void onClickComments(View view){
        bundle.putInt("board_id", info.id);
        bundle.putString("board_title", info.title);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(DialogFragment.newInstance(bundle));
    }

    public void onClickJoin(View view){
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        CheckBox favorite = getActivity().findViewById(R.id.heart);
        favorite.setEnabled(false);
        if(isNetworkConnected()) {
            HttpService httpService = new HttpService();
            if (!is_join)
                httpService.joinAnnouncement(getActivity(), HomeActivity.getMainUser(), info.id, this);
            else
                httpService.unsubscribeAnnouncement(getActivity(), HomeActivity.getMainUser(), info.id, this);
        } else Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

}
