package sliedregt.martijn.jtitan.physics.shape;

import org.joml.Vector2f;

import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.physics.CollisionShape;


public class EndlessCylinder extends CollisionShape
{

	private float radius;
	private float radiusSquared;
	
	public EndlessCylinder(Position position, float radius)
	{
		super(new Position(position));
		this.setRadius(radius);
		this.setRadiusSquared(radius * radius);
	}

	@Override
	public boolean isInside(Position p)
	{
		Position delta = new Position(p.x - position.x, 0f, p.z - position.z, 0f);
		delta.x *= delta.x;
		delta.z *= delta.z;
		return (delta.x + delta.z < radiusSquared);
	}
	
	public float getRadiusSquared()
	{
		return radiusSquared;
	}

	public void setRadiusSquared(float radiusSquared)
	{
		this.radiusSquared = radiusSquared;
		radius = (float) Math.sqrt(radiusSquared);
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
		radiusSquared = radius * radius;
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	@Override
	public Position calculateCollisionPosition(Position p, float slack)
	{
		Vector2f delta = new Vector2f();
		delta.x = p.x - position.x;
		delta.y = p.z - position.z;
		float distance = getRadius();
		float difference = (float) Math.sqrt((delta.x * delta.x) + (delta.y * delta.y));
		float scale = distance / difference;		
		delta = delta.mul(scale + slack);
		p.x = position.x + delta.x;
		p.z = position.z + delta.y;
		return p;
	}
}
