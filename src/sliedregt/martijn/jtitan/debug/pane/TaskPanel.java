package sliedregt.martijn.jtitan.debug.pane;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import sliedregt.martijn.jtitan.datatype.task.JTitanTask;
import sliedregt.martijn.jtitan.manager.TaskManager;

public class TaskPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	JTree tree = new JTree();

	{
		this.setName("Task");
		this.add(tree);
	}

	public void update(TaskManager t)
	{
		this.remove(tree);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Tasks");
		DefaultMutableTreeNode tet = new DefaultMutableTreeNode("Engine Tasks");
		DefaultMutableTreeNode tjt = new DefaultMutableTreeNode("JTitan Tasks");

		//DefaultListModel<String> m = new DefaultListModel<String>();

		for (int i = 0; i < t.getEt().getCurrentTasks().size(); i++)
		{
			tet.add(new TaskNode(t.getEt().getCurrentTasks().get(i), i).newNode());
		}

		for (int i = 0; i < t.getJt().getCurrentTasks().size(); i++)
		{
			tjt.add(new TaskNode(t.getJt().getCurrentTasks().get(i), i).newNode());
		}

		top.add(tet);
		top.add(tjt);

		tree = new JTree(top);
		for (int i = 0; i < tree.getRowCount(); i++)
			tree.expandRow(i);
		this.add(tree);
		validate();
		repaint();
	}

	private class TaskNode
	{

		JTitanTask t;
		int i;

		TaskNode(JTitanTask t, int i)
		{
			this.t = t;
			this.i = i;
		}

		DefaultMutableTreeNode newNode()
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode("Task " + i);
			String[][] d = new String[][]
			{ new String[]
					{ "Index: " + i }, new String[]
					{ "Description: " + t.getDescription() } };
			for (int i = 0; i < d.length; i++)
			{
				DefaultMutableTreeNode noded = new DefaultMutableTreeNode(d[i][0]);
				for (int j = 1; j < d[i].length; j++)
					noded.add(new DefaultMutableTreeNode(d[i][j]));
				node.add(noded);
			}

			return node;
		}
	}
}
