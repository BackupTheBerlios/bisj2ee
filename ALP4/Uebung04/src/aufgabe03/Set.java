package aufgabe03;

public interface Set <$ValueType>
{
	void add($ValueType obj)
	throws Overflow;

	boolean contains($ValueType obj);
}

