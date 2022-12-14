package sliedregt.martijn.jtitan.datatype;

import java.util.ArrayList;
import java.util.List;

import sliedregt.martijn.jtitan.datatype.resource.FrameBuffer;
import sliedregt.martijn.jtitan.datatype.scene.Camera;
import sliedregt.martijn.jtitan.datatype.scene.Fog;
import sliedregt.martijn.jtitan.datatype.scene.Light;
import sliedregt.martijn.jtitan.datatype.scene.Renderable;

public class RenderBatch
{
	private FrameBuffer fbo;
	private Camera camera;
	private List<Renderable> renderable;
	private List<Light> light;
	private Fog fog;
	
	public RenderBatch(FrameBuffer fbo, Camera camera, List<Renderable> renderable, List<Light> light, Fog fog)
	{
		this.setFbo(fbo);
		this.setCamera(camera);
		this.setRenderable(renderable);
		this.setLight(light);
		this.setFog(fog);
	}

	public RenderBatch(FrameBuffer fbo, Camera camera, Renderable renderable, List<Light> light, Fog fog)
	{
		List<Renderable> r = new ArrayList<Renderable>();
		r.add(renderable);
		this.setFbo(fbo);
		this.setCamera(camera);
		this.setRenderable(r);
		this.setLight(light);
		this.setFog(fog);
	}
	
	public List<Renderable> getRenderable()
	{
		return renderable;
	}

	public void setRenderable(List<Renderable> renderable)
	{
		this.renderable = renderable;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	public FrameBuffer getFbo()
	{
		return fbo;
	}

	public void setFbo(FrameBuffer fbo)
	{
		this.fbo = fbo;
	}

	public List<Light> getLight()
	{
		return light;
	}

	public void setLight(List<Light> light)
	{
		this.light = light;
	}

	public Fog getFog()
	{
		return fog;
	}

	public void setFog(Fog fog)
	{
		this.fog = fog;
	}
	
	public void doAtPreRender()
	{
	}

	public void doAtPostRender()
	{
	}

}
