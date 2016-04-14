package cn.cmbc.alm.notifer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;


public class SystemTray extends Shell {

	
	public static TrayItem trayItem;
	public static org.eclipse.swt.widgets.List list;
	public static Label label;
	public Text nameText, rsText;
	public static boolean open=true;
	static Image noproblem = SWTResourceManager.getImage(SystemTray.class, "/images/toc_closed.png");
	static Image problem = SWTResourceManager.getImage(SystemTray.class, "/images/problem.png");
	static List<Entity> entitys;
	TaskThread task=null;

    /**
     * Create the shell
     * 
     * @param display
     * @param style
     */
    public SystemTray(Display display, int style) {
        super(display, style);
        createContents();
    }

    /**
     * Create contents of the window
     */
    protected void createContents() {
        setText("HP-ALM 通知中心");
        setSize(600, 300);
        
        Composite comp=new Composite(this,SWT.NONE);
        comp.setLayout(new GridLayout(2,false));
        
        nameText=new Text(comp, SWT.BORDER);
        nameText.setText("何威");
        nameText.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,false,1,1));
        label=new Label(comp,SWT.None);
        label.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,false,1,1));
        
        list=new org.eclipse.swt.widgets.List(comp, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
        list.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true,1,1));
        
        rsText=new Text(comp, SWT.BORDER|SWT.WRAP|SWT.V_SCROLL|SWT.H_SCROLL);
        rsText.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true,1,1));
        
        list.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				int index=list.getSelectionIndex();
				if(index==-1)return;
				rsText.setText(entitys.get(index).getDescription());
			}
		});
        
        nameText.addListener(SWT.FocusOut, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				String name=nameText.getText();
				task.$http.setUname(name);
			}
		});
    }
    
    public static void refreshTable(List<Entity> rs){
    	entitys=rs;
    	if(Entity.notifed.equals("0")){
    		trayItem.setImage(noproblem);
    	}else{
    		trayItem.setImage(problem);
    	}
    	if(rs==null)return;
    	Collections.sort(rs,new Comparator<Entity>() {

			@Override
			public int compare(Entity o1, Entity o2) {
				return o2.getUser13().compareTo(o1.getUser13());
			}
		});
    	if(!open)return;
    	list.removeAll();
    	for(Entity ent:rs){
//    		System.out.println(ent.getDescription());
//    		System.out.println();
    		list.add(ent.toString());
    	}
    	label.setText(trayItem.getToolTipText());
    }

    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void createSystemTray(final Shell shell) {
        // ���������Ч���ǣ�������������ʾ
        final int hWnd = (int) shell.handle;
        OS.SetWindowLong(hWnd, OS.GWL_EXSTYLE, OS.WS_EX_CAPTIONOKBTN);
        shell.addShellListener(new ShellListener() {
            public void shellDeactivated(org.eclipse.swt.events.ShellEvent e) {
                System.out.println("shellDeactivated");
            }

            public void shellActivated(org.eclipse.swt.events.ShellEvent e) {
                System.out.println("shellActivated");
                open=true;
            }

            public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
            	 e.doit = false;
                 shell.setVisible(false);
                System.out.println("shellClosed");
                open=false;
            }

            /**
             * Sent when a shell is un-minimized.
             */
            public void shellDeiconified(org.eclipse.swt.events.ShellEvent e) {
                System.out.println("shellDeiconified");
            }

            /**
             * Sent when a shell is minimized. Shell ��С�����¼�
             */
            public void shellIconified(org.eclipse.swt.events.ShellEvent e) {
                shell.setVisible(false);
                System.out.println("shell.setVisible(false);");
            }

        });

        Display display = shell.getDisplay();
        // Image image = new Image (display, 16, 16);
        final Tray tray = display.getSystemTray();
        trayItem = new TrayItem(tray, SWT.NONE);
        trayItem.setToolTipText("HP-ALM Notification");
        trayItem.addListener(SWT.Show, new Listener() {
            public void handleEvent(Event event) {
                System.out.println("show");
            }
        });
        trayItem.addListener(SWT.Hide, new Listener() {
            public void handleEvent(Event event) {
                System.out.println("hide");
            }
        });

        // ���̵����¼�
        trayItem.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                System.out.println("selection");
//                 shell.setVisible(true);
//                 shell.setMinimized(false);
            }
        });

        //双击托盘
        trayItem.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event event) {
//                System.out.println("default selection");
                shell.setVisible(true);
                shell.forceActive();
                open=true;
            }
        });

        // ��������Ҽ�˵�
        final Menu menu = new Menu(shell, SWT.POP_UP);

        MenuItem menuItemMaximize = new MenuItem(menu, SWT.PUSH);// ��󻯲˵�
        menuItemMaximize.setText("HP-ALM通知 v0.1");
        

//        MenuItem menuItemMinimize = new MenuItem(menu, SWT.PUSH);// ��С���˵�
//        menuItemMinimize.setText("Minimize");
//        menuItemMinimize.addSelectionListener(new SelectionListener() {
//            public void widgetSelected(SelectionEvent e) {
//                shell.setMinimized(true);
//            }
//
//            public void widgetDefaultSelected(SelectionEvent e) {
//                // widgetSelected(e);
//            }
//        });
//
        new MenuItem(menu, SWT.SEPARATOR);// �ָ���

        MenuItem menuItemClose = new MenuItem(menu, SWT.PUSH);// �رղ˵�
        menuItemClose.setText("退出");
        menuItemClose.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                Display.getCurrent().close();
                System.exit(0);
            }

            public void widgetSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });

        trayItem.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                System.out.println("shell.isVisible():" + shell.isVisible());
                menu.setVisible(true);
                if (shell.isVisible()) {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(1).setEnabled(true);
                } else {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(1).setEnabled(false);
                }
            }
        });
        trayItem.setImage(noproblem);

        //
        String name=nameText.getText();
        task=new TaskThread(name);
        new Thread(task).start();
        
    }

}