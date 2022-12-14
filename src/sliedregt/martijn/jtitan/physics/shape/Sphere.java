package sliedregt.martijn.jtitan.physics.shape;

import org.joml.Vector3f;

import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.physics.CollisionShape;


public class Sphere extends CollisionShape
{
	private float radius;
	private float radiusSquared;
	
	public Sphere(Position position, float radius)
	{
		super(new Position(position));
		this.setRadius(radius);
		this.setRadiusSquared(radius * radius);
	}

	@Override
	public boolean isInside(Position p)
	{
		Position delta = new Position(p.x - position.x, p.y - position.y, p.z - position.z, 0f);
		delta.x *= delta.x;
		delta.y *= delta.y;
		delta.z *= delta.z;
		return (delta.x + delta.z + delta.y < radiusSquared);
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

	@Override
	public Position calculateCollisionPosition(Position p, float slack)
	{
		Vector3f delta = new Vector3f();
		delta.x = p.x - position.x;
		delta.y = p.y - position.y;
		delta.z = p.z - position.z;
		float distance = getRadius();
		float difference = (float) Math.sqrt((delta.x * delta.x) + (delta.y * delta.y) + (delta.z * delta.z));
		float scale = distance / difference;		
		delta = delta.mul(scale + slack);
		p.x = position.x + delta.x;
		p.y = position.y + delta.y;
		p.z = position.z + delta.z;
		return p;
	}


}
