package sliedregt.martijn.jtitan.debug;

import java.awt.GridLayout;

import javax.swing.JPanel;

import sliedregt.martijn.jtitan.datatype.scene.Scene;
import sliedregt.martijn.jtitan.manager.TaskManager;

public class DebugPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DebugPane d;

	{
		d = new DebugPane();
		add(d);
		setLayout(new GridLayout(1, 1));
	}

	public void update(TaskManager t, Scene s)
	{
		d.update(t, s);
	}
	
	public void writeLogText(String s)
	{
		d.writeLogText(s);
	}
	
	public void writeEngineText(String s)
	{
		d.writeEngineText(s);
	}
}
