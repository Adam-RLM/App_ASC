package com.example.adminassistcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MostrandoAdministrador extends AppCompatActivity {
    TextView PNombre, SNombre, PApellido, SApellido, Email, Sexo, FNacimiento, Administrador;
    ImageButton regresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrando_administrador);

        PNombre = findViewById(R.id.txt_pnombre_user_admin);
        SNombre = findViewById(R.id.txt_snombre_user_admin);
        PApellido = findViewById(R.id.txt_papellido_user_admin);
        SApellido = findViewById(R.id.txt_sapellido_user_admin);
        Email = findViewById(R.id.txt_email_user_admin);
        Sexo = findViewById(R.id.txt_sexo_user_admin);
        FNacimiento = findViewById(R.id.txt_fechanacimiento_user_admin);
        Administrador = findViewById(R.id.si_administrador);
        regresa = findViewById(R.id.btn_regre);

        regresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        String pnombre = "Primer Nombre no seteado";
        String snombre = "Segundo Nombre no seteado";
        String papellido = "Primer Apellido no seteado";
        String sapellido = "Segundo Apellido no seteado";
        String email = "E-mail no seteado";
        String sexo = "Sexo no seteado";
        String fnacimiento = "F. Nacimiento no seteado";
        String administrador = "Admin no seteado";

        Bundle extras = getIntent().getExtras();

        if (extras != null){
            pnombre = extras.getString("pnombre");
            snombre = extras.getString("snombre");
            papellido = extras.getString("papellido");
            sapellido = extras.getString("sapellido");
            email = extras.getString("email");
            sexo = extras.getString("sexo");
            fnacimiento = extras.getString("fnacimiento");
            administrador = extras.getString("admin");
        }

        PNombre.setText(pnombre);
        SNombre.setText(snombre);
        PApellido.setText(papellido);
        SApellido.setText(sapellido);
        Email.setText(email);
        Sexo.setText(sexo);
        FNacimiento.setText(fnacimiento);
        Administrador.setText(administrador);
    }

    /*Funci??n para ocultar la barra de navegaci??n*/
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