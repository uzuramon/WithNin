package com.uzuramon.withnin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
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
	    
    	//インテントの設定
    	Intent alarmBroadcast = new Intent();

    	if((timing.equals(getString(R.string.alarm_morning))) || (timing.equals(getString(R.string.alarm_night)))){
        	alarmBroadcast.putExtra(getString(R.string.weather),WeatherUpdate(Integer.parseInt(p.getString(getString(R.string.weather_pref),getString(R.string.weather_default_value))),timing));
    	}else{
        	alarmBroadcast.putExtra(getString(R.string.weather),"");
    	}
    	
    	alarmBroadcast.putExtra(getString(R.string.timing),timing);
        
        alarmBroadcast.setAction("MyAlarmAction");//独自のメッセージを送信します
        sendBroadcast(alarmBroadcast);

		Log.v(getString(R.string.log),"MyAlarmReceiver　通知画面起動メッセージ送信");

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
    
}
