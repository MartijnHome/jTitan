package sliedregt.martijn.jtitan.datatype.scene;

import sliedregt.martijn.jtitan.datatype.primal.Color;
import sliedregt.martijn.jtitan.datatype.primal.Direction;
import sliedregt.martijn.jtitan.datatype.primal.Position;

public class Light
{
	public final static int TYPE_DIRECTIONAL = 0, TYPE_SPOTLIGHT = 1, TYPE_FLASHLIGHT = 2;
	private Position position;
	private Direction direction;
	private int type;
	private float range;
	private float cutOffRange;
	private float power;
	private float cutOffAngle;
	private float cutOffAngleOuter;
	private Color color;

	/**
	 * Create a light which can be added to a scene.
	 * @param position
	 * @param direction
	 * @param type
	 * @param range
	 * @param cutOffRange
	 * @param color
	 */
	public Light(Position position, Direction direction, int type, float range, float cutOffRange, Color color, float power, float cutOffAngle, float cutOffAngleOuter)
	{
		this.position = position;
		this.direction = direction;
		this.type = type;
		this.range = range;
		this.cutOffRange = cutOffRange;
		this.color = color;
		this.power = power;
		this.setCutOffAngle(cutOffAngle);
		this.cutOffAngleOuter = cutOffAngleOuter;
	}

	/**
	 * Creates a directional light.
	 * @param direction
	 * @param color
	 * @return
	 */
	public static Light DirectionalLight(Direction direction, Color color)
	{
		return new Light(new Position(0.0f, 0.0f, 0.0f, 0.0f), direction, TYPE_DIRECTIONAL, 1f, 1f, color, 1f, 0f, 0f);
	}
	
	/**
	 * Creates a positioned spotlight which has an adjustable range
	 * @param position
	 * @param color
	 * @param range
	 * @param cutOffRange
	 * @return
	 */
	public static Light SpotLight(Position position, Color color, float range, float cutOffRange)
	{
		return new Light(position, new Direction(0.0f, 0.0f, 0.0f), TYPE_SPOTLIGHT, range, cutOffRange, color, 1f, 0f, 0f);
	}
	
	public static Light FlashLight(Position position, Direction direction, Color color, float range, float cutOffRange, float cutOffAngle, float cutOffAngleOuter)
	{
		return new Light(position, direction, TYPE_FLASHLIGHT, range, cutOffRange, color, 1f, cutOffAngle, cutOffAngleOuter);
	}
	
	public Position getPosition()
	{
		return position;
	}

	public Color getColor()
	{
		return color;
	}

	public Direction getDirection()
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public float getRange()
	{
		return range;
	}

	public void setRange(float range)
	{
		this.range = range;
	}

	public float getCutOffRange()
	{
		return cutOffRange;
	}

	public void setCutOffRange(float cutOffRange)
	{
		this.cutOffRange = cutOffRange;
	}

	public float getPower()
	{
		return power;
	}

	public void setPower(float power)
	{
		this.power = power;
	}

	public float getCutOffAngle()
	{
		return cutOffAngle;
	}

	public void setCutOffAngle(float cutOffAngle)
	{
		this.cutOffAngle = cutOffAngle;
	}

	public float getCutOffAngleOuter()
	{
		return cutOffAngleOuter;
	}

	public void setCutOffAngleOuter(float cutOffAngleOuter)
	{
		this.cutOffAngleOuter = cutOffAngleOuter;
	}
}
