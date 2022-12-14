package sliedregt.martijn.jtitan.debug;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import sliedregt.martijn.jtitan.datatype.scene.Scene;
import sliedregt.martijn.jtitan.debug.pane.EnginePanel;
import sliedregt.martijn.jtitan.debug.pane.LightPanel;
import sliedregt.martijn.jtitan.debug.pane.LogPanel;
import sliedregt.martijn.jtitan.debug.pane.RenderablePanel;
import sliedregt.martijn.jtitan.debug.pane.TaskPanel;
import sliedregt.martijn.jtitan.manager.TaskManager;

public class DebugPane extends JTabbedPane
{
	private static final long serialVersionUID = 1L;
	
	private JPanel[] t;

	{
		t = new JPanel[]
		{ new EnginePanel(), new RenderablePanel(), new LightPanel(), new TaskPanel(), new LogPanel()};

		JScrollPane e = new JScrollPane(t[0]);
		JScrollPane s = new JScrollPane(t[1]);
		JScrollPane l = new JScrollPane(t[4]);
		this.addTab(t[0].getName(), e);
		this.addTab(t[1].getName(), s);
		this.addTab(t[2].getName(), t[2]);
		this.addTab(t[3].getName(), t[3]);
		this.addTab(t[4].getName(), l);

		this.setTabPlacement(JTabbedPane.TOP);
		this.setSelectedIndex(2);
	}

	public void update(TaskManager t, Scene s)
	{
		if (s != null)
		{
			((RenderablePanel) this.t[1]).update(s);
			((LightPanel) this.t[2]).update(s);
		}
			
		if (t != null)
		{
			((TaskPanel) this.t[3]).update(t);
		}
		
	}
	
	public void writeLogText(String s)
	{
		((LogPanel) this.t[4]).writeLogText(s);
	}
	
	public void writeEngineText(String s)
	{
		((EnginePanel) this.t[0]).writeLogText(s);
	}
}
