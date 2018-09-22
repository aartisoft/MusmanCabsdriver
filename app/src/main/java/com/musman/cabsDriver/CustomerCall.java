package com.musman.cabsDriver;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.musman.cabsDriver.Common.Common;
import com.musman.cabsDriver.Model.FCMResponse;
import com.musman.cabsDriver.Model.Notification;
import com.musman.cabsDriver.Model.Sender;
import com.musman.cabsDriver.Model.Token;
import com.musman.cabsDriver.Remote.IFCMService;
import com.musman.cabsDriver.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCall extends AppCompatActivity {
    TextView txtTime,txtDistance,txtAddress;
    Button btnDecline,btnAccept;
    MediaPlayer mediaPlayer;
    IGoogleAPI mService;
    IFCMService mFCMService;
    String customerId;

    // latitiudr and longitude of user
    double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();


        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtDistance= (TextView)findViewById(R.id.txtDistance);
        txtTime = (TextView)findViewById(R.id.txtTime);
        btnAccept = (Button)findViewById(R.id.btnAccept);
        btnDecline = (Button)findViewById(R.id.btnDecline);


        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        mService = Common.getGoogleAPI();

        if(getIntent() != null)
        {
            lat = getIntent().getDoubleExtra("lat",-1.0);
            lng = getIntent().getDoubleExtra("lng",-1.0);

            //cuastomer id
            customerId= getIntent().getStringExtra("customer");
            // get direction to the user
            getDirection(lat,lng);
        }

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(customerId))
                {
                    cancelBooking(customerId);
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send user location to the new activity
                Intent intent = new Intent(CustomerCall.this,DriverTracking.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("customerId",customerId);
                startActivity(intent);
                finish();
            }
        });


    }

    private void cancelBooking(String customerId) {
        Token token = new Token(customerId);

        Notification notification = new Notification("Cancel","Driver has cancelled your request");
        Sender sender = new Sender(notification,token.getToken());

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success==1)
                        {
                            Toast.makeText(CustomerCall.this, "Request has been cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }

    private void getDirection(double lat,double lng) {


        String requestAPi= null;
        try
        {
            requestAPi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preferences=less_driving&"+
                    "origin="+ Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);
            Log.d("Direction api",requestAPi);

            mService.getPath(requestAPi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray routes = jsonObject.getJSONArray("routes");

                                // the routes info is received, get the rest of the info
                                //starting with the first element of the route
                                JSONObject object  = routes.getJSONObject(0);

                                // we need get array with name legs
                                JSONArray legs = object.getJSONArray("legs");
                                //get first element of legs array
                                JSONObject legsObject = legs.getJSONObject(0);

                                //get the distance:
                                JSONObject distance  = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                //get the distance:
                                JSONObject time  = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));
                                // get the time
                                String address  = legsObject.getString("end_address");
                                txtAddress.setText(address);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomerCall.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();

    }

}
