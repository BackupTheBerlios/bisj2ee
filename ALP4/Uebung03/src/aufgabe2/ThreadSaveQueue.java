package aufgabe2;

class ThreadSaveQueue
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

	/**
	 * @param obj das hinzuzufügende Oject
	 *            <p/>
	 *            Die Methode erzeugt zunächst ein neues Schalngenelement mit dem einzufügenden Objekt.
	 *            Dies kann noch parallel ausgeführt werden.
	 *            Anschließend wird das Element eingetragen, dieser Vorgang kann nur geschützt durchgeführt werden, um die
	 *            genannten Fehler zu vermeiden. Synchronisiert wird dabei auf this (warum auch nicht?)
	 *            <p/>
	 *            Das Einfügen selbst funktioniert genau so wie bei der unsynchronisierten Queue (die leider gar nicht kommentiert wurde!)
	 */
	public void add(Object obj)
	{   // Erzeugen des einzufügenden Knotens kann parallel ausgeführt werden!
		QueueElement nw = new QueueElement(obj);

		// Eintragen muß unteilbar sein:
		synchronized (this)
		{
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
	}

	/**
	 * Fall die Schlange leer ist, wird null zurückgegeben (parralelle Ausführung möglich, da nur lesender Zugriff
	 * auf last).
	 * Das anschließende Austragen der zu entfernenden Knotens wird ebenfalls, durch Synchronisation auf this, geschützt ausgeführt.
	 * Am Austragemechanismus selbst wurde nicht verändert im Vergleich zur unkommentierten Originalschlange.
	 */
	public Object remove()
	{
		// Nur lesender Zugriff, kann auch bei gleichzeitigem add
		// noch korrekt parellel ausgeführt werden.
		if (last == null)
		{
			return null;
		}

		// geschützter Block:
		synchronized (this)
		{
			if (last.next == last)
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
}