/*
 * Copyright 2006 Mat Gessel <mat.gessel@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package asquare.gwt.tkdemo.client;

import asquare.gwt.debug.client.BrowserInfo;
import asquare.gwt.debug.client.Debug;
import asquare.gwt.debug.client.DebugElementDumpInspector;
import asquare.gwt.tk.client.ui.RowPanel;
import asquare.gwt.tk.client.util.DomUtil;
import asquare.gwt.tkdemo.client.demos.*;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.*;

/**
 * This is the entry point for the demo application. It builds the GUI after 
 * the script loads.
 */
public class Demo implements EntryPoint
{
	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad()
	{
		HistoryWidgetCollection tabs = new HistoryWidgetCollection();
		tabs.add("dropdown", "DropDownPanel", new DropDownPanelPanel(), new String[] {"DropDownPanel"});
		tabs.add("dialogs", "Dialogs", new DialogPanel(), new String[] {"ModalDialog", "AlertDialog"});
		tabs.add("cellpanel", "Cell Panels", new ExposedCellPanelPanel(), new String[] {"RowPanel", "ColumnPanel"});
		tabs.add("events", "Events", new EventsPanel(), new String[] {"EventWrapper", "Controller"});
		tabs.add("debug", "Debug", new DebugPanel(), new String[] {"Debug"});
		tabs.add("misc", "Misc", new MiscPanel(), new String[] {"SimpleHyperLink", "ExternalHyperLink", FocusCycleDemo.NAME, GlassPanelDemo.NAME});
		
		// use a table for border to work around bugs with 100% child width
		RowPanel outer = new RowPanel();
		DomUtil.setAttribute(outer, "id", "main");
		outer.setWidth("100%");
		
		TabBar tabBar = new TabBar();
		tabBar.setWidth("100%");
		DemoContent tabContent = new DemoContent();
		tabBar.addTabListener(tabContent);
		tabContent.setWidth("100%");
		for (int i = 0; i < tabs.size(); i++)
		{
			// nest a table so inner content can be 100% and bordered/padded
			RowPanel border = new RowPanel();
			border.setSize("100%", "100%");
			border.setStyleName("TabPanel-contentBorder");
			border.add(tabs.getWidget(i));
			
			tabBar.addTab(tabs.getDescription(i));
			tabContent.add(border);
		}
		outer.add(tabBar);
		outer.add(tabContent);
		
		String initialTabToken = History.getToken();
		if (initialTabToken.length() == 0)
		{
			initialTabToken = tabs.getToken(0);
		}
		new TabHistoryCoordinator(tabs, tabBar, initialTabToken);
		
		BrowserInfo browserInfo = (BrowserInfo) GWT.create(BrowserInfo.class);
		String compatMode = describeCompatMode();
		Label env = new Label(compatMode + " (" + browserInfo.getUserAgent() + ")");
		env.setStyleName("compatMode");
		outer.add(env);
		
		RootPanel.get().add(outer);
		
	    Debug.enableSilently();
	    new DebugElementDumpInspector().install();
	}
	
	/**
	 * Generates a human-readable string representing the CSS rendering mode.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Quirks_mode">quirks mode</a>
	 */
	public native String describeCompatMode() /*-{
		var result = "unknown";
		if ($doc.compatMode == "BackCompat")
		{
			result = "quirks mode";
		}
		else if ($doc.compatMode == "CSS1Compat")
		{
			result = "standards compliance mode";
		}
		return result;
	}-*/;
}
