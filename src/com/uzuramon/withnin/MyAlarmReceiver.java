package com.uzuramon.withnin;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.uzuramon.withnin.AlermSetting;

public class MyAlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.v(context.getString(R.string.log),"MyAlarmReceiver　onReceive start");

		String timing = intent.getStringExtra(context.getString(R.string.timing));
		String weather =  intent.getStringExtra(context.getString(R.string.weather));

	    //設定の取得
    	SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(context);

    	//キャラクターゲット
    	String character = p.getString(context.getString(R.string.character_pref), context.getString(R.string.character_default_value));
		if(character.equals(context.getString(R.string.character_random))){
    		Random r = new Random(System.currentTimeMillis());
			String[] random_char = context.getResources().getStringArray(context.getResources().getIdentifier(context.getString(R.string.character_list_value), "array", context.getPackageName()));
			character = random_char[r.nextInt(random_char.length-1)];
		}
   	
    	//インテント設定
    	Intent i = new Intent(context.getApplicationContext(),MainActivity.class);

    	MessageFormat mf = new MessageFormat("{0,date,yyyyMMddHHmmss}");
    	Object[] objs = {Calendar.getInstance().getTime()};

    	i.setData(Uri.parse("http://action" + timing + mf.format(objs)));
        i.putExtra(context.getString(R.string.timing),timing);
        i.putExtra(context.getString(R.string.character),character);
        i.putExtra(context.getString(R.string.weather),weather);

    	//アラームの再設定
    	MyAlarmManager mam = new MyAlarmManager(context);
        mam.addAlarm(p.getInt(timing+"_h", 0),p.getInt(timing+"_m", 0),timing);

        //通知の設定
        AlermSetting as = new AlermSetting();
        as.setNortification(context,i,timing,character);
        
		Log.v(context.getString(R.string.log),"MyAlarmReceiver　onReceive end");
   	}


}

