package sg.tgonet.singtransport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sg.tgonet.singtransport.Adapter.BusStopListAdapter;
import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.ListOfArrivalClass;
import sg.tgonet.singtransport.Notifications.AlarmReceiver;
import sg.tgonet.singtransport.ui.home.HomeFragment;

public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, BusStopListAdapter.OnItemClickListener {

    int resourceId;
    String[] busNos;
    ArrayList<String> aList;
    ArrayList<BusStopClass> BusStopList,storage;
    ArrayList<ListOfArrivalClass> FavouriteBusStop;
    ExpandableListView expandableListView;
    BusStopListAdapter busStopListAdapter;
    ArrayList<ArrivalClass> ArrivalList;

    GoogleMap map;
    SupportMapFragment mapFragment;
    IconGenerator iconGenerator;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE = 101;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    Lib lib = new Lib(getApplicationContext(),this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getWindow().setStatusBarColor(ContextCompat.getColor(Map.this, R.color.colorPrimary));

        FavouriteBusStop = lib.loadFavouriteData(getApplicationContext());
        BusStopList = lib.loadBusStopList(getApplicationContext(),this);
        storage = NearestStops(new LatLng(1.28941, 103.8022));
        expandableListView = findViewById(R.id.ExpandList);
        busStopListAdapter = new BusStopListAdapter(getApplicationContext(),storage,new HashMap<BusStopClass, ArrayList<ArrivalClass>>(),getInitials(storage),this);
        busStopListAdapter.setArrivalList(getList(storage,busStopListAdapter));
        expandableListView.setAdapter(busStopListAdapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ArrivalList = new ArrayList<>();
                String BusStopCode = storage.get(groupPosition).getBusStopCode();
                getData(BusStopCode,groupPosition,busStopListAdapter);

                return false;
            }
        });

        iconGenerator = new IconGenerator(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);
        mapFragment.getMapAsync(this);
    }

    public HashMap<BusStopClass,ArrayList<ArrivalClass>> getList(ArrayList<BusStopClass> BusStopList,BusStopListAdapter busStopListAdapter){
        HashMap<BusStopClass,ArrayList<ArrivalClass>> arrivallist = new HashMap<>();
        int count = 0;
        ArrayList<ArrivalClass> hi = new ArrayList<>();
        if(lib.isNetworkAvailable()){
            for(BusStopClass i : BusStopList){
                arrivallist.put(i,getData(i.getBusStopCode(), count,busStopListAdapter));
                count++;
            }
        }
        else{
            for(BusStopClass i : BusStopList){
                hi = new ArrayList<>();
                resourceId = this.getResources().getIdentifier("a" + i.getBusStopCode(), "array", Map.this.getPackageName());
                busNos = getResources().getStringArray(resourceId)[0].split(",");
                for(String j : busNos){
                    hi.add(new ArrivalClass(j,false));
                }
                for(ListOfArrivalClass a : FavouriteBusStop){
                    if(a.busStopClass.getBusStopCode().equals(i.getBusStopCode())){
                        for(ArrivalClass k : a.arrivalClasses){
                            for(ArrivalClass z : hi){
                                if(k.getServiceNo().equals(z.getServiceNo())){

                                    z.setFavourite(true);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                arrivallist.put(i,hi);
            }
        }

        return arrivallist;
    }

    public ArrayList<String> getInitials(ArrayList<BusStopClass> NearestList){
        aList = new ArrayList<>();
        for(BusStopClass i : NearestList) {
            String[] busStopName = i.getDescription().split("");
            String iconName = busStopName[0] + busStopName[busStopName.length - 1].toUpperCase();
            if (aList.contains(iconName)) {
                iconName = busStopName[0] + "Q";
            }
            aList.add(iconName);
        }
        return aList;
    }

    public ArrayList<BusStopClass> NearestStops(LatLng position){
        ArrayList<BusStopClass> NearestList = new ArrayList<>();
        for(BusStopClass i : BusStopList){
            if((position.latitude-0.003 <= i.getLatitude() && position.latitude+0.003 >=i.getLatitude()) && ((position.longitude+0.0030) >= i.getLongitude() && (position.longitude-0.0030) <= i.getLongitude())){
                Double distance = SphericalUtil.computeDistanceBetween(new LatLng(position.latitude,position.longitude), new LatLng(i.getLatitude(),i.getLongitude()));
                BusStopClass b = new BusStopClass(i.getBusStopCode(),i.getDescription(),i.getRoadName(),i.getLatitude(),i.getLongitude(),distance);
                NearestList.add(b);
            }
        }
        Collections.sort(NearestList, new Comparator<BusStopClass>(){
            public int compare(BusStopClass one, BusStopClass two) {
                return (int) (one.getDistance() - two.getDistance());
            }
        });
        return NearestList;
    }

    public ArrayList<ArrivalClass> getData(final String BusStopCode, final int position, final BusStopListAdapter busStopListAdapter) {

        final ArrayList<ArrivalClass> ArrivalList = new ArrayList<>();
        String url = "http://datamall2.mytransport.sg/ltaodataservice/BusArrivalv2?BusStopCode=";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + BusStopCode)
                .method("GET", null)
                .addHeader("AccountKey", "ws1VgUJYRfSaUo06ge+V5g==")
                .build();

        resourceId = this.getResources().getIdentifier("a" + BusStopCode, "array", this.getPackageName());
        busNos = getResources().getStringArray(resourceId)[0].split(",");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    final String myResponse = response.body().string();
                    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    final DateTime t1 = new DateTime();

                    Map.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Double lat1,lat2,lat3,lag1,lag2,lag3;
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(myResponse);
                                // Do something here

                                JSONArray Array = jsonObject.getJSONArray("Services");
                                for (int i = 0; i < Array.length(); i++) {
                                    JSONObject object = Array.getJSONObject(i);
                                    String ServiceNo = object.getString("ServiceNo");

                                    String timing1 = object.getJSONObject("NextBus").getString("EstimatedArrival");
                                    String load1 = object.getJSONObject("NextBus").getString("Load");

                                    try{
                                        lat1 = Double.parseDouble(object.getJSONObject("NextBus").getString("Latitude"));
                                    }
                                    catch (Exception e) {
                                        lat1 = null;
                                    }

                                    try {
                                        lag1 = Double.parseDouble(object.getJSONObject("NextBus").getString("Longitude"));
                                    }
                                    catch (Exception e) {
                                        lag1 = null;
                                    }

                                    if (timing1.equals("")) {
                                        timing1 = "-";
                                    }
                                    else {

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
                                    }
                                    catch (Exception e) {
                                        lat2 = null;
                                    }

                                    try {
                                        lag2 = Double.parseDouble(object.getJSONObject("NextBus2").getString("Longitude"));
                                    }
                                    catch (Exception e) {
                                        lag2 = null;
                                    }

                                    if (timing2.equals("")) {
                                        timing2 = "-";
                                    }
                                    else {

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
                                        }
                                        catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    String timing3 = object.getJSONObject("NextBus3").getString("EstimatedArrival");
                                    String load3 = object.getJSONObject("NextBus3").getString("Load");

                                    try{
                                        lat3 = Double.parseDouble(object.getJSONObject("NextBus3").getString("Latitude"));
                                    }
                                    catch (Exception e) {
                                        lat3 = null;
                                    }

                                    try {
                                        lag3 = Double.parseDouble(object.getJSONObject("NextBus3").getString("Longitude"));
                                    }
                                    catch (Exception e) {
                                        lag3 = null;
                                    }

                                    if (timing3.equals("")) {
                                        timing3 = "-";
                                    }
                                    else {
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
                                        }
                                        catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    ArrivalList.add(new ArrivalClass(ServiceNo, timing1, timing2, timing3, load1, load2, load3,lat1,lag1,lat2,lag2,lat3,lag3,false));
                                }

                                for(String k : busNos){
                                    boolean checker = false;
                                    for(ArrivalClass j : ArrivalList){
                                        if(k.equals(j.getServiceNo())){
                                            checker = true;
                                        }
                                    }
                                    if(!checker){
                                        ArrivalList.add(new ArrivalClass(k,false));
                                    }
                                }

                                for(ListOfArrivalClass j : FavouriteBusStop){
                                    if(j.busStopClass.getBusStopCode().equals(BusStopCode)){
                                        for(ArrivalClass k : j.arrivalClasses){
                                            for(ArrivalClass z : ArrivalList){
                                                if(k.getServiceNo().equals(z.getServiceNo())){

                                                    z.setFavourite(true);
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }

                                busStopListAdapter.updateitem(ArrivalList,position);
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
        return ArrivalList;
    }


    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
                    testing();
            return;
        }
        else{
            map.setMyLocationEnabled(true);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), 16));
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void testing(){
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(Map.this,
                                                REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }

            }
        });
    }

    @Override
    public void onCameraIdle() {
        LatLng position = map.getCameraPosition().target;
        storage = NearestStops(position);
        aList = getInitials(storage);
        busStopListAdapter.Update(storage,aList,getList(storage,busStopListAdapter));
        if (map != null) {
            map.clear();
        }
        for (int i = 0; i < storage.size(); i++) {
            map.addMarker(new MarkerOptions().position(new LatLng(storage.get(i).getLatitude(), storage.get(i).getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(aList.get(i)))));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng Singapore;
        Singapore = new LatLng(1.28941, 103.8022);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Singapore, 16));
        map.setOnCameraIdleListener(this);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            testing();
        }
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 16));
            }
        });
        fetchLastLocation();
    }

    @Override
    public void onFavouriteClick(int groupPosition, int childPosition) {
        int help = 0;
        ArrayList<ArrivalClass> buslist = new ArrayList<>();
        BusStopClass busObject = busStopListAdapter.getHeaders().get(groupPosition);
        ArrivalClass object = busStopListAdapter.getArrivalList().get(busStopListAdapter.getHeaders().get(groupPosition)).get(childPosition);
        if(object.getFavourite()){
            object.setFavourite(false);
            for(ListOfArrivalClass i : FavouriteBusStop){
                if(i.busStopClass.getBusStopCode().equals(busStopListAdapter.getHeaders().get(groupPosition).getBusStopCode())){
                    for(ArrivalClass j : i.arrivalClasses){
                        if(j.getServiceNo().equals(object.getServiceNo())){
                            i.arrivalClasses.remove(j);
                            break;
                        }
                    }
                    if(i.arrivalClasses.size() == 0){
                        FavouriteBusStop.remove(i);
                    }
                    break;
                }
            }
        }
        else {
            object.setFavourite(true);

            for (ListOfArrivalClass i : FavouriteBusStop) {
                if (i.busStopClass.getBusStopCode().equals(busObject.getBusStopCode())) {
                    help = 1;
                    i.arrivalClasses.add(new ArrivalClass(object.getServiceNo(), "0", "0", "0", "0", "0", "0", true));
                    break;
                }
            }
            if (help == 0) {
                buslist.add(new ArrivalClass(object.getServiceNo(), "0", "0", "0", "0", "0", "0", true));
                FavouriteBusStop.add(new ListOfArrivalClass(new BusStopClass(busObject.getBusStopCode(), busObject.getDescription(), busObject.getRoadName(), 0.0, 0.0), buslist));
            }
        }
        busStopListAdapter.notifyDataSetChanged();
        lib.saveFavouriteData(FavouriteBusStop, getApplicationContext());
    }

    @Override
    public void onAlarmClick(int groupPosition, int childPosition) {
        int i = 0;
        boolean check = false;
        ArrivalClass object = busStopListAdapter.getArrivalList().get(busStopListAdapter.getHeaders().get(groupPosition)).get(childPosition);
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
    public void onItemClick(int groupPosition, int childPosition) {
        String busStopCode = busStopListAdapter.getHeaders().get(groupPosition).getBusStopCode();
        String serviceNo =  busStopListAdapter.getArrivalList().get(busStopListAdapter.getHeaders().get(groupPosition)).get(childPosition).getServiceNo();
        Intent intent = new Intent(this,BusRoute.class);
        intent.putExtra("ServiceNo",serviceNo);
        intent.putExtra("BusStopCode",busStopCode);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}

