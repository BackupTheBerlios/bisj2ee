package aufgabe02;

import java.util.NoSuchElementException;

interface Iterator <$ValueType>
{
	$ValueType next()
	throws NoSuchElementException;

	boolean hasNext();
}
