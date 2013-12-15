package com.example.patronagetest1;

import java.io.InputStream;

import android.os.AsyncTask;

public class MyTask extends AsyncTask<Void, Integer, Void> 
{
	MainActivity mainActivity;
	InputStream inStream;
	
	MyTask(MainActivity act, InputStream inS)
	{
		mainActivity = act;
		inStream = inS;
	}
	
    public Void doInBackground(Void... v) 
    {
    	mainActivity.processData(inStream);
		
    	return null;
    }

    protected void onPreExecute () 
    {
    	mainActivity.displayInitialMessage();
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Void v) 
    {
    	mainActivity.displayFinalMessage();
    	
    }
}
