package sg.tgonet.singtransport;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.ListOfArrivalClass;
import sg.tgonet.singtransport.Notifications.AlarmReceiver;

import static android.content.Context.MODE_PRIVATE;

public class Lib {
    Context context;
    Activity activity;

    public Lib(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    interface timing{
        void update(ArrayList<ArrivalClass> list);
    }

    public static ArrayList<BusStopClass> loadBusStopList(Context context, Activity activity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BusStopListFile", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("BusStopList", null);
        Type type = new TypeToken<ArrayList<BusStopClass>>() {
        }.getType();
        ArrayList<BusStopClass> BusStopList = gson.fromJson(json, type);
        if(BusStopList == null){
            BusStopList = new ArrayList<>();
            String[] BusStopString = activity.getResources().getStringArray(R.array.BusStops);
            for (String i : BusStopString)
            {
                String[] j = i.split(",");
                BusStopList.add(new BusStopClass(j[0],j[1],j[4],Double.parseDouble(j[2]),Double.parseDouble(j[3])));
            }
            saveBusStopList(BusStopList,activity.getApplicationContext());
        }
        return BusStopList;
    }

    public static void saveBusStopList(ArrayList<BusStopClass> BusStopList, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BusStopListFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(BusStopList);
        editor.putString("BusStopList", json);
        editor.apply();
    }

    public static ArrayList<BusServiceClass> loadBusServiceList(Context context,Activity activity){
        SharedPreferences sharedPreferences = context.getSharedPreferences("BusNumberListFile", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("BusNumberList", null);
        Type type = new TypeToken<ArrayList<BusServiceClass>>() {
        }.getType();
        ArrayList<BusServiceClass> BusNumberList = gson.fromJson(json, type);

        if(BusNumberList == null){
            BusNumberList = new ArrayList<>();

            String[] BusServiceString = activity.getResources().getStringArray(R.array.BusRoutes);

            for (String i : BusServiceString)
            {
                String[] j = i.split(",");
                BusNumberList.add(new BusServiceClass(j[0],j[2],j[3],j[1],j[4],j[5]));
            }

            saveBusServiceList(BusNumberList,activity.getApplicationContext());
        }

        return BusNumberList;
    }

    public static void saveBusServiceList(ArrayList<BusServiceClass> BusNumberList, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BusNumberListFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(BusNumberList);
        editor.putString("BusNumberList", json);
        editor.apply();
    }

    public static ArrayList<ListOfArrivalClass> loadFavouriteData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavouriteBusStopServiceListFile", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("FavouriteBusStopServiceList", null);
        Type type = new TypeToken<ArrayList<ListOfArrivalClass>>() {}.getType();
        ArrayList<ListOfArrivalClass> FavouriteBusStop = gson.fromJson(json, type);

        if (FavouriteBusStop == null) {
            FavouriteBusStop = new ArrayList<>();
        }

        return FavouriteBusStop;
    }

    public static void saveFavouriteData(ArrayList<ListOfArrivalClass> FavouriteBusStopList, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavouriteBusStopServiceListFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(FavouriteBusStopList);
        editor.putString("FavouriteBusStopServiceList", json);
        editor.apply();
    }

    public void getData(final String BusStopCode, final ArrayList<ListOfArrivalClass> FavouriteBuses, final timing timing) {
        String url = "http://datamall2.mytransport.sg/ltaodataservice/BusArrivalv2?BusStopCode=";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + BusStopCode)
                .method("GET", null)
                .addHeader("AccountKey", "ws1VgUJYRfSaUo06ge+V5g==")
                .build();

        final ArrayList<ArrivalClass> ArrivalList = new ArrayList<>();
        final String[] busNos;

        int resourceId = activity.getResources().getIdentifier("a" + BusStopCode, "array", activity.getPackageName());
        busNos = activity.getResources().getStringArray(resourceId)[0].split(",");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    final String myResponse = response.body().string();
                    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    final DateTime t1 = new DateTime();


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Double lat1,lat2,lat3,lag1,lag2,lag3;
                            JSONObject jsonObject = null;

                            try {

                                jsonObject = new JSONObject(myResponse);

                                // Do something here
                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;

                                JSONArray Array = jsonObject.getJSONArray("Services");

                                for (int i = 0; i < Array.length(); i++) {
                                    JSONObject object = Array.getJSONObject(i);
                                    String ServiceNo = object.getString("ServiceNo");
                                    String timing1 = object.getJSONObject("NextBus").getString("EstimatedArrival");
                                    String load1 = object.getJSONObject("NextBus").getString("Load");

                                    try{
                                        lat1 = Double.parseDouble(object.getJSONObject("NextBus").getString("Latitude"));
                                    } catch (Exception e) {
                                        lat1 = null;
                                    }

                                    try {
                                        lag1 = Double.parseDouble(object.getJSONObject("NextBus").getString("Longitude"));
                                    } catch (Exception e) {
                                        lag1 = null;
                                    }

                                    if (timing1.equals("")) {
                                        timing1 = "-";

                                    } else {
                                        try {
                                            Date date = formatter.parse(timing1);
                                            DateTime t2 = new DateTime(date);
                                            timing1 = String.valueOf((int)Math.floor(Double.parseDouble(String.valueOf(Seconds.secondsBetween(t1,t2).getSeconds()))/60));

                                            if(Integer.parseInt(timing1) < 1 && Integer.parseInt(timing1) > -2){
                                                timing1 = "Arr";
                                            }
                                            else if(Integer.parseInt(timing1) <= -2){
                                                timing1 = "Left";
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    String timing2 = object.getJSONObject("NextBus2").getString("EstimatedArrival");
                                    String load2 = object.getJSONObject("NextBus2").getString("Load");

                                    try{
                                        lat2 = Double.parseDouble(object.getJSONObject("NextBus2").getString("Latitude"));
                                    } catch (Exception e) {
                                        lat2 = null;
                                    }

                                    try {
                                        lag2 = Double.parseDouble(object.getJSONObject("NextBus2").getString("Longitude"));
                                    } catch (Exception e) {
                                        lag2 = null;
                                    }

                                    if (timing2.equals("")) {
                                        timing2 = "-";
                                    } else {

                                        try {
                                            Date date = formatter.parse(timing2);
                                            DateTime t2 = new DateTime(date);
                                            timing2 = String.valueOf((int) Math.floor(Double.parseDouble(String.valueOf(Seconds.secondsBetween(t1,t2).getSeconds()))/60));

                                            if(Integer.parseInt(timing2) < 1 && Integer.parseInt(timing2) > -2){
                                                timing2 = "Arr";
                                            }
                                            else if(Integer.parseInt(timing2) <= -2){
                                                timing2 = "Left";
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    String timing3 = object.getJSONObject("NextBus3").getString("EstimatedArrival");
                                    String load3 = object.getJSONObject("NextBus3").getString("Load");

                                    try{
                                        lat3 = Double.parseDouble(object.getJSONObject("NextBus3").getString("Latitude"));
                                    } catch (Exception e) {
                                        lat3 = null;
                                    }

                                    try {
                                        lag3 = Double.parseDouble(object.getJSONObject("NextBus3").getString("Longitude"));
                                    } catch (Exception e) {
                                        lag3 = null;
                                    }

                                    if (timing3.equals("")) {
                                        timing3 = "-";
                                    } else {

                                        try {
                                            Date date = formatter.parse(timing3);
                                            DateTime t2 = new DateTime(date);
                                            timing3 = String.valueOf((int)Math.floor(Double.parseDouble(String.valueOf(Seconds.secondsBetween(t1,t2).getSeconds()))/60));

                                            if(Integer.parseInt(timing3) < 1 && Integer.parseInt(timing3) > -2){
                                                timing1 = "Arr";
                                            }
                                            else if(Integer.parseInt(timing3) <= -2){
                                                timing3 = "Left";
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    ArrivalList.add(new ArrivalClass(ServiceNo, timing1, timing2, timing3, load1, load2, load3,lat1,lag1,lat2,lag2,lat3,lag3,false));
                                    Collections.sort(ArrivalList);
                                }

                                for(String i : busNos){
                                    boolean checker = false;
                                    for(ArrivalClass j : ArrivalList){
                                        if(i.equals(j.getServiceNo())){
                                            checker = true;
                                        }
                                    }
                                    if(!checker){
                                        ArrivalList.add(new ArrivalClass(i,false));
                                    }
                                }
                                for(ListOfArrivalClass i : FavouriteBuses){
                                    if(i.busStopClass.getBusStopCode().equals(BusStopCode)){
                                        for(ArrivalClass j : i.arrivalClasses){
                                            for(ArrivalClass z : ArrivalList){
                                                if(j.getServiceNo().equals(z.getServiceNo())){

                                                    z.setFavourite(true);
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                                timing.update((ArrivalList));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void alarmClick(ArrivalClass object){
        /*int i = 0;
        boolean check = false;
        String timing1 = object.getTiming();
        String timing2  = object.getTiming2();
        String timing3  = object.getTiming3();
        if(!timing1.equals("Left") && !timing1.equals("Arr")){
            if(Integer.parseInt(timing1) > 3){
                i = Integer.parseInt(timing1);
                check = true;
            }
            else{
                Toast.makeText(context,"Bus is arriving soon",Toast.LENGTH_SHORT).show();
            }
        }
        else if(!timing2.equals("Left") && !timing2.equals("Arr")){
            if(Integer.parseInt(timing2) > 3){
                i = Integer.parseInt(timing2);
                check = true;
            }
            else{
                Toast.makeText(context,"Bus is arriving soon",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(!timing3.equals("Left") && !timing3.equals("Arr")){
                if(Integer.parseInt(timing3) > 3){
                    i = Integer.parseInt(timing3);
                    check = true;
                }
                else{
                    Toast.makeText(context,"Bus is arriving soon",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(context,"Please wait for the next bus arrival update",Toast.LENGTH_SHORT).show();
            }
        }
        if(check) {
            AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putString("BusNumber", object.getServiceNo());
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            myIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((i - 3) * 1000 * 60), pendingIntent);
            Toast.makeText(context, "Alarm has been set", Toast.LENGTH_SHORT).show();
        }
    }*/
    }
}
