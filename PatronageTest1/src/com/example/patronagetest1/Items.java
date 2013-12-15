package com.example.patronagetest1;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class Items 
{
	SortType sortType;
	
	List<Integer> intList;
	List<String> stringList;
	
	Items()
	{
		//a default type of sorting
		sortType = SortType.ascending;
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
		
		
	}//end SaveAsXMLFile
	
}
