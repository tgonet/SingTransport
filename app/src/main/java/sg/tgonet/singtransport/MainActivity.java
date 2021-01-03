package sg.tgonet.singtransport;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Type;
import java.util.ArrayList;

import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.ListOfArrivalClass;
import sg.tgonet.singtransport.Fragments.MapFragment;
import sg.tgonet.singtransport.Fragments.TimingFragment;

public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    public static FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fm = getSupportFragmentManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            toolbar.setElevation(0.0f);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        loadBusStopList(getApplicationContext(),this);
        loadBusServiceList(getApplicationContext(),this);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

        Log.d("HI", String.valueOf(getResources().getDisplayMetrics()));
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static ArrayList<BusStopClass> loadBusStopList(Context context,Activity activity) {
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

    @Override
    public void onBackPressed() {
        final int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        }
        else {
           getSupportFragmentManager().popBackStack();
        }

    }


}