package aufgabe02;

import java.util.NoSuchElementException;

public class ThreadSaveLinkedList
implements Set
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private final ListElement firstNode = new ListElement(null);
	private volatile ListElement lastNode = firstNode;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Modifiers                     |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public void add(Object value)
	{
		if ( value == null ) return;

		ListElement newNode = new ListElement(value);

		synchronized ( this )
		{
			lastNode = lastNode.next = newNode;
		}
	}

	public void remove(Object value)
	{
		if ( value == null ) return;

		firstNode.OCCUPY();

		ListElement index = firstNode;
		ListElement nextNode = index.next;

		while ( nextNode != null )
		{
			if ( value.equals(nextNode.value) )
			{
				synchronized ( this )
				{
					if ( nextNode == lastNode )
					{
						index.next = null;
						lastNode = index;

						index.RELEASE();
						return;
					}
				}

				index.next = nextNode.next;

				index.RELEASE();
				return;
			}

			nextNode = nextNode.next;

			if ( nextNode != null ) nextNode.OCCUPY();
			index.RELEASE();

			index = index.next;
		}

		// Element was not found:
		index.RELEASE();
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Producers                     |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public Iterator iterator()
	{
		return new MyIterator();
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Inner Classes                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private class ListElement
	extends Semaphore
	{
		private volatile ListElement next;
		private volatile Object value;

		private ListElement(Object value)
		{
			this.value = value;
		}
	}

	private class MyIterator
	implements Iterator
	{
		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                 Instanzvariablen                  |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private ListElement index = firstNode.next;

		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                     Scanners                      |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		public Object next()
		throws NoSuchElementException
		{
			if ( !hasNext() ) throw new NoSuchElementException();

			Object value = index.value;

			index = index.next;
			return value;
		}

		public boolean hasNext()
		{
			return index != null;
		}
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                       Test                        |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public static void main(String[] args)
	{
		final ThreadSaveLinkedList list = new ThreadSaveLinkedList();

		final Thread[] threads = new Thread[100];

		for ( int i = 0; i < threads.length; i++ )
		{
			final int k = i;
			threads[i] = new Thread(new Runnable()
			{
				public void run()
				{

					list.add(new Integer(k));

				}

			});

		}

		for ( int i = 0; i < threads.length; i++ )
		{
			threads[i].start();

		}

		for ( int i = 0; i < threads.length; i++ )
		{
			try
			{
				threads[i].join();
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}

		}

		Iterator it = list.iterator();

		int count = 0;
		while ( it.hasNext() )
		{
			count++;
			System.out.print("" + it.next() + ", ");
		}

		System.out.println("\nLänge: " + count);

		for ( int i = threads.length - 1; i >= 0; i-- )
		{
			final int k = i;
			threads[i] = new Thread(new Runnable()
			{
				public void run()
				{
					list.remove(new Integer(k));

				}

			});

		}

		for ( int i = threads.length / 2; i >= 0; i-- )
		{
			threads[i].start();
		}

		for ( int i = 0; i <= threads.length / 2; i++ )
		{
			try
			{
				threads[i].join();
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}

		}

		it = list.iterator();

		count = 0;
		while ( it.hasNext() )
		{
			count++;
			System.out.print("" + it.next() + ", ");
		}

		System.out.println("\nLänge: " + count);


		threads[0] = new Thread(new Runnable()
		{
			public void run()
			{
				list.remove(new Integer(154));
			}

		});

		threads[1] = new Thread(new Runnable()
		{
			public void run()
			{
				list.remove(new Integer(99));
			}

		});

		threads[2] = new Thread(new Runnable()
		{
			public void run()
			{
				list.remove(new Integer(98));
			}

		});

		threads[0].start();
		threads[1].start();
		threads[2].start();

		try
		{
			threads[0].join();
			threads[1].join();
			threads[2].join();
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}

		it = list.iterator();

		count = 0;
		while ( it.hasNext() )
		{
			count++;
			System.out.print("" + it.next() + ", ");
		}

		System.out.println("\nLänge: " + count);

	}


}
