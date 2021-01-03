package sg.tgonet.singtransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.tgonet.singtransport.Class.Buses;
import sg.tgonet.singtransport.Class.BusServiceClass;
import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.R;


public class BusStopServiceAdapter extends RecyclerView.Adapter<BusStopServiceAdapter.MyViewHolder> {
    ArrayList<Buses> BusStopList;
    OnItemListener onItemListener;

    public static final int CLASS_TYPE_STOP = 0;
    public static final int CLASS_TYPE_SERVICE = 1;

    public BusStopServiceAdapter(ArrayList<Buses> busStopList, OnItemListener onItemListener) {
        BusStopList = busStopList;
        this.onItemListener = onItemListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        TextView BusStopNo,StopDesc,RoadName,ServiceNo;
        OnItemListener onItemListener;
        Button Favouritebtn;

        public MyViewHolder(@NonNull View itemView, final OnItemListener onItemListener) {
            super(itemView);

            this.ServiceNo = itemView.findViewById(R.id.ServiceNo);
            this.BusStopNo = itemView.findViewById(R.id.BusStopNo);
            this.StopDesc = itemView.findViewById(R.id.StopDesc);
            this.RoadName = itemView.findViewById(R.id.RoadName);
            this.onItemListener = onItemListener;
            //this.Favouritebtn = itemView.findViewById(R.id.Favourite);


            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public BusStopServiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.busstopitem, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.busserviceitem, parent, false);
        }
        MyViewHolder myViewHolder = new MyViewHolder(view,onItemListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopServiceAdapter.MyViewHolder holder, final int position) {
        if(getItemViewType(position) == 0){
            holder.BusStopNo.setText(((BusStopClass) BusStopList.get(position)).getBusStopCode());
            holder.StopDesc.setText(((BusStopClass) BusStopList.get(position)).getDescription());
            holder.RoadName.setText(((BusStopClass) BusStopList.get(position)).getRoadName());
            /*if(((BusStopClass) BusStopList.get(position)).getFavourite() == true){
                holder.Favouritebtn.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            }
            else{
                holder.Favouritebtn.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
            }
            holder.Favouritebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onFavouriteClick(position);
                }
            });*/
        }
        else{
            BusServiceClass item = (BusServiceClass) BusStopList.get(position);
            holder.ServiceNo.setText(item.getServiceNo());
        }

    }

    @Override
    public int getItemCount() {
        return  BusStopList.size();
    }

    public  void filteredList(ArrayList<Buses> filtered){
        BusStopList = filtered;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(BusStopList.get(position).getClass() == BusStopClass.class){
            return CLASS_TYPE_STOP;
        }
        else{
            return CLASS_TYPE_SERVICE;
        }
    }

    /*public void Update(ArrayList<BusStopClass> newlist,ArrayList<String> bList){
        BusStopList.clear();
        BusStopList.addAll(newlist);
        aList.clear();
        aList.addAll(bList);
        this.notifyDataSetChanged();
    }
    public void Update(ArrayList<BusStopClass> newlist){
        BusStopList.clear();
        BusStopList.addAll(newlist);
        this.notifyDataSetChanged();
    }*/

    public interface OnItemListener{
        void onItemClick(int position);
        void onFavouriteClick(int position);
    }

}

