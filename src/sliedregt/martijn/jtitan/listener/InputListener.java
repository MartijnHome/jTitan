package sliedregt.martijn.jtitan.listener;

public interface InputListener
{

	static final int KEY_LEFT = org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
	static final int KEY_UP = org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
	static final int KEY_RIGHT = org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
	static final int KEY_DOWN = org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
	static final int KEY_SPACE = org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
	static final int KEY_ESCAPE = org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

	static final int KEY_W = org.lwjgl.glfw.GLFW.GLFW_KEY_W;
	static final int KEY_A = org.lwjgl.glfw.GLFW.GLFW_KEY_A;
	static final int KEY_S = org.lwjgl.glfw.GLFW.GLFW_KEY_S;
	static final int KEY_D = org.lwjgl.glfw.GLFW.GLFW_KEY_D;
	static final int KEY_E = org.lwjgl.glfw.GLFW.GLFW_KEY_E;
	static final int KEY_R = org.lwjgl.glfw.GLFW.GLFW_KEY_R;
	static final int KEY_F = org.lwjgl.glfw.GLFW.GLFW_KEY_F;
	
	static final int KEY_LEFT_SHIFT = org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
	
	public void atUpdate();
}
