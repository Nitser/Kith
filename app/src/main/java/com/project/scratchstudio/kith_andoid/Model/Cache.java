package com.project.scratchstudio.kith_andoid.Model;

import java.util.ArrayList;

public class Cache {

    private static int allUsers ;
    private static int myUsers = 0;

    public static int getAllUsers() {
        return allUsers;
    }

    public static void setAllUsers(int allUsers) {
        Cache.allUsers = allUsers;
    }

    public static int getMyUsers() {
        return myUsers;
    }

    public static void setMyUsers(int myUsers) {
        Cache.myUsers = myUsers;
    }

    public static boolean hasImageCaptureBug() {

        ArrayList<String> devices = new ArrayList<String>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");

        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/" + android.os.Build.DEVICE);

    }

}
