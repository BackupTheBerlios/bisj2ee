package aufgabe02;

interface Set <$Value>
{
	void add($Value value);

	void remove($Value value);

	Iterator<$Value> iterator();
}
