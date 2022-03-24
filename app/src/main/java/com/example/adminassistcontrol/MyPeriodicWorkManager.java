package com.example.adminassistcontrol;

/*public class MyPeriodicWorkManager extends Worker {
    private static final String TAG = "MyPeriodicWork";

    public MyPeriodicWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void guardarnotificacion(Data data, String TAG){
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                MyPeriodicWorkManager.class, 15, TimeUnit.MINUTES)
                .setInputData(data)
                .addTag(TAG)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "doWork: Work is funcionando");*/

        /*String titulo = getInputData().getString("titulo");
        String detalle = getInputData().getString("detalle");
        int id = (int) getInputData().getLong("idnoti", 0);*/

        //return Result.success();
   // }
//}
