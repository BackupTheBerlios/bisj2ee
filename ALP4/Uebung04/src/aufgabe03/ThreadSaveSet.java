package aufgabe03;

public class ThreadSaveSet
implements Set
{
	private final Object[] values;
	private final Integer[] locks;

	public ThreadSaveSet(int size)
	{
		values = new Object[size];

		locks = new Integer[size];

		for ( int i = 0; i < locks.length; i++ )
		{
			locks[i] = new Integer(i);
		}
	}

	public void add(Object obj)
	throws Overflow
	{
		int index = obj.hashCode() % values.length;

		add(obj, index, 0);

	}

	private void add(Object obj, int index, int iteration)
	throws Overflow
	{
		if ( iteration == values.length ) throw new Overflow();

		synchronized ( locks[index] )
		{
			if ( values[index] == null )
				values[index] = obj;
			else
				add(obj, (index + 1) % values.length, iteration + 1);
		}
	}

	public boolean contains(Object obj)
	{
		return contains(obj, obj.hashCode() % values.length, 0);
	}

	private boolean contains(Object obj, int index, int iteration)
	{
		if ( iteration == values.length ) return false;
		if ( values[index] == null ) return false;

		return values[index].equals(obj) || contains(obj, (index + 1) % values.length, iteration + 1);
	}

	public static void main(String[] args)
	{
		ThreadSaveSet testSet = new ThreadSaveSet(10);

		for ( int i = 1; i <= 11; i++ )
		{
			try
			{
				testSet.add(new Integer(i));
			}
			catch ( Overflow overflow )
			{
				System.out.println("Overflow beim Einfügen von " + i);
			}

			System.out.println("Contains " + i + " ? " + testSet.contains(new Integer(i)));
			System.out.println("Contains " + (i * i) + " ? " + testSet.contains(new Integer(i * i)));
		}


	}
}
