package com.musman.cabsDriver.Common;

import android.location.Location;

import com.musman.cabsDriver.Model.User;
import com.musman.cabsDriver.Remote.FCMClient;
import com.musman.cabsDriver.Remote.IFCMService;
import com.musman.cabsDriver.Remote.IGoogleAPI;
import com.musman.cabsDriver.Remote.RetrofitClient;

/**
 * Created by Musman on 2018-01-03.
 */

public class Common {
    //database fields
    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl = "Tokens";
    private static final String baseURL = "https://maps.googleapis.com";
    private static final String fcmURL = "https://fcm.googleapis.com";

    public static Location mLastLocation= null;

    public static User currentUser;

    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
