package aufgabe02;

import java.util.NoSuchElementException;

interface Iterator
{
	Object next()
	throws NoSuchElementException;

	boolean hasNext();
}
