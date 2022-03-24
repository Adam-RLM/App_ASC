package com.example.adminassistcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private AdaptadorMACs myAdapter;
    private MostrarMACs activity;

    public TouchHelper(MostrarMACs activity, AdaptadorMACs myAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.myAdapter = myAdapter;
        this.activity = activity;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        boolean online = internetIsConnected();
        /*Variables Comprobación de internet*/
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexionWIFI = false;
        /*--------------------------------------------------*/
        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected() && online) {
                tipoConexionWIFI = true;
                if (tipoConexionWIFI == true) {
                    if (direction == ItemTouchHelper.LEFT) {
                        myAdapter.UpdateDatos(position);
                        myAdapter.notifyDataSetChanged();
                        activity.finish();
                    } else {
                        myAdapter.deleteDatos(position);
                    }
                }
            } else {
                activity.maCsArrayList.clear();
                activity.EventChangeListener();
                Toast activo = Toast.makeText(activity, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
                activo.show();
            }
        } else {
            activity.maCsArrayList.clear();
            activity.EventChangeListener();
            Toast activo = Toast.makeText(activity, "Revise su conexión a Internet.", Toast.LENGTH_SHORT);
            activo.show();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightBackgroundColor(Color.RED)
                .addSwipeRightActionIcon(R.drawable.ic_delete)
                .addSwipeLeftBackgroundColor(Color.YELLOW)
                .addSwipeLeftActionIcon(R.drawable.ic_edit)
                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
}
