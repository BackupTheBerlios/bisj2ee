package aufgabe02;

import java.util.NoSuchElementException;

/**
 * Implementation einer Threadsicheren einfach verketteten Liste.
 * <p/>
 * Einzutragene Werte werden in Knoten verpackt. Jeder Knoten hat Semaphoren - Funktionalit�t und einen Zeiger auf
 * ihren Nachfolger. In der Schlange wird ein Zeiger auf das erste und letzte Element gehalten. Zu Beginn werden beide
 * Zeiger mit dem Startknoten (enth�lt den Wert null) initialisiert.
 */
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

	/**
	 * Prinzip Einf�gen:
	 * <p/>
	 * Eingef�gt wird direkt am Ende der Schlange. Dazu wird zun�chst der letzte Knoten gelockt, um ein paralleles
	 * L�schen auszuschlie�en. Da sich der Zeiger last w�hrend des Einf�gens �ndert, mu� das parallele Einf�gen extra
	 * auschgeschlossen werden. Die geschieht, in dem man die ganze Aktion auf this synchronisiert. Das Einf�gen selber
	 * wird durch das Einf�gen des neuen Knotens ans Ende und das anchlie�ende aktualisieren des last-Pointers
	 * realisiert.
	 */
	public void add(Object value)
	{
		if (value == null) return;

		ListElement newNode = new ListElement(value);
		ListElement insertNode;

		// Synchronisationsarbeit:
		synchronized (this)
		{
			insertNode = lastNode;

			insertNode.ACQUIRE();
			{
				lastNode = lastNode.next = newNode;
			}
			insertNode.RELEASE();
		}
	}

	//    --------|=|-----------|=||=|-----------|=|--------    \\

	/**
	 * Prinzip L�schen:
	 * <p/>
	 * Um den wechselseitigen Ausschlu� zu minimieren und den Grad an Parallelit�t zu maximieren wurde folgendes
	 * Prinzip verwendet:
	 * <p/>
	 * Das L�schen beginnt immer am Anfang der Schlange. Daf�r werden die ersten beiden Knoten zun�chst gelockt.
	 * Es wird immer im zweiten der gelockten Knoten gesucht, ob der zu finden Wert vorliegt.
	 * <p/>
	 * Falls nicht, geht man so vor, um in der Schlange eine Position weiterzugehen (Rahmen symbolisieren die
	 * erhaltenenen Locks):
	 * <p/>
	 * <p/>
	 * .                        ???                            ???
	 * ?????    ?????    ?????  ???   ?????    ?????    ?????  ???  ?????    ?????    ?????
	 * ? A ?????? B ?????? C ?  ???   ? A ?????? B ?????? C ?  ???  ? A ?????? B ?????? C ?
	 * ?????    ?????    ?????  ???   ?????    ?????    ?????  ???  ?????    ?????    ?????
	 * .                        ???                            ???
	 * <p/>
	 * Dies tut man solange, bis man den gesuchten Wert im zweiten gelockten Knoten gefunden hat. Dann wird der
	 * next-Pointer des ersten Knotens auf dessen �bern�chsten Knoten ge�ndert. Dadurch wird der Knoten, der den zu
	 * entfernenden Wert enth�lt aus der Liste ausgekoppelt und durch den garbage collector entsorgt.
	 * <p/>
	 * .      ???????????????
	 * .      ?             ?
	 * ?????  ?  ?????    ?????
	 * ? A ????  ? B ?????? C ?
	 * ?????     ?????    ?????
	 * <p/>
	 * Anschlie�end werden die Sperren freigegeben.
	 * <p/>
	 * Der Vorteil bei diesem Verfahren ist, das pro parallelem Remove-Aufruf immer nur zwei bis drei Knoten blockiert
	 * werden und nicht die ganze Schlange. Das maximiert den Grad der Nebenl�ufigkeit. Theoretisch k�nnen so die H�lfte
	 * aller Elemente gleichzeitig entfernt werden.
	 */
	public void remove(Object value)
	{
		// Sonderf�lle null wird gesucht oder Schlage ist leer:
		if (value == null || firstNode.next == null) return;


		ListElement currentNode, nextNode;

		// Die ersten beiden Knoten werden gelockt.
		firstNode.ACQUIRE();
		firstNode.next.ACQUIRE();

		currentNode = firstNode;
		nextNode = currentNode.next;

		while (nextNode != null)
		{
			// Element gefunden?
			if (value.equals(nextNode.value))
			{
				currentNode.next = nextNode.next;

				// Sonderfall: L�schen des letzten Knotens:
				if (nextNode == lastNode)
				{
					// Zeiger mu� umgesetzt werden:
					lastNode = currentNode;
				}


				currentNode.RELEASE();
				return;
			}

			// Index- und Lockverschiebung:
			nextNode = nextNode.next;

			if (nextNode != null) nextNode.ACQUIRE();
			currentNode.RELEASE();

			currentNode = currentNode.next;
		}

		// Element was not found:
		currentNode.RELEASE();
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
		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                 Instanzvariablen                  |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private volatile ListElement next;
		private volatile Object value;

		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                   Konstruktoren                   |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private ListElement(Object value)
		{
			this.value = value;
		}
	}

	//    --------|=|-----------|=||=|-----------|=|--------    \\

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
			if (!hasNext()) throw new NoSuchElementException();

			Object value = index.value;

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

		System.out.println("\nL�nge: " + count);

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

		System.out.println("\nL�nge: " + count);


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

		System.out.println("\nL�nge: " + count);

	}
}