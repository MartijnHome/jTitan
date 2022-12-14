package sliedregt.martijn.jtitan.datatype.primal;

import java.util.ArrayList;

public class Indices
{

	private ArrayList<Integer> index;

	public Indices()
	{
		index = new ArrayList<Integer>();
	}

	public Indices(int[] i)
	{
		this();
		int s = 0;
		for (int a = 0; a < i.length; a++)
		{
			int ai = a;
			if (s == 1)
				ai += 1;
			if (s == 2)
				ai -= 1;
			index.add(Integer.valueOf(i[ai]));
			s++;
			if (s == 3)
				s = 0;
		}
	}

	public ArrayList<Integer> getIndex()
	{
		return index;
	}

	public void setIndex(ArrayList<Integer> index)
	{
		this.index = index;
	}

}
