package sg.tgonet.singtransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.R;

public class ListAdapter extends BaseAdapter {

    ArrayList<BusStopClass> BusStopList;

    public ListAdapter(ArrayList<BusStopClass> busStopList) {
        BusStopList = busStopList;
    }

    @Override
    public int getCount() {
        return BusStopList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        View view;
        TextView BusStopNo,StopDesc,RoadName;

        if(viewType== 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.busstoprouteitem, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.busstoproutemiddleitem, parent, false);
        }
        BusStopNo = view.findViewById(R.id.BusStopNo);
        StopDesc = view.findViewById(R.id.StopDesc);
        RoadName = view.findViewById(R.id.RoadName);
        BusStopNo.setText(BusStopList.get(position).getBusStopCode());
        StopDesc.setText(BusStopList.get(position).getDescription());
        RoadName.setText(BusStopList.get(position).getRoadName());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 1;
        }
        else{
            return 0;
        }
    }

    public void Update(ArrayList<BusStopClass> newlist) {
        BusStopList.clear();
        BusStopList.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
