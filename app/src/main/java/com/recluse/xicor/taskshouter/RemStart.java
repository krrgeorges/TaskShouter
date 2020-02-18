package com.recluse.xicor.taskshouter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ROJIT on 5/11/2018.
 */

public class RemStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, ReminderNotif.class);
        context.startService(myIntent);
    }
}
