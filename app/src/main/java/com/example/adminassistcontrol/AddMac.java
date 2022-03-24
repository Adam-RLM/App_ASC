package com.example.adminassistcontrol;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddMac extends AppCompatActivity {
    private static final String TAG = "AddMAC";
    ImageButton regresa;
    EditText addSsid, addMac;
    TextView ssid_Actual, mac_Actual;
    Button BTNaddMac;
    FirebaseFirestore db;
    String obt_MAC;
    String obt_SSID;
    int REQUESTCODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mac);
        regresa = findViewById(R.id.btn_regre);
        addSsid = findViewById(R.id.add_SSID);
        addMac = findViewById(R.id.add_MAC);
        BTNaddMac = findViewById(R.id.btn_add_Pacceso);
        ssid_Actual = findViewById(R.id.ssid_actual);
        mac_Actual = findViewById(R.id.mac_actual);
        db = FirebaseFirestore.getInstance();

        /*Verificar si está activo el GPS*/
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGPS();
            }
        }

        permisosm();

        getMacId();

        ssid_Actual.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // obtenemos el texto del textView3
                String text = ssid_Actual.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(AddMac.this, "SSID: " + text + " copiado.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mac_Actual.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // obtenemos el texto del textView3
                String text = mac_Actual.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(AddMac.this, "MAC: " + text + " copiado.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mac_Actual.setText(obt_MAC);
        ssid_Actual.setText(obt_SSID);

        regresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        BTNaddMac.setOnClickListener(new View.OnClickListener() {
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
                            String s_ssid = addSsid.getText().toString();
                            String s_mac = addMac.getText().toString();
                            String id = s_mac;

                            GuardarMAC(id, s_ssid, s_mac);
                        }
                    } else {
                        Toast activo = Toast.makeText(AddMac.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                        activo.show();
                    }
                } else {
                    Toast activo = Toast.makeText(AddMac.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                    activo.show();
                }
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

    private void GuardarMAC(String id, String s_ssid, String s_mac) {
        if (!s_ssid.isEmpty() && !s_mac.isEmpty()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ID", id);
            map.put("SSID", s_ssid);
            map.put("MAC", s_mac);
            db.collection("Puntos de Acceso").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddMac.this, "¡MAC Guardada!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddMac.this, MostrarMACs.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddMac.this, "Error al Guardar MAC.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Campos vacíos.", Toast.LENGTH_SHORT).show();
        }
    }

    public void AlertNoGPS() {
        AlertDialog alert;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS no está activado.\n\nPor razones de Seguridad en API 26 o mayor, es necesario activar el Hardware GPS aparte del Permiso de Ubicación para:\n\n1) Mostrar el SSID de la red a la que está conectado.\n" +
                "2) Reconocer la dirección MAC para poder Marcar Entrada/Salida.\n3)Poder ver tu SSID y MAC actual al agregar nuevo AP.\n\n¿Deseas Activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void permisosm() {
        int permiso_ubicacion = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permiso_ubicacion == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTCODE);
        }
    }

    /*Obtener MAC del enrutador*/
    public void getMacId() {
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

            if (mWifi.isConnected()) {
                tipoConexionWIFI = true;
            }
            if (tipoConexionWIFI == true) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d(TAG, wifiInfo.getBSSID());
                obt_MAC = wifiInfo.getBSSID();
                obt_SSID = wifiInfo.getSSID();
            }//fin comprobación internet
        } else {
            Log.d(TAG, "No hay conexión para obtener la MAC.");
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