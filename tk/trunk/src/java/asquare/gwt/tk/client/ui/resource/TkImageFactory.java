/*
 * Copyright 2007 Mat Gessel <mat.gessel@gmail.com>
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
package asquare.gwt.tk.client.ui.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Localizable;

public class TkImageFactory implements Localizable
{
	private static TkImageFactory s_instance = null;
	
	private AlertDialogImages m_images = null;
	
	public static TkImageFactory getInstance()
	{
		if (s_instance == null)
		{
			s_instance = new TkImageFactory();
		}
		return s_instance;
	}
	
	public static void setInstance(TkImageFactory instance)
	{
		s_instance = instance;
	}
	
	public AlertDialogImages createAlertDialogImages()
	{
		if (m_images == null)
		{
			m_images = (AlertDialogImages) GWT.create(AlertDialogImages.class);
		}
		return m_images;
	}
}