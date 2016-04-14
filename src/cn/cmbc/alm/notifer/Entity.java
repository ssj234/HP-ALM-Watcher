package cn.cmbc.alm.notifer;

import java.util.List;

public class Entity {
	public static String count;
	public static String notifed;
	private String id;
	private String user13;
	private String detectedby;
	private String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser13() {
		return user13;
	}
	public void setUser13(String user13) {
		this.user13 = user13;
	}
	public String getDetectedby() {
		return detectedby;
	}
	public void setDetectedby(String detectedby) {
		this.detectedby = detectedby;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		description=description.replace("&nbsp;", "");
		description=description.replace("<html><body>", "");
		description=description.replace("<div align=\"left\">", "");
		description=description.replace("<font face=\"Arial Unicode MS\"><span style=\"font-size:8pt\">", "");
		description=description.replace("</span></font>", "");
		description=description.replace("</div>", "");
		description=description.replace("</body></html>", "");
		this.description = description;
	}
	
	
	public static String getSummary(List<Entity> list) {
		if(list==null){
			notifed="0";
			return "连接失败！";
		}
		String ret="共计："+count;
		int count=0;
		for(int i=0;i<list.size();i++){
			Entity ent=list.get(i);
			if(inNotify(ent)){
				count++;
			}
		}
		ret+=" 需要处理："+count;
		notifed=Integer.toString(count);
		return ret;
	}
	
	private static boolean inNotify(Entity ent){
		if("新建|重新打开".indexOf(ent.getUser13())>-1){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getId()+" "+this.getUser13()+" "+this.getDetectedby();
	}
}
