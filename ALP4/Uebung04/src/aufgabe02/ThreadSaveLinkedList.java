package aufgabe02;

import java.util.NoSuchElementException;

public class ThreadSaveLinkedList
implements Set
{
	private ListElement root = null;

	public void add(Object obj)
	{
		ListElement neu = new ListElement(obj);

		synchronized (this)
		{
			if (root == null) root = neu;
		}

		ListElement index = root;

		while (index.next != null)
		{
			synchronized (index)
			{
				index = index.next;
			}
		}

		index.next = neu;
	}

	public void remove(Object obj)
	{
		if (root == null) return;

		synchronized (this)
		{
			if (root.value.equals(obj))
			{
				root = root.next;
				return;
			}
		}

		ListElement index = root;

		while (index.next != null)
		{

			synchronized (index.next)
			{
				if (index.next.value.equals(obj))
				{
					index.next = index.next.next;
					return;
				}
			}

			synchronized (index)
			{
				index = index.next;
			}
		}

	}

	public Iterator iterator()
	{
		return new MyIterator();

	}

	private class ListElement
	{
		private ListElement next;
		private Object value;

		private ListElement(Object value)
		{
			this.value = value;
		}
	}

	private class MyIterator
	implements Iterator
	{
		private ListElement index = root;

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
			return !(index == null || index.next == null);
		}

	}

}
