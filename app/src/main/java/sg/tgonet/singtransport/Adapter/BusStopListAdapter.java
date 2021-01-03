package sg.tgonet.singtransport.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.R;


public class BusStopListAdapter extends BaseExpandableListAdapter {

    private Context Context;
    private ArrayList<BusStopClass> Headers;
    private HashMap<BusStopClass, ArrayList<ArrivalClass>> ArrivalList;
    private ArrayList<String> Initials;
    private OnItemClickListener onItemClickListener;

    public android.content.Context getContext() {
        return Context;
    }

    public void setContext(android.content.Context context) {
        Context = context;
    }

    public ArrayList<BusStopClass> getHeaders() {
        return Headers;
    }

    public void setHeaders(ArrayList<BusStopClass> headers) {
        Headers = headers;
    }

    public HashMap<BusStopClass, ArrayList<ArrivalClass>> getArrivalList() {
        return ArrivalList;
    }

    public void setArrivalList(HashMap<BusStopClass, ArrayList<ArrivalClass>> arrivalList) {
        ArrivalList = arrivalList;
    }

    public ArrayList<String> getInitials() {
        return Initials;
    }

    public void setInitials(ArrayList<String> initials) {
        Initials = initials;
    }

    public BusStopListAdapter(android.content.Context context, ArrayList<BusStopClass> headers, HashMap<BusStopClass, ArrayList<ArrivalClass>> arrivalList, ArrayList<String> initials, OnItemClickListener onItemClickListener) {
        Context = context;
        Headers = headers;
        ArrivalList = arrivalList;
        Initials = initials;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getGroupCount() {
        return this.Headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ArrivalList.get(Headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return Headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ArrivalList.get(Headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        BusStopClass i  = (BusStopClass) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater infalInflater = (LayoutInflater) this.Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.busstopmapitem, parent,false);
        }

        /*int resourceId = getContext().getResources().getIdentifier("a" + i.getBusStopCode(), "array", getContext().getPackageName());
        String[] busNos = getContext().getResources().getStringArray(resourceId)[0].split(",");*/

        TextView StopDesc = convertView.findViewById(R.id.StopDesc);
        TextView BusCode = convertView.findViewById(R.id.BusCode);
        ImageView arrow = convertView.findViewById(R.id.expandarrow);
        /*RecyclerView rv = convertView.findViewById(R.id.rv_busstops);
        LinearLayoutManager layoutManager
                = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        NumberAdapter adapter = new NumberAdapter(busNos);
        rv.setAdapter(adapter);
        rv.setFocusable(false);
        rv.setClickable(false);*/

        StopDesc.setText(i.getDescription());
        BusCode.setText(Initials.get(groupPosition));
        if (isExpanded) {
            arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.downarrow));
        } else {
            arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rightarrow));
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ArrivalClass i = (ArrivalClass) getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater infalInflater = (LayoutInflater) this.Context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.arrivaltimingmapitem,parent,false);
        }
        TextView ServiceNo = convertView.findViewById(R.id.BusNumber);
        TextView Timing1 = convertView.findViewById(R.id.Timing1);
        TextView Timing2 = convertView.findViewById(R.id.Timing2);
        TextView Timing3 = convertView.findViewById(R.id.Timing3);
        Button alarm = convertView.findViewById(R.id.Alarm);

        ServiceNo.setText(i.getServiceNo());
        Timing1.setText(i.getTiming());
        Timing2.setText(i.getTiming2());
        Timing3.setText(i.getTiming3());
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onAlarmClick(groupPosition, childPosition);
            }
        });

        ServiceNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(groupPosition,childPosition);
            }
        });

        if(i.getTiming() == null || i.getTiming().equals("-")){
            Timing1.setText("-");
            Timing1.setTextColor(Color.parseColor("#000000"));
        }
        else{
            Timing1.setTextColor(TextColourChange(i.getLoad()));
        }
        if(i.getTiming2() == null || i.getTiming2().equals("-")){
            Timing2.setText("-");
            Timing2.setTextColor(Color.parseColor("#000000"));
        }
        else{
            Timing2.setTextColor(TextColourChange(i.getLoad2()));
        }
        if(i.getTiming3() == null || i.getTiming3().equals("-")){
            Timing3.setText("-");
            Timing3.setTextColor(Color.parseColor("#000000"));
        }
        else{
            Timing3.setTextColor(TextColourChange(i.getLoad3()));
        }

        Button Favourite = convertView.findViewById(R.id.Favourite);
        Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onFavouriteClick(groupPosition,childPosition);
            }
        });
        if(i.getFavourite() == true){
            Favourite.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
        }
        else{
            Favourite.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void Update(ArrayList<BusStopClass> newlist,ArrayList<String> bList,HashMap<BusStopClass, ArrayList<ArrivalClass>> arrivalList){
        Headers.clear();
        Headers.addAll(newlist);
        ArrivalList.clear();
        ArrivalList.putAll(arrivalList);
        Initials.clear();
        Initials.addAll(bList);
        this.notifyDataSetChanged();
    }
    public void Update(ArrayList<BusStopClass> newlist,HashMap<BusStopClass, ArrayList<ArrivalClass>> arrivalList){
        Headers.clear();
        Headers.addAll(newlist);
        ArrivalList.clear();
        ArrivalList.putAll(arrivalList);
        this.notifyDataSetChanged();
    }

    public void updateitem(ArrayList<ArrivalClass> list,int position){
        ArrivalList.put(Headers.get(position),list);
        this.notifyDataSetChanged();
    }

    public int TextColourChange(String load){
        if(load.equals("SEA")){
            return Color.parseColor("#329932");
        }
        else if(load.equals("SDA")){
            return Color.parseColor("#ffa500");
        }
        else{
            return Color.parseColor("#ff3232");
        }
    }

    public interface OnItemClickListener{
        void onFavouriteClick(int groupPosition, int childPosition);
        void onAlarmClick(int groupPosition, int childPosition);
        void onItemClick(int groupPosition, int childPosition);
    }
}
