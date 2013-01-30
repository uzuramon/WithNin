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

	//設定
	private SharedPreferences p;
	private Context c;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.v(getString(R.string.log),"MenuActivity　setText start");

    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.pref);

    	c = getApplicationContext();
        p = PreferenceManager.getDefaultSharedPreferences(c);
        
    	//初回起動設定
    	firstListener();
    	
    	//初期表示の設定
    	onSharedPreferenceChanged();
    	
    	//おはようアラーム通知設定
    	setClickListener("morning_checkbox");
    	
    	//おはようアラーム時間設定
    	setClickListener("morning_time");
    	
    	//おやすみアラーム通知設定
    	setClickListener("night_checkbox");

    	//おやすみアラーム時間設定
    	setClickListener("night_time");

    	//おやすみアラーム通知設定
    	setClickListener("nintama_checkbox");

    	//おやすみアラーム時間設定
    	setClickListener("nintama_time");

    	//キャラクター設定
        ListPreference prefChar = (ListPreference)findPreference(getString(R.string.character_pref));   
        // リスナーを設定する  
        prefChar.setOnPreferenceChangeListener(listPreference_OnPreferenceChangeListener);  

    	//キャラクター設定
        ListPreference prefWether = (ListPreference)findPreference(getString(R.string.weather_pref));   
        // リスナーを設定する  
        prefWether.setOnPreferenceChangeListener(listPreference_OnPreferenceChangeListener);  
    	
    	//バイブレーション設定
    	setClickListener("vibration_checkbox");
    	
    	//LED設定
    	setClickListener("led_checkbox");

    	//通知音設定
        RingtonePreference pref = (RingtonePreference)findPreference(getString(R.string.ringtone_pref));   
        // リスナーを設定する  
        pref.setOnPreferenceChangeListener(ringtonePreference_OnPreferenceChangeListener);  

		Log.v(getString(R.string.log),"MenuActivity　setText end");
    }
    
    //クリック待ち
	private void setClickListener(String prefName){
    	Preference pref = this.findPreference(prefName);
    	pref.setOnPreferenceClickListener(onPreferenceClickListener);
    }
    
    //初回起動設定
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

        	//クリックされたキーを取得
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

    	//おはようアラーム通知設定読み込み
        alermCheckGet("morning");
    	 
    	//おはようアラーム通知時間読み込み
        alermTimeGet("morning");
 
    	//おやすみアラーム通知設定読み込み
        alermCheckGet("night");
    	 
    	//おやすみアラーム通知時間読み込み
        alermTimeGet("night");

    	//にんたまアラーム通知設定読み込み
        alermCheckGet("nintama");
    	 
    	//にんたまアラーム通知時間読み込み
        alermTimeGet("nintama");

        //キャラクター読み込み
        listPreference_OnPreferenceChange_char();
        
        //キャラクター読み込み
        listPreference_OnPreferenceChange_wether();

        //バイブレーション
        settingCheckGet("vibration");
        
        //LED
        settingCheckGet("led");
        
        //通知音
        ringtonePreference_OnPreferenceChange();

    }
    
    //チェックボックス通知読み込み
    private void alermCheckGet(String alermTiming){
    	CheckBoxPreference checkbox_preference = (CheckBoxPreference)getPreferenceScreen().findPreference(alermTiming + "_checkbox");
    	if (checkbox_preference.isChecked()) {
    		checkbox_preference.setSummary("お知らせする");
    	} else {
    		checkbox_preference.setSummary("お知らせしない");
    	}
    }
    
    //通知時間読み込み
    private void alermTimeGet(String alermTiming){
    	String text = String.format("%1$02d時%2$02d分", p.getInt(alermTiming + "_h", 0),p.getInt(alermTiming + "_m", 0));
    	Preference time_preference = (Preference)getPreferenceScreen().findPreference(alermTiming + "_time");
    	time_preference.setSummary(text);
    }
 
    
    //チェックボックス通知読み込み
    private void settingCheckGet(String key){
    	CheckBoxPreference checkbox_preference = (CheckBoxPreference)getPreferenceScreen().findPreference(key + "_checkbox");
    	if (checkbox_preference.isChecked()) {
    		checkbox_preference.setSummary("有効");
    	} else {
    		checkbox_preference.setSummary("無効");
    	}
    }
    
    // 着信音Preferenceの　PreferenceChangeリスナー  
    private OnPreferenceChangeListener ringtonePreference_OnPreferenceChangeListener = new OnPreferenceChangeListener(){  
        @Override  
        public boolean onPreferenceChange(Preference preference, Object newValue) {  
            return ringtonePreference_OnPreferenceChange(preference,newValue);  
        }
    };  
            
    //通知音読み込み
    private boolean ringtonePreference_OnPreferenceChange(Preference preference, Object newValue){     
        String url = (String)newValue; 
        Uri uri;  
        Ringtone ringtone;  
        if ("".equals(url)) {
            preference.setSummary("サイレント");
        } else {
            uri = Uri.parse(url);  
            ringtone = RingtoneManager.getRingtone(this, uri);  
            preference.setSummary(ringtone.getTitle(this));  
        }  
        return true;  
    }

    //通知音読み込み
    private void ringtonePreference_OnPreferenceChange(){     
    	Preference ring_preference = (Preference)getPreferenceScreen().findPreference(getString(R.string.ringtone_pref));
    	String url = p.getString(getString(R.string.ringtone_pref), getString(R.string.ringtone_default_value)); 
    	Uri uri;  
        Ringtone ringtone;  
        if ("".equals(url)) {
        	ring_preference.setSummary("サイレント");
        } else {
            uri = Uri.parse(url);  
            ringtone = RingtoneManager.getRingtone(this, uri);  
            ring_preference.setSummary(ringtone.getTitle(this));  
        }
    }  

    //キャラクターの　PreferenceChangeリスナー  
    private OnPreferenceChangeListener listPreference_OnPreferenceChangeListener = new OnPreferenceChangeListener(){  
        @Override  
        public boolean onPreferenceChange(Preference preference, Object newValue) {  
            return listPreference_OnPreferenceChange(preference,newValue);  
        }
    };  
            
    //キャラクター読み込み
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

    //キャラクター読み込み
    private void listPreference_OnPreferenceChange_char(){     
    	ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference(getString(R.string.character_pref));
    	list_preference.setSummary(list_preference.getEntry());
    }

    private void listPreference_OnPreferenceChange_wether(){     
    	ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference(getString(R.string.weather_pref));
    	list_preference.setSummary(list_preference.getEntry());
    }
    
    
    //アラームスタート・ストップ
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
			"稚内",
			"旭川",
			"留萌",
			"札幌",
			"岩見沢",
			"倶知安",
			"網走",
			"北見",
			"紋別",
			"根室",
			"釧路",
			"帯広",
			"室蘭",
			"浦河",
			"函館",
			"江差",
			"青森",
			"むつ",
			"八戸",
			"秋田",
			"横手",
			"盛岡",
			"宮古",
			"大船渡",
			"仙台",
			"白石",
			"山形",
			"米沢",
			"酒田",
			"新庄",
			"福島",
			"小名浜",
			"若松",
			"静岡",
			"網代",
			"三島",
			"浜松",
			"名古屋",
			"豊橋",
			"岐阜",
			"高山",
			"津",
			"尾鷲",
			"富山",
			"伏木",
			"金沢",
			"輪島",
			"福井",
			"敦賀",
			"新潟",
			"長岡",
			"高田",
			"相川",
			"水戸",
			"土浦",
			"宇都宮",
			"大田原",
			"前橋",
			"みなかみ",
			"さいたま",
			"熊谷",
			"秩父",
			"東京",
			"大島",
			"八丈島",
			"父島",
			"千葉",
			"銚子",
			"館山",
			"横浜",
			"小田原",
			"長野",
			"松本",
			"飯田",
			"甲府",
			"河口湖",
			"大津",
			"彦根",
			"京都",
			"舞鶴",
			"大阪",
			"神戸",
			"豊岡",
			"奈良",
			"風屋",
			"和歌山",
			"潮岬",
			"岡山",
			"津山",
			"広島",
			"庄原",
			"松江",
			"浜田",
			"西郷",
			"鳥取",
			"米子",
			"下関",
			"山口",
			"柳井",
			"萩",
			"徳島",
			"日和佐",
			"高松",
			"松山",
			"新居浜",
			"宇和島",
			"高知",
			"室戸",
			"清水",
			"福岡",
			"八幡",
			"飯塚",
			"久留米",
			"大分",
			"中津",
			"日田",
			"佐伯",
			"長崎",
			"佐世保",
			"厳原",
			"福江",
			"佐賀",
			"伊万里",
			"熊本",
			"阿蘇乙姫",
			"牛深",
			"人吉",
			"宮崎",
			"延岡",
			"都城",
			"高千穂",
			"鹿児島",
			"鹿屋",
			"種子島",
			"名瀬",
			"那覇",
			"名護",
			"久米島",
			"南大東島",
			"宮古島",
			"石垣島",
			"与那国島"
	};

}

