package sliedregt.martijn.jtitan.debug.pane;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class EnginePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	JTextArea ta = new JTextArea();
	
	{
		this.setName("Scene");
		this.add(ta);
		ta.setText("jTitan Engine Log");
	}
	
	public void writeLogText(String s)
	{
		ta.setText(ta.getText() + '\n' + s);
	}
}
