package com.project.scratchstudio.kith_andoid.Model;

import java.io.Serializable;

public class AnnouncementInfo implements Serializable {
    public int id;
    public String title;

    public int enabled;
    public int boardSubscription;
    public int subscriptionOnBoard;

    public String startDate;
    public String endDate;

    public String participants;
    public String needParticipants;
    public String url;
    public String description;

    public int organizerId;
    public String organizerName;
    public String organizerLastName;
    public String organizerMiddleName;
    public String organizerLogin;
    public String organizerPhoto;

}
