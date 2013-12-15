package com.example.patronagetest1;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MyXMLParser 
{
	XmlPullParser parser;
	
	Items items = null;
    String itemString = null;
    int itemInt = 0;
    String dataType = null;
    boolean validInt = false;
    
    
	
    public MyXMLParser(XmlPullParser p) 
    {
		parser = p;
	}
    
	private void parseXMLItem() throws XmlPullParserException,IOException
	{
		String attributeName = parser.getAttributeName(0);
		
		
    	if (attributeName != null && attributeName.equals("type")) 
    	{
    		dataType = parser.getAttributeValue(0);
    		
			if (dataType.equals("integer")) 
			{
				try
				{
					itemInt = Integer.parseInt(parser.nextText());
					validInt = true;
				}
				catch(NumberFormatException e)
				{
					//this variable is used to prevent elements
					//which are not ints to be added to intList
					validInt = false;
					
					//this variable is used to inform a user
					//that the exception during parsing occurred
					MainActivity.intParseError = true;
				}
				
			}//dataType == integer
			else if(dataType.equals("string"))
			{
				itemString = parser.nextText();
			}
			else
			{
				itemString = parser.nextText();
				//by default (everything can be interpreted as string)
		    	dataType = "string"; 
		    	MainActivity.isDataTypeDefault = true;
			}
	
		}// end if(attributeName == "type")
    	else
    	{
    		itemString = parser.nextText();
			//by default (everything can be interpreted as string)
	    	dataType = "string"; 
	    	MainActivity.isDataTypeDefault = true;
    	}
    	
	}// end parseXMLItem
	
	private void parseXMLItems() throws XmlPullParserException,IOException
	{
		String attributeName = parser.getAttributeName(0);
    	
    	if (attributeName != null && attributeName.equals("to_sort")) 
    	{
    		String sortType = parser.getAttributeValue(0);
    		
			if (sortType.equals("ascending"))
			{
				items.sortType = SortType.ascending;
				MainActivity.isSortTypeDefault = false;
			}
			else if (sortType.equals("descending")) 
			{
				items.sortType = SortType.descending;
				MainActivity.isSortTypeDefault = false;
			}
    	}
    	
	}
    	
    public Items parseXML() throws XmlPullParserException,IOException
	{
		
    	int eventType = parser.getEventType();
    	int tagCounter = 0;
    	
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            String name = null;
            switch (eventType)
            {
                    
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    itemString = null;
                    dataType = null;
                    tagCounter++;
                    
                    if (name.equals("items")) 
                    {
                    	if (tagCounter==1) 
                    	{
                    		MainActivity.isValidItemsTag = true;
            				items = new Items();
            				
                    		MainActivity.isItemsTagRoot = true;
                    		parseXMLItems();
                    	}
                    	else 
                    		MainActivity.isItemsTagRoot = false;	
					}
                    else if (name.equals("item") && MainActivity.isValidItemsTag
                    		&& MainActivity.isItemsTagRoot)
                    {
                    	parseXMLItem();
                    	
                    }
                    else if(name != null && MainActivity.isValidItemsTag
                    		&& MainActivity.isItemsTagRoot)
                    {
                    	MainActivity.additionalElems = true;
                    }
                    
                    break;
                    
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    
                    if (name.equals("item") && MainActivity.isValidItemsTag
                    		&& MainActivity.isItemsTagRoot)
                    {
                    	if (dataType.equals("string")) 
                    	{
                    		items.stringList.add(itemString);
						}
                    	else if (dataType.equals("integer") && validInt)
                    	{
							items.intList.add(itemInt);
						}
	
                    } 
                    
            }//end switch
            
            eventType = parser.next();
            
        }//end while
        
        return items;

	}//end parseXML
    
}//end MyXMLParser

	
	
