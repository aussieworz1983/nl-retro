package com.example.aussie.retroshare;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by aussie on 28/03/17.
 * The emulator enviroment class will check what emulators are installed check for controller configuratiuon to
 * also will allow the deletion of roms or entire ron folders
 */

public class EmulatorEnviroment {


    /// when any emulator is detected this will contain what
    public enum EmulatorSetupType {
        NONE, SD, INTERNAL
    }

    /// represents our context (usually the activity)
    private Context m_ctx;

    /// setup type
    private EmulatorSetupType m_setupType = EmulatorSetupType.NONE;

    // roms home dir
    private String m_romsHome ;

    /// log file. for our simple purpose we dont need a logging framework, yet.
    private File m_log;
    private FileWriter m_logWriter;

    /// our downloader instance. used to start and cancel
    private RomDownloader m_downloader;


    /*
     * main ctor
     * @param ctx Our app context
     */
    public EmulatorEnviroment(Context ctx)
    {
        m_ctx = ctx;

    }


    public void OpenLog()
    {
        if (m_logWriter != null)
        {
            return;
        }

        String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/em_log_" + android.os.Process.myPid() + ".txt";
        m_log = new File(logPath);
        try
        {
            m_logWriter = new FileWriter(m_log);
            Log.i("EmulatorEnviroment", "Opened log file: "+logPath);
        }
        catch (IOException e)
        {
            Log.e("EmulatorEnviroment", "Cannot open log for writing:  "+logPath);
        }
    }

    /**
     * Detect what folders are made for emulation roms
     * detect what emulators are installed
     */
    public void DetectEnvironment()
    {
        OpenLog();
        m_setupType = EmulatorSetupType.NONE;
        m_romsHome = "";
        try
        {
            // attempt to detect userdata directory for n64
            //home path string points towards .n64oid folder in the root of the home directory
            String internalPath = new String(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Roms");
            m_logWriter.write("Testing roms home at: " + internalPath+"\n");
            File finternalPath = new File(internalPath);
            if (finternalPath.exists() && finternalPath.isDirectory()) {
                m_logWriter.write("Found roms folder internally dir!\n");
                m_setupType = EmulatorSetupType.INTERNAL;
            }
            else
            {
                // attempt detection via package manager
                m_logWriter.write("checking sd for rom folders\n");

            }

            String externalPath = new String(Environment.getExternalStorageDirectory().getAbsolutePath() + "/storage/emulated/legacy");
            m_logWriter.write("Testing roms home at: " + externalPath+"\n");
            File fexternalPath = new File(externalPath);
            if (fexternalPath.exists() && fexternalPath.isDirectory()) {
                m_logWriter.write("Found roms folder internally dir!\n");
                m_setupType = EmulatorSetupType.SD;
            }
            else
            {
                // attempt detection via package manager
                m_logWriter.write("checking sd for rom folders\n");

            }
            m_logWriter.write("Setting roms home to: "+m_romsHome+"\n");
            m_logWriter.flush();
        }
        catch(Exception e)
        {
            Log.e("EmulatorEnivroment", e.toString());
        }
    }





    /**
     * Easily detect if anything is installed
     * @return
     */
    public boolean isInstalled()
    {
        return m_setupType != EmulatorSetupType.NONE;
    }

    /**
     * easily understood
     * @return
     */
    public EmulatorSetupType getSetupType()
    {
        return m_setupType;
    }

    /**
     * used to close our log. if everything was successful we'll also delete it
     */
    public void Cleanup(boolean bSuccess)
    {
        // stop downloading if need be

        try
        {
            m_logWriter.close();
            if (bSuccess)
            {
                // ok to remove log file
                if (m_log.delete())
                {
                    Log.i("KodiEnvironmentLogger", "Deleted log file " + m_log.getAbsolutePath());
                }
                m_log = null;
            }
        }
        catch(Exception e)
        {
            Log.e("KodiEnvironmentLogger", "Failed to close log file");
        }
    }

    /**
     * retrieve the settings and extract them. this should only be called from the UI thread
     */
    public void DeploySettingsFile()
    {
        // simple launch, download and extract
        if (m_downloader == null)
        {
            m_downloader= new RomDownloader(m_ctx, m_logWriter);
         //   m_downloader.execute(m_ctx.getString(R.string.download_url), m_kodiHome);
        }
    }

    /**
     * notify our downloader to stop execution
     */
    public void CancelDeployment()
    {
        if (m_downloader != null)
        {
            m_downloader.cancel(true);
            m_downloader = null;
        }
    }
}
