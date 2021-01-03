package sg.tgonet.singtransport.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import sg.tgonet.singtransport.Adapter.BusStopAdapter;
import sg.tgonet.singtransport.Adapter.ListAdapter;
import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.FragmentListener;
import sg.tgonet.singtransport.MainActivity;
import sg.tgonet.singtransport.MapOfBusService;
import sg.tgonet.singtransport.R;
import sg.tgonet.singtransport.Timing;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class BusRouteFragment extends Fragment implements BusStopAdapter.OnItemListener {

    TextView BusNumber,To,From;
    Button routesExchange,viewMap;
    int resourceId;
    String[] BusRouteString;
    String direction,serviceNo,BusStopCode;
    ArrayList<BusStopClass> BusStopList,DisplayList;
    RecyclerView rv;
    BusStopAdapter adapter;
    boolean check;

    public BusRouteFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BusRouteFragment newInstance(String ServiceNo, String BusStopCode) {
        BusRouteFragment fragment = new BusRouteFragment();
        Bundle args = new Bundle();
        args.putString("ServiceNo", ServiceNo);
        args.putString("BusStopCode", BusStopCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            serviceNo = getArguments().getString("ServiceNo");
            BusStopCode = getArguments().getString("BusStopCode");
        }

        View v = inflater.inflate(R.layout.fragment_bus_route, container, false);
        check = true;
        direction = "a";

        BusStopList = SearchFragment.loadBusStopList(getContext());
        DisplayList = getDisplayList(direction + serviceNo,BusStopCode);

        BusNumber = v.findViewById(R.id.BusNumber);
        BusNumber.setText(serviceNo);

        To = v.findViewById(R.id.To);
        To.setText(DisplayList.get(0).getDescription());

        From = v.findViewById(R.id.From);
        From.setText(DisplayList.get(DisplayList.size()-1).getDescription());

        routesExchange = v.findViewById(R.id.routes_exchange);
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

                DisplayList = getDisplayList(direction + serviceNo,null);
                adapter.Update(DisplayList);
                To.setText(DisplayList.get(0).getDescription());
                From.setText(DisplayList.get(DisplayList.size()-1).getDescription());
            }
        });

        viewMap = v.findViewById(R.id.ViewMapBtn);
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapOfBusService.class);
                intent.putExtra("DisplayList",DisplayList);
                intent.putExtra("BusStopCode",BusStopCode);
                startActivity(intent);
            }
        });

        rv = v.findViewById(R.id.rv_BusRoute);
        CreateView();

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if( keyCode == KeyEvent.KEYCODE_BACK )
                    {
                        getActivity().onBackPressed();
                        return true;
                    }
                }

                return false;
            }
        } );

        return v;
    }

    private void CreateView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BusStopAdapter(DisplayList, this);
        rv.setAdapter(adapter);
    }

    public ArrayList<BusStopClass> getDisplayList(String busService,String busStopCode){
        ArrayList<BusStopClass> DisplayList = new ArrayList<>();

        if(busStopCode == null){

            resourceId = this.getResources().getIdentifier(busService, "array", getActivity().getPackageName());

            try {
                BusRouteString = getResources().getStringArray(resourceId);
            } catch (Resources.NotFoundException e) {
                resourceId = this.getResources().getIdentifier("a" + busService.substring(1), "array", getActivity().getPackageName());
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
            resourceId = this.getResources().getIdentifier("a" + busService.substring(1), "array", getActivity().getPackageName());
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
                resourceId = this.getResources().getIdentifier("b" + busService.substring(1), "array", getActivity().getPackageName());
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
       /*Intent intent = new Intent(getActivity(), Timing.class);
        intent.putExtra("StopDesc",DisplayList.get(position).getDescription());
        intent.putExtra("BusStopCode",DisplayList.get(position).getBusStopCode());
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        TimingFragment fragmentDemo = TimingFragment.newInstance(DisplayList.get(position).getBusStopCode(),DisplayList.get(position).getDescription(),DisplayList.get(position).getRoadName());
        ft.add(R.id.nav_host_fragment, fragmentDemo).addToBackStack(null).commit();
        //ft.addToBackStack(null);
        //ft.commit();
    }
}