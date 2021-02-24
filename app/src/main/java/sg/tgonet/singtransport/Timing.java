package sg.tgonet.singtransport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sg.tgonet.singtransport.Adapter.ArrivalAdapter;
import sg.tgonet.singtransport.Adapter.BusStopServiceAdapter;
import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.Buses;
import sg.tgonet.singtransport.Class.ListOfArrivalClass;
import sg.tgonet.singtransport.Notifications.AlarmReceiver;
import sg.tgonet.singtransport.ui.home.HomeFragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Timing extends AppCompatActivity implements ArrivalAdapter.OnItemListener{

    RecyclerView rv;
    Button Back;
    TextView stopDescription;
    int resourceId;
    String[] busNos;
    String stopDesc,busStopCode,roadName;
    ArrayList<ArrivalClass> ArrivalList;
    ArrayList<ListOfArrivalClass> FavouriteBuses;
    ArrivalAdapter adapter;
    Lib lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        getWindow().setStatusBarColor(ContextCompat.getColor(Timing.this, R.color.colorPrimary));
        //toolbar1 = findViewById(R.id.toolbar1);
        Back = findViewById(R.id.Timing_back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lib = new Lib(getApplicationContext(),this);

        stopDesc = getIntent().getStringExtra("StopDesc");
        busStopCode = getIntent().getStringExtra("BusStopCode");

        roadName = getIntent().getStringExtra("RoadName");

        resourceId = this.getResources().getIdentifier("a" + busStopCode, "array", Timing.this.getPackageName());
        busNos = this.getResources().getStringArray(resourceId)[0].split(",");

        ArrivalList = new ArrayList<>();
        FavouriteBuses = lib.loadFavouriteData(getApplicationContext());

        CreateView();

        if(lib.isNetworkAvailable()){
            lib.getData(busStopCode, FavouriteBuses, new Lib.timing() {
                @Override
                public void update(ArrayList<ArrivalClass> list) {
                    Log.d("HIHIHI", String.valueOf(list.size()));
                    adapter.update(list);
                }
            });
        }
        else{
            for (String i : busNos) {
                ArrivalList.add(new ArrivalClass(i,false));
            }
            for(ListOfArrivalClass i : FavouriteBuses){
                if(i.busStopClass.getBusStopCode().equals(busStopCode)){
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
        }


        stopDescription = findViewById(R.id.StopDesc);
        stopDescription.setText(stopDesc + " - " + busStopCode );


    }

    private void CreateView() {
        rv = findViewById(R.id.rv_timing);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ArrivalAdapter(ArrivalList,this);
        rv.setAdapter(adapter);
    }



    @Override
    public void onItemClick(int position, ArrivalClass Service) {
        Intent intent = new Intent(this,BusRoute.class);
        intent.putExtra("ServiceNo",ArrivalList.get(position).getServiceNo());
        intent.putExtra("BusStopCode",busStopCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }

    @Override
    public void onTimingClick(int position) {

    }

    @Override
    public void onAlarmClick(int position, ArrivalClass Service) {
        int i = 0;
        boolean check = false;
        ArrivalClass object = ArrivalList.get(position);
        String timing1 = object.getTiming();
        String timing2  = object.getTiming2();
        String timing3  = object.getTiming3();
        if(!timing1.equals("Left") && !timing1.equals("Arr")){
            if(Integer.parseInt(timing1) > 3){
                i = Integer.parseInt(timing1);
                check = true;
            }
            else{
                Toast.makeText(getApplicationContext(),"Bus is arriving soon",Toast.LENGTH_SHORT).show();
            }
        }
        else if(!timing2.equals("Left") && !timing2.equals("Arr")){
            if(Integer.parseInt(timing2) > 3){
                i = Integer.parseInt(timing2);
                check = true;
            }
            else{
                Toast.makeText(getApplicationContext(),"Bus is arriving soon",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(!timing3.equals("Left") && !timing3.equals("Arr")){
                if(Integer.parseInt(timing3) > 3){
                    i = Integer.parseInt(timing3);
                    check = true;
                }
                else{
                    Toast.makeText(getApplicationContext(),"Bus is arriving soon",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Please wait for the next bus arrival update",Toast.LENGTH_SHORT).show();
            }
        }
        if(check) {
            AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putString("BusNumber", object.getServiceNo());
            Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            myIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((i - 3) * 1000 * 60), pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm has been set", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavouriteClick(int position, ArrivalClass Service) {
        int help = 0;
        ArrayList<ArrivalClass> buslist = new ArrayList<>();
        if (ArrivalList.get(position).getFavourite() == false) {
            ArrivalList.get(position).setFavourite(true);

            for (ListOfArrivalClass i : FavouriteBuses) {
                if (i.busStopClass.getBusStopCode().equals(busStopCode)) {
                    help = 1;
                    i.arrivalClasses.add(new ArrivalClass(ArrivalList.get(position).getServiceNo(), "0", "0", "0", "0", "0", "0", true));
                    break;
                }
            }
            if (help == 0) {
                buslist.add(new ArrivalClass(ArrivalList.get(position).getServiceNo(), "0", "0", "0", "0", "0", "0", true));
                FavouriteBuses.add(new ListOfArrivalClass(new BusStopClass(busStopCode, stopDesc, roadName, 0.0, 0.0), buslist));
            }
        }
        else {
            ArrivalList.get(position).setFavourite(false);
            for (ListOfArrivalClass i : FavouriteBuses) {
                if (i.busStopClass.getBusStopCode().equals(busStopCode)) {
                    for(ArrivalClass j : i.arrivalClasses){
                        if(j.getServiceNo().equals(ArrivalList.get(position).getServiceNo())){
                            i.arrivalClasses.remove(j);
                            break;
                        }
                    }

                }
                if(i.arrivalClasses.size() == 0){
                    FavouriteBuses.remove(i);
                }
                break;
            }
        }

        new Lib(getApplicationContext(),this).saveFavouriteData(FavouriteBuses, getApplicationContext());
        adapter.notifyDataSetChanged();
    }
}