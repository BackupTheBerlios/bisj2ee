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
	 * @param obj das hinzuzuf�gende Oject
	 *            <p/>
	 *            Die Methode erzeugt zun�chst ein neues Schalngenelement mit dem einzuf�genden Objekt.
	 *            Dies kann noch parallel ausgef�hrt werden.
	 *            Anschlie�end wird das Element eingetragen, dieser Vorgang kann nur gesch�tzt durchgef�hrt werden, um die
	 *            genannten Fehler zu vermeiden. Synchronisiert wird dabei auf this (warum auch nicht?)
	 *            <p/>
	 *            Das Einf�gen selbst funktioniert genau so wie bei der unsynchronisierten Queue (die leider gar nicht kommentiert wurde!)
	 */
	public void add(Object obj)
	{   // Erzeugen des einzuf�genden Knotens kann parallel ausgef�hrt werden!
		QueueElement nw = new QueueElement(obj);

		// Eintragen mu� unteilbar sein:
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
	 * Fall die Schlange leer ist, wird null zur�ckgegeben (parralelle Ausf�hrung m�glich, da nur lesender Zugriff
	 * auf last).
	 * Das anschlie�ende Austragen der zu entfernenden Knotens wird ebenfalls, durch Synchronisation auf this, gesch�tzt ausgef�hrt.
	 * Am Austragemechanismus selbst wurde nicht ver�ndert im Vergleich zur unkommentierten Originalschlange.
	 */
	public Object remove()
	{
		// Nur lesender Zugriff, kann auch bei gleichzeitigem add
		// noch korrekt parellel ausgef�hrt werden.
		if (last == null)
		{
			return null;
		}

		// gesch�tzter Block:
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