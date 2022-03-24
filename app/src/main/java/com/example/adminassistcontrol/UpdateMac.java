package com.example.adminassistcontrol;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UpdateMac extends AppCompatActivity {
    private String uId, Ssid, Mac;
    EditText ed_SSID, ed_MAC;
    Button btn_update;
    ImageButton regresa;
    FirebaseFirestore db;
    MostrarMACs obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mac);
        regresa = findViewById(R.id.btn_regre);
        ed_SSID = findViewById(R.id.edit_SSID);
        ed_MAC = findViewById(R.id.edit_MAC);
        btn_update = findViewById(R.id.btn_actualizar_MAC);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            btn_update.setText("Actualizar");
            uId = bundle.getString("uId");
            Ssid = bundle.getString("ssid");
            Mac = bundle.getString("mac");

            ed_SSID.setText(Ssid);
            ed_MAC.setText(Mac);
        } else {
            btn_update.setText("Update");
        }

        regresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                Intent intent = new Intent(UpdateMac.this, MostrarMACs.class);
                startActivity(intent);
                finish();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean online = internetIsConnected();
                /*Variables Comprobación de internet*/
                ConnectivityManager cm;
                NetworkInfo ni;
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                ni = cm.getActiveNetworkInfo();
                boolean tipoConexionWIFI = false;
                /*--------------------------------------------------*/
                if (ni != null) {
                    ConnectivityManager connManager1 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (mWifi.isConnected() && online) {
                        tipoConexionWIFI = true;
                        if (tipoConexionWIFI == true) {
                            String ssid = ed_SSID.getText().toString();
                            String mac = ed_MAC.getText().toString();

                            if (!ssid.isEmpty() && !mac.isEmpty()) {
                                Bundle bundle1 = getIntent().getExtras();
                                if (bundle1 != null) {
                                    String id = uId;
                                    db.collection("Puntos de Acceso").document(id).delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(UpdateMac.this, "", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(UpdateMac.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    UpdateDatosMAC(id, ssid, mac);
                                } else {
                                    Log.d("UpdateMAC", "sin datos para actualizar MAC.");
                                }
                            } else {
                                Toast.makeText(UpdateMac.this, "Campos vacíos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast activo = Toast.makeText(UpdateMac.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                        activo.show();
                    }
                } else {
                    Toast activo = Toast.makeText(UpdateMac.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                    activo.show();
                }
            }
        });
    }

    private void UpdateDatosMAC(String id, String ssid, String mac) {
        HashMap<String, Object> map = new HashMap<>();
        id = mac;
        map.put("ID", mac);
        map.put("SSID", ssid);
        map.put("MAC", mac);

        db.collection("Puntos de Acceso").document(mac).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateMac.this, "¡MAC Actualizada!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateMac.this, MostrarMACs.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateMac.this, "Error al Actualizar MAC.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*Comprobar que la red no está Limitada*/
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
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