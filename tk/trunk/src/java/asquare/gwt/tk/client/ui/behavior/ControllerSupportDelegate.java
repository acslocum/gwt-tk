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
package asquare.gwt.tk.client.ui.behavior;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * A delegate class which manages controller bookkeeping and lifecycle. 
 * Ideally, this fuctionality would be in a superclass like
 * {@link com.google.gwt.user.client.ui.Widget Widget}.
 * 
 * TODO: unplug (remove) controllers when disabling?
 */
public class ControllerSupportDelegate
{
	private final Widget m_widget;
	
	private List<Controller> m_controllers = null;
	private List<Class<? extends Controller>> m_disablableControllerIds = null;
	private int m_bitsForOnBrowserEvent = 0; // only valid after attached
	private int m_bitsForControllers = 0; // only valid after attached
	private boolean m_isOrWasAttached = false;
	private boolean m_enabled = true;
	
	/**
	 * Creates a delegate for the specified widget. 
	 * 
	 * @param widget the widget which will be passed to Controller methods
	 */
	public ControllerSupportDelegate(Widget widget)
	{
		m_widget = widget;
	}
	
    /**
     * Gets the event bits which were sunk on the widget itself. This can happen
     * when subclassing or wrapping a widget. Used to determine which events
     * should be passed to a widget's
     * {@link EventListener#onBrowserEvent(Event) onBrowserEvent()} method.
     * <p>
     * Only valid after the widget has been attached. This is because a wrapped
     * widget may sink events internally, and we cannot get the event bits until
     * it has been attached.
     * 
     * @return a bitmask of the event types
     * @throws IllegalStateException if called before widget has been attached
     * @see Event
     */
	public int getBitsForOnBrowserEvent()
	{
		if (! isOrWasAttached())
		    throw new IllegalStateException();
		
		return m_bitsForOnBrowserEvent;
	}
	
	private final boolean isOrWasAttached()
	{
	    return m_isOrWasAttached;
	}

	private int calculateControllerBits()
	{
		int result = 0;
		if (m_controllers != null)
		{
			for (int i = 0, size = m_controllers.size(); i < size; i++)
			{
				result |= m_controllers.get(i).getEventBits();
			}
		}
		return result;
	}
	
	public void sinkEvents(int eventBits)
	{
		m_bitsForOnBrowserEvent |= eventBits;
		if (isOrWasAttached())
		{
			m_widget.sinkEvents(m_bitsForOnBrowserEvent);
		}
	}
	
	public void unsinkEvents(int eventBits)
	{
		m_bitsForOnBrowserEvent &= ~eventBits;
		if (isOrWasAttached())
		{
			DOM.sinkEvents(m_widget.getElement(), m_bitsForOnBrowserEvent | m_bitsForControllers);
		}
	}
	
	public Controller getController(Class<? extends Controller> id)
	{
		if (m_controllers != null)
		{
			for (int i = 0, size = m_controllers.size(); i < size; i++)
			{
				if (m_controllers.get(i).getId() == id)
				{
					return m_controllers.get(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * @throws IllegalArgumentException if <code>controller</code> is null
	 */
	public Widget addController(Controller controller)
	{
		if (controller == null)
			throw new IllegalArgumentException();
		
		if (m_controllers == null)
		{
			m_controllers = new ArrayList<Controller>();
		}
		m_controllers.add(controller);
		if(isOrWasAttached())
		{
			int eventBits = controller.getEventBits();
			m_bitsForControllers |= eventBits;
			DOM.sinkEvents(m_widget.getElement(), m_bitsForOnBrowserEvent | m_bitsForControllers);
		}
		if (m_widget.isAttached())
		{
			controller.plugIn(m_widget);
		}
		return m_widget;
	}
	
	/**
	 * @throws IllegalArgumentException if <code>controller</code> is <code>null</code>
	 * @throws IllegalArgumentException if <code>controller</code> is not present
	 */
	public Widget removeController(Controller controller)
	{
		if (controller == null)
			throw new IllegalArgumentException();
		
		int index = -1;
		if (m_controllers != null)
		{
			index = m_controllers.indexOf(controller);
		}
		
		if (index == -1)
			throw new IllegalArgumentException();
		
		m_controllers.remove(index);
		if (m_widget.isAttached())
		{
			controller.unplug(m_widget);
		}
		if (isOrWasAttached())
		{
			m_bitsForControllers = calculateControllerBits();
			DOM.sinkEvents(m_widget.getElement(), m_bitsForOnBrowserEvent | m_bitsForControllers);
		}
		return m_widget;
	}
	
	public void setControllers(List<? extends Controller> controllers)
	{
		if (m_widget.isAttached())
		{
			unplugControllers();
		}
		m_controllers = null;
		if (controllers != null)
		{
			m_controllers = new ArrayList<Controller>();
			for (int i = 0, size = controllers.size(); i < size; i++)
			{
				Controller controller = controllers.get(i);
				m_controllers.add(controller);
			}
		}
		if (isOrWasAttached())
		{
			m_bitsForControllers = calculateControllerBits();
			DOM.sinkEvents(m_widget.getElement(), m_bitsForOnBrowserEvent | m_bitsForControllers);
		}
		if (m_widget.isAttached())
		{
			plugInControllers();
		}
	}
	
	public boolean isControllerDisablable(Class<? extends Controller> id)
	{
		return m_disablableControllerIds != null && m_disablableControllerIds.contains(id);
	}
	
	/**
	 * @param id a non-null controller id
	 * @param disablable <code>true</code> to disable the specified controller
	 *            when the component is disabled
	 * @throws IllegalArgumentException if <code>id</code> is <code>null</code>
	 */
	public void setControllerDisablable(Class<? extends Controller> id, boolean disablable)
	{
		if (id == null)
			throw new IllegalArgumentException();
		
		if (disablable)
		{
			if (m_disablableControllerIds == null)
			{
				m_disablableControllerIds = new ArrayList<Class<? extends Controller>>();
			}
			m_disablableControllerIds.add(id);
		}
		else
		{
			if (m_disablableControllerIds != null)
			{
				m_disablableControllerIds.remove(id);
			}
		}
	}
	
	private void plugInControllers()
	{
		if (m_controllers != null)
		{
			for (int i = 0, size = m_controllers.size(); i < size; i++)
			{
				m_controllers.get(i).plugIn(m_widget);
			}
		}
	}
	
	private void unplugControllers()
	{
		if (m_controllers != null)
		{
			for (int i = m_controllers.size() - 1; i >= 0; i--)
			{
				m_controllers.get(i).unplug(m_widget);
			}
		}
	}
	
	public boolean isEnabled()
	{
		return m_enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		m_enabled = enabled;
	}
	
	/**
	 * Widget.onAttach() must be called before this. 
	 */
	public void onAttach()
	{
	    assert m_widget.isAttached();
	    if (! isOrWasAttached())
	    {
			m_isOrWasAttached = true;
	    	/**
			 * The widget may have sunk events internally, so read from the DOM to
			 * ensure we get all bits. This is why widget must first be attached. 
			 */
			m_bitsForOnBrowserEvent |= DOM.getEventsSunk(m_widget.getElement());
			m_bitsForControllers = calculateControllerBits();
			DOM.sinkEvents(m_widget.getElement(), m_bitsForOnBrowserEvent | m_bitsForControllers);
	    }
	    plugInControllers();
	}
	
	public void onDetach()
	{
		unplugControllers();
	}
	
	public void onBrowserEvent(Event event)
	{
		/*
		 * Avoid processing the same event twice (issue with the GWT dispatcher). 
		 * If you sink events on a child, but do not set a listener (as in the case of UIObjects), 
		 * the following will happen:
		 *
		 * 1) child element's onXXX event handler is invoked
		 * 2) $wnd.__dispatchEvent() is called with the event and child element
		 * 3) this crawls up the heirarchy until it finds a GWT event listener (i.e. CComposite)
		 * 4) CComposite.onBrowserEvent() is called (e.target = child, e.currentTarget = child)
		 * 5) the event bubbles up to the parent element
		 * 6) parent element's onXXX event handler is invoked
		 * 7) $wnd.__dispatchEvent() is called with the event and the parent element
		 * 8) CComposite.onBrowserEvent() is called (e.target = child, e.currentTarget = parent)
		 * 
		 * @see DOMImplStandard#init()
		 * @see Tree#onBrowserEvent(Event)
		 * 
		 * Note: don't test current target when capturing. DOM.eventGetCurrentTarget() returns $wnd 
		 * when capturing in Firefox and $wnd.equals() blows up. 
		 * @see http://groups.google.com/group/Google-Web-Toolkit-Contributors/browse_thread/thread/41f12a9def68f97a
		 * 
		 * Note: eventGetCurrentTarget(event) is wrong in FocusImplOld (Safari, old Mozilla, Opera)--it returns the 
		 * nested INPUT. Focus events don't bubble anyway, so bypass the check in this case. Bleh. 
		 */
		if (m_controllers != null)
		{
			int eventType = DOM.eventGetType(event);
			if (DOM.getCaptureElement() != null || (eventType & Event.FOCUSEVENTS) != 0 || DOM.eventGetCurrentTarget(event).equals(m_widget.getElement()))
			{
				for (int i = 0, size = m_controllers.size(); i < size; i++)
				{
					Controller controller = m_controllers.get(i);
					if ((controller.getEventBits() & eventType) != 0 && (m_enabled || ! isControllerDisablable(controller.getId())))
					{
						controller.onBrowserEvent(m_widget, event);
					}
				}
			}
		}
	}
	
	public String toString()
	{
		return m_controllers.toString();
	}
}
