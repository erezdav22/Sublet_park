package com.example.subletpark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ParkingAdapter2 extends FirestoreRecyclerAdapter<ParkTest,ParkingAdapter2.ViewHolder> {



    public ParkingAdapter2(@NonNull FirestoreRecyclerOptions<ParkTest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull ParkTest parkTest) {
        viewHolder.getAddress().setText(parkTest.getAddress());
        viewHolder.getDesc().setText(parkTest.getDesc());
        viewHolder.getDaily_price().setText(parkTest.getPrice());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_card,
                parent,false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        TextView desc;
        TextView daily_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address=itemView.findViewById(R.id.address);
            desc=itemView.findViewById(R.id.desc);
            daily_price=itemView.findViewById(R.id.daily_price);


        }

        public TextView getAddress() {
            return address;
        }


        public TextView getDesc() {
            return desc;
        }

        public TextView getDaily_price() {
            return daily_price;
        }





    }
}
