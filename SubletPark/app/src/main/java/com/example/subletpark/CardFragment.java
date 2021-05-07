package com.example.subletpark;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment {

    public CardFragment() {
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Parking_Class> parkings;


    public CardFragment(List<Parking_Class> parkings) {
        this.parkings = parkings;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main_page, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_view_id);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        parkings = new ArrayList<>();



        ParkingAdapter parkingAdapter = new ParkingAdapter(parkings);

        recyclerView.setAdapter(parkingAdapter);


        return v;
    }


}
