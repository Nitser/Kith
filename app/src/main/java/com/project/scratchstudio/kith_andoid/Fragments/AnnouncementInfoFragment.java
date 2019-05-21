package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

public class AnnouncementInfoFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private AnnouncementInfo info;

    public static AnnouncementInfoFragment newInstance(Bundle bundle) {
        AnnouncementInfoFragment fragment = new AnnouncementInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        info = (AnnouncementInfo) (bundle != null ? bundle.getSerializable("info") : null);
        return inflater.inflate(R.layout.fragment_announcement_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();

        CustomFontTextView title = view.findViewById(R.id.title);
        CustomFontTextView date = view.findViewById(R.id.date);
        CustomFontTextView need = view.findViewById(R.id.need);
        CustomFontTextView have = view.findViewById(R.id.have);
        CustomFontTextView description = view.findViewById(R.id.description);
        ImageView photo = view.findViewById(R.id.photo);

        title.setText(info.title);
        if(info.endDate.equals("null"))
            info.endDate = "Неограниченно";
        date.setText(info.endDate.replaceAll("\\s.*$", ""));
        if(info.needParticipants.equals("null"))
            info.needParticipants = "-";
        need.setText(info.needParticipants);
        have.setText(info.participants);
        description.setText(info.description);
        Log.i("URL", info.url);
        if(info.url != null && !info.url.equals("null") && info.url.equals("")) {
            Picasso.with(getActivity()).load(info.url.replaceAll("@[0-9]*", ""))
                    .into(photo);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setButtonsListener(){
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        Button join = getActivity().findViewById(R.id.join);
        join.setOnClickListener(this::onClickJoin);
    }

    public void onClickBack(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
    }

    public void onClickJoin(View view){
        HttpService httpService = new HttpService();
        httpService.joinAnnouncement(getActivity(), HomeActivity.getMainUser(), info.id);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

}
