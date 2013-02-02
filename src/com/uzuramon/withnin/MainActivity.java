package com.uzuramon.withnin;

import java.util.Random;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

	//�ݒ�
	private View clickSetting;
    private ImageView imgv;

	@Override
    public void onCreate(Bundle savedInstanceState) {

		Log.v(getString(R.string.log),"MainActivity�@onCreate start");

    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	//�ݒ�{�^���C�x���g
        clickSetting = findViewById(R.id.button_setting);
        clickSetting.setOnClickListener(oCLforShowButton);

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
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
	    imgv.setBackgroundDrawable(null);
	    clickSetting.setOnClickListener(null);
	    System.gc();
    }
    
    //��ʕ\���̐ݒ�
    private void setText(){
		Log.v(getString(R.string.log),"MainActivity�@setText start");

		//�C���e���g�f�[�^�擾
		String timing = getIntent().getStringExtra(getString(R.string.timing));
		String character = getIntent().getStringExtra(getString(R.string.character));
		String wether = getIntent().getStringExtra(getString(R.string.weather));
		String cal = getIntent().getStringExtra(getString(R.string.calendar));

    	//��ʕ��i�擾
    	imgv = (ImageView)findViewById(R.id.imageView1);
    	TextView edit = (TextView)findViewById( R.id.textView1 );
    	
    	//�ݒ�f�[�^�擾
    	SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //�����_������
        Random r = new Random(System.currentTimeMillis());
        
    	//�L�����N�^�[�ݒ�
		if(character == null){
			character = p.getString(getString(R.string.character_pref), getString(R.string.character_default_value));
			if(character.equals(getString(R.string.character_random))){
				String[] random_char = getResources().getStringArray(getResources().getIdentifier(getString(R.string.character_list_value), "array", getPackageName()));
				character = random_char[r.nextInt(random_char.length-1)];
			}
		}

		//�g�[�N�擾
		String[] random_talk = getResources().getStringArray(getResources().getIdentifier(character + "_random", "array", getPackageName()));
		String[] morning_talk = getResources().getStringArray(getResources().getIdentifier(character + "_morning", "array", getPackageName()));
		String[] night_talk = getResources().getStringArray(getResources().getIdentifier(character + "_night", "array", getPackageName()));
		String[] weather_talk = getResources().getStringArray(getResources().getIdentifier(character + "_weather", "array", getPackageName()));
		String[] rain_talk = getResources().getStringArray(getResources().getIdentifier(character + "_rain", "array", getPackageName()));
		String[] cal_talk = getResources().getStringArray(getResources().getIdentifier(character + "_cal", "array", getPackageName()));
		String[] nintama_talk = getResources().getStringArray(getResources().getIdentifier(character + "_nintama", "array", getPackageName()));

	    //�L�����N�^�[�摜�ݒ�
	    imgv.setBackgroundDrawable(getResources().getDrawable(getResources().getIdentifier(character, "drawable", getPackageName())));	

    	//�g�[�N������
	    String text = "";

	    //�����_���g�[�N
		if(timing == null){
			text = random_talk[r.nextInt(random_talk.length)];
        
   		//���͂悤
		}else if(timing.equals(getString(R.string.alarm_morning))){
			text = morning_talk[r.nextInt(morning_talk.length)];
    		
			if(!(wether.equals(""))){
    			text = text + String.format(weather_talk[r.nextInt(weather_talk.length)],getString(R.string.today_name),wether);

    			if((wether.indexOf("�J") != -1) || (wether.indexOf("��") != -1)){
        			text = text + rain_talk[r.nextInt(rain_talk.length)];

        		}
        	}
			
			if(!(cal.equals(""))){
				text = text + String.format(cal_talk[r.nextInt(cal_talk.length)],getString(R.string.today_name)) +  cal;
			}

    	//���₷��
        }else if(timing.equals(getString(R.string.alarm_night))){
			text = night_talk[r.nextInt(night_talk.length)];

    		if(!(wether.equals(""))){
    			text = text + String.format(weather_talk[r.nextInt(weather_talk.length)],getString(R.string.tomorrow_name),wether);

        		if((wether.indexOf("�J") != -1) || (wether.indexOf("��") != -1)){
        			text = rain_talk[r.nextInt(rain_talk.length)];

        		}
        	}

			if(!(cal.equals(""))){
				text = text + String.format(cal_talk[r.nextInt(cal_talk.length)],getString(R.string.tomorrow_name)) +  cal;
			}

    	//�ɂ񂽂�
        }else if(timing.equals(getString(R.string.alarm_nintama))){
			text = nintama_talk[r.nextInt(nintama_talk.length)];
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
            	Intent intent = new Intent( getApplicationContext(), MenuActivity.class );
                startActivity( intent );
                break;
            }
        }
    };
    

    

}
