
public class CodeLine
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	String label;
	String opcode;
	String operand;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Konstruktoren                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Zerlegt eine Programmzeile in ihre Bestandteile.
	 *
	 * @param line Programmzeile
	 */
	public CodeLine(String line)
	{
		if (line == null) throw new IllegalArgumentException("line must not be null");

		// GGF. existierendes Label extrahieren:
		if (line.indexOf(':') > 0)
		{
			label = line.substring(0, line.indexOf(':'));
			line = line.substring(line.indexOf(':') + 1).trim();
		}

		// Opcode und Operand mit Standard - Javafunktionalität extrahieren.
		// (Das Pattern beschreibt ein bis beliebig viele whitespaces)
		String[] components = line.trim().split("\\s+");

		opcode = components[0];

		// ggf. existierenden Operanden extrahieren.
		if (components.length > 1)
			operand = components[1];
	}
}