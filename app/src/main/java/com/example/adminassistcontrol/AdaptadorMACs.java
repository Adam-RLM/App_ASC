package com.example.adminassistcontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdaptadorMACs extends RecyclerView.Adapter<AdaptadorMACs.MyViewHolderMACs> {
    private MostrarMACs activity;
    ArrayList<MACs> maCsArrayList;
    private AdaptadorMACs.RecyclerViewClickListenerMACs listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdaptadorMACs(MostrarMACs activity, ArrayList<MACs> maCsArrayList, AdaptadorMACs.RecyclerViewClickListenerMACs listener) {
        this.activity = activity;
        this.maCsArrayList = maCsArrayList;
        this.listener = listener;
    }

    public void UpdateDatos(int position){
        MACs item = maCsArrayList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uId", item.getID());
        bundle.putString("ssid", item.getSSID());
        bundle.putString("mac", item.getMAC());

        Intent intent = new Intent(activity, UpdateMac.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    public void deleteDatos(final int position){
        MACs item = maCsArrayList.get(position);
        db.collection("Puntos de Acceso").document(item.getID()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            notifyRemovido(position);
                            Toast.makeText(activity, "¡MAC Eliminado!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void notifyRemovido(int position){
        maCsArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public AdaptadorMACs.MyViewHolderMACs onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_macs, parent, false);
        return new AdaptadorMACs.MyViewHolderMACs(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMACs.MyViewHolderMACs holder, int position) {
        MACs maCs = maCsArrayList.get(position);

        holder.tv_Macs.setText(maCs.getSSID());
    }

    @Override
    public int getItemCount() {
        return maCsArrayList.size();
    }

    public interface RecyclerViewClickListenerMACs {
        void onClick(View v, int position);
    }

    public class MyViewHolderMACs extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_Macs;
        public MyViewHolderMACs(@NonNull View itemView) {
            super(itemView);
            tv_Macs = itemView.findViewById(R.id.tv_MAC);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
