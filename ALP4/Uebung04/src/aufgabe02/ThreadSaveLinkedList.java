package aufgabe02;

public class ThreadSaveLinkedList
implements Set
{
	private ListElement root = null;

	public void add(Object obj)
	{
		ListElement neu = new ListElement(obj);

		if (root == null) root = neu;

		ListElement index = root;

		while (index.next != null)
		{
			index = index.next;
		}

		index.next = neu;
	}

	public void remove(Object obj)
	{
		if (root == null) return;

		if (root.value.equals(obj))
		{
			root = root.next;
			return;
		}

		ListElement index = root;

		while (index.next != null)
		{
			if (index.next.value.equals(obj))
			{
				index.next = index.next.next;
				return;
			}

			index = index.next;
		}

	}

	public Iterator iterator()
	{
		return null;
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

}
