package aufgabe03;

public interface Set <$Value>
{
	void add($Value obj)
	throws Overflow;

	boolean contains($Value obj);
}

