package aufgabe02;

interface Set <ValueType>
{
	void add(ValueType value);

	void remove(ValueType value);

	Iterator<ValueType> iterator();
}
