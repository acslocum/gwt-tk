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
package asquare.gwt.tk.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class TableUtil
{
	public static Element createAppendTr(Element tBody)
	{
		Element tr = DOM.createTR();
		DOM.appendChild(tBody, tr);
		return tr;
	}
	
	public static Element createInsertTr(Element tBody, int trIndex)
	{
		Element tr = DOM.createTR();
		DOM.insertChild(tBody, tr, trIndex);
		return tr;
	}

	public static Element createAppendTd(Element tr)
	{
		Element td = DOM.createTD();
		DOM.appendChild(tr, td);
		return td;
	}

	public static Element createInsertTd(Element tr, int tdIndex)
	{
		Element td = DOM.createTD();
		DOM.insertChild(tr, td, tdIndex);
		return td;
	}

	public static void setTableCellPadding(Element table, int value)
	{
		DOM.setIntAttribute(table, "cellPadding", value);
	}
	
	public static void setTableCellSpacing(Element table, int value)
	{
		DOM.setIntAttribute(table, "cellSpacing", value);
	}
	
	public static void setTdHorizontalAlignment(Element td, HorizontalAlignmentConstant hAlign)
	{
		DOM.setAttribute(td, "align", hAlign.getTextAlignString());
	}
	
	public static void setTdVerticalAlignment(Element td, VerticalAlignmentConstant vAlign)
	{
		DOM.setStyleAttribute(td, "verticalAlign", vAlign.getVerticalAlignString());
	}
	
	public static void setTdWidth(Element td, String width)
	{
		DOM.setAttribute(td, "width", width);
	}
	
	public static void setTdHeight(Element td, String height)
	{
		DOM.setAttribute(td, "height", height);
	}
}
