package sg.tgonet.singtransport.ui.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sg.tgonet.singtransport.Adapter.ArrivalAdapter;
import sg.tgonet.singtransport.Adapter.HomeAdapter;
import sg.tgonet.singtransport.BusRoute;
import sg.tgonet.singtransport.FragmentListener;
import sg.tgonet.singtransport.Fragments.BusRouteFragment;
import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.Class.ListOfArrivalClass;
import sg.tgonet.singtransport.Fragments.MapFragment;
import sg.tgonet.singtransport.Fragments.SearchFragment;
import sg.tgonet.singtransport.MainActivity;
import sg.tgonet.singtransport.Map;
import sg.tgonet.singtransport.Notifications.AlarmReceiver;
import sg.tgonet.singtransport.R;
import sg.tgonet.singtransport.Search;


public class HomeFragment extends Fragment implements ArrivalAdapter.OnItemListener {

    CardView cardView;
    Button mapButton;
    ArrayList<ListOfArrivalClass> FavouriteBusStopList;
    String[] BusNumberList;
    RecyclerView rv;
    HomeAdapter adapter;


    @Override
    public void onResume() {
        super.onResume();
        FavouriteBusStopList = MainActivity.loadFavouriteData(getContext());
        buildItemList(FavouriteBusStopList);
        adapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        FavouriteBusStopList = MainActivity.loadFavouriteData(getContext());

        cardView = root.findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SearchFragment searchFragment = SearchFragment.newInstance();
                ft.add(R.id.nav_host_fragment, searchFragment);
                ft.addToBackStack(null);
                ft.commit();*/
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
            }
        });

        mapButton = root.findViewById(R.id.MapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                MapFragment mapFragment = MapFragment.newInstance();
                ft.add(R.id.nav_host_fragment, mapFragment);
                ft.addToBackStack(null);
                ft.commit();*/
                Intent intent = new Intent(getActivity(), Map.class);
                startActivity(intent);
            }
        });

        rv = root.findViewById(R.id.rv_home);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HomeAdapter(FavouriteBusStopList,this);
        rv.setAdapter(adapter);

        buildItemList(FavouriteBusStopList);

        MainActivity.fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(MainActivity.fm != null){
                    if(MainActivity.fm.getBackStackEntryCount() == 0){
                        FavouriteBusStopList = MainActivity.loadFavouriteData(getContext());
                        buildItemList(FavouriteBusStopList);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });

        return root;
    }

    private void buildItemList(ArrayList<ListOfArrivalClass> itemList) {
        for (ListOfArrivalClass i : itemList) {
            getData(i.busStopClass.getBusStopCode(),i.arrivalClasses);
        }
        adapter.update(itemList);
    }



    private void getData(final String BusStopCode, final ArrayList<ArrivalClass> ArrivalList) {
        String url = "http://datamall2.mytransport.sg/ltaodataservice/BusArrivalv2?BusStopCode=";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + BusStopCode)
                .method("GET", null)
                .addHeader("AccountKey", "ws1VgUJYRfSaUo06ge+V5g==")
                .build();

        final ArrayList<String> serviceList = new ArrayList<>();
        for(ArrivalClass i : ArrivalList){
            serviceList.add(i.getServiceNo());
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    final DateTime t1 = new DateTime();
                    getActivity().runOnUiThread(new Runnable() {
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
                                    if(serviceList.contains(ServiceNo)){

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
                                        ArrivalList.get(serviceList.indexOf(ServiceNo)).setTiming(timing1);
                                        ArrivalList.get(serviceList.indexOf(ServiceNo)).setTiming2(timing2);
                                        ArrivalList.get(serviceList.indexOf(ServiceNo)).setTiming3(timing3);
                                        ArrivalList.get(serviceList.indexOf(ServiceNo)).setLoad(load1);
                                        ArrivalList.get(serviceList.indexOf(ServiceNo)).setLoad2(load2);
                                        ArrivalList.get(serviceList.indexOf(ServiceNo)).setLoad3(load3);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
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

    @Override
    public void onItemClick(int position, ArrivalClass Service) {
        int first = 0,second = 0;
        boolean check = false;
        String serviceno = Service.getServiceNo();
        String timing = Service.getTiming();
        String timing2 = Service.getTiming2();
        String timing3 = Service.getTiming3();
        String load = Service.getLoad();
        String load2 = Service.getLoad2();
        String load3 = Service.getLoad3();
        for(int i = 0; i < FavouriteBusStopList.size(); i++){
            first = i;
            for(int j = 0; j < FavouriteBusStopList.get(i).arrivalClasses.size(); j++){
                if(FavouriteBusStopList.get(i).arrivalClasses.get(j).getServiceNo().equals(serviceno) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getLoad().equals(load) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getLoad2().equals(load2) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getLoad3().equals(load3) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getTiming().equals(timing) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getTiming2().equals(timing2) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getTiming3().equals(timing3))
                {
                    second = j;
                    check = true;
                    break;
                }
            }
            if(check){break;}
        }
        String busStopCode = FavouriteBusStopList.get(first).busStopClass.getBusStopCode();
        /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        BusRouteFragment fragmentDemo = BusRouteFragment.newInstance(serviceno, busStopCode);
        ft.replace(R.id.nav_host_fragment, fragmentDemo).addToBackStack(null).commit();*/
        //ft.addToBackStack(null);
        //ft.commit();
        Intent intent = new Intent(getContext(), BusRoute.class);
        intent.putExtra("ServiceNo",Service.getServiceNo());
        intent.putExtra("BusStopCode",busStopCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onTimingClick(int position) {

    }

    @Override
    public void onAlarmClick(int position, ArrivalClass Service) {
        int i = 0;
        boolean check = false;
        String timing1 = Service.getTiming();
        String timing2  = Service.getTiming2();
        String timing3  = Service.getTiming3();
        if(!timing1.equals("Left") && !timing1.equals("Arr")){
            if(Integer.parseInt(timing1) > 3){
                i = Integer.parseInt(timing1);
                check = true;
            }
            else{
                Toast.makeText(getContext(),"Bus is arriving soon",Toast.LENGTH_SHORT).show();
            }
        }
        else if(!timing2.equals("Left") && !timing2.equals("Arr")){
            if(Integer.parseInt(timing2) > 3){
                i = Integer.parseInt(timing2);
                check = true;
            }
            else{
                Toast.makeText(getContext(),"Bus is arriving soon",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(!timing3.equals("Left") && !timing3.equals("Arr")){
                if(Integer.parseInt(timing3) > 3){
                    i = Integer.parseInt(timing3);
                    check = true;
                }
                else{
                    Toast.makeText(getContext(),"Bus is arriving soon",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(),"Please wait for the next bus arrival update",Toast.LENGTH_SHORT).show();
            }
        }
        if(check) {
            AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putString("BusNumber", Service.getServiceNo());
            Intent myIntent = new Intent(getContext(), AlarmReceiver.class);
            myIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((i - 3) * 1000 * 60), pendingIntent);
            Toast.makeText(getContext(), "Alarm has been set", Toast.LENGTH_SHORT).show();
        }
    }

   @Override
    public void onFavouriteClick(int position, ArrivalClass Service) {
        int first = 0,second = 0;
        boolean check = false;
        String serviceno = Service.getServiceNo();
        String timing = Service.getTiming();
        String timing2 = Service.getTiming2();
        String timing3 = Service.getTiming3();
        String load = Service.getLoad();
        String load2 = Service.getLoad2();
        String load3 = Service.getLoad3();
        for(int i = 0; i < FavouriteBusStopList.size(); i++){
            first = i;
            for(int j = 0; j < FavouriteBusStopList.get(i).arrivalClasses.size(); j++){
                if(FavouriteBusStopList.get(i).arrivalClasses.get(j).getServiceNo().equals(serviceno) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getLoad().equals(load) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getLoad2().equals(load2) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getLoad3().equals(load3) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getTiming().equals(timing) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getTiming2().equals(timing2) &&
                        FavouriteBusStopList.get(i).arrivalClasses.get(j).getTiming3().equals(timing3))
                {
                    second = j;
                    check = true;
                    break;
                }
            }
            if(check){break;}
        }
        FavouriteBusStopList.get(first).arrivalClasses.remove(second);
        if(FavouriteBusStopList.get(first).arrivalClasses.size() == 0){
            FavouriteBusStopList.remove(first);
        }

        MainActivity.saveFavouriteData(FavouriteBusStopList,getContext());
        adapter.notifyDataSetChanged();
    }
}