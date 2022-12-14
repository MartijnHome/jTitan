package sliedregt.martijn.jtitan.datatype.scene;

import java.util.ArrayList;
import java.util.List;

import sliedregt.martijn.jtitan.config.graphics.RenderSettings;
import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.datatype.primal.Rotation;
import sliedregt.martijn.jtitan.datatype.primal.Scale;
import sliedregt.martijn.jtitan.datatype.resource.Model;

public class Renderable
{

	private Position position;
	private Rotation rotation;
	private Scale scale;

	private Model model;
	private String name;
	private String description;
	
	private List<Object> custom;
	
	private RenderSettings renderSettings;
	
	public Renderable()
	{
		setName("");
		setDescription("");
		setRenderSettings(new RenderSettings());
	}
	
	public List<Object> getCustom()
	{
		return custom;
	}

	{
		custom = new ArrayList<Object>();
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	public Rotation getRotation()
	{
		return rotation;
	}

	public void setRotation(Rotation rotation)
	{
		this.rotation = rotation;
	}

	public Scale getScale()
	{
		return scale;
	}

	public void setScale(Scale scale)
	{
		this.scale = scale;
	}

	public Model getModel()
	{
		return model;
	}

	public void setModel(Model model)
	{
		this.model = model;
	}

	public void doAtPreRender()
	{
	}

	public void doAtPostRender()
	{
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public RenderSettings getRenderSettings()
	{
		return renderSettings;
	}

	public void setRenderSettings(RenderSettings renderSettings)
	{
		this.renderSettings = renderSettings;
	}
}
