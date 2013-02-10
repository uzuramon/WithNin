package com.uzuramon.withnin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.util.Xml;

public class MyAlarmService extends Service {

	private Intent i;
	
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
		Log.v(getString(R.string.log),"MyAlarmReceiver　onCreate");
    }

	@Override
	public int onStartCommand(Intent intent, int flags,int startId) {
		super.onStartCommand(intent, flags, startId);
		
		Log.v(getString(R.string.log),"MyAlarmReceiver　onStartCommand");
	    
		//インテント設定
	    i = intent;
	    
	    //スレッドスタート
	    Thread thr = new Thread(null, mTask, "MyAlarmServiceThread");
	    thr.start();
	    
		Log.v(getString(R.string.log),"MyAlarmReceiver　スレッド開始");
		
		return START_NOT_STICKY;
	}
	
    /**
     * アラームサービス
     */
    Runnable mTask = new Runnable() {
    public void run() {

    	//インテントから処理タイミングを取得
    	String timing = i.getStringExtra(getString(R.string.timing));

	    //設定の取得
    	SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    
    	String weather = "";
    	String caltext = "";
    	String days24 = "";
    	String days72 = "";
    	String dayssetsu = "";

    	if((timing.equals(getString(R.string.alarm_morning))) || (timing.equals(getString(R.string.alarm_night)))){
        	//天気取得
    		weather = WeatherUpdate(Integer.parseInt(p.getString(getString(R.string.weather_pref),getString(R.string.weather_default_value))),timing);
        	//カレンダー取得
    		caltext = getCalendar(timing);
        	//暦取得
			Calendar today = Calendar.getInstance();
			SimpleDateFormat daysFormat = new SimpleDateFormat("yyyyMMdd");
			if(timing.equals(getString(R.string.alarm_night))){
				today.add(Calendar.DATE, 1);
			}
   			days24 = getDays(daysFormat.format(today.getTime()),"days24");
   			days72 = getDays(daysFormat.format(today.getTime()),"days72");
   			dayssetsu = getZassetsu(daysFormat.format(today.getTime()),"daysother");
    	}
    	

    	//通知の設定
    	setNotification(timing,weather,caltext,days24,days72,dayssetsu,p);

        MyAlarmService.this.stopSelf();//サービスを止める
        
		Log.v(getString(R.string.log),"MyAlarmReceiver　サービス停止");
    }
    };
    

	//天気予報更新
    public String WeatherUpdate(int wetherPoint, String timing) {

		Log.v(getString(R.string.log),"WeatherUpdate　start" + timing);

    	String day = "";
    	if(timing.equals(getString(R.string.alarm_morning))){
    		day="today";
    	}else{
    		day="tomorrow";
    	}
    	String telop = "";
        String weburl = "http://weather.livedoor.com/forecast/webservice/rest/v1?city=" + wetherPoint  + "&day=" + day;

        HttpResponse httpResponse;
        HttpClient httpClient = new DefaultHttpClient();
 
        try {
            //天気予報のＷＥＢサービスから予報取得
            httpResponse = httpClient.execute(new HttpGet(weburl));  
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                //XMLから各情報を抽出
                XmlPullParser xppr = Xml.newPullParser();
                xppr.setInput(new StringReader(outputStream.toString()));
        		int eventType;
                while ((eventType = xppr.next()) != XmlPullParser.END_DOCUMENT) {
                    //天気予報
                    if (eventType == XmlPullParser.START_TAG && "telop".equals(xppr.getName())) {
                        telop =  xppr.nextText();
                    }
                }
            }

        } catch (ClientProtocolException e) {
            Log.e(getString(R.string.log), "clientProtocol", e);
        } catch (IOException e) {
            Log.e(getString(R.string.log), "IOException", e);
        } catch(Exception e){
            Log.e(getString(R.string.log), "Exception", e);
        }finally {
            // ここではfinallyでshutdown()しているが、HttpClientを使い回す場合は、
            // 適切なところで行うこと。当然だがshutdown()したインスタンスは通信できなくなる。
            httpClient.getConnectionManager().shutdown();
        }

		Log.v(getString(R.string.log),"WeatherUpdate　end");
        
        return telop;


    }

    
    //カレンダー取得
    @SuppressLint("NewApi")
	public String getCalendar(String timing){

    	String text = "";
    	String[] INSTANCE_PROJECTION = new String[] {
			    Instances.EVENT_ID,      // 0
			    Instances.BEGIN,         // 1
			    Instances.END,         // 2
			    Instances.TITLE          // 3
		 };

    	 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){

    		 Log.v("Calendar Data", "calendar start");

    			// The indices for the projection array above.
    			int PROJECTION_ID_INDEX = 0;
    			int PROJECTION_BEGIN_INDEX = 1;
    			int PROJECTION_END_INDEX = 2;
    			int PROJECTION_TITLE_INDEX = 3;

    			// Specify the date range you want to search for recurring
    			// event instances
    			Calendar beginTime = Calendar.getInstance();
    			Calendar endTime = Calendar.getInstance();
    			Calendar nowTime = Calendar.getInstance();

    			beginTime.set(nowTime.get(nowTime.YEAR), nowTime.get(nowTime.MONTH), nowTime.get(nowTime.DATE),0,0,0);
				endTime.set(nowTime.get(nowTime.YEAR), nowTime.get(nowTime.MONTH), nowTime.get(nowTime.DATE),0,0,0);

				endTime.add(Calendar.DATE,1);
    			
    			if(timing.equals(getString(R.string.alarm_night))){
    				beginTime.add(Calendar.DATE,1);
    				endTime.add(Calendar.DATE,1);
    			}
    			
    			long startMillis = beginTime.getTimeInMillis();
    			long endMillis = endTime.getTimeInMillis();
    			
    			//Log.v("start",Integer.toString(beginTime.get(beginTime.MONTH)) + "/" + Integer.toString(beginTime.get(beginTime.DATE)));
    			  
    			ContentResolver cr = getContentResolver();

    			// Construct the query with the desired date range.
    			Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
    			ContentUris.appendId(builder, startMillis);
    			ContentUris.appendId(builder, endMillis);

    			String[] params = new String[]{"" + startMillis, "" + endMillis };

    			//終日
           		// Submit the query
        		Cursor cur =  cr.query(builder.build(), 
        			    INSTANCE_PROJECTION, 
        			    //selection,
        			    Instances.BEGIN + " >= ? and " +  Instances.BEGIN + " < ? and " + Instances.ALL_DAY + " = 1",
        			    params,
        			    //selectionArgs, 
        			    Instances.BEGIN + " asc");
        			   
       			while (cur.moveToNext()) {
       			    String title = null;
        			    
       			    // Get the field values
       			    title = cur.getString(PROJECTION_TITLE_INDEX);
       			    
       			    text = text + "終日：" + title + "\n";
       			}

       			//時間帯
        		// Submit the query
        		Cursor curT =  cr.query(builder.build(), 
        			    INSTANCE_PROJECTION, 
        			    //selection,
        			    Instances.BEGIN + " >= ? and " +  Instances.BEGIN + " < ? and " + Instances.ALL_DAY + " = 0",
        			    params,
        			    //selectionArgs, 
        			    Instances.BEGIN + " asc");
        			   
       			while (curT.moveToNext()) {
       			    String title = null;
       			    long beginVal = 0;    
       			    long endVal = 0;
        			    
       			    // Get the field values
       			    beginVal = curT.getLong(PROJECTION_BEGIN_INDEX);
       			    endVal = curT.getLong(PROJECTION_END_INDEX);
       			    title = curT.getString(PROJECTION_TITLE_INDEX);
       			    
       			    // Do something with the values. 
       			    Calendar calendarS = Calendar.getInstance();
       			    calendarS.setTimeInMillis(beginVal);
      			    Calendar calendarE = Calendar.getInstance();
      			    calendarE.setTimeInMillis(endVal);
       			    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

       			    text = text + formatter.format(calendarS.getTime()) + "-" + formatter.format(calendarE.getTime()) + "：" + title + "\n";
   			    }
        			              
         } else {
             
         }

    	 
    	return text;
    }

    //暦取得
    public String getDays(String today,String koyomi){
    	
	    String[] days_list =  getResources().getStringArray(getResources().getIdentifier(koyomi + "_list_date", "array", getPackageName()));
	    String[] days_value =  getResources().getStringArray(getResources().getIdentifier(koyomi + "_list_value", "array", getPackageName()));
	    String days_num = "";
	    
	    for(int i=0; i<days_list.length; i++){
	    	if(today.compareTo(days_list[i]) < 0){
	    		days_num = days_value[i - 1];
	    		break;
	    	}
	    }
	    
	    return days_num;
    }

    //雑節取得
    public String getZassetsu(String today,String koyomi){
    	
	    String[] days_list =  getResources().getStringArray(getResources().getIdentifier(koyomi + "_list_date", "array", getPackageName()));
	    String[] days_value =  getResources().getStringArray(getResources().getIdentifier(koyomi + "_list_value", "array", getPackageName()));
	    String days_num = "";
	    
	    for(int i=0; i<days_list.length; i++){
	    	Log.v("days_list_today",String.valueOf(today));
	    	Log.v("days_list",String.valueOf(days_list[i]));
	    	if(today.compareTo(days_list[i]) == 0){
	    		days_num = days_value[i];
	    		break;
	    	}
	    }
	    
	    return days_num;
    }

    
    
    //通知の設定
    public void setNotification(String timing,String weather,String caltext,String days24,String days72,String dayssetsu,SharedPreferences p){
		Log.v(getString(R.string.log),"setNotification start");

    	//キャラクターゲット
    	String character = p.getString(getString(R.string.character_pref), getString(R.string.character_default_value));
		if(character.equals(getString(R.string.character_random))){
    		Random r = new Random(System.currentTimeMillis());
			String[] random_char = (getResources().getStringArray(getResources().getIdentifier(getString(R.string.character_list_value), "array", getPackageName())));
			character = random_char[r.nextInt(random_char.length-1)];
		}
   	
    	//インテント設定
    	Intent intAct = new Intent(getApplicationContext(),MainActivity.class);

    	MessageFormat mf = new MessageFormat("{0,date,yyyyMMddHHmmss}");
    	Object[] objs = {Calendar.getInstance().getTime()};

    	intAct.setData(Uri.parse("http://action" + timing + mf.format(objs)));
    	intAct.putExtra(getString(R.string.timing),timing);
    	intAct.putExtra(getString(R.string.character),character);
    	intAct.putExtra(getString(R.string.weather),weather);
    	intAct.putExtra(getString(R.string.calendar),caltext);
    	intAct.putExtra(getString(R.string.days24),days24);
    	intAct.putExtra(getString(R.string.days72),days72);
    	intAct.putExtra(getString(R.string.daysother),dayssetsu);

    	//アラームの再設定
    	MyAlarmManager mam = new MyAlarmManager(getApplicationContext());
        mam.addAlarm(p.getInt(timing+"_h", 0),p.getInt(timing+"_m", 0),timing);

        //通知の設定
        AlermSetting as = new AlermSetting();
        as.setNortification(getApplicationContext(),intAct,timing,character,p);
        
		Log.v(getString(R.string.log),"setNotification end");
    }
    
}
