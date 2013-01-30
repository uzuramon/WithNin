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
		Log.v(getString(R.string.log),"MyAlarmReceiver�@onCreate");
    }

	@Override
	public int onStartCommand(Intent intent, int flags,int startId) {
		super.onStartCommand(intent, flags, startId);
		
		Log.v(getString(R.string.log),"MyAlarmReceiver�@onStartCommand");
	    
		//�C���e���g�ݒ�
	    i = intent;
	    
	    //�X���b�h�X�^�[�g
	    Thread thr = new Thread(null, mTask, "MyAlarmServiceThread");
	    thr.start();
	    
		Log.v(getString(R.string.log),"MyAlarmReceiver�@�X���b�h�J�n");
		
		return START_NOT_STICKY;
	}
	
    /**
     * �A���[���T�[�r�X
     */
    Runnable mTask = new Runnable() {
    public void run() {

    	//�C���e���g���珈���^�C�~���O���擾
    	String timing = i.getStringExtra(getString(R.string.timing));

	    //�ݒ�̎擾
    	SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    
    	//�C���e���g�̐ݒ�
    	Intent alarmBroadcast = new Intent();

    	if((timing.equals(getString(R.string.alarm_morning))) || (timing.equals(getString(R.string.alarm_night)))){
        	alarmBroadcast.putExtra(getString(R.string.weather),WeatherUpdate(Integer.parseInt(p.getString(getString(R.string.weather_pref),getString(R.string.weather_default_value))),timing));
    	}else{
        	alarmBroadcast.putExtra(getString(R.string.weather),"");
    	}
    	
    	alarmBroadcast.putExtra(getString(R.string.timing),timing);
        
        alarmBroadcast.setAction("MyAlarmAction");//�Ǝ��̃��b�Z�[�W�𑗐M���܂�
        sendBroadcast(alarmBroadcast);

		Log.v(getString(R.string.log),"MyAlarmReceiver�@�ʒm��ʋN�����b�Z�[�W���M");

        MyAlarmService.this.stopSelf();//�T�[�r�X���~�߂�
        
		Log.v(getString(R.string.log),"MyAlarmReceiver�@�T�[�r�X��~");
    }
    };
    

	//�V�C�\��X�V
    public String WeatherUpdate(int wetherPoint, String timing) {

		Log.v(getString(R.string.log),"WeatherUpdate�@start" + timing);

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
            //�V�C�\��̂v�d�a�T�[�r�X����\��擾
            httpResponse = httpClient.execute(new HttpGet(weburl));  
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                //XML����e���𒊏o
                XmlPullParser xppr = Xml.newPullParser();
                xppr.setInput(new StringReader(outputStream.toString()));
        		int eventType;
                while ((eventType = xppr.next()) != XmlPullParser.END_DOCUMENT) {
                    //�V�C�\��
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
            // �����ł�finally��shutdown()���Ă��邪�AHttpClient���g���񂷏ꍇ�́A
            // �K�؂ȂƂ���ōs�����ƁB���R����shutdown()�����C���X�^���X�͒ʐM�ł��Ȃ��Ȃ�B
            httpClient.getConnectionManager().shutdown();
        }

		Log.v(getString(R.string.log),"WeatherUpdate�@end");
        
        return telop;


    }
    
}
