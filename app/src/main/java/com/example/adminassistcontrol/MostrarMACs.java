package com.example.adminassistcontrol;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MostrarMACs extends AppCompatActivity {
    RecyclerView recyclerMAC;
    ArrayList<MACs> maCsArrayList;
    AdaptadorMACs myAdapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton regresa;
    String userID;
    private AdaptadorMACs.RecyclerViewClickListenerMACs listenermacs;
    FloatingActionButton addMAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_m_a_cs);

        regresa = findViewById(R.id.btn_regre);
        recyclerMAC = findViewById(R.id.recyclerMACs);
        addMAC = findViewById(R.id.btn_floating_agregar_mac);

        setOnClickListener();
        recyclerMAC.setHasFixedSize(true);
        recyclerMAC.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        maCsArrayList = new ArrayList<MACs>();
        myAdapter = new AdaptadorMACs(MostrarMACs.this, maCsArrayList, listenermacs);
        recyclerMAC.setAdapter(myAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(MostrarMACs.this, myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerMAC);
        EventChangeListener();

        regresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        addMAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MostrarMACs.this, AddMac.class);
                startActivity(intent);
                onPause();
            }
        });
    }

    public void EventChangeListener(){
        db.collection("Puntos de Acceso")
                .orderBy("SSID", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!= null){
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                maCsArrayList.add(dc.getDocument().toObject(MACs.class));
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void setOnClickListener() {
        listenermacs = new AdaptadorMACs.RecyclerViewClickListenerMACs() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), MostrandoMACs.class);
                intent.putExtra("mac", maCsArrayList.get(position).getMAC());
                intent.putExtra("ssid", maCsArrayList.get(position).getSSID());

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