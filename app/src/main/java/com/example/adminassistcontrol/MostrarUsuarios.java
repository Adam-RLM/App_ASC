package com.example.adminassistcontrol;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MostrarUsuarios extends AppCompatActivity {
    RecyclerView recyclerusuario;
    ArrayList<Usuarios> usuariosArrayList;
    AdaptadorUsuarios myAdapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton regresa;
    String userID;
    private AdaptadorUsuarios.RecyclerViewClickListenerusuarios listenerusuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_usuarios);
        regresa = findViewById(R.id.btn_regre);
        recyclerusuario = findViewById(R.id.recyclerUsuarios);
        setOnClickListener();
        recyclerusuario.setHasFixedSize(true);
        recyclerusuario.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        usuariosArrayList = new ArrayList<Usuarios>();
        myAdapter = new AdaptadorUsuarios(MostrarUsuarios.this, usuariosArrayList, listenerusuarios);

        recyclerusuario.setAdapter(myAdapter);

        EventChangeListener();
        regresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    public void EventChangeListener(){
        db.collection("UNAN, León")
                .orderBy("PrimerNombre", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!= null){
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                usuariosArrayList.add(dc.getDocument().toObject(Usuarios.class));
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void setOnClickListener() {
        listenerusuarios = new AdaptadorUsuarios.RecyclerViewClickListenerusuarios() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), MostrandoUsuarios.class);
                intent.putExtra("pnombre", usuariosArrayList.get(position).getPrimerNombre());
                intent.putExtra("snombre", usuariosArrayList.get(position).getSegundoNombre());
                intent.putExtra("papellido", usuariosArrayList.get(position).getPrimerApellido());
                intent.putExtra("sapellido", usuariosArrayList.get(position).getSegundoApellido());
                intent.putExtra("email", usuariosArrayList.get(position).getEmail());
                intent.putExtra("fnacimiento", usuariosArrayList.get(position).getFechaNacimiento());
                intent.putExtra("sexo", usuariosArrayList.get(position).getSexo());
                boolean aux = usuariosArrayList.get(position).isAdministrador();
                if (aux){
                    String ad = "Administrador";
                    intent.putExtra("admin", ad);
                }else {
                    String noad = "";
                    intent.putExtra("admin", noad);
                }

                startActivity(intent);
            }
        };
    }

    /*Función para ocultar la barra de navegación*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        /*| View.SYSTEM_UI_FLAG_FULLSCREEN);*/
    }
    /*----------------------------------------------------------------------*/
}