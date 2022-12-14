package sliedregt.martijn.jtitan.listener;

public abstract class KeyListener
{

	private int key;
	private int state;

	public static final int STATE_PRESSED = 1, STATE_RELEASED = 0;

	public KeyListener(int key, int state)
	{
		this.key = key;
		this.state = state;
	}

	public int getKey()
	{
		return key;
	}

	public int getState()
	{
		return state;
	}

	public abstract void run();
}
