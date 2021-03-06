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
package asquare.gwt.tests.test;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupListenerCollection;
import com.google.gwt.user.client.ui.PopupPanel;

public class ListenerCollectionTC extends GWTTestCase
{
	public String getModuleName()
	{
		return "asquare.gwt.tests.UnitTests";
	}
	
	public void testConcurrentModification()
	{
		PopupListenerCollection popupListenerCollection = new PopupListenerCollection();
		popupListenerCollection.add(new PopupListenerStub());
		popupListenerCollection.add(new PopupListenerStub());
		popupListenerCollection.add(new PopupListener()
		{
			public void onPopupClosed(PopupPanel sender, boolean autoClosed)
			{
				// do some cleanup
				
				sender.removePopupListener(this);

				// schedule a focus event
			}
		});
		popupListenerCollection.add(new PopupListenerStub());
		popupListenerCollection.add(new PopupListenerStub());
	}
	
	private static class PopupListenerStub implements PopupListener
	{
		public void onPopupClosed(PopupPanel sender, boolean autoClosed)
		{
		}
	}
}
