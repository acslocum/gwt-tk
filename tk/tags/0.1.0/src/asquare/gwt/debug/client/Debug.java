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
package asquare.gwt.debug.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;

/**
 * <p>
 * Facade for debugging methods. Installs hooks for printing debug statements
 * from JSNI and native JavaScript. You can enable/disable debug methods by
 * pressing <code>Caps Lock</code> twice sequentially. Uses a
 * {@link asquare.gwt.debug.client.DebugEventListener DebugEventListener} to
 * listen for the enabler key. See
 * {@link asquare.gwt.debug.client.DebugEventListener DebugEventListener} for
 * potential conflicts with popup/dialog event filtering.
 * </p>
 * <p>
 * A stub version of this class is provided which will remove this class
 * definition from the final deliverable if placed ahead of this implementation
 * in the classpath. The stub class will prevent debug statements from being
 * displayed, however, the string will still be created by the browser's
 * interpreter. To entirely remove debugging statements from the deliverable you
 * will need to do something like this:
 * 
 * <pre>
 * if (Debug.ENABLED)
 * {
 * 	Debug.println(&quot;Foo.bar(&quot; + value1 + ',' + value2 + ')');
 * }
 * </pre>
 * <code>Debug.ENABLED</code> is false in the stub version.
 * </p>
 * <P>
 * The compiler may optimize out the class if you only reference this class
 * through JSNI. Consider calling {@link #init()} in your entry point or test
 * case if you are getting this message: <br/>
 * <code>com.google.gwt.core.client.JavaScriptException: JavaScript TypeError exception: Object doesn't support this property or method</code>.
 * </P>
 */
public class Debug
{
	public static final boolean ENABLED = true;
	
	static final char DEFAULT_ENABLE_KEY = (char) 20;
	
	private static boolean s_runtimeEnabled = false;
	
	private static boolean s_initRunning = false;
	
	private static boolean s_initialized = false;
	
	private static DebugEventListener s_eventTracer = null;
	
	static
	{
		init();
	}
	
	/**
	 * This is called automatically when the class/webpage is loaded. If you
	 * only reference this class via JavaScript you need to call init() (or
	 * another method) to ensure this class is not optimized out by the
	 * compiler.
	 */
	public static void init()
	{
		if (ENABLED && ! s_initialized && ! s_initRunning)
		{
			s_initRunning = true;
			
			jsInit();
			
			new Enabler().install();
			
			s_initialized = true;
			s_initRunning = false;
		}
	}
	
	/**
	 * Sets up a "native" JS API so you don't have to type the contorted JNI
	 * syntax to print debug statements from JS.
	 */
	private static native void jsInit() /*-{
		if (typeof $wnd.Debug == "undefined")
		{
			 $wnd.Debug = function() {};
		}
		
		if (typeof Debug == "undefined")
		{
			Debug = $wnd.Debug;
		}
		
		Debug.enable = $wnd.Debug.enable = function()
		{
			@asquare.gwt.debug.client.Debug::enable()();
		};

		Debug.disable = $wnd.Debug.disable = function()
		{
			@asquare.gwt.debug.client.Debug::disable()();
		};

		Debug.print = $wnd.Debug.print = function(object)
		{
			@asquare.gwt.debug.client.Debug::print(Ljava/lang/String;)("" + object);
		};

		Debug.println = $wnd.Debug.println = function(object)
		{
			@asquare.gwt.debug.client.Debug::println(Ljava/lang/String;)("" + object);
		};
		
		Debug.prettyPrint = $wnd.Debug.prettyPrint = function(object)
		{
			Debug.println(object);
			if (object instanceof Array)
			{
				for (var i = 0; i < object.length; i++)
				{
					Debug.println("  [" + i + "]" + object[i]);
				}
			}
			else
			{
				for (member in object)
				{
					Debug.println("  +" + member + "=" + object[member]);
				}
			}
		};
	}-*/;
	
	
	/**
	 * Enable debugging, but does not show the in-browser console. 
	 */
	public static void enableSilently()
	{
		if (ENABLED)
		{
			s_runtimeEnabled = true;
		}
	}
	
	/**
	 * Enables debugging and prints a message stating so. 
	 */
	public static void enable()
	{
		if (ENABLED)
		{
			s_runtimeEnabled = true;
			Debug.println("Debug enabled");
		}
	}
	
	/**
	 * Disables debugging, prints a message stating so and hides the in-browser console. 
	 */
	public static void disable()
	{
		if (ENABLED)
		{
			Debug.println("Debug disabled");
			DebugConsole.getInstance().hide();
			s_runtimeEnabled = false;
		}
	}
	
	/**
	 * Is debugging enabled at compile time and runtime. 
	 * 
	 * @return true if enabled
	 */
	public static boolean isEnabled()
	{
		return ENABLED && s_runtimeEnabled;
	}
	
	/**
	 * Prints a debugging message to the in-browser console. 
	 * In hosted mode the message also goes to {@link System#out} and the GWT Shell. 
	 * 
	 * @param message
	 */
	public static void print(String message)
	{
		if (ENABLED && s_runtimeEnabled)
		{
			System.out.print(message);
			GWT.log(message, null);
			DebugConsole.getInstance().print(message);
		}
	}
	
	/**
	 * Prints a debugging message to the in-browser console, follwed by "\r\n". 
	 * In hosted mode the message also goes to {@link System#out} and the GWT Shell. 
	 * 
	 * @param message
	 */
	public static void println(String message)
	{
		if (ENABLED && s_runtimeEnabled)
		{
			System.out.println(message);
			GWT.log(message, null);
			DebugConsole.getInstance().println(message);
		}
	}
	
	/**
	 * Displays members of native JavaScript objects. Not very useful for Java
	 * objects.
	 * 
	 * @see #prettyPrint(JavaScriptObject)
	 */
	public static native void prettyPrint(Object o) /*-{
		if (@asquare.gwt.debug.client.Debug::isEnabled()())
		{
			Debug.prettyPrint(o);
		}
	}-*/;
	
	/**
	 * Displays members of native JavaScript objects. This is implemented in
	 * JavaScript where Java objects are opaque. GWT doesn't support reflection
	 * so we can't see what the objects properties are anyway.
	 */
	public static native void prettyPrint(JavaScriptObject o) /*-{
		if (@asquare.gwt.debug.client.Debug::isEnabled()())
		{
			Debug.prettyPrint(o);
		}
	}-*/;
	
	/**
	 * Installs an experimental event tracer. Press the specified key twice to
	 * start tracing. A message will be printed to the console for each event
	 * matching eventMask.
	 * <p>
	 * Some events are not tracing in Firefox at this time, even though the
	 * events trigger listeners in GWT widgets.
	 * </p>
	 * 
	 * @param enableKey a keyCode or (char) -1 for the default
	 * @param eventMask a bitwise event mask, 0 for no events or -1 for the
	 *            default ('e')
	 * @see DebugEventListener
	 * @see Event
	 */
	public static void installEventTracer(char enableKey, int eventMask)
	{
		if (ENABLED && s_eventTracer == null)
		{
			s_eventTracer = new DebugEventListener(enableKey, eventMask);
			s_eventTracer.install();
		}
	}
	
	/**
	 * @see #installEventTracer(char, int)
	 */
	public static void uninstallEventTracer()
	{
		if (s_eventTracer != null)
		{
			s_eventTracer.uninstall();
			s_eventTracer = null;
		}
	}
	
	/**
	 * Listens for the enable key and enables/disables debugging. 
	 */
	private static final class Enabler extends DebugEventListener
	{
		Enabler()
		{
			super(Debug.DEFAULT_ENABLE_KEY, 0);
		}
		
		protected void doDisabled()
		{
			Debug.disable();
		}
		
		protected void doEnabled()
		{
			Debug.enable();
		}
		
		protected void doEvent(Event event)
		{
			// NOOP
		}
	}
}
