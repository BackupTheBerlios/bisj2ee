package aufgabe03;


/**
 * Implementiert eine Menge vom Basistyp $ValueType. Die Elemente werden in einem Array abgelegt. Der Index wird aus
 * dem hashCode des einzutragenen Wertes errechnet (hashcode modulo gr��e des Sets). Ist das errechnete Feld schon
 * belegt, wird der Index inkrementiert solange, bis ein freies Feld gefunden wird, oder das ganze Feld durchlaufen
 * wurde (Overflow).
 * <p/>
 * Nach dem gleichen Verfahren wird ein Element gesucht. Wurde das ganze Feld durchsucht oder st��t man auf ein leeres
 * Feld, ist das Element nicht vorhanden.
 * <p/>
 * Synchronisiert wird das Schreiben und Schreiben eines Feld des Arrays, um mehrfaches Schreiben und
 * �berlappendes Lesen / Schreiben zu vermeiden.
 * In einem zweiten Array gleicher Gr��e wird daf�r f�r jeden ArrayIndex ein Integer-Objekt gehalten, �ber das der
 * Uugriff synchronisiert werden kann (ein Arrayfeld direkt kann nicht gesch�tzt werden), da nur ein Thread den lock
 * auf das Objekt gleichzeitig haben kann.
 */
public class ThreadSaveSet <$Value>
implements Set<$Value>
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private final Object[] values;
	private final Integer[] locks;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Konstruktoren                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Initialisiert das Array f�r die Werte und die Lock-Objekte. F�llt das Array mit den Lock-Objekten.
	 *
	 * @param size die Gr��e des sets.
	 */
	public ThreadSaveSet(int size)
	{
		if (size < 0)
			throw new IllegalArgumentException("size must not be negative");

		values = new Object[size];
		locks = new Integer[size];

		for (int i = 0; i < locks.length; i++)
		{
			locks[i] = new Integer(i);
		}
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Scanners                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Der zu suchende Wert wird an eine Hilfsfunktion ({@link #contains($Value, int, int)} �bergeben.
	 * Der Startindex ist der Hashcode des Objekts modulo der Setgr��e. Der Initerationsz�hler wird mit 0 gestartet.
	 *
	 * @param value der zu suchende Wert
	 * @return ob der Wert vorhanden ist.
	 */
	public boolean contains($Value value)
	{
		return value != null && contains(value, value.hashCode() % values.length, 0);
	}

	/**
	 * Falls das Array komplett durchlaufen wurde oder das der zu untersuchenden Stelle null vorliegt, enth�lt das Set
	 * den zu suchenden Wert nicht.
	 * Falls an der Stelle der Wert gefunden wurde, wird true zur�ckgegeben, ansonsten wird die Methode rekursiv
	 * aufgerufen mit den inkrementierten Feldindex und Iterationsz�hler. Der Lesezugriff wird �ber das jeweils
	 * passende Lockobjekt synchronisiert.
	 *
	 * @param value     der zu suchende Wert
	 * @param index     der aktuelle SuchIndex
	 * @param iteration der wievielte Versuch?
	 * @return Wert vorhanden?
	 */
	private boolean contains($Value value, int index, int iteration)
	{
		if (iteration == values.length) return false;

		final Object compare;

		synchronized (locks[index])
		{
			compare = values[index];
		}

		if (compare == null) return false;

		return compare.equals(value) || contains(value, (index + 1) % values.length, iteration + 1);
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Modifiers                     |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Der hinzuzuf�gende Wert (darf nicht null sein) wird an eine Hilfsfunktion ({@link #add(Object, int, int)}
	 * �bergeben. Das Iterieren des Arrays funktioniert nach dem gleichen Prinzip wie das Suchen.
	 *
	 * @param value der hinzuzuf�gende Wert
	 * @throws Overflow falls das Set keinen Platz mehr hat.
	 */
	public void add($Value value)
	throws Overflow
	{
		if (value == null) throw new IllegalArgumentException("value must not be null");

		add(value, value.hashCode() % values.length, 0);
	}

	/**
	 * Falls das FEld komplett durchlaufen wurde, wird ein Overflow ausgel�st. Falls man an der aktuellen Stelle das
	 * Objekt bereits eingetragen ist, wird die Methode beendet (keine Doppleintragungen im Set). Falls ein anderes
	 * Objekt gefunden wird, erfolgt der rekursive Aufruf mit den inkrementierten Feldindex und Iterationsz�hler.
	 * Falls an der Stelle null vorliegt, wird der einzutragende Wert an die aktuelle Stelle geschrieben.
	 * <p/>
	 * Auch der Schreibzugriff wird �ber das paasende Lockobjekt synchronisiert.
	 *
	 * @param value     der hinzuzuf�gende Wert
	 * @param index     aktueller Feldlindex
	 * @param iteration der wievielte Versuch?
	 * @throws Overflow falls das Set keinen Platz mehr hat.
	 */
	private void add(Object value, int index, int iteration)
	throws Overflow
	{
		if (iteration == values.length) throw new Overflow();

		synchronized (locks[index])
		{
			final Object compare = values[index];

			if (compare == value)
			{
				return;
			}

			if (compare == null)
			{
				values[index] = value;
				return;
			}
		}

		add(value, (index + 1) % values.length, iteration + 1);
	}
}
