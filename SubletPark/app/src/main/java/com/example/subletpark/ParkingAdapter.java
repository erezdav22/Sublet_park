
package com.example.subletpark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

    List<Parking_Class> parkings;

    public ParkingAdapter(List<Parking_Class> parkings) {
        this.parkings = parkings;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView address;
        TextView start_date;
        TextView end_date;
        TextView descr;
        TextView daily_price;
        ImageView parking_pic;
        Button contact_owner;

        public TextView getAddress() {
            return address;
        }

        public TextView getStart_date() {
            return start_date;
        }

        public TextView getEnd_date() {
            return end_date;
        }

        public TextView getDescr() {
            return descr;
        }

        public TextView getDaily_price() {
            return daily_price;
        }

        public ImageView getParking_pic() {
            return parking_pic;
        }

        public Button getContact_owner() {
            return contact_owner;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address=itemView.findViewById(R.id.address);
            start_date=itemView.findViewById(R.id.start_date);
            end_date=itemView.findViewById(R.id.end_date);
            descr=itemView.findViewById(R.id.decr);
            daily_price=itemView.findViewById(R.id.daily_price);
            parking_pic=itemView.findViewById(R.id.parking_image);
            contact_owner=itemView.findViewById(R.id.cardBtn);

            contact_owner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Position: "+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    @NonNull
    @Override
    public ParkingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
         */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingAdapter.ViewHolder holder, int position) {
        holder.getAddress().setText(parkings.get(position).getAddress());
        holder.getStart_date().setText(String.valueOf(parkings.get(position).getStart_date()));
        holder.getEnd_date().setText(String.valueOf(parkings.get(position).getEnd_date()));
        holder.getDescr().setText(parkings.get(position).getDesc());
        holder.getDaily_price().setText(parkings.get(position).getPrice());
        //holder.getParking_pic().setText(parkings.get(position).getUri());
    }

    @Override
    public int getItemCount() {
        return parkings.size();
    }
}
