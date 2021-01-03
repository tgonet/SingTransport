package sg.tgonet.singtransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.tgonet.singtransport.Class.BusStopClass;
import sg.tgonet.singtransport.Class.ListOfArrivalClass;
import sg.tgonet.singtransport.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemViewHolder> {

    //private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ListOfArrivalClass> itemList;
    private ArrivalAdapter.OnItemListener onItemListener;


    public HomeAdapter(List<ListOfArrivalClass> itemList, ArrivalAdapter.OnItemListener onItemListener) {
        this.itemList = itemList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favouritebusitem, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ListOfArrivalClass item = itemList.get(position);
        BusStopClass busStop = item.busStopClass;
        holder.BusStopNo.setText(busStop.getBusStopCode());
        holder.RoadName.setText(busStop.getRoadName());
        holder.StopDesc.setText(busStop.getDescription());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(item.arrivalClasses.size());

        // Create sub item view adapter
        ArrivalAdapter subItemAdapter = new ArrivalAdapter(item.arrivalClasses,onItemListener);

        holder.rvSubItem.setLayoutManager(layoutManager);
        holder.rvSubItem.setAdapter(subItemAdapter);
        //holder.rvSubItem.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder  {
        private TextView BusStopNo,StopDesc,RoadName;
        private RecyclerView rvSubItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.BusStopNo = itemView.findViewById(R.id.BusStopNo);
            this.StopDesc = itemView.findViewById(R.id.StopDesc);
            this.RoadName = itemView.findViewById(R.id.RoadName);
            rvSubItem = itemView.findViewById(R.id.rv_sub);
        }
    }

    public void update(ArrayList<ListOfArrivalClass>favouriteList){
        itemList = favouriteList;
    }
}
