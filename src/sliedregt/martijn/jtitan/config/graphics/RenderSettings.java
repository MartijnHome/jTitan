package sliedregt.martijn.jtitan.config.graphics;

public class RenderSettings
{
	public static final int RENDER_ORDER_NO_CARE = 0, RENDER_ORDER_FRONT_TO_BACK = 1, RENDER_ORDER_BACK_TO_FRONT = 2, RENDER_ORDER_BACK_LAST = 3, RENDER_ORDER_BACK_FIRST = 4;
	private int renderOrder;
	private boolean useMaximumDistance;
	private float maximumDistance;
	private boolean isVisible;
	
	public RenderSettings()
	{
		setRenderOrder(RENDER_ORDER_NO_CARE);
		setUseMaximumDistance(false);
		setMaximumDistance(0.0f);
		setVisible(true);
	}

	public int getRenderOrder()
	{
		return renderOrder;
	}

	public void setRenderOrder(int renderOrder)
	{
		this.renderOrder = renderOrder;
	}

	public float getMaximumDistance()
	{
		return maximumDistance;
	}

	public void setMaximumDistance(float maximumDistance)
	{
		this.maximumDistance = maximumDistance;
	}

	public boolean isUseMaximumDistance()
	{
		return useMaximumDistance;
	}

	public void setUseMaximumDistance(boolean useMaximumDistance)
	{
		this.useMaximumDistance = useMaximumDistance;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}
}
