package sliedregt.martijn.jtitan.debug.pane;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import sliedregt.martijn.jtitan.datatype.scene.Renderable;
import sliedregt.martijn.jtitan.datatype.scene.Scene;


public class RenderablePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private boolean blank;
	private JComboBox<String> selection;
	private Renderable r;
	private RenderableControlPanel panel;
	private float adjustment;
	
	{
		adjustment = 0.001f;
		blank = true;
		this.setName("Renderable");
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	public void update(Scene s)
	{
		if (blank)
		{
		
			String[] list = new String[s.getR().size()];
			
			for (int i = 0; i < s.getR().size(); i++)
				list[i] = "Renderable " + i + " " + s.getR().get(i).getName();
		
			
			selection = new JComboBox<String>(list);
			selection.addActionListener(e -> r = s.getR().get(selection.getSelectedIndex()));
			selection.setMaximumSize(new Dimension(selection.getPreferredSize().width, 50));
			selection.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
			r = s.getR().get(0);
			panel = new RenderableControlPanel();
			this.add(selection);
			this.add(new JPanel()
					{
						private static final long serialVersionUID = 1L;
						{
							JToggleButton[] b = new JToggleButton[]
							{
									new JToggleButton("0.001f"),
									new JToggleButton("0.01f"),
									new JToggleButton("0.1f"),
									new JToggleButton("1f"),
									new JToggleButton("10f"),
									new JToggleButton("100f"),
									new JToggleButton("1000f")
							};
							
							float[] f = new float[] {0.001f, 0.01f, 0.1f, 1f, 10f, 100f, 1000f};
							for (int i = 0; i < b.length; i++)
							{
								b[i].addActionListener(new Listener(f[i], b));
								add(b[i]);
							}
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							setAlignmentX(JPanel.LEFT_ALIGNMENT);
						}
						
						class Listener implements ActionListener
						{
							private float f;
							JToggleButton[] b;
							
							Listener(float f, JToggleButton[] b)
							{
								this.f = f;
								this.b = b;
							}
						
							@Override
							public void actionPerformed(ActionEvent e)
							{
								for (JToggleButton b : b)
								{
									if (b != (JToggleButton) e.getSource())
										b.setSelected(false);
								}
					
					                   adjustment = f;
							}
						}
					});
			this.add(panel);
			blank = false;
			this.repaint();
		} else
		{
			panel.updateControls();
		}
	}
	
	private class RenderableControlPanel extends JPanel
	{
		private static final long serialVersionUID = 7447458644964962671L;
		
		JLabel[] position;
		JLabel[] rotation;
		JLabel[] scale;
		
		RenderableControlPanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			JLabel[] jLabel = new JLabel[] {
					new JLabel("Name: " + r.getName()),
					new JLabel("Description: " + r.getDescription()),
					new JLabel("Model: " + r.getModel().toString()),
					new JLabel("Mesh: " + r.getModel().getMesh().toString()),
					new JLabel("Shader: " + r.getModel().getShaderProgram()),
					new JLabel("Texture: " + r.getModel().getTexture())
			};
		
			for (JLabel i : jLabel)
			{
				i.setAlignmentX(JLabel.LEFT_ALIGNMENT);
				add(i);
			}
			
			position = new JLabel[] {
					new JLabel(String.format("%.5f", r.getPosition().x)),
					new JLabel(String.format("%.5f", r.getPosition().y)),
					new JLabel(String.format("%.5f", r.getPosition().z))
			};
			
			rotation = new JLabel[] {
					new JLabel(String.format("%.5f", r.getRotation().getX())),
					new JLabel(String.format("%.5f", r.getRotation().getY())),
					new JLabel(String.format("%.5f", r.getRotation().getZ()))
			};
			
			scale = new JLabel[] {
					new JLabel(String.format("%.5f", r.getScale().x)),
					new JLabel(String.format("%.5f", r.getScale().y)),
					new JLabel(String.format("%.5f", r.getScale().z))
			};

			add(new JPanel() 
			{
				private static final long serialVersionUID = 1L;
				{
					setBorder(BorderFactory.createTitledBorder("Position"));
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
					setAlignmentX(JPanel.LEFT_ALIGNMENT);
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
								
							dec.addActionListener(e -> r.getPosition().setX(r.getPosition().getX() - adjustment));
							inc.addActionListener(e -> r.getPosition().setX(r.getPosition().getX() + adjustment));
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
								
							dec.addActionListener(e -> r.getPosition().setY(r.getPosition().getY() - adjustment));
							inc.addActionListener(e -> r.getPosition().setY(r.getPosition().getY() + adjustment));
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
								
							dec.addActionListener(e -> r.getPosition().setZ(r.getPosition().getZ() - adjustment));
							inc.addActionListener(e -> r.getPosition().setZ(r.getPosition().getZ() + adjustment));
						}
					});
				}
			});
			
			add(new JPanel() 
			{
				private static final long serialVersionUID = 1L;
				{
					setBorder(BorderFactory.createTitledBorder("Rotation"));
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
					setAlignmentX(JPanel.LEFT_ALIGNMENT);
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("X: "));
							add(dec);
							add(rotation[0]);
							add(inc);
								
							dec.addActionListener(e -> r.getRotation().setX(r.getRotation().getX() - adjustment));
							inc.addActionListener(e -> r.getRotation().setX(r.getRotation().getX() + adjustment));
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
							add(rotation[1]);
							add(inc);
								
							dec.addActionListener(e -> r.getRotation().setY(r.getRotation().getY() - adjustment));
							inc.addActionListener(e -> r.getRotation().setY(r.getRotation().getY() + adjustment));
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
							add(rotation[2]);
							add(inc);
								
							dec.addActionListener(e -> r.getRotation().setZ(r.getRotation().getZ() - adjustment));
							inc.addActionListener(e -> r.getRotation().setZ(r.getRotation().getZ() + adjustment));
						}
					});
				}
			});
			
			add(new JPanel() 
			{
				private static final long serialVersionUID = 1L;
				{
					setBorder(BorderFactory.createTitledBorder("Scale"));
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
					setAlignmentX(JPanel.LEFT_ALIGNMENT);
					add(new JPanel() 
					{
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							JButton dec = new JButton("-");
							JButton inc = new JButton("+");
							add(new JLabel("X: "));
							add(dec);
							add(scale[0]);
							add(inc);
								
							dec.addActionListener(e -> r.getScale().setX(r.getScale().getX() - adjustment));
							inc.addActionListener(e -> r.getScale().setX(r.getScale().getX() + adjustment));
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
							add(scale[1]);
							add(inc);
								
							dec.addActionListener(e -> r.getScale().setY(r.getScale().getY() - adjustment));
							inc.addActionListener(e -> r.getScale().setY(r.getScale().getY() + adjustment));
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
							add(scale[2]);
							add(inc);
								
							dec.addActionListener(e -> r.getScale().setZ(r.getScale().getZ() - adjustment));
							inc.addActionListener(e -> r.getScale().setZ(r.getScale().getZ() + adjustment));
						}
					});
				}
			});
		}
		
		void updateControls()
		{
			
			position[0].setText(String.format("%.5f", r.getPosition().x));
			position[1].setText(String.format("%.5f", r.getPosition().y));
			position[2].setText(String.format("%.5f", r.getPosition().z));
			
			rotation[0].setText(String.format("%.5f", r.getRotation().getX()));
			rotation[1].setText(String.format("%.5f", r.getRotation().getY()));
			rotation[2].setText(String.format("%.5f", r.getRotation().getZ()));
			
			scale[0].setText(String.format("%.5f", r.getScale().getX()));
			scale[1].setText(String.format("%.5f", r.getScale().getY()));
			scale[2].setText(String.format("%.5f", r.getScale().getZ()));
		}
	}
}
