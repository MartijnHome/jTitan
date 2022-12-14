package sliedregt.martijn.jtitan.datatype.scene;

import java.awt.Dimension;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import sliedregt.martijn.jtitan.api.graphics.Window;
import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.datatype.primal.Rotation;

public class Camera
{

	private Position position;
	private Rotation rotation;

	private float FOV;
	private float Z_NEAR;
	private float Z_FAR;
	private float aspectRatio;

	private final Matrix4f projectionMatrix;
	private final Matrix4f viewMatrix;
	private final Matrix4f yMatrix;

	private int mode;

	//private Window window;
	private Dimension dimension;

	public final static int MODE_3D_ORTHO = 1, MODE_3D_PERSP = 2;

	public Camera(int mode, Dimension d)
	{
		this.mode = mode;
		this.dimension = d;

		position = new Position(0.0f, 5.0f, 50.0f, 1.0f);
		rotation = new Rotation(0.0f, 0.0f, 0.0f);
		FOV = (float) Math.toRadians(65.0f);
		Z_NEAR = 0.1f;
		Z_FAR = 4000.0f;
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		yMatrix = new Matrix4f();
		update();
	}

	public void update()
	{
		final float w = (float) dimension.getWidth(), h = (float) dimension.getHeight();

		aspectRatio = w / h;

		projectionMatrix.identity();

		if (mode == MODE_3D_ORTHO)
			projectionMatrix.ortho(-250.0f, 250.0f, -250.0f, 250.0f, Z_NEAR, Z_FAR);

		if (mode == MODE_3D_PERSP)
			projectionMatrix.perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);

		viewMatrix.identity();
		// First do the rotation so camera rotates over its position
		viewMatrix.rotate((float) Math.toRadians(rotation.getX()), new Vector3f(1, 0, 0))
				.rotate((float) Math.toRadians(rotation.getY()), new Vector3f(0, 1, 0))
				.rotate((float) Math.toRadians(rotation.getZ()), new Vector3f(0, 0, 1));
		// Then do the translation
		viewMatrix.translate(-position.x, -position.y, -position.z);
		
		yMatrix.identity();
		yMatrix.rotate((float) Math.toRadians(0), new Vector3f(1, 0, 0))
			.rotate((float) Math.toRadians(rotation.getY()), new Vector3f(0, 1, 0))
			.rotate((float) Math.toRadians(0), new Vector3f(0, 0, 1));
	}

	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix()
	{
		return viewMatrix;
	}
	
	public Matrix4f getYMatrix()
	{
		return yMatrix;
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

	public Dimension getDimension()
	{
		return dimension;
	}
	
	public void pointAtPosition(Position p)
	{
		float[] angle = new float[3];
		angle[2] = 0.0f;
		
		float distanceX = position.getX() - p.getX();
		float distanceZ = position.getZ() - p.getZ();
		float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));
		angle[1] = (distanceX >= 0.0f) ? 270.0f + (float) Math.toDegrees(Math.atan(distanceZ / distanceX)) : 90.0f + (float) Math.toDegrees(Math.atan(distanceZ / distanceX));
		angle[0] = -(float) Math.toDegrees(Math.atan((p.getY() - position.getY()) / distance));
		
		rotation.set(angle[0], angle[1], angle[2]);
	}
	
	public Position placeBehindPosition(Position p, float distance, float angle)
	{
		float x = p.getX() - (distance * (float) Math.sin(Math.toRadians(angle)));
		float z = p.getZ() + (distance * (float) Math.cos(Math.toRadians(angle)));
		
		return new Position(x, p.getY(), z, 1.0f);
	}
}
