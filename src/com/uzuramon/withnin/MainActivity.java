package com.uzuramon.withnin;

import java.util.Random;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

	//設定
	private SharedPreferences p;
	private Context c;
	private Random r;

	@Override
    public void onCreate(Bundle savedInstanceState) {

		Log.v(getString(R.string.log),"MainActivity　onCreate start");

    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.activity_main);
    	
    	//コンテキスト設定
    	c = getApplicationContext();
    	
    	//設定ボタンイベント
        View clickSetting = findViewById(R.id.button_setting);
        clickSetting.setOnClickListener(oCLforShowButton);
        
        //ランダム生成
        r = new Random(System.currentTimeMillis());

		Log.v(getString(R.string.log),"MainActivity　onCreate end");
	}
    
    @Override
    public void onStart(){
    	super.onStart();
    	setText();
   }
    
    @Override
    public void onRestart(){
    	super.onRestart();
    	setText();
    }
    
    private String timing;
    private String character;
    private String wether;
    private ImageView imgv;
    private TextView edit ;
    private String text;
    
    private String[] random_char;
    private String[] random_talk;
    private String[] morning_talk; 
    private String[] night_talk;
    private String[] weather_talk; 
    private String[] rain_talk;
    private String[] nintama_talk;
    
    //画面表示の設定
    private void setText(){
		Log.v(getString(R.string.log),"MainActivity　setText start");

		//インテントデータ取得
    	timing = getIntent().getStringExtra(getString(R.string.timing));
    	character = getIntent().getStringExtra(getString(R.string.character));
    	wether = getIntent().getStringExtra(getString(R.string.weather));

    	//画面部品取得
    	imgv = (ImageView)findViewById(R.id.imageView1);
    	edit = (TextView)findViewById( R.id.textView1 );
    	
    	//設定データ取得
    	p = PreferenceManager.getDefaultSharedPreferences(c);

    	//キャラクター設定
		if(character == null){
			character = p.getString(getString(R.string.character_pref), getString(R.string.character_default_value));
			if(character.equals(getString(R.string.character_random))){
				random_char = getResources().getStringArray(getResources().getIdentifier(getString(R.string.character_list_value), "array", getPackageName()));
				character = random_char[r.nextInt(random_char.length-1)];
			}
		}

		//トーク取得
	    random_talk = getResources().getStringArray(getResources().getIdentifier(character + "_random", "array", getPackageName()));
	    morning_talk = getResources().getStringArray(getResources().getIdentifier(character + "_morning", "array", getPackageName()));
	    night_talk = getResources().getStringArray(getResources().getIdentifier(character + "_night", "array", getPackageName()));
	    weather_talk = getResources().getStringArray(getResources().getIdentifier(character + "_weather", "array", getPackageName()));
	    rain_talk = getResources().getStringArray(getResources().getIdentifier(character + "_rain", "array", getPackageName()));
	    nintama_talk = getResources().getStringArray(getResources().getIdentifier(character + "_nintama", "array", getPackageName()));

	    //キャラクター画像設定
	    imgv.setBackgroundDrawable(getResources().getDrawable(getResources().getIdentifier(character, "drawable", getPackageName())));	

    	//トーク初期化
    	text = "";

	    //ランダムトーク
		if(timing == null){
   			text = random_talk[0];
        
   		//おはよう
		}else if(timing.equals(getString(R.string.alarm_morning))){
   			text = morning_talk[0];
    		if(!(wether.equals(""))){
        		text = text + String.format(weather_talk[0],getString(R.string.today_name),wether);
        		if((wether.indexOf("雨") != -1) || (wether.indexOf("雪") != -1)){
            		text = text + rain_talk[0];
        		}
        	}

    	//おやすみ
        }else if(timing.equals(getString(R.string.alarm_night))){
   			text = night_talk[0];
    		if(!(wether.equals(""))){
        		text = text + String.format(weather_talk[0],getString(R.string.tomorrow_name),wether);
        		if((wether.indexOf("雨") != -1) || (wether.indexOf("雪") != -1)){
            		text = text + rain_talk[0];
        		}
        	}
    	
    	//にんたま
        }else if(timing.equals(getString(R.string.alarm_nintama))){
   			text = nintama_talk[0];
        } 

    	edit.setText(text);

    	Log.v(getString(R.string.log),"MainActivity　setText end");
    }
    
    //ボタンクリック時のリスナ
    private final OnClickListener oCLforShowButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){

            case R.id.button_setting:
            	Intent intent = new Intent( c, MenuActivity.class );
                startActivity( intent );
                break;
            }
        }
    };
    

    

}
