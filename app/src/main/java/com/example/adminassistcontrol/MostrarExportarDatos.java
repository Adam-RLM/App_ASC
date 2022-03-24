package com.example.adminassistcontrol;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.instacart.library.truetime.TrueTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.widget.Toast.LENGTH_SHORT;

public class MostrarExportarDatos extends AppCompatActivity {
    ImageButton regresa;
    ProgressBar progressBar;
    private String[] header = {"No.", "Nombre Completo", "Entrada 1", "Salida 1", "Entrada 2", "Salida2"};
    private String[] header2 = {"No.", "Nombre Completo"};
    private String[] header3 = {"No.", "Nombre Completo", "Hora", "Justificación"};
    private String shortText = "Datos:";
    private String longText = "Datos de Asistencia diaria por cada Usuario.";
    private String longText2 = "Datos de Usuarios que no Marcaron.";

    TextView fecha;
    CalendarView registro;
    String fecha_calendario = "";
    String fecha_calendario2 = "";
    TemplatePDF templatePDF;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;
    String userID;
    int i = 1, j = 1, k = 1;
    String s_nombreCompleto = "", s_entrada1 = "", s_salida1 = "", s_entrada2 = "", s_salida2 = "", s_hora = "", s_justificacion = "";
    String hora, day, month, year, s_fecha;
    String nombre_entero = "", nombre1 = "", nombre2 = "", apellido1 = "", apellido2 = "", usuarioNM = "", usuario = "", estado3 = "0";
    Date trueTime;
    Date hora_dispositivo;
    ArrayList<String[]> usuarios;
    ArrayList<String[]> usuarios_no_marcaron = new ArrayList<>();
    ArrayList<String[]> justificacion;
    private String s_ano;
    private String s_mes;
    private String s_dia;
    String Nombre = "";
    String usuario1 = "";
    String usuario2 = "";
    Button v_PDF, v_PDF_APP;
    CardView cardView;
    DocumentReference marcaron;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_exportar_datos);
        progressBar = findViewById(R.id.progressBarExportar);
        v_PDF = findViewById(R.id.b_ver_PDF);
        v_PDF_APP = findViewById(R.id.b_ver_PDF_App);
        cardView = findViewById(R.id.cardViewED);

        iniciandoTruetime();

        hora = getString(R.string.tt_time_gmt, _formatDate(trueTime, "HH:mm:ss", TimeZone.getTimeZone("GMT-06:00")));
        day = getString(R.string.tt_time_gmt, _formatDate(trueTime, "d", TimeZone.getTimeZone("GMT-06:00")));
        month = getString(R.string.tt_time_gmt, _formatDate(trueTime, "M", TimeZone.getTimeZone("GMT-06:00")));
        year = getString(R.string.tt_time_gmt, _formatDate(trueTime, "yyyy", TimeZone.getTimeZone("GMT-06:00")));

        s_fecha = day + "" + month + "" + year;

        Log.d("FECHA", s_fecha);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1000);

        }

        regresa = findViewById(R.id.btn_regre);
        fecha = findViewById(R.id.fecha_seleccionada);
        registro = findViewById(R.id.calendario_registro);

        /*Obteniendo datos de firebase*/
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        regresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        registro.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                fecha_calendario = dayOfMonth + "" + (month + 1) + "" + year + "";
                fecha_calendario2 = dayOfMonth + "/" + (month + 1) + "/" + year + "";

                Log.d("MainActivity", "Mi fecha: " + fecha_calendario);
                Log.d("MainActivity", "Mi fecha 2: " + fecha_calendario2);

                datos();
                //datosNoMarcaron();
                justificaciones();
                porSiNoMarcaronNadaTodavia();

                fecha.setText(fecha_calendario2);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void pdfView(View view) {
        boolean online = internetIsConnected();
        progressBar.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);
        v_PDF.setEnabled(false);
        v_PDF_APP.setEnabled(false);

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
                    if (fecha_calendario.equals("")) {
                        v_PDF.setEnabled(true);
                        v_PDF_APP.setEnabled(true);
                        cardView.setVisibility(View.GONE);
                        progressBar.setVisibility(progressBar.GONE);
                        Toast.makeText(MostrarExportarDatos.this, "Seleccione una Fecha.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                v_PDF.setEnabled(true);
                                v_PDF_APP.setEnabled(true);
                                cardView.setVisibility(View.GONE);
                                progressBar.setVisibility(progressBar.GONE);
                                agregando();
                                templatePDF.viewPDF();
                            }
                        }, 4000);
                    }
                }//fin comprobación internet
            } else {
                v_PDF.setEnabled(true);
                v_PDF_APP.setEnabled(true);
                cardView.setVisibility(View.GONE);
                progressBar.setVisibility(progressBar.GONE);
                Toast activo = Toast.makeText(MostrarExportarDatos.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                activo.show();
            }
        } else {
            v_PDF.setEnabled(true);
            v_PDF_APP.setEnabled(true);
            cardView.setVisibility(View.GONE);
            progressBar.setVisibility(progressBar.GONE);
            Toast activo = Toast.makeText(MostrarExportarDatos.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
            activo.show();
        }
    }

    public void pdfApp(View view) {
        boolean online = internetIsConnected();
        progressBar.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);
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
                    if (fecha_calendario.equals("")) {
                        cardView.setVisibility(View.GONE);
                        progressBar.setVisibility(progressBar.GONE);
                        Toast.makeText(MostrarExportarDatos.this, "Seleccione una Fecha.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(progressBar.GONE);
                                cardView.setVisibility(View.GONE);
                                agregando();
                                templatePDF.appViewPDF(MostrarExportarDatos.this);
                            }
                        }, 4000);
                    }
                }//fin comprobación internet
            } else {
                progressBar.setVisibility(progressBar.GONE);
                cardView.setVisibility(View.GONE);
                Toast activo = Toast.makeText(MostrarExportarDatos.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                activo.show();
            }
        } else {
            progressBar.setVisibility(progressBar.GONE);
            cardView.setVisibility(View.GONE);
            Toast activo = Toast.makeText(MostrarExportarDatos.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
            activo.show();
        }
    }

    public void agregando() {
        templatePDF = new TemplatePDF(getApplicationContext());

        templatePDF.openDocument(fecha_calendario);

        templatePDF.addTitles("Assist Control", "Datos de Entrada y Salida por Usuario de la fecha " + fecha_calendario2, s_fecha, hora);
        templatePDF.addMetaData("AssistControl", "Marcaje", "Adan Rainerio López Maradiaga");
        templatePDF.addParagraph(shortText);
        templatePDF.addParagraph(longText);
        templatePDF.createTable(header, datos());
        templatePDF.addParagraph(longText2);
        templatePDF.createTableNoMarca(header2, porSiNoMarcaronNadaTodavia());
        templatePDF.createTableJustificaciones(header3, justificaciones());
        templatePDF.closeDocument();
    }

    public ArrayList<String[]> datos() {
        db.collection("UNAN, León")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }

                        usuarios = new ArrayList<>();

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String aux = dc.getDocument().getId();
                                db.collection("UNAN, León")
                                        .document(aux)
                                        .collection("Entrada")
                                        .document(fecha_calendario)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        s_nombreCompleto = document.getString("NombreCompleto");
                                                        s_entrada1 = document.getString("Hora");
                                                        s_salida1 = document.getString("HoraSalida");
                                                        s_entrada2 = document.getString("HoraEntrada2");
                                                        s_salida2 = document.getString("HoraSalida2");

                                                        if (s_entrada1 == "" || s_entrada1 == null) {
                                                            Log.d("Information sin nada=> ", s_nombreCompleto + ": No marcó Entrada/Salida");
                                                            return;
                                                        } else if (s_salida1 == "" || s_salida1 == null) {
                                                            s_salida1 = " ";
                                                            s_entrada2 = " ";
                                                            s_salida2 = " ";
                                                            Log.d("Information SS1 => ", s_nombreCompleto + " = " + s_entrada1 + " = " + s_salida1 + " = " + s_entrada2 + " = " + s_salida2);
                                                            usuarios.add(new String[]{i++ + "", s_nombreCompleto, s_entrada1, s_salida1, s_entrada2, s_salida2});
                                                            return;
                                                        } else if (s_entrada2 == "" || s_entrada2 == null) {
                                                            s_entrada2 = " ";
                                                            s_salida2 = " ";
                                                            Log.d("Information SE2 => ", s_nombreCompleto + " = " + s_entrada1 + " = " + s_salida1 + " = " + s_entrada2 + " = " + s_salida2);
                                                            usuarios.add(new String[]{i++ + "", s_nombreCompleto, s_entrada1, s_salida1, s_entrada2, s_salida2});
                                                            return;
                                                        } else if (s_salida2 == "" || s_salida2 == null) {
                                                            s_salida2 = " ";
                                                            Log.d("Information SS2 => ", s_nombreCompleto + " = " + s_entrada1 + " = " + s_salida1 + " = " + s_entrada2 + " = " + s_salida2);
                                                            usuarios.add(new String[]{i++ + "", s_nombreCompleto, s_entrada1, s_salida1, s_entrada2, s_salida2});
                                                            return;
                                                        } else {
                                                            Log.d("Information => ", s_nombreCompleto + " = " + s_entrada1 + " = " + s_salida1 + " - " + s_entrada2 + " - " + s_salida2);
                                                            usuarios.add(new String[]{i++ + "", s_nombreCompleto, s_entrada1, s_salida1, s_entrada2, s_salida2});
                                                        }
                                                    } else {
                                                        Log.d("FalloUsuario", "No está el archivo.");
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                        i = 1;
                    }
                });

        return usuarios;
    }

    public ArrayList<String[]> datosNoMarcaron() {
        db.collection("UNAN, León").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firestore Error", e.getMessage());
                    return;
                }

                usuarios_no_marcaron = new ArrayList<>();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        usuario = dc.getDocument().getId();
                        db.collection("UNAN, León").document(usuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        nombre1 = document.getString("PrimerNombre");
                                        nombre2 = document.getString("SegundoNombre");
                                        apellido1 = document.getString("PrimerApellido");
                                        apellido2 = document.getString("SegundoApellido");
                                        estado3 = document.getString("Estado3");
                                        nombre_entero = nombre1 + " " + nombre2 + " " + apellido1 + " " + apellido2;
                                        Log.d("NOMBRES", "Nombres : " + nombre_entero);

                                        if (estado3.equals("0")) {
                                            usuarios_no_marcaron.add(new String[]{k++ + "", nombre_entero});
                                            Log.d("NOMARCA", "No Marcó: " + nombre_entero);
                                            return;
                                        } else {
                                            Log.d("MARCA", "Marcó: " + nombre_entero);
                                        }
                                    } else {
                                        Log.d("Fallo", "No está el archivo.");
                                    }
                                }
                            }
                        });
                    }
                }
                k = 1;
            }
        });

        return usuarios_no_marcaron;
    }

    public ArrayList<String[]> porSiNoMarcaronNadaTodavia() {
        db.collection("UNAN, León").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firestore Error", e.getMessage());
                    return;
                }

                assert queryDocumentSnapshots != null;
                usuarios_no_marcaron = new ArrayList<>();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        usuario1 = dc.getDocument().getId();
                        Log.d("Usuario", "Usuario: " + usuario1);

                        prueba(usuario1);
                    }
                }
                k = 1;
            }
        });

        return usuarios_no_marcaron;
    }

    public void prueba(final String user) {
        iniciandoTruetime();

        s_ano = getString(R.string.tt_time_gmt, _formatDate(trueTime, "yyyy", TimeZone.getTimeZone("GMT-06:00")));
        s_mes = getString(R.string.tt_time_gmt, _formatDate(trueTime, "M", TimeZone.getTimeZone("GMT-06:00")));
        s_dia = getString(R.string.tt_time_gmt, _formatDate(trueTime, "d", TimeZone.getTimeZone("GMT-06:00")));

        usuarios_no_marcaron = new ArrayList<>();
        s_fecha = s_dia + s_mes + s_ano;
        db.collection("UNAN, León").document(user).collection("Entrada").document(fecha_calendario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                usuario2 = user;
                if (task.isSuccessful()) {
                    DocumentSnapshot dc = task.getResult();

                    //marcaron = db.collection("UNAN, León").document(usuario);

                    if (dc.exists()) {
                        Nombre = dc.getString("NombreCompleto");

                        Log.d("MarcasteEntrada", "Nombre: " + " - " + Nombre + " - " + usuario2);
                        return;
                    } else {
                        marcaron = db.collection("UNAN, León").document(usuario2);
                        marcaron.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot ds = task.getResult();
                                    if (ds.exists()) {
                                        String nombre = ds.getString("PrimerNombre");
                                        String apellido = ds.getString("PrimerApellido");
                                        String snombre = ds.getString("SegundoNombre");
                                        String sapellido = ds.getString("SegundoApellido");
                                        String nombrec = nombre + " " + snombre + " " + apellido + " " + sapellido;

                                        usuarios_no_marcaron.add(new String[]{k++ + "", nombrec});
                                        Log.d("NoMarcaste", "No marcó el usuario: " + nombrec);
                                    }
                                }
                            }
                        });
                        return;
                    }
                }
            }
        });
    }

    public ArrayList<String[]> justificaciones() {
        db.collection("UNAN, León")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }

                        justificacion = new ArrayList<>();

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String aux = dc.getDocument().getId();
                                db.collection("UNAN, León")
                                        .document(aux)
                                        .collection("Justificaciones")
                                        .document(fecha_calendario)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        s_nombreCompleto = document.getString("NombreCompleto");
                                                        s_hora = document.getString("Hora");
                                                        s_justificacion = document.getString("Justificacion");

                                                        if (s_hora == "" || s_hora == null) {
                                                            Log.d("Justification SN=> ", s_nombreCompleto + ": No agregó Justificación");
                                                            return;
                                                        } else {
                                                            Log.d("Justification => ", s_nombreCompleto + " = " + s_hora + " = " + s_justificacion);
                                                            justificacion.add(new String[]{j++ + "", s_nombreCompleto, s_hora, s_justificacion});
                                                        }
                                                    } else {
                                                        Log.d("Fallo", "No está el archivo.");
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                        j = 1;
                    }
                });

        return justificacion;
    }

    public void iniciandoTruetime() {
        if (!TrueTime.isInitialized()) {
            Toast.makeText(getApplicationContext(), "Sorry TrueTime not yet initialized. Trying again.", LENGTH_SHORT).show();

            return;
        }

        trueTime = TrueTime.now();
        hora_dispositivo = new Date();
    }

    private String _formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(timeZone);

        return format.format(date);
    }

    /*Función para ocultar la barra de navegación*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
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