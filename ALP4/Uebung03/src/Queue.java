
class Queue
{
	private class QueueElement
	{
		volatile Object value;
		volatile QueueElement next = null;

		QueueElement(Object val)
		{
			this.value = val;
		}
	}

	// Verweis auf das letzte Element der Liste
	// ist dieses null, dann ist die Schlange leer.
	volatile private QueueElement last = null;

	public void add(Object obj)
	{
		QueueElement nw = new QueueElement(obj);
		if (last == null)
		{
			nw.next = nw;
		}
		else
		{
			nw.next = last.next;
			last.next = nw;
		}
		last = nw;
	}

	public Object remove()
	{
		if (last == null)
		{
			return null;
		}
		else if (last.next == last)
		{
			Object save = last.value;
			last = null;
			return save;
		}
		else
		{
			Object val = last.next.value;
			last.next = last.next.next;
			return val;
		}
	}
}
