package com.skynsoft.collageapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NotificacionesService extends IntentService {

    public NotificacionesService() {
        super("NotificacionesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("NotificacionesService", "Servicio ejecutandose. Comunicados");
    }
}

