package sliedregt.martijn.jtitan.physics;

import sliedregt.martijn.jtitan.datatype.primal.Position;

public abstract class CollisionShape
{
	protected Position position;
	
	abstract public boolean isInside(Position p);
	abstract public Position calculateCollisionPosition(Position p, float slack);
	
	public CollisionShape(Position position)
	{
		this.setPosition(new Position(position));
	}
	
	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}
	
}
