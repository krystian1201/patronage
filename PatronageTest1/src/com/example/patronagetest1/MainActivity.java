package com.example.patronagetest1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.Menu;
import android.widget.TextView;


public class MainActivity extends Activity 
{
	public static boolean intParseError = false;
	public static boolean additionalElems = false;
	
	//if there is a valid <items> tag
	public static boolean isValidItemsTag = false;
	
	//if the <items> tag is a root in the input file
	public static boolean isItemsTagRoot = false;
	
	public static boolean isValidXMLDocument = false;
	public static XmlPullParserException xmlException;
	public static boolean isSortTypeDefault = true;
	public static boolean isDataTypeDefault = false;
	
	//displays a message just before data begin to be processed
 	public void displayInitialMessage()
	{
 		
 		String processingMessage = getResources().getString(R.string.processing_data_message);
 			
 		TextView textView = (TextView)findViewById(R.id.main_text_view);
 		textView.append(processingMessage + "\n");

	}
	
 	//displays a message right after data has been processed
	public void displayFinalMessage()
	{
		TextView textView = (TextView)findViewById(R.id.main_text_view);
		

		if (!isValidXMLDocument) 
		{
			textView.append("\nThis file is not a valid XML document.\n\n"
					+ xmlException.getMessage() + "\n\n"
					+ "Processing stopped. No output written.\n\n");
		}
		else
		{
			if (!isItemsTagRoot) 
			{
				textView.append("\n<items> is not a root element in this file\n"
						+ "Processing stopped. No output written.\n\n");
			}	
			else 
			{
				if(!isValidItemsTag)
				{
					textView.append("\nInput file does not contain an 'items' tag or this tag is invalid\n"
							+ "Processing stopped. No output written.\n\n");
				}
				else //messages for non-critical cases (they don't cause program to finish early)
				{
					if (isSortTypeDefault) 
					{
						textView.append("\nInput file does not contain a sorting order attribute "
								+ "or its value is incorrect. Ascending order was assumed as default.\n");
					}
					
					if (additionalElems) 
					{
						textView.append("\nInput file contains one or more elements which are not valid 'item' " +
										" elements. They were ignored.\n");
					}
					
					if (intParseError) 
					{
						textView.append("\nOne or more elements with the attribute 'type' = 'integer' failed " +
							"parsing (were not in fact integers). They were ignored.\n");
					}
					
					if (isDataTypeDefault) 
					{
						textView.append("\nOne or more elements do not have 'type' attribute " +
								"or its value is incorrect.\nString type was assumed as default.\n");
					}
					
					String processingFinishedMessage = getResources().getString(R.string.processing_data_finished_message);
					textView.append("\n" + processingFinishedMessage);
				}
		
			}
		}
			
	}
		

	public void processData(InputStream inStream)
	{
		Items items = null;
		
		try
		{
			//create and set an XML parser
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inStream, null);
			
			//instance of class containing methods for
			//processing the particular type of input XML file
			//used in this task
			MyXMLParser myParser = new MyXMLParser(parser);
			
			items = myParser.parseXML();
			
			if (items != null) 
			{
				items.SortIntList();
				
				String outputFilePath = Environment.getExternalStorageDirectory() + 
						"/patronage/task1/out.xml";
				
				items.SaveAsXMLFile(outputFilePath);
			}
			isValidXMLDocument = true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		//in case our file is not a valid XML document
		catch(XmlPullParserException e)
		{
			xmlException = e;
			isValidXMLDocument = false;
			e.printStackTrace();
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		String inputFilePath = Environment.getExternalStorageDirectory() + 
				"/patronage/task1/in.xml";
		
		try
		{
			//create an input stream
			InputStream inStream = new FileInputStream(inputFilePath);
			
			//start a background thread which will process our data
			MyTask myTask = new MyTask(this, inStream);
			myTask.execute();
			
		}
		catch(IOException e)
		{
			TextView textView = (TextView)findViewById(R.id.main_text_view);
			textView.append("\nIO Exception: \n" + e.getMessage() + "\n\n");
			
			e.printStackTrace();
		}	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}



