package sg.tgonet.singtransport.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import sg.tgonet.singtransport.Adapter.BusStopServiceAdapter;
import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.Buses;
import sg.tgonet.singtransport.R;

import static android.content.Context.MODE_PRIVATE;


public class SearchFragment extends Fragment implements BusStopServiceAdapter.OnItemListener {

    EditText searchBar;
    ArrayList<BusServiceClass> BusNumberList;
    ArrayList<BusStopClass> BusStopList;
    RecyclerView rv;
    BusStopServiceAdapter adapter;
    ArrayList<Buses> newList;
    View v;

    public SearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();

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
        v = inflater.inflate(R.layout.fragment_search, container, false);
        searchBar = v.findViewById(R.id.searchBar);
        searchBar.requestFocus();
        searchBar.setFocusable(true);
        searchBar.setFocusableInTouchMode(true);
        BusNumberList = loadBusServiceList(getContext());
        BusStopList = loadBusStopList(getContext());
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

        rv = v.findViewById(R.id.Rv_search);
        CreateView();

        InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        return v;
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
                Log.d("HI", j.getServiceNo());
                newList.add(j);
            }
        }
        adapter.filteredList(newList);
        adapter.notifyDataSetChanged();
    }

    private void CreateView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
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
//            Intent intent = new Intent(getActivity(), Timing.class );
//            intent.putExtra("BusStopCode", ((BusStopClass) newList.get(position)).getBusStopCode());
//            intent.putExtra("StopDesc",((BusStopClass) newList.get(position)).getDescription());
//            intent.putExtra("RoadName",((BusStopClass) newList.get(position)).getRoadName());
//            startActivity(intent);
            BusStopClass object = ((BusStopClass) newList.get(position));
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            TimingFragment fragmentDemo = TimingFragment.newInstance(object.getBusStopCode(),object.getDescription(),object.getRoadName());
            ft.replace(R.id.nav_host_fragment, fragmentDemo).addToBackStack(null).commit();
            //ft.addToBackStack(null);
            //ft.commit();
        }
        else{
            /*Intent intent = new Intent(getActivity(), BusRoute.class);
            intent.putExtra("ServiceNo", ((BusServiceClass) newList.get(position)).getServiceNo());
            startActivity(intent);*/
            BusServiceClass object = ((BusServiceClass) newList.get(position));
            Log.d("HI", object.getServiceNo());
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            BusRouteFragment busRouteFragment = BusRouteFragment.newInstance(object.getServiceNo(), null);
            ft.replace(R.id.nav_host_fragment, busRouteFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onFavouriteClick(int position) {

    }
}