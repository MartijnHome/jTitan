package sliedregt.martijn.jtitan.manager;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import sliedregt.martijn.jtitan.listener.InputListener;
import sliedregt.martijn.jtitan.listener.KeyListener;

public final class InputManager
{
	private static final int KEYBOARD_SIZE = 512;
	private static final int MOUSE_SIZE = 16;

	private static int[] keyStates = new int[KEYBOARD_SIZE];
	private static boolean[] activeKeys = new boolean[KEYBOARD_SIZE];

	private static int[] mouseButtonStates = new int[MOUSE_SIZE];
	private static boolean[] activeMouseButtons = new boolean[MOUSE_SIZE];
	private static long lastMouseNS = 0;
	private static long mouseDoubleClickPeriodNS = 1000000000 / 5; // 5th of a second for double click.

	private static int NO_STATE = -1;

	private List<InputListener> l;

	{
		l = new ArrayList<InputListener>();
		k = new ArrayList<KeyListener>();
	}

	public void addInputListener(InputListener l)
	{
		this.l.add(l);
	}

	public List<InputListener> getInputListener()
	{
		return l;
	}

	private List<KeyListener> k;

	{
		k = new ArrayList<KeyListener>();
	}

	public void addKeyListener(KeyListener k)
	{
		this.k.add(k);
	}

	public List<KeyListener> getKeyListener()
	{
		return k;
	}

	public static GLFWKeyCallback keyboard = new GLFWKeyCallback()
	{
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods)
		{
			// System.out.println(key);
			activeKeys[key] = action != GLFW_RELEASE;
			keyStates[key] = action;
		}
	};

	public static GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback()
	{
		@Override
		public void invoke(long window, int button, int action, int mods)
		{
			activeMouseButtons[button] = action != GLFW_RELEASE;
			mouseButtonStates[button] = action;
		}
	};

	public void init()
	{
		resetKeyboard();
		resetMouse();
	}

	public void update()
	{
		resetKeyboard();
		resetMouse();

		glfwPollEvents();

		synchronized (this)
		{

			synchronized (l)
			{
				for (InputListener l : this.l)
				{
					l.atUpdate();
				}
			}
			synchronized (k)
			{
				for (KeyListener k : this.k)
				{
					if (k.getState() == KeyListener.STATE_PRESSED)
						if (keyDown(k.getKey()))
							k.run();
					if (k.getState() == KeyListener.STATE_RELEASED)
						if (!keyDown(k.getKey()))
							k.run();
				}
			}
		}

	}

	private static void resetKeyboard()
	{
		for (int i = 0; i < keyStates.length; i++)
		{
			keyStates[i] = NO_STATE;
		}
	}

	private static void resetMouse()
	{
		for (int i = 0; i < mouseButtonStates.length; i++)
		{
			mouseButtonStates[i] = NO_STATE;
		}

		long now = System.nanoTime();

		if (now - lastMouseNS > mouseDoubleClickPeriodNS)
			lastMouseNS = 0;
	}

	public static boolean keyDown(int key)
	{
		return activeKeys[key];
	}

	public static boolean keyPressed(int key)
	{
		return keyStates[key] == GLFW_PRESS;
	}

	public static boolean keyReleased(int key)
	{
		return keyStates[key] == GLFW_RELEASE;
	}

	public static boolean mouseButtonDown(int button)
	{
		return activeMouseButtons[button];
	}

	public static boolean mouseButtonPressed(int button)
	{
		return mouseButtonStates[button] == GLFW_RELEASE;
	}

	public static boolean mouseButtonReleased(int button)
	{
		boolean flag = mouseButtonStates[button] == GLFW_RELEASE;

		if (flag)
			lastMouseNS = System.nanoTime();

		return flag;
	}

	public static boolean mouseButtonDoubleClicked(int button)
	{
		long last = lastMouseNS;
		boolean flag = mouseButtonReleased(button);

		long now = System.nanoTime();

		if (flag && now - last < mouseDoubleClickPeriodNS)
		{
			lastMouseNS = 0;
			return true;
		}

		return false;
	}

}
