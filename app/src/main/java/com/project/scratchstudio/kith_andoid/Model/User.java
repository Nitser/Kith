package com.project.scratchstudio.kith_andoid.Model;

import android.graphics.Bitmap;

public class User {

    private static String firstName;
    private static String lastName;
    private static Bitmap image;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
