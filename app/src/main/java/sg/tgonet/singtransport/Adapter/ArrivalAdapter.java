package sg.tgonet.singtransport.Adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.tgonet.singtransport.Class.ArrivalClass;
import sg.tgonet.singtransport.R;


public class ArrivalAdapter extends RecyclerView.Adapter<ArrivalAdapter.MyViewHolder> {

    List<ArrivalClass> ArrivalList;
    OnItemListener onItemListener;

    public ArrivalAdapter(List<ArrivalClass> arrivalList, OnItemListener onItemListener) {
        ArrivalList = arrivalList;
        this.onItemListener = onItemListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ServiceNo,Timing1,Timing2,Timing3;
        OnItemListener onItemListener;
        Button Alarm,Favourite;


        public MyViewHolder(@NonNull final View itemView, final OnItemListener onItemListener) {
            super(itemView);

            this.ServiceNo = itemView.findViewById(R.id.BusNumber);
            this.Timing1 = itemView.findViewById(R.id.Timing1);

            Timing1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onTimingClick(getAdapterPosition());
                }
            });
            this.Timing2 = itemView.findViewById(R.id.Timing2);
            Timing2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onTimingClick(getAdapterPosition());
                }
            });
            this.Timing3 = itemView.findViewById(R.id.Timing3);
            Timing3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onTimingClick(getAdapterPosition());
                }
            });
            this.onItemListener = onItemListener;

            this.Alarm = itemView.findViewById(R.id.Alarm);
            Alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onAlarmClick(getAdapterPosition(),ArrivalList.get(getAdapterPosition()));
                }
            });
            this.Favourite = itemView.findViewById(R.id.Favourite);
            Favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onFavouriteClick(getAdapterPosition(),ArrivalList.get(getAdapterPosition()));
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition(),ArrivalList.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public ArrivalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrivaltimingitem, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view,onItemListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArrivalAdapter.MyViewHolder holder, int position) {
        holder.ServiceNo.setText(ArrivalList.get(position).getServiceNo());
        holder.Timing1.setText(ArrivalList.get(position).getTiming()) ;
        holder.Timing2.setText(ArrivalList.get(position).getTiming2());
        holder.Timing3.setText(ArrivalList.get(position).getTiming3());

        if(ArrivalList.get(position).getTiming() != null && !ArrivalList.get(position).getTiming().equals("-")){
            holder.Timing1.setTextColor(TextColourChange(ArrivalList.get(position).getLoad()));
        }
        else{
            holder.Timing1.setText("-");
            holder.Timing1.setTextColor(Color.parseColor("#000000"));
        }
        if(ArrivalList.get(position).getTiming2() != null && !ArrivalList.get(position).getTiming2().equals("-")){
            holder.Timing2.setTextColor(TextColourChange(ArrivalList.get(position).getLoad2()));

        }
        else{
            holder.Timing2.setText("-");
            holder.Timing2.setTextColor(Color.parseColor("#000000"));
        }
        if(ArrivalList.get(position).getTiming3() != null && !ArrivalList.get(position).getTiming3().equals("-")){
            holder.Timing3.setTextColor(TextColourChange(ArrivalList.get(position).getLoad3()));
        }
        else{
            holder.Timing3.setText("-");
            holder.Timing3.setTextColor(Color.parseColor("#000000"));
        }

        if(ArrivalList.get(position).getFavourite() != false){
            holder.Favourite.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
        }
        else{
            holder.Favourite.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return ArrivalList.size();
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

    public interface OnItemListener{
        void onItemClick(int position, ArrivalClass Service);
        void onTimingClick(int position);
        void onAlarmClick(int position,ArrivalClass Service);
        void onFavouriteClick(int position,ArrivalClass Service);
    }
}