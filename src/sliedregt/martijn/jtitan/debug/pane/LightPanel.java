package sliedregt.martijn.jtitan.debug.pane;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sliedregt.martijn.jtitan.datatype.scene.Light;
import sliedregt.martijn.jtitan.datatype.scene.Scene;

public class LightPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private boolean blank;
	private JComboBox<String> selection;
	private LightControlPanel panel;
	private Light l;

	
	{
		blank = true;
		this.setName("Light");
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	public void update(Scene s)
	{
		if (blank)
		{
			String[] list = new String[s.getR().size()];
			
			for (int i = 0; i < s.getL().size(); i++)
				list[i] = "Light " + i;	
			
			selection = new JComboBox<String>(list);
			selection.addActionListener(e -> l = s.getL().get(selection.getSelectedIndex()));
			selection.setMaximumSize(new Dimension(selection.getPreferredSize().width, 50));
			l = s.getL().get(0);
			panel = new LightControlPanel();
			this.add(selection);
			this.add(panel);
			blank = false;
			this.repaint();
		} else
		{
			panel.updateControls();
		}
	}
	
	private class LightControlPanel extends JPanel
	{
		private static final long serialVersionUID = 7447458644964962671L;
		
		JLabel[] position;
		JLabel[] direction;
		JLabel[] color;
		
		LightControlPanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			position = new JLabel[] {
					new JLabel(String.format("%.5f", l.getPosition().x)),
					new JLabel(String.format("%.5f", l.getPosition().y)),
					new JLabel(String.format("%.5f", l.getPosition().z))
			};
			
			direction = new JLabel[] {
					new JLabel(String.format("%.5f", l.getDirection().x)),
					new JLabel(String.format("%.5f", l.getDirection().y)),
					new JLabel(String.format("%.5f", l.getDirection().z))
			};
			
			color = new JLabel[] {
					new JLabel(String.format("%.5f", l.getColor().r)),
					new JLabel(String.format("%.5f", l.getColor().g)),
					new JLabel(String.format("%.5f", l.getColor().b))
			};
			
			add(new JPanel() 
			{
				private static final long serialVersionUID = 1L;
				{
					setBorder(BorderFactory.createTitledBorder("Position"));
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
					
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("X: "));
							add(dec);
							add(position[0]);
							add(inc);
								
							dec.addActionListener(e -> l.getPosition().setX(l.getPosition().getX() - 0.1f));
							inc.addActionListener(e -> l.getPosition().setX(l.getPosition().getX() + 0.1f));
						}
					});
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("Y: "));
							add(dec);
							add(position[1]);
							add(inc);
								
							dec.addActionListener(e -> l.getPosition().setY(l.getPosition().getY() - 0.1f));
							inc.addActionListener(e -> l.getPosition().setY(l.getPosition().getY() + 0.1f));
						}
					});
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("Z: "));
							add(dec);
							add(position[2]);
							add(inc);
								
							dec.addActionListener(e -> l.getPosition().setZ(l.getPosition().getZ() - 0.1f));
							inc.addActionListener(e -> l.getPosition().setZ(l.getPosition().getZ() + 0.1f));
						}
					});
				}
			});
			
			add(new JPanel() 
			{
				private static final long serialVersionUID = 1L;
				{
					setBorder(BorderFactory.createTitledBorder("Direction"));
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
					
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("X: "));
							add(dec);
							add(direction[0]);
							add(inc);
								
							dec.addActionListener(e -> l.getDirection().setX(l.getDirection().getX() - 0.1f));
							inc.addActionListener(e -> l.getDirection().setX(l.getDirection().getX() + 0.1f));
						}
					});
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("Y: "));
							add(dec);
							add(direction[1]);
							add(inc);
								
							dec.addActionListener(e -> l.getDirection().setY(l.getDirection().getY() - 0.1f));
							inc.addActionListener(e -> l.getDirection().setY(l.getDirection().getY() + 0.1f));
						}
					});
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("Z: "));
							add(dec);
							add(direction[2]);
							add(inc);
								
							dec.addActionListener(e -> l.getDirection().setZ(l.getDirection().getZ() - 0.1f));
							inc.addActionListener(e -> l.getDirection().setZ(l.getDirection().getZ() + 0.1f));
						}
					});
				}
			});
			
			add(new JPanel() 
			{
				private static final long serialVersionUID = 1L;
				{
					setBorder(BorderFactory.createTitledBorder("Color"));
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
					
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("R: "));
							add(dec);
							add(color[0]);
							add(inc);
								
							dec.addActionListener(e -> l.getColor().setR(l.getColor().getR() - 0.1f));
							inc.addActionListener(e -> l.getColor().setR(l.getColor().getR() + 0.1f));
						}
					});
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("G: "));
							add(dec);
							add(color[1]);
							add(inc);
								
							dec.addActionListener(e -> l.getColor().setG(l.getColor().getG() - 0.1f));
							inc.addActionListener(e -> l.getColor().setG(l.getColor().getG() + 0.1f));
						}
					});
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("B: "));
							add(dec);
							add(color[2]);
							add(inc);
								
							dec.addActionListener(e -> l.getColor().setB(l.getColor().getB() - 0.1f));
							inc.addActionListener(e -> l.getColor().setB(l.getColor().getB() + 0.1f));
						}
					});
				}
			});
		}
		
		
		void updateControls()
		{
			position[0].setText(String.format("%.5f", l.getPosition().x));
			position[1].setText(String.format("%.5f", l.getPosition().y));
			position[2].setText(String.format("%.5f", l.getPosition().z));
			
			direction[0].setText(String.format("%.5f", l.getDirection().x));
			direction[1].setText(String.format("%.5f", l.getDirection().y));
			direction[2].setText(String.format("%.5f", l.getDirection().z));
			
			color[0].setText(String.format("%.5f", l.getColor().r));
			color[1].setText(String.format("%.5f", l.getColor().g));
			color[2].setText(String.format("%.5f", l.getColor().b));
		}
	}
}
