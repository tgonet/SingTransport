package sg.tgonet.singtransport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import sg.tgonet.singtransport.Adapter.BusStopServiceAdapter;
import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.Buses;


public class Search extends AppCompatActivity implements BusStopServiceAdapter.OnItemListener{

    EditText searchBar;
    Button Back;
    ArrayList<BusServiceClass> BusNumberList;
    ArrayList<BusStopClass> BusStopList;
    RecyclerView rv;
    BusStopServiceAdapter adapter;
    ArrayList<Buses> newList;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getWindow().setStatusBarColor(ContextCompat.getColor(Search.this, R.color.colorPrimary));

        searchBar = findViewById(R.id.searchBar);
        searchBar.requestFocus();
        searchBar.setFocusable(true);
        searchBar.setFocusableInTouchMode(true);
        BusNumberList = loadBusServiceList(getApplicationContext());
        BusStopList = loadBusStopList(getApplicationContext());
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imm = (InputMethodManager)   Search.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        Back = findViewById(R.id.Search_back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                imm.hideSoftInputFromWindow(Search.this.getCurrentFocus().getWindowToken(), 0);
            }
        });

        rv = findViewById(R.id.Rv_search);
        CreateView();


    }

    public void filter(String text){
        newList = new ArrayList<>();
        for(BusStopClass i : BusStopList){
            if(i.getBusStopCode().equals(text) || i.getDescription().contains(text)){
                newList.add(i);
            }
        }
        for(BusServiceClass j : BusNumberList){
            if(j.getServiceNo().equals(text)){
                newList.add(j);
            }
        }
        adapter.filteredList(newList);
        adapter.notifyDataSetChanged();
    }

    private void CreateView() {
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new BusStopServiceAdapter(new ArrayList<Buses>(), this);
        rv.setAdapter(adapter);
    }

    public static ArrayList<BusStopClass> loadBusStopList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BusStopListFile", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("BusStopList", null);
        Type type = new TypeToken<ArrayList<BusStopClass>>() {
        }.getType();
        ArrayList<BusStopClass> BusStopList = gson.fromJson(json, type);
        return BusStopList;
    }

    public static ArrayList<BusServiceClass> loadBusServiceList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BusNumberListFile", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("BusNumberList", null);
        Type type = new TypeToken<ArrayList<BusServiceClass>>() {
        }.getType();
        ArrayList<BusServiceClass> BusNumberList = gson.fromJson(json, type);

        return BusNumberList;
    }

    @Override
    public void onItemClick(int position) {
        if(newList.get(position).getClass() == BusStopClass.class){
            Intent intent = new Intent(Search.this, Timing.class );
            intent.putExtra("BusStopCode", ((BusStopClass) newList.get(position)).getBusStopCode());
            intent.putExtra("StopDesc",((BusStopClass) newList.get(position)).getDescription());
            intent.putExtra("RoadName",((BusStopClass) newList.get(position)).getRoadName());
            imm.hideSoftInputFromWindow(Search.this.getCurrentFocus().getWindowToken(), 0);
            startActivity(intent);
//            BusStopClass object = ((BusStopClass) newList.get(position));
//            FragmentTransaction ft = Search.this.getSupportFragmentManager().beginTransaction();
//            TimingFragment fragmentDemo = TimingFragment.newInstance(object.getBusStopCode(),object.getDescription(),object.getRoadName());
//            ft.replace(R.id.nav_host_fragment, fragmentDemo).addToBackStack(null).commit();
            //ft.addToBackStack(null);
            //ft.commit();
        }
        else{
            Intent intent = new Intent(Search.this, BusRoute.class);
            intent.putExtra("ServiceNo", ((BusServiceClass) newList.get(position)).getServiceNo());
            imm.hideSoftInputFromWindow(Search.this.getCurrentFocus().getWindowToken(), 0);
            startActivity(intent);
//            BusServiceClass object = ((BusServiceClass) newList.get(position));
//            Log.d("HI", object.getServiceNo());
//            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//            BusRouteFragment busRouteFragment = BusRouteFragment.newInstance(object.getServiceNo(), null);
//            ft.replace(R.id.nav_host_fragment, busRouteFragment);
//            ft.addToBackStack(null);
//            ft.commit();
        }
    }

    @Override
    public void onFavouriteClick(int position) {

    }
}