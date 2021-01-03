package sg.tgonet.singtransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.R;


public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.MyViewHolder> {
    List<BusStopClass> BusStopList;
    OnItemListener onItemListener;

    public BusStopAdapter(List<BusStopClass> busStopList, OnItemListener onItemListener) {
        BusStopList = busStopList;
        this.onItemListener = onItemListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        TextView BusStopNo,StopDesc,RoadName,BusCode;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, final OnItemListener onItemListener) {
            super(itemView);

            this.BusStopNo = itemView.findViewById(R.id.BusStopNo);
            this.StopDesc = itemView.findViewById(R.id.StopDesc);
            this.RoadName = itemView.findViewById(R.id.RoadName);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public BusStopAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType== 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.busstoprouteitem, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.busstoproutemiddleitem, parent, false);
        }
        MyViewHolder myViewHolder = new MyViewHolder(view,onItemListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopAdapter.MyViewHolder holder, int position) {
        holder.BusStopNo.setText(BusStopList.get(position).getBusStopCode());
        holder.StopDesc.setText(BusStopList.get(position).getDescription());
        holder.RoadName.setText(BusStopList.get(position).getRoadName());
    }

    @Override
    public int getItemCount() {
        return  BusStopList.size();
    }

    public  void filteredList(List<BusStopClass> filtered){
        BusStopList = filtered;
        notifyDataSetChanged();
    }

    /*public void Update(ArrayList<BusStopClass> newlist,ArrayList<String> bList){
        BusStopList.clear();
        BusStopList.addAll(newlist);
        aList.clear();
        aList.addAll(bList);
        this.notifyDataSetChanged();
    }

    */
    public void Update(ArrayList<BusStopClass> newlist) {
        BusStopList.clear();
        BusStopList.addAll(newlist);
        this.notifyDataSetChanged();
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

    public interface OnItemListener{
        void onItemClick(int position);
    }

}

