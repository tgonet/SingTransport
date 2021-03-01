package sg.tgonet.singtransport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import sg.tgonet.singtransport.Adapter.BusStopAdapter;
import sg.tgonet.singtransport.Adapter.BusStopServiceAdapter;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.Buses;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class BusRoute extends AppCompatActivity implements BusStopAdapter.OnItemListener{

    TextView BusNumber,To,From;
    Button routesExchange,viewMap,Back;
    int resourceId;
    String[] BusRouteString;
    String direction,serviceNo,BusStopCode;
    ArrayList<BusStopClass> BusStopList,DisplayList;
    RecyclerView rv;
    BusStopAdapter adapter;
    boolean check;
    Lib lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        getWindow().setStatusBarColor(ContextCompat.getColor(BusRoute.this, R.color.colorPrimary));

        lib = new Lib(getApplicationContext(),this);

        serviceNo = getIntent().getStringExtra("ServiceNo");
        BusStopCode = getIntent().getStringExtra("BusStopCode");
        check = true;
        direction = "a";

        BusStopList = lib.loadBusStopList(getApplicationContext(),this);
        DisplayList = getDisplayList(direction + serviceNo,BusStopCode);

        BusNumber = findViewById(R.id.BusNumber);
        BusNumber.setText(serviceNo);

        To = findViewById(R.id.To);
        To.setText(DisplayList.get(0).getDescription());

        From = findViewById(R.id.From);
        From.setText(DisplayList.get(DisplayList.size()-1).getDescription());

        routesExchange = findViewById(R.id.routes_exchange);
        routesExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    direction = "b";
                    check = false;
                }
                else{
                    direction= "a";
                    check = true;
                }
                DisplayList = getDisplayList(direction+serviceNo,null);
                adapter.Update(DisplayList);
                To.setText(DisplayList.get(0).getDescription());
                From.setText(DisplayList.get(DisplayList.size()-1).getDescription());
            }
        });

        Back = findViewById(R.id.BusRoute_back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewMap = findViewById(R.id.ViewMapBtn);
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapOfBusService.class);
                intent.putExtra("DisplayList",DisplayList);
                intent.putExtra("BusStopCode",BusStopCode);
                startActivity(intent);
            }
        });

        rv = findViewById(R.id.rv_BusRoute);
        CreateView();
    }

    private void CreateView() {
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new BusStopAdapter(DisplayList, this);
        rv.setAdapter(adapter);
    }

    public ArrayList<BusStopClass> getDisplayList(String busService,String busStopCode){
        ArrayList<BusStopClass> DisplayList = new ArrayList<>();

        if(busStopCode == null){
            resourceId = this.getResources().getIdentifier(busService, "array", BusRoute.this.getPackageName());

            try {
                BusRouteString = getResources().getStringArray(resourceId);
            } catch (Resources.NotFoundException e) {
                resourceId = this.getResources().getIdentifier("a" + busService.substring(1), "array", BusRoute.this.getPackageName());
                BusRouteString = getResources().getStringArray(resourceId);
            }

            for(String i :BusRouteString) {
                String busstopcode = i.split(",")[1];
                for (BusStopClass j : BusStopList) {
                    if (j.getBusStopCode().equals(busstopcode)) {
                        //Log.d("HIHI", j.getDescription());
                        DisplayList.add(j);
                        break;
                    }
                }
            }
        }
        else{
            resourceId = this.getResources().getIdentifier("a" + busService.substring(1), "array", BusRoute.this.getPackageName());

            BusRouteString = getResources().getStringArray(resourceId);

            boolean check = false;
            this.check = true;

            for(String i :BusRouteString){
                String busstopcode = i.split(",")[1];
                for(BusStopClass j : BusStopList){
                    if(busstopcode.equals(busStopCode)){
                        check = true;
                    }
                    if(j.getBusStopCode().equals(busstopcode)){
                        DisplayList.add(j);
                        break;
                    }
                }
            }

            if(!check){
                this.check = false;
                DisplayList.clear();
                resourceId = this.getResources().getIdentifier("b" + busService.substring(1), "array", BusRoute.this.getPackageName());
                BusRouteString = getResources().getStringArray(resourceId);

                for(String i :BusRouteString) {
                    String busstopcode = i.split(",")[1];
                    for (BusStopClass j : BusStopList) {
                        if (j.getBusStopCode().equals(busstopcode)) {
                            DisplayList.add(j);
                            break;
                        }
                    }
                }

            }

        }
        return DisplayList;
    }
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this,Timing.class);
        intent.putExtra("StopDesc",DisplayList.get(position).getDescription());
        intent.putExtra("BusStopCode",DisplayList.get(position).getBusStopCode());
        Log.d("HISSS",DisplayList.get(position).getBusStopCode());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }
}