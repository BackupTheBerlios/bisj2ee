package aufgabe2;

class ColorSet
{
	// zulässige Farben
	final static int RED = 0;
	final static int GREEN = 1;
	final static int BLUE = 2;

	private final boolean[] myColors = new boolean[3];

	// auf eine Überprüfung der Parameter wird hier
	// sträflicherweise verzichtet

	public void add(int col)
	{
		myColors[col] = true;
	}

	public void remove(int col)
	{
		myColors[col] = false;
	}

	public boolean contains(int col)
	{
		return myColors[col];
	}
}