/**


package com.example.subletpark;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ParkingAdapter extends FirestoreRecyclerAdapter<Parking_Class,ParkingAdapter.ParkingHolder> {



    public ParkingAdapter(@NonNull FirestoreRecyclerOptions<Parking_Class> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ParkingHolder parkingHolder, int i, @NonNull Parking_Class parking_class) {
        parkingHolder.address.setText(parking_class.getAddress());
        parkingHolder.start_date.setText(parking_class.getStart_date());
        parkingHolder.end_date.setText(parking_class.getEnd_date());
        parkingHolder.descr.setText(parking_class.getDesc());
        parkingHolder.daily_price.setText(parking_class.getPrice());
        String imageUri1= parking_class.getUri();
        Picasso.with(getApplicationContext()).load(imageUri1).into(parking_pic);
);



    }

    @NonNull
    @Override
    public ParkingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    class ParkingHolder extends RecyclerView.ViewHolder{
        TextView address;
        TextView start_date;
        TextView end_date;
        TextView descr;
        TextView daily_price;
        ImageView parking_pic;
        Button contact_owner;

        public ParkingHolder(View itemView){
            super(itemView);
            address=itemView.findViewById(R.id.address);
            start_date=itemView.findViewById(R.id.start_date);
            end_date=itemView.findViewById(R.id.end_date);
            descr=itemView.findViewById(R.id.decr);
            daily_price=itemView.findViewById(R.id.daily_price);
            parking_pic=itemView.findViewById(R.id.parking_image);
            contact_owner=itemView.findViewById(R.id.contact_owner);



        }
    }



}

 **/
