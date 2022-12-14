package sliedregt.martijn.jtitan.datatype.task;

public final class TaskState
{

	private boolean isRunning;
	private boolean isFinished;

	{
		isFinished = false;
		isRunning = false;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public void setFinished(boolean isFinished)
	{
		this.isFinished = isFinished;
	}
}