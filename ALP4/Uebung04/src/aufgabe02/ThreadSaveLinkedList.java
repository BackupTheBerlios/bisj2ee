package aufgabe02;

import java.util.NoSuchElementException;

/**
 * Implementation einer threadsicheren einfach verketteten Liste mit generischen Elementtyp ($Value).
 * <p/>
 * Einzutragene Werte werden in Knoten verpackt. Jeder Knoten hat Semaphorenfunktionalität und einen Zeiger auf seinen
 * jeweiligen Nachfolger. In der Schlange wird ein Zeiger auf das erste und letzte Element gehalten.
 * Zu Beginn werden beide Zeiger mit einem Dummyknoten (enthält den Wert null) initialisiert.
 */
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

	/**
	 * Prinzip Einfügen:
	 * <p/>
	 * Eingefügt wird immer direkt am Ende der Schlange. Dazu wird zunächst der Zugriff auf den letzten Knoten gelockt,
	 * um ein paralleles Löschen auszuschließen. Da sich der Zeiger "last" während des Einfügens ändert, muß das
	 * parallele Einfügen extra ausgeschlossen werden. Dies geschieht, indem man die ganze Aktion auf this
	 * synchronisiert.
	 * <p/>
	 * Das Einfügen selber wird durch das Anhängen des neuen Knotens ans Ende und das anschließende aktualisieren
	 * des "last"-Pointers realisiert.
	 */
	public void add($Value value)
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
	 * Prinzip Löschen:
	 * <p/>
	 * Um den wechselseitigen Ausschluß zu minimieren und den Grad an Parallelität zu maximieren wurde folgendes
	 * Prinzip verwendet:
	 * <p/>
	 * Das Löschen beginnt immer am Anfang der Schlange. Dafür werden die ersten beiden Knoten zunächst gelockt.
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
	 * next-Pointer des ersten Knotens auf dessen übernächsten Knoten geändert. Dadurch wird der Knoten, der den zu
	 * entfernenden Wert enthält aus der Liste ausgekoppelt und durch den garbage collector entsorgt.
	 * <p/>
	 * .      ???????????????
	 * .      ?             ?
	 * ?????  ?  ?????    ?????
	 * ? A ????  ? B ?????? C ?
	 * ?????     ?????    ?????
	 * <p/>
	 * Anschließend werden die Sperren freigegeben.
	 * <p/>
	 * Der Vorteil bei diesem Verfahren ist, das pro parallelem Remove-Aufruf immer nur zwei bis drei Knoten blockiert
	 * werden und nicht die ganze Schlange. Das maximiert den Grad der Nebenläufigkeit. Theoretisch können so die Hälfte
	 * aller Elemente gleichzeitig entfernt werden.
	 */
	public void remove($Value value)
	{
		// Sonderfälle null wird gesucht oder Schlage ist leer:
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

				// Sonderfall: Löschen des letzten Knotens:
				if (nextNode == lastNode)
				{
					// Zeiger muß umgesetzt werden:
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

	public Iterator<$Value> iterator()
	{
		return new MyIterator();
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Inner Classes                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Implementation eines Knotens der Liste. Erbst Semaphorenfunktionalität aus {@link Semaphore}.
	 * Enthält ein Feld für den zu übergebenen Wert und einen Zeiger auf den Nachfolgerknoten.
	 */
	private class ListElement
	extends Semaphore
	{
		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                 Instanzvariablen                  |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private volatile ListElement next;
		private final $Value value;

		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                   Konstruktoren                   |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private ListElement($Value value)
		{
			this.value = value;
		}
	}

	//    --------|=|-----------|=||=|-----------|=|--------    \\

	/**
	 * Implementation eines Iterators, der durch die Schlange iteriert.
	 * Startelement ist der zweite Knoten der Schlange (1. ist Dummy).
	 */
	private class MyIterator
	implements Iterator<$Value>
	{
		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                 Instanzvariablen                  |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		private ListElement pointer = firstNode.next;

		//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
		//  |                     Scanners                      |   \\
		//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

		/**
		 * Gibt den Wert des aktuellen Knotens zurück und verschiebt den pointer auf dessen Nachfolger
		 *
		 * @return das nächste Element in der Schlange
		 * @throws NoSuchElementException wenn Schlange leer ist
		 */
		public $Value next()
		throws NoSuchElementException
		{
			if (!hasNext()) throw new NoSuchElementException();

			$Value value = pointer.value;

			pointer = pointer.next;
			return value;
		}

		//    --------|=|-----------|=||=|-----------|=|--------    \\

		/**
		 * Solange der pointer noch nicht null ist, kann auch noch bein Wert abgerufen werden.
		 *
		 * @return ob noch ein Wert abgerufen werden kann
		 */
		public boolean hasNext()
		{
			return pointer != null;
		}
	}
}