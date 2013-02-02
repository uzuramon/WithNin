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
	    
    	//�V�C�擾
    	String weather = "";
    	String text = "";

    	if((timing.equals(getString(R.string.alarm_morning))) || (timing.equals(getString(R.string.alarm_night)))){
    		weather = WeatherUpdate(Integer.parseInt(p.getString(getString(R.string.weather_pref),getString(R.string.weather_default_value))),timing);
        	//�J�����_�[�擾
        	text = getCalendar(timing);
    	}

    	//�ʒm�̐ݒ�
    	setNotification(timing,weather,text,p);

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

    
    //�J�����_�[�擾
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

    			//�I��
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
       			    
       			    text = text + "�I���F" + title + "\n";
       			}

       			//���ԑ�
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

       			    text = text + formatter.format(calendarS.getTime()) + "-" + formatter.format(calendarE.getTime()) + "�F" + title + "\n";
   			    }
        			              
         } else {
             
         }

    	 
    	return text;
    }
    
    //�ʒm�̐ݒ�
    public void setNotification(String timing,String weather,String text,SharedPreferences p){
		Log.v(getString(R.string.log),"setNotification start");

    	//�L�����N�^�[�Q�b�g
    	String character = p.getString(getString(R.string.character_pref), getString(R.string.character_default_value));
		if(character.equals(getString(R.string.character_random))){
    		Random r = new Random(System.currentTimeMillis());
			String[] random_char = (getResources().getStringArray(getResources().getIdentifier(getString(R.string.character_list_value), "array", getPackageName())));
			character = random_char[r.nextInt(random_char.length-1)];
		}
   	
    	//�C���e���g�ݒ�
    	Intent intAct = new Intent(getApplicationContext(),MainActivity.class);

    	MessageFormat mf = new MessageFormat("{0,date,yyyyMMddHHmmss}");
    	Object[] objs = {Calendar.getInstance().getTime()};

    	intAct.setData(Uri.parse("http://action" + timing + mf.format(objs)));
    	intAct.putExtra(getString(R.string.timing),timing);
    	intAct.putExtra(getString(R.string.character),character);
    	intAct.putExtra(getString(R.string.weather),weather);
    	intAct.putExtra(getString(R.string.calendar),text);
    	

    	//�A���[���̍Đݒ�
    	MyAlarmManager mam = new MyAlarmManager(getApplicationContext());
        mam.addAlarm(p.getInt(timing+"_h", 0),p.getInt(timing+"_m", 0),timing);

        //�ʒm�̐ݒ�
        AlermSetting as = new AlermSetting();
        as.setNortification(getApplicationContext(),intAct,timing,character,p);
        
		Log.v(getString(R.string.log),"setNotification end");
    }
    
}
