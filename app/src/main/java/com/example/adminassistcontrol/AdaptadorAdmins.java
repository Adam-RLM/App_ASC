package com.example.adminassistcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorAdmins extends RecyclerView.Adapter<AdaptadorAdmins.MyViewHolderadmin>   {
    Context context;
    ArrayList<Administradores> administradoresArrayList;
    private RecyclerViewClickListeneradmin listener;

    public AdaptadorAdmins(Context context, ArrayList<Administradores> administradoresArrayList, RecyclerViewClickListeneradmin listener) {
        this.context = context;
        this.administradoresArrayList = administradoresArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdaptadorAdmins.MyViewHolderadmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new MyViewHolderadmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAdmins.MyViewHolderadmin holder, int position) {

        Administradores administradores = administradoresArrayList.get(position);

        holder.tvPnombre.setText(administradores.getPrimerNombre());
        holder.tvSnombre.setText(administradores.getSegundoNombre());
        holder.tvPapellido.setText(administradores.getPrimerApellido());
        holder.tvSapellido.setText(administradores.getSegundoApellido());
    }

    @Override
    public int getItemCount() {
        return administradoresArrayList.size();
    }

    public interface RecyclerViewClickListeneradmin{
        void onClick(View v, int position);
    }

    public class MyViewHolderadmin extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvPnombre, tvSnombre, tvPapellido, tvSapellido;

        public MyViewHolderadmin(@NonNull View itemView) {
            super(itemView);

            tvPnombre = itemView.findViewById(R.id.tv_primerNombre);
            tvSnombre = itemView.findViewById(R.id.tv_segundoNombre);
            tvPapellido = itemView.findViewById(R.id.tv_primerApellido);
            tvSapellido = itemView.findViewById(R.id.tv_segundoApellido);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
