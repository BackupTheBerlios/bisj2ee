package aufgabe03;

public class ThreadSaveSet <$ValueType>
implements Set<$ValueType>
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private final $ValueType[] values;
	private final Integer[] locks;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Konstruktoren                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public ThreadSaveSet(int size)
	{
		if (size < 0)
			throw new IllegalArgumentException("size must not be negative");

		values = ($ValueType[]) new Object[size];
		locks = new Integer[size];

		for (int i = 0; i < locks.length; i++)
		{
			locks[i] = i;
		}
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Scanners                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public boolean contains($ValueType value)
	{
		return value != null && contains(value, value.hashCode() % values.length, 0);
	}

	private boolean contains($ValueType value, int index, int iteration)
	{
		if (iteration == values.length) return false;

		return values[index] != null && (values[index].equals(value) ||
		                                 contains(value, (index + 1) % values.length, iteration + 1));
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Modifiers                     |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public void add($ValueType value)
	throws Overflow
	{
		add(value, value.hashCode() % values.length, 0);
	}

	private void add($ValueType value, int index, int iteration)
	throws Overflow
	{
		if (iteration == values.length) throw new Overflow();

		synchronized (locks[index])
		{
			if (values[index] == null)
			{
				values[index] = value;
				return;
			}
		}

		add(value, (index + 1) % values.length, iteration + 1);
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                       Tests                       |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public static void main(String[] args)
	{
		ThreadSaveSet<Integer> testSet = new ThreadSaveSet<Integer>(10);

		for (int i = 11; i >= 1; i--)
		{
			try
			{
				testSet.add(i);
			}
			catch (Overflow overflow)
			{
				System.out.println("Overflow beim Einfügen von " + i);
			}

			System.out.println("Contains " + i + " ? " + testSet.contains(i));
			System.out.println("Contains " + (i * i) + " ? " + testSet.contains(i * i));
		}
	}
}
