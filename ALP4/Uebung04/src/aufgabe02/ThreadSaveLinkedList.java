package aufgabe02;

import java.util.NoSuchElementException;

public class ThreadSaveLinkedList <$Value>
implements Set<$Value>
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private final ListElement firstNode = new ListElement(null);
	private volatile ListElement lastNode = firstNode;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Modifiers                     |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public void add($Value value)
	{
		if (value == null) return;
		ListElement newNode = new ListElement(value);

		ListElement insertNode;

		// Synchronisationsarbeit:
		synchronized (this)
		{
			insertNode = lastNode;

			insertNode.OCCUPY();
			{
				lastNode = lastNode.next = newNode;
			}
			insertNode.RELEASE();
		}
	}

	//    --------|=|-----------|=||=|-----------|=|--------    \\

	public void remove($Value value)
	{
		if (value == null || firstNode.next == null) return;
		ListElement currentNode, nextNode;

		firstNode.OCCUPY();
		firstNode.next.OCCUPY();

		currentNode = firstNode;
		nextNode = currentNode.next;

		while (nextNode != null)
		{
			if (value.equals(nextNode.value))
			{
				//pause();

				currentNode.next = nextNode.next;

				if (nextNode == lastNode)
				{
					lastNode = currentNode;
				}

				currentNode.RELEASE();
				return;
			}

			nextNode = nextNode.next;

			if (nextNode != null) nextNode.OCCUPY();
			currentNode.RELEASE();

			currentNode = currentNode.next;
		}

		// Element was not found:
		currentNode.RELEASE();
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Producers                     |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public Iterator<$Value> iterator()
	{
		return new MyIterator();
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Inner Classes                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private class ListElement
	extends Semaphore
	{
		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                 Instanzvariablen                  |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private volatile ListElement next;
		private volatile $Value value;

		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                   Konstruktoren                   |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private ListElement($Value value)
		{
			this.value = value;
		}
	}

	//    --------|=|-----------|=||=|-----------|=|--------    \\

	private class MyIterator
	implements Iterator<$Value>
	{
		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                 Instanzvariablen                  |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private ListElement index = firstNode.next;

		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                     Scanners                      |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		public $Value next()
		throws NoSuchElementException
		{
			if (!hasNext()) throw new NoSuchElementException();

			$Value value = index.value;

			index = index.next;
			return value;
		}

		//    --------|=|-----------|=||=|-----------|=|--------    \\

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

		for (int i = 0; i < threads.length; i++)
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

		for (int i = 0; i < threads.length; i++)
		{
			threads[i].start();

		}

		for (int i = 0; i < threads.length; i++)
		{
			try
			{
				threads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}

		Iterator it = list.iterator();

		int count = 0;
		while (it.hasNext())
		{
			count++;
			System.out.print("" + it.next() + ", ");
		}

		System.out.println("\nLänge: " + count);

		for (int i = threads.length - 1; i >= 0; i--)
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

		for (int i = threads.length / 2; i >= 0; i--)
		{
			threads[i].start();
		}

		for (int i = 0; i <= threads.length / 2; i++)
		{
			try
			{
				threads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}

		it = list.iterator();

		count = 0;
		while (it.hasNext())
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
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		it = list.iterator();

		count = 0;
		while (it.hasNext())
		{
			count++;
			System.out.print("" + it.next() + ", ");
		}

		System.out.println("\nLänge: " + count);

	}
}