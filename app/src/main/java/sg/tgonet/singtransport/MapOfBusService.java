package sg.tgonet.singtransport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.ui.home.HomeFragment;

public class MapOfBusService extends AppCompatActivity implements OnMapReadyCallback {

    ArrayList<BusStopClass> DisplayList;
    String BusStopCode;

    GoogleMap map;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_of_bus_service);
        DisplayList = (ArrayList<BusStopClass>) getIntent().getSerializableExtra("DisplayList");
        BusStopCode = getIntent().getStringExtra("BusStopCode");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng Singapore;
        int count = 0;
        Boolean check = false;
        //Singapore = new LatLng(1.3521,103.8198);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(Singapore,12));
        for (int i = 0; i < DisplayList.size(); i++) {

            if(BusStopCode != null && BusStopCode.equals(DisplayList.get(i).getBusStopCode()))
            {
                LatLng position = new LatLng(DisplayList.get(i).getLatitude(),DisplayList.get(i).getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,16));
                check = true;
            }

            count++;
            Singapore = new LatLng(DisplayList.get(i).getLatitude(), DisplayList.get(i).getLongitude());
            map.addMarker(new MarkerOptions().position(Singapore).title(String.valueOf(i + 1))).setTag(count);
        }
        if(!check){
            LatLng position = new LatLng(DisplayList.get(0).getLatitude(),DisplayList.get(0).getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,16));
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.isInfoWindowShown()){
                    marker.showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                }
                else{
                    marker.hideInfoWindow();
                }
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int position = (int) marker.getTag() - 1;
                Intent intent = new Intent(getApplicationContext(), Timing.class);
                intent.putExtra("BusStopCode", DisplayList.get(position).getBusStopCode());
                intent.putExtra("StopDesc", DisplayList.get(position).getDescription());
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v =  getLayoutInflater().inflate(R.layout.mapinfowindow, null);
                int latLng = (int)marker.getTag() - 1;

                BusStopClass i = DisplayList.get(latLng);

                TextView description = v.findViewById(R.id.description);
                TextView busStopCode = v.findViewById(R.id.BusStopCode);
                TextView roadName = v.findViewById(R.id.RoadName);

                description.setText(i.getDescription());
                busStopCode.setText(i.getBusStopCode());
                roadName.setText(i.getRoadName());
                return v;
            }
        });
    }
}