package cn.cmbc.alm.notifer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import sun.misc.BASE64Encoder;


public class HttpHelper {
	String protocol="http://";
	String hostport="197.3.133.65:8080";
	String baseUrl=protocol+hostport;
	String cookies="";
	String uname="何威";

	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public HttpHelper(String uname){
		this.uname=uname;
	}
	public List<Entity> getIssue() {
//		HttpHelper x=new HttpHelper();
		try {
			if(cookies.length()==0){
				login();
				getSession();
			}
			if(uname==null|uname.trim().length()==0)
				uname="何威";
			List<Entity> rs=getDefect(uname);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	public void login() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String auth=new BASE64Encoder().encode("史圣杰:123456".getBytes());
		HttpGet httpget = new HttpGet(baseUrl+"/qcbin/authentication-point/authenticate");
		httpget.setHeader("Authorization", "Basic "+auth);
		CloseableHttpResponse response = httpclient.execute(httpget);
		cookies=getCookie(response);
	}
	
	public void getSession() throws ClientProtocolException, IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost=new HttpPost(baseUrl+"/qcbin/rest/site-session");
		httppost.setHeader("Cookie", cookies);
		CloseableHttpResponse response = httpclient.execute(httppost);
		String rs=getCookie(response);
		cookies+=rs;
	}
	
	public List<Entity> getDefect(String name) throws ClientProtocolException, IOException, UnsupportedOperationException, DocumentException{
//		String url=protocol+hostport+getUrl("/qcbin/rest/domains/测试管理中心/projects/缺陷管理");
//		url=url+"defects?query={owner["+URLEncoder.encode(name)+"]}";
//		System.out.println(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(baseUrl+"/qcbin/rest/domains/测试管理中心/projects/缺陷管理/defects?query=%7Bowner["+name+"]%7D&fields=description,id,name,detected-by,user-13");
		httpget.setHeader("Cookie", cookies);
		httpget.setHeader("Content-Type","application/atom+xml");  
		httpget.setHeader("charset","utf-8"); 
		CloseableHttpResponse response = httpclient.execute(httpget);
//		printBody(response);
		toFile("root--->>"+httpget.getURI().toURL().toString());
		return  parseDom4j(response.getEntity().getContent());
	}
	
	private List<Entity> parseDom4j(InputStream in){
		List<Entity> ret=new ArrayList<Entity>();
		SAXReader reader = new SAXReader();
		 Document document = null;
		try {
			document = reader.read(in);
			toFile("root--->>"+document.getRootElement());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		 Element root = document.getRootElement();
		 Entity.count=root.attribute("TotalResults").getText();
		 
		 //
		 List EntityList=root.elements("Entity");
		 for(int i=0;i<EntityList.size();i++){
			 Entity ent=new Entity();
			 Element entElement=(Element)(EntityList.get(i));
			 Element fields=(Element) entElement.elements("Fields").get(0);
			 
			 List fieldList=fields.elements("Field");
			 for(int j=0;j<fieldList.size();j++){
				 Element field=(Element) fieldList.get(j);
				 String name=field.attribute("Name").getText();
				 Element val= (Element)(field.elements("Value").get(0));
				 String value=val.getText();
				 setValue(ent,name,value);
			 }
			 ret.add(ent);
		 }
		 
		 return ret;
		 
	}
	
	
	private void setValue(Entity ent, String name, String value) {
		if("id".equals(name)){
			ent.setId(value);
		}else if("user-13".equals(name)){
			ent.setUser13(value);
		}else if("detected-by".equals(name)){
			ent.setDetectedby(value);
		}else if("description".equals(name)){
			ent.setDescription(value);
		}  
	}

	private void parse(InputStream in){
		try{
			SAXParserFactory factory = SAXParserFactory. newInstance();//sax使用的工厂设计模式，通过SAXParserFactory 获取解析器parser 
			SAXParser saxParser = factory.newSAXParser();//在从解析器中获得解析xml文件的xmlReader 
			SAXHandler handle = new SAXHandler();
			saxParser.parse(in , handle );//xmlReader 读取流式的xml文件时，需要完成一个Handler的设置，Handler是继承的DefaultHandler
			in.close();//关闭流
		}catch(Exception e){
			
		}
	}
	
	private String getCookie(CloseableHttpResponse response){
		Header[] headers=response.getAllHeaders();
		String rs="";
		for(Header header:headers){
			if("Set-Cookie".equals(header.getName())){
				rs+=header.getValue().split(";")[0]+";";
			}
		}
		return rs;
	}
	
	public String getUrl(String url){
		String array[]=url.split("/"),rs="";
		for(String str:array){
			rs+=URLEncoder.encode(str)+"/";
		}
		return rs;
	}
	private void printHeader(CloseableHttpResponse response){
		Header[] headers=response.getAllHeaders();
		for(Header header:headers){
			System.out.println(header.getName()+" "+header.getValue());
		}
	}
	private void printBody(CloseableHttpResponse response) throws IOException{
		StringBuffer sb=new StringBuffer();
		InputStream in=response.getEntity().getContent();
		InputStreamReader reader=new InputStreamReader(in);
		BufferedReader br=new BufferedReader(reader);
		String line=br.readLine();
		while(line!=null){
			System.out.println(line);
			line=br.readLine();
		}
		
		
		
		System.out.println("=============================");
	}
	
	private void toFile(String name){
		File file=new File("d:\\rs.log");
		try {
			FileWriter wr=new FileWriter(file);
			wr.append(name);
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

//http://197.3.133.65:8080/qcbin/rest/domains/%E6%B5%8B%E8%AF%95%E7%AE%A1%E7%90%86%E4%B8%AD%E5%BF%83/projects/%E7%BC%BA%E9%99%B7%E7%AE%A1%E7%90%86/defects?query={owner[%E4%BD%95%E5%A8%81]}
