package sg.tgonet.singtransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sg.tgonet.singtransport.R;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.MyViewHolder> {

    String[] NumberList;

    public NumberAdapter(String[] numberList) {
        NumberList = numberList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Busnumber;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            Busnumber = itemView.findViewById(R.id.busnumber);
        }
    }
    @NonNull
    @Override
    public NumberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.numberitem, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NumberAdapter.MyViewHolder holder, int position) {
        holder.Busnumber.setText(NumberList[position]);
    }

    @Override
    public int getItemCount() {
        return NumberList.length;
    }
}
