package aufgabe02;

import java.util.NoSuchElementException;

public class ThreadSaveLinkedList
implements Set
{
	private final ListElement first = new ListElement(null);
	private volatile ListElement last = first;

	public void add(Object obj)
	{
		ListElement neu = new ListElement(obj);

		synchronized (last)
		{
			last = last.next = neu;
		}
	}

	public void remove(Object obj)
	{
		ListElement previous;
		ListElement index;

		synchronized (first)
		{
			previous = first;
			index = first.next;
		}

		while (index != null)
		{
			synchronized (previous)
			{
				synchronized (index)
				{
					if (index.value.equals(obj))
					{
						previous.next = index.next;

						if (index == last)
							last = previous;

						return;
					}
					else
					{
						previous = index;
						index = index.next;

					}
				}
			}
		}
	}

	public Iterator iterator()
	{
		return new MyIterator();

	}

	private class ListElement
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
		private ListElement index = first.next;

		public Object next()
		throws NoSuchElementException
		{
			if (index == null) throw new NoSuchElementException();

			Object value = index.value;
			index = index.next;

			return value;
		}

		public boolean hasNext()
		{
			return index != null;
		}

	}

}
