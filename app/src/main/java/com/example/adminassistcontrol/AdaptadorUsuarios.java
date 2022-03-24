package com.example.adminassistcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.MyViewHolderusuarios>{
    Context context;
    ArrayList<Usuarios> usuariosArrayList;
    private AdaptadorUsuarios.RecyclerViewClickListenerusuarios listener;

    public AdaptadorUsuarios(Context context, ArrayList<Usuarios> usuariosArrayList, AdaptadorUsuarios.RecyclerViewClickListenerusuarios listener) {
        this.context = context;
        this.usuariosArrayList = usuariosArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdaptadorUsuarios.MyViewHolderusuarios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuarios, parent, false);
        return new AdaptadorUsuarios.MyViewHolderusuarios(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorUsuarios.MyViewHolderusuarios holder, int position) {

        Usuarios usuarios = usuariosArrayList.get(position);

        holder.tvPnombre.setText(usuarios.getPrimerNombre());
        holder.tvSnombre.setText(usuarios.getSegundoNombre());
        holder.tvPapellido.setText(usuarios.getPrimerApellido());
        holder.tvSapellido.setText(usuarios.getSegundoApellido());
    }

    @Override
    public int getItemCount() {
        return usuariosArrayList.size();
    }

    public interface RecyclerViewClickListenerusuarios{
        void onClick(View v, int position);
    }

    public class MyViewHolderusuarios extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvPnombre, tvSnombre, tvPapellido, tvSapellido;

        public MyViewHolderusuarios(@NonNull View itemView) {
            super(itemView);

            tvPnombre = itemView.findViewById(R.id.tv_primerNombre_usuario);
            tvSnombre = itemView.findViewById(R.id.tv_segundoNombre_usuario);
            tvPapellido = itemView.findViewById(R.id.tv_primerApellido_usuario);
            tvSapellido = itemView.findViewById(R.id.tv_segundoApellido_usuario);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
