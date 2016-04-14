package cn.cmbc.alm.notifer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class TaskThread implements Runnable{
	//获取名称
	public static HttpHelper $http=null;
	List<Entity> ret=null;
	public TaskThread(String uname){
		$http=new HttpHelper(uname);
	}
	@Override
	public void run() {
		
		while(true){
			ret=$http.getIssue();
			Display display=Display.getCurrent();
			if(display==null)display=Display.getDefault();
			if(display==null||ret==null)continue;
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					SystemTray.trayItem.setToolTipText(Entity.getSummary(ret));
					SystemTray.refreshTable(ret);
				}
			});
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
