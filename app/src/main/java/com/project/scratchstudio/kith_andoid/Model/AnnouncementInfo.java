package com.project.scratchstudio.kith_andoid.Model;

import java.io.Serializable;

public class AnnouncementInfo implements Serializable {
    public int id;
    public int enabled;
    public String title;

    public String startDate;
    public String endDate;

    public String participants;
    public String needParticipants;
    public String url;
    public String description;

    public int organizerId;
    public String organizerName;
    public String organizerPhoto;

}
