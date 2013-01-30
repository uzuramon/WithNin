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

	//�ݒ�
	private SharedPreferences p;
	private Context c;
	private Random r;

	@Override
    public void onCreate(Bundle savedInstanceState) {

		Log.v(getString(R.string.log),"MainActivity�@onCreate start");

    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.activity_main);
    	
    	//�R���e�L�X�g�ݒ�
    	c = getApplicationContext();
    	
    	//�ݒ�{�^���C�x���g
        View clickSetting = findViewById(R.id.button_setting);
        clickSetting.setOnClickListener(oCLforShowButton);
        
        //�����_������
        r = new Random(System.currentTimeMillis());

		Log.v(getString(R.string.log),"MainActivity�@onCreate end");
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
    
    //��ʕ\���̐ݒ�
    private void setText(){
		Log.v(getString(R.string.log),"MainActivity�@setText start");

		//�C���e���g�f�[�^�擾
    	timing = getIntent().getStringExtra(getString(R.string.timing));
    	character = getIntent().getStringExtra(getString(R.string.character));
    	wether = getIntent().getStringExtra(getString(R.string.weather));

    	//��ʕ��i�擾
    	imgv = (ImageView)findViewById(R.id.imageView1);
    	edit = (TextView)findViewById( R.id.textView1 );
    	
    	//�ݒ�f�[�^�擾
    	p = PreferenceManager.getDefaultSharedPreferences(c);

    	//�L�����N�^�[�ݒ�
		if(character == null){
			character = p.getString(getString(R.string.character_pref), getString(R.string.character_default_value));
			if(character.equals(getString(R.string.character_random))){
				random_char = getResources().getStringArray(getResources().getIdentifier(getString(R.string.character_list_value), "array", getPackageName()));
				character = random_char[r.nextInt(random_char.length-1)];
			}
		}

		//�g�[�N�擾
	    random_talk = getResources().getStringArray(getResources().getIdentifier(character + "_random", "array", getPackageName()));
	    morning_talk = getResources().getStringArray(getResources().getIdentifier(character + "_morning", "array", getPackageName()));
	    night_talk = getResources().getStringArray(getResources().getIdentifier(character + "_night", "array", getPackageName()));
	    weather_talk = getResources().getStringArray(getResources().getIdentifier(character + "_weather", "array", getPackageName()));
	    rain_talk = getResources().getStringArray(getResources().getIdentifier(character + "_rain", "array", getPackageName()));
	    nintama_talk = getResources().getStringArray(getResources().getIdentifier(character + "_nintama", "array", getPackageName()));

	    //�L�����N�^�[�摜�ݒ�
	    imgv.setBackgroundDrawable(getResources().getDrawable(getResources().getIdentifier(character, "drawable", getPackageName())));	

    	//�g�[�N������
    	text = "";

	    //�����_���g�[�N
		if(timing == null){
   			text = random_talk[0];
        
   		//���͂悤
		}else if(timing.equals(getString(R.string.alarm_morning))){
   			text = morning_talk[0];
    		if(!(wether.equals(""))){
        		text = text + String.format(weather_talk[0],getString(R.string.today_name),wether);
        		if((wether.indexOf("�J") != -1) || (wether.indexOf("��") != -1)){
            		text = text + rain_talk[0];
        		}
        	}

    	//���₷��
        }else if(timing.equals(getString(R.string.alarm_night))){
   			text = night_talk[0];
    		if(!(wether.equals(""))){
        		text = text + String.format(weather_talk[0],getString(R.string.tomorrow_name),wether);
        		if((wether.indexOf("�J") != -1) || (wether.indexOf("��") != -1)){
            		text = text + rain_talk[0];
        		}
        	}
    	
    	//�ɂ񂽂�
        }else if(timing.equals(getString(R.string.alarm_nintama))){
   			text = nintama_talk[0];
        } 

    	edit.setText(text);

    	Log.v(getString(R.string.log),"MainActivity�@setText end");
    }
    
    //�{�^���N���b�N���̃��X�i
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
