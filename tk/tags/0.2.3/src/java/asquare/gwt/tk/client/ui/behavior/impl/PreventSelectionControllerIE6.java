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
package asquare.gwt.tk.client.ui.behavior.impl;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class PreventSelectionControllerIE6 extends PreventSelectionControllerStandard
{
	public void plugIn(Widget widget)
	{
		installHook(widget.getElement());
	}
	
	public void unplug(Widget widget)
	{
		uninstallHook(widget.getElement());
	}
	
	private native void installHook(Element element) /*-{
		element.onselectstart = function() { return false; };
	}-*/;
	
	private native void uninstallHook(Element element) /*-{
		element.onselectstart = null;
	}-*/;
}
