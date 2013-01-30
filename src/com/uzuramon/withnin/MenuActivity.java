package com.uzuramon.withnin;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.widget.TimePicker;

@SuppressWarnings("deprecation")
public class MenuActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//�ݒ�
	private SharedPreferences p;
	private Context c;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.v(getString(R.string.log),"MenuActivity�@setText start");

    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.pref);

    	c = getApplicationContext();
        p = PreferenceManager.getDefaultSharedPreferences(c);
        
    	//����N���ݒ�
    	firstListener();
    	
    	//�����\���̐ݒ�
    	onSharedPreferenceChanged();
    	
    	//���͂悤�A���[���ʒm�ݒ�
    	setClickListener("morning_checkbox");
    	
    	//���͂悤�A���[�����Ԑݒ�
    	setClickListener("morning_time");
    	
    	//���₷�݃A���[���ʒm�ݒ�
    	setClickListener("night_checkbox");

    	//���₷�݃A���[�����Ԑݒ�
    	setClickListener("night_time");

    	//���₷�݃A���[���ʒm�ݒ�
    	setClickListener("nintama_checkbox");

    	//���₷�݃A���[�����Ԑݒ�
    	setClickListener("nintama_time");

    	//�L�����N�^�[�ݒ�
        ListPreference prefChar = (ListPreference)findPreference(getString(R.string.character_pref));   
        // ���X�i�[��ݒ肷��  
        prefChar.setOnPreferenceChangeListener(listPreference_OnPreferenceChangeListener);  

    	//�L�����N�^�[�ݒ�
        ListPreference prefWether = (ListPreference)findPreference(getString(R.string.weather_pref));   
        // ���X�i�[��ݒ肷��  
        prefWether.setOnPreferenceChangeListener(listPreference_OnPreferenceChangeListener);  
    	
    	//�o�C�u���[�V�����ݒ�
    	setClickListener("vibration_checkbox");
    	
    	//LED�ݒ�
    	setClickListener("led_checkbox");

    	//�ʒm���ݒ�
        RingtonePreference pref = (RingtonePreference)findPreference(getString(R.string.ringtone_pref));   
        // ���X�i�[��ݒ肷��  
        pref.setOnPreferenceChangeListener(ringtonePreference_OnPreferenceChangeListener);  

		Log.v(getString(R.string.log),"MenuActivity�@setText end");
    }
    
    //�N���b�N�҂�
	private void setClickListener(String prefName){
    	Preference pref = this.findPreference(prefName);
    	pref.setOnPreferenceClickListener(onPreferenceClickListener);
    }
    
    //����N���ݒ�
    private void firstListener(){

    	if(p.getInt("morning_h", 99) == 99){
    		p.edit().putInt("morning_h", 7).commit();
   		}
    	if(p.getInt("night_h", 99) == 99){
    		p.edit().putInt("night_h", 22).commit();
   		}
    	if(p.getInt("nintama_h", 99) == 99){
    		p.edit().putInt("nintama_h", 18).commit();
   		}
    	if(p.getInt("nintama_m", 99) == 99){
    		p.edit().putInt("nintama_m", 10).commit();
   		}
    }


    private OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {

        	//�N���b�N���ꂽ�L�[���擾
        	String prekey = preference.getKey();

    	    if(prekey.equals("morning_checkbox")){
    	    	checkOnOff("morning");
    	    }
    	    
    	    if(prekey.equals("morning_time")){
    	    	timeGet("morning");
    	    } 	    

    	    if(prekey.equals("night_checkbox")){
    	    	checkOnOff("night");
    	    }
    	    
    	    if(prekey.equals("night_time")){
    	    	timeGet("night");
    	    } 	    

    	    if(prekey.equals("nintama_checkbox")){
    	    	checkOnOff("nintama");
    	    }
    	    
    	    if(prekey.equals("nintama_time")){
    	    	timeGet("nintama");
    	    } 	    

    	    if(prekey.equals("vibration_checkbox")){
    		    onSharedPreferenceChanged();
    	    }
    	    
    	    if(prekey.equals("led_checkbox")){
    		    onSharedPreferenceChanged();
    	    }

            return true;
        }
    };

    private void checkOnOff(String alermTiming){
	    onSharedPreferenceChanged();
	    alermStart(alermTiming);
    }
    
    private void timeGet(final String alermTiming){
    	final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    p.edit().putInt(alermTiming + "_h", hourOfDay).commit();
                    p.edit().putInt(alermTiming + "_m", minute).commit();
            	    onSharedPreferenceChanged();
                    alermStart(alermTiming);
                }
            }, p.getInt(alermTiming + "_h", 0), p.getInt(alermTiming + "_m", 0), true);
        timePickerDialog.show();
    }

    
    public void onSharedPreferenceChanged() {

    	//���͂悤�A���[���ʒm�ݒ�ǂݍ���
        alermCheckGet("morning");
    	 
    	//���͂悤�A���[���ʒm���ԓǂݍ���
        alermTimeGet("morning");
 
    	//���₷�݃A���[���ʒm�ݒ�ǂݍ���
        alermCheckGet("night");
    	 
    	//���₷�݃A���[���ʒm���ԓǂݍ���
        alermTimeGet("night");

    	//�ɂ񂽂܃A���[���ʒm�ݒ�ǂݍ���
        alermCheckGet("nintama");
    	 
    	//�ɂ񂽂܃A���[���ʒm���ԓǂݍ���
        alermTimeGet("nintama");

        //�L�����N�^�[�ǂݍ���
        listPreference_OnPreferenceChange_char();
        
        //�L�����N�^�[�ǂݍ���
        listPreference_OnPreferenceChange_wether();

        //�o�C�u���[�V����
        settingCheckGet("vibration");
        
        //LED
        settingCheckGet("led");
        
        //�ʒm��
        ringtonePreference_OnPreferenceChange();

    }
    
    //�`�F�b�N�{�b�N�X�ʒm�ǂݍ���
    private void alermCheckGet(String alermTiming){
    	CheckBoxPreference checkbox_preference = (CheckBoxPreference)getPreferenceScreen().findPreference(alermTiming + "_checkbox");
    	if (checkbox_preference.isChecked()) {
    		checkbox_preference.setSummary("���m�点����");
    	} else {
    		checkbox_preference.setSummary("���m�点���Ȃ�");
    	}
    }
    
    //�ʒm���ԓǂݍ���
    private void alermTimeGet(String alermTiming){
    	String text = String.format("%1$02d��%2$02d��", p.getInt(alermTiming + "_h", 0),p.getInt(alermTiming + "_m", 0));
    	Preference time_preference = (Preference)getPreferenceScreen().findPreference(alermTiming + "_time");
    	time_preference.setSummary(text);
    }
 
    
    //�`�F�b�N�{�b�N�X�ʒm�ǂݍ���
    private void settingCheckGet(String key){
    	CheckBoxPreference checkbox_preference = (CheckBoxPreference)getPreferenceScreen().findPreference(key + "_checkbox");
    	if (checkbox_preference.isChecked()) {
    		checkbox_preference.setSummary("�L��");
    	} else {
    		checkbox_preference.setSummary("����");
    	}
    }
    
    // ���M��Preference�́@PreferenceChange���X�i�[  
    private OnPreferenceChangeListener ringtonePreference_OnPreferenceChangeListener = new OnPreferenceChangeListener(){  
        @Override  
        public boolean onPreferenceChange(Preference preference, Object newValue) {  
            return ringtonePreference_OnPreferenceChange(preference,newValue);  
        }
    };  
            
    //�ʒm���ǂݍ���
    private boolean ringtonePreference_OnPreferenceChange(Preference preference, Object newValue){     
        String url = (String)newValue; 
        Uri uri;  
        Ringtone ringtone;  
        if ("".equals(url)) {
            preference.setSummary("�T�C�����g");
        } else {
            uri = Uri.parse(url);  
            ringtone = RingtoneManager.getRingtone(this, uri);  
            preference.setSummary(ringtone.getTitle(this));  
        }  
        return true;  
    }

    //�ʒm���ǂݍ���
    private void ringtonePreference_OnPreferenceChange(){     
    	Preference ring_preference = (Preference)getPreferenceScreen().findPreference(getString(R.string.ringtone_pref));
    	String url = p.getString(getString(R.string.ringtone_pref), getString(R.string.ringtone_default_value)); 
    	Uri uri;  
        Ringtone ringtone;  
        if ("".equals(url)) {
        	ring_preference.setSummary("�T�C�����g");
        } else {
            uri = Uri.parse(url);  
            ringtone = RingtoneManager.getRingtone(this, uri);  
            ring_preference.setSummary(ringtone.getTitle(this));  
        }
    }  

    //�L�����N�^�[�́@PreferenceChange���X�i�[  
    private OnPreferenceChangeListener listPreference_OnPreferenceChangeListener = new OnPreferenceChangeListener(){  
        @Override  
        public boolean onPreferenceChange(Preference preference, Object newValue) {  
            return listPreference_OnPreferenceChange(preference,newValue);  
        }
    };  
            
    //�L�����N�^�[�ǂݍ���
    private boolean listPreference_OnPreferenceChange(Preference preference, Object newValue){
    	String nin = (String)newValue; 
        String ninX = "";
        String[] charName = getResources().getStringArray(R.array.character_list_name);
        String[] charValue = getResources().getStringArray(R.array.character_list_value);

        try{
        	ninX = wetherArray[Integer.parseInt(nin)];
        }catch(NumberFormatException e){
        	for (int j=0 ; j<charValue.length ; j++) {
        		if (charValue[j].equals(nin)){
        			ninX = charName[j];
        			break;
        		}
        	}
        }

        preference.setSummary(ninX);  
        return true;
    }

    //�L�����N�^�[�ǂݍ���
    private void listPreference_OnPreferenceChange_char(){     
    	ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference(getString(R.string.character_pref));
    	list_preference.setSummary(list_preference.getEntry());
    }

    private void listPreference_OnPreferenceChange_wether(){     
    	ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference(getString(R.string.weather_pref));
    	list_preference.setSummary(list_preference.getEntry());
    }
    
    
    //�A���[���X�^�[�g�E�X�g�b�v
    private void alermStart(String alermTiming){
    	CheckBoxPreference checkbox_preference = (CheckBoxPreference)getPreferenceScreen().findPreference(alermTiming + "_checkbox");
        MyAlarmManager mam = new MyAlarmManager(c);

    	if (checkbox_preference.isChecked()) {
            mam.addAlarm(p.getInt(alermTiming + "_h", 0),p.getInt(alermTiming + "_m", 0),alermTiming); 
    	} else {
            mam.stopAlarm(alermTiming); 
    	}
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
    
	public String[] wetherArray = {
			"",
			"�t��",
			"����",
			"���G",
			"�D�y",
			"�〈��",
			"��m��",
			"�ԑ�",
			"�k��",
			"���",
			"����",
			"���H",
			"�эL",
			"����",
			"�Y��",
			"����",
			"�]��",
			"�X",
			"�ނ�",
			"����",
			"�H�c",
			"����",
			"����",
			"�{��",
			"��D�n",
			"���",
			"����",
			"�R�`",
			"�đ�",
			"��c",
			"�V��",
			"����",
			"�����l",
			"�ᏼ",
			"�É�",
			"�ԑ�",
			"�O��",
			"�l��",
			"���É�",
			"�L��",
			"��",
			"���R",
			"��",
			"���h",
			"�x�R",
			"����",
			"����",
			"�֓�",
			"����",
			"�։�",
			"�V��",
			"����",
			"���c",
			"����",
			"����",
			"�y�Y",
			"�F�s�{",
			"��c��",
			"�O��",
			"�݂Ȃ���",
			"��������",
			"�F�J",
			"����",
			"����",
			"�哇",
			"���䓇",
			"����",
			"��t",
			"���q",
			"�َR",
			"���l",
			"���c��",
			"����",
			"���{",
			"�ѓc",
			"�b�{",
			"�͌���",
			"���",
			"�F��",
			"���s",
			"����",
			"���",
			"�_��",
			"�L��",
			"�ޗ�",
			"����",
			"�a�̎R",
			"����",
			"���R",
			"�ÎR",
			"�L��",
			"����",
			"���]",
			"�l�c",
			"����",
			"����",
			"�Ďq",
			"����",
			"�R��",
			"����",
			"��",
			"����",
			"���a��",
			"����",
			"���R",
			"�V���l",
			"�F�a��",
			"���m",
			"����",
			"����",
			"����",
			"����",
			"�ђ�",
			"�v����",
			"�啪",
			"����",
			"���c",
			"����",
			"����",
			"������",
			"����",
			"���]",
			"����",
			"�ɖ���",
			"�F�{",
			"���h���P",
			"���[",
			"�l�g",
			"�{��",
			"����",
			"�s��",
			"�����",
			"������",
			"����",
			"��q��",
			"����",
			"�ߔe",
			"����",
			"�v�ē�",
			"��哌��",
			"�{�Ó�",
			"�Ί_��",
			"�^�ߍ���"
	};

}

