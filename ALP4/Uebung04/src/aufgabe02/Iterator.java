package aufgabe02;

import java.util.NoSuchElementException;

interface Iterator <$Value>
{
	$Value next()
	throws NoSuchElementException;

	boolean hasNext();
}
