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
package com.google.gwt.user.client.impl;

import com.google.gwt.user.client.DOMExtenstion;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * Modifications to GWT's Opera implementation go here. 
 */
class DOMImplOperaPatched extends DOMImplOpera
{
	public void eventSetKeyCode(Event evt, char key)
	{
		throw new UnsupportedOperationException("Method disabled because it is unsupported on Mozilla");
	}
	
	public int getAbsoluteLeft(Element elem)
	{
		return DOMExtenstion.getAbsoluteLeft(elem);
	}

	public int getAbsoluteTop(Element elem)
	{
		return DOMExtenstion.getAbsoluteTop(elem);
	}
}
