package sliedregt.martijn.jtitan.api.graphics;

import java.awt.Dimension;
import java.util.List;

import org.lwjgl.glfw.GLFWKeyCallback;

import sliedregt.martijn.jtitan.api.Api;
import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.RenderBatch;
import sliedregt.martijn.jtitan.datatype.primal.Color;
import sliedregt.martijn.jtitan.datatype.resource.FrameBuffer;
import sliedregt.martijn.jtitan.datatype.scene.Camera;
import sliedregt.martijn.jtitan.datatype.scene.Fog;
import sliedregt.martijn.jtitan.datatype.scene.Light;
import sliedregt.martijn.jtitan.datatype.scene.Renderable;

public abstract class GraphicsApi extends Api
{

	protected GraphicsApi(final String name, final long version)
	{
		super(Api.TYPE_GRAPHICS, name, version);
	}

	abstract public void swap();

	abstract public void clear(Color c);

	abstract public void render(List<Renderable> r, Camera c, List<Light> l, Fog fog, Dimension screenSize);

	abstract public void renderBatch(RenderBatch r, Dimension screenSize);
	
	abstract public void bindFrameBuffer(FrameBuffer fbo);
	
	abstract public void setup();

	abstract public long createWindow(Window w);

	abstract public void setWindowVisible(Window w, boolean v);

	abstract public void setContext(Window w);

	abstract public void setVSync(boolean v);

	abstract public void setKeyCallBack(long w, GLFWKeyCallback k);
	
	abstract public boolean initialize(Configuration c);
}
