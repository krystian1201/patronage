package com.example.patronagetest1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.Menu;
import android.widget.TextView;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 




import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



class MyTask extends AsyncTask<Void, Integer, Void> 
{
	MainActivity mainActivity;
	
	MyTask(MainActivity act)
	{
		mainActivity = act;
	}
	
    // Do the long-running work in here
    public Void doInBackground(Void... params) 
    {
    	mainActivity.processData();
    	
    	return null;
    }

    // This is called each time you call publishProgress()
    /*protected void onProgressUpdate(Integer... progress) 
    {

        String content = mainActivity.getResources().getString(R.string.processing_data_message);
        content += "\n" + "Percent processed: " + progress[0] + "\n";
        
        TextView textView = (TextView)mainActivity.findViewById(R.id.main_text_view);
    	textView.setText(content);

		textView.append(progress[0].toString() + "\n");
    }*/
    
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


class Items
{

	SortType sortType;
	
	List<Integer> intList;
	List<String> stringList;
	
	Items()
	{
		sortType = null;
		intList = new ArrayList<Integer>();
    	stringList = new ArrayList<String>();
	}
	
	public void SortIntList()
	{
		Integer [] intArray = new Integer[0];
		intArray = (Integer [])this.intList.toArray(intArray);
		
		if (this.sortType==SortType.ascending) 
		{
			Arrays.sort(intArray);
		} 
		else if(this.sortType==SortType.descending)
		{
			Arrays.sort(intArray);
			
			for(int i=0;i<intArray.length/2;i++) 
			{
			     // swap the elements
			     int temp = intArray[i];
			     intArray[i] = intArray[intArray.length-(i+1)];
			     intArray[intArray.length-(i+1)] = temp;
			}
		}
		
		this.intList = Arrays.asList(intArray);
	}
	
	/*public void Print(Activity activity)
	{
		String content = "";
		content += Environment.getExternalStorageDirectory() + "\n";
		content += "sort type: " + this.sortType + "\n";
		
		for(int itemInt : this.intList)
		{
			content += "Integer Item: " +  itemInt + "\n";
		}
		
		for (String itemString : this.stringList)
		{
			
			content += "String Item: " +  itemString + "\n";
		}
		
		
		TextView textView = (TextView)activity.findViewById(R.id.main_text_view);
		textView.append(content);
		//textView.setText(content);
	}*/
	
	public void SaveAsXMLFile(String filePath)
	{
		try 
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			//root (items) element
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			
			
			Element rootElement = doc.createElement("items");
			doc.appendChild(rootElement);
			
			for(int itemInt : this.intList)
			{
				//child (item) elements
				Element item = doc.createElement("item");
				item.appendChild(doc.createTextNode(String.valueOf(itemInt)));
				rootElement.appendChild(item);
				
				// set attribute to item element
				Attr attr = doc.createAttribute("type");
				attr.setValue("integer");
				item.setAttributeNode(attr);
			}
			
			for (String itemString : this.stringList)
			{
				
				//child (item) elements
				Element item = doc.createElement("item");
				item.appendChild(doc.createTextNode(itemString));
				rootElement.appendChild(item);
				
				// set attribute to item element
				Attr attr = doc.createAttribute("type");
				attr.setValue("string");
				item.setAttributeNode(attr);
			}
			
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			//transformerFactory.setAttribute("indent-number", new Integer(2));
			
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			
			
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			transformer.transform(source, result);
			
		} 
		catch (ParserConfigurationException pce) 
		{
			pce.printStackTrace();
		}
		catch (TransformerException tfe) 
		{
			tfe.printStackTrace();
		}
		
		
	}

}//end class Items

public class MainActivity extends Activity 
{

	
	
	public void displayInitialMessage()
	{
		String processingMessage = getResources().getString(R.string.processing_data_message);
		
		TextView textView = (TextView)findViewById(R.id.main_text_view);
		textView.append(processingMessage + "\n");
		
	}
	
	public void displayFinalMessage()

	{
		String processingFinishedMessage = getResources().getString(R.string.processing_data_finished_message);
		TextView textView = (TextView)findViewById(R.id.main_text_view);
		textView.append(processingFinishedMessage);
	}
		
	
	private Items parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		Items items = null;
        int eventType = parser.getEventType();
        String itemString = null;
        int itemInt = 0;
        String dataType = null;
        

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            String name = null;
            switch (eventType)
            {
                case XmlPullParser.START_DOCUMENT:
                	items = new Items();
                	
                    break;
                    
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    itemString = null;
                    dataType = null;
                    
                    if (name.equals("items")) 
                    {
                    	String attributeName = parser.getAttributeName(0);
                    	
                    	if (attributeName.equals("to_sort")) 
                    	{
                    		String sortType = parser.getAttributeValue(0);
                    		
							if (sortType.equals("ascending"))
							{
								items.sortType = SortType.ascending;
							}
							else if (sortType.equals("descending")) 
							{
								items.sortType = SortType.descending;
							}
                    	}
					}
                    else if (name.equals("item"))
                    {
                    	String attributeName = parser.getAttributeName(0);
                    	
                    	if (attributeName.equals("type")) 
                    	{
                    		dataType = parser.getAttributeValue(0);
                    		
							if (dataType.equals("string"))
							{
								itemString = parser.nextText();
							}
							else if (dataType.equals("integer")) 
							{
								itemInt = Integer.parseInt(parser.nextText());
							}
						}
                    	
                    } 
                    
                    break;
                    
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    
                    if (name.equals("item"))
                    {
                    	if (dataType.equals("string")) 
                    	{
                    		items.stringList.add(itemString);
						}
                    	else if (dataType.equals("integer"))
                    	{
							items.intList.add(itemInt);
						}
                    	
                    	
                    } 
                    
            }//end switch
            
            eventType = parser.next();
            
        }//end while
        
        return items;

	}
	
	
	
	public void processData()
	{
		
		try
		{
			String inputFilePath = Environment.getExternalStorageDirectory() + "/patronage/task1/in.xml";
			
			
			//create an input stream
			//InputStream inStream = getApplicationContext().getAssets().open(filePath);
			InputStream inStream = new FileInputStream(inputFilePath);
			
			//create and set an XML parser
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inStream, null);
			
			Items items = parseXML(parser);
			
			items.SortIntList();
			
			//items.Print(this);
			
			String outputFilePath = Environment.getExternalStorageDirectory() + "/patronage/task1/out.xml";
			
			
			items.SaveAsXMLFile(outputFilePath);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(XmlPullParserException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		MyTask myTask = new MyTask(this);
		myTask.execute();
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}


/*int max = 10000000;

for (int i = 0; i <= max; i++) 
{
	if (i%(max/100) == 0) 
	{
		publishProgress( (int)((i/(float)max)*100) );
	}
	
	
    // Escape early if cancel() is called
    if (isCancelled()) break;
}*/
