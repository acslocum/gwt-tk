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
package asquare.gwt.tk.client.ui.behavior;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public interface EventBase
{
	Widget getSourceWidget();

	Event getDomEvent();

	Element getTarget();
	
	/**
	 * Get the element whose listener is currently being invoked. Typically used
	 * to ignore events generated by child elements.
	 * 
	 * @see #getTarget()
	 */
	Element getCurrentTarget();
	
	int getType();
	
	boolean isPreviewPhase();
	
	/**
	 * @return <code>true</code> if the occured in the specified UIObject or
	 *         its child elements
	 */
	boolean didEventOccurIn(UIObject uio);

	/**
	 * @return <code>true</code> if the occured in the specified element or
	 *         its children
	 */
	boolean didEventOccurIn(Element e);
	
	/**
	 * Prevents a preview event from continuing on to the normal phase. Also
	 * does {@link DOM#eventPreventDefault(Event)} (this is a GWT behavior).
	 * 
	 * @throws IllegalStateException if this event is <strong>not</strong> in
	 *             preview phase
	 */
	
	void killPreviewEvent();
	
	/**
	 * Used by {@link EventPreviewController} to determine if this event should
	 * be cancelled.
	 */
	boolean isKillPreviewEvent();

	/**
	 * Stops the event from bubbling up the DOM tree
	 * 
	 * @throws IllegalStateException if this event is in preview phase
	 */
	void stopPropagation();
}
