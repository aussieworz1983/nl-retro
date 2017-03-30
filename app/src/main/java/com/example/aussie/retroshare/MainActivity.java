package com.example.aussie.retroshare;
/*
* The main activity will offer the option to selcet each rom selection by console simple gird button layout
* */
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {

    //array of emu names
    String[] emulators = new String[]{
      "Atari 2700",
            "Amiga",
            "Commodore",
            "Dreamcast",
            "Gameboy",
            "Master System",
            "Nes",
            "Snes",
            "Megadrive",
            "Playstation"
    };


    EmulatorEnviroment m_emuEnv;

    // label state
    int m_progressType=0;



/*on creation off the activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView emulator_list = (ListView)findViewById(R.id.emulator_list);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,emulators);
        emulator_list.setAdapter(adapter);
        // detect XBMC/Kodi
        m_emuEnv = new EmulatorEnviroment(this);
        m_emuEnv.DetectEnvironment();
    }
    /*on start*/
    @Override
    protected void onStart(){

        super.onStart();
        TextView setup_text = (TextView)findViewById(R.id.enviroment_text);
        if(m_emuEnv.isInstalled()){
            if (m_emuEnv.getSetupType() == EmulatorEnviroment.EmulatorSetupType.INTERNAL) {
                setup_text.setText(R.string.found_internal);
            } else if (m_emuEnv.getSetupType() == EmulatorEnviroment.EmulatorSetupType.SD) {
                setup_text.setText(R.string.found_external);
            }

        }
        else{
            setup_text.setText(R.string.no_internal);
        }
    }
    /**
     * cancel our downloading.
     */
    public void onBackPressed()
    {
        // will tell the async task to exit.
        m_emuEnv.CancelDeployment();

        // exit now
        super.onBackPressed();
    }

}
