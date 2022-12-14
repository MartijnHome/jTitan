package sliedregt.martijn.jtitan.datatype.primal;

public class Uniform
{
	public static final int TYPE_FLOAT = 1, 
							TYPE_INT = 2,
							TYPE_VEC2 = 3,
							TYPE_VEC3 = 4;
	
	private Object value;
	private String name;
	private int type;
	
	public Uniform(String name, Object value, int type)
	{
		this.setValue(value);
		this.setName(name);
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public int getType()
	{
		return type;
	}
}
