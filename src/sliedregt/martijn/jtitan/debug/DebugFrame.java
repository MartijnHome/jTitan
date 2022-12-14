package sliedregt.martijn.jtitan.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.scene.Scene;
import sliedregt.martijn.jtitan.manager.TaskManager;

public class DebugFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private DebugPanel d;
	private JButton b;
	private JButton p;
	private JButton r;
	
	public DebugFrame()
	{
		d = new DebugPanel();
		b = new JButton("Next");
		p = new JButton("Pause");
		r = new JButton("Resume");
		

		
		this.add(BorderLayout.CENTER, d);
		this.add(BorderLayout.SOUTH, new JPanel()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(b);
				add(p);
				add(r);
			}
		});
		
		Dimension s = new Dimension(500, 800);
		this.setPreferredSize(s);
		pack();
		validate();
	}

	public void update(TaskManager t, Scene s)
	{
		d.update(t, s);
	}

	public void setContinueButton(ActionListener l)
	{
		if (l == null)
		{
			b.removeActionListener(b.getActionListeners()[0]);
			return;
		}
		b.addActionListener(l);
	}
	
	public void setPauseButton(ActionListener l)
	{
		if (l == null)
		{
			p.removeActionListener(p.getActionListeners()[0]);
			return;
		}
		p.addActionListener(l);
	}
	
	public void setResumeButton(ActionListener l)
	{
		if (l == null)
		{
			r.removeActionListener(r.getActionListeners()[0]);
			return;
		}
		r.addActionListener(l);
	}
	
	public void writeLogText(String s)
	{
		d.writeLogText(ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH.mm.ss")) + ": " + s);
	}
	
	public void writeEngineText(String s)
	{
		d.writeEngineText(s);
	}
}
