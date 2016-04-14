package cn.cmbc.alm.notifer;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {
	
	String field=null;
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("Field".equals(qName)){
			field=qName;
		}else if("Value".equals(qName)){
			field=null;
//			System.out.println("");
		}
		
		System.out.println(">>>>>>>"+localName+" "+qName+" ");
		
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		
	}
	
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		System.out.println(new String(ch,0,length));
	}
	
}
