package org.openlca.app.rcp;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class RcpTheme {

	public static void setDarkTheme(boolean isDarkTheme) {
		var display = Display.getCurrent();
		if (display == null)
			return;

		display.setData("org.eclipse.swt.internal.win32.useDarkModeExplorerTheme", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.menuBarForegroundColor",
				isDarkTheme ? new Color(display, 0xD0, 0xD0, 0xD0) : null);
		display.setData("org.eclipse.swt.internal.win32.menuBarBackgroundColor",
				isDarkTheme ? new Color(display, 0x30, 0x30, 0x30) : null);
		display.setData("org.eclipse.swt.internal.win32.menuBarBorderColor",
				isDarkTheme ? new Color(display, 0x50, 0x50, 0x50) : null);
		display.setData("org.eclipse.swt.internal.win32.Canvas.use_WS_BORDER", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.List.use_WS_BORDER", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.Table.use_WS_BORDER", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.Text.use_WS_BORDER", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.Tree.use_WS_BORDER", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.Table.headerLineColor",
				isDarkTheme ? new Color(display, 0x50, 0x50, 0x50) : null);
		display.setData("org.eclipse.swt.internal.win32.Label.disabledForegroundColor",
				isDarkTheme ? new Color(display, 0x80, 0x80, 0x80) : null);
		display.setData("org.eclipse.swt.internal.win32.Combo.useDarkTheme", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.ProgressBar.useColors", isDarkTheme);
		display.setData("org.eclipse.swt.internal.win32.useShellTitleColoring", isDarkTheme);
	}

}