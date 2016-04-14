package cn.cmbc.alm.notifer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;

public class Notifer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        try {
            Display display = Display.getDefault();
            SystemTray shell = new SystemTray(display, SWT.SHELL_TRIM);
            shell.setLayout(new FillLayout());
            shell.createSystemTray(shell);
            shell.open();
            shell.layout();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
