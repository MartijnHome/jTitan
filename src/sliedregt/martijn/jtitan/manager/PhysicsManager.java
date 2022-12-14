package sliedregt.martijn.jtitan.manager;

import java.util.ArrayList;
import java.util.List;

import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.physics.CollisionShape;

public class PhysicsManager
{
	private List<CollisionShape> shape;
	
	
	public PhysicsManager()
	{
		setShape(new ArrayList<CollisionShape>());
	}



	
	public CollisionShape isColliding(Position p)
	{
		for (CollisionShape s : shape)
			if (s.isInside(p)) return s;
		return null;
	}
	
	public Position check(Position oldPosition, Position newPosition, int limit, float slack)
	{
		for (int i = 0; i < limit; i++)
		{
			CollisionShape s = isColliding(newPosition);

			if (s == null)
				return newPosition;
			
			newPosition = s.calculateCollisionPosition(newPosition, slack);
		}

		return oldPosition;
	}

	public List<CollisionShape> getShape()
	{
		return shape;
	}

	public void setShape(List<CollisionShape> shape)
	{
		this.shape = shape;
	}
}
