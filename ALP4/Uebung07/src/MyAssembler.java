
import java.util.HashMap;

public class MyAssembler
implements Assembler
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                Statische Methoden                 |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Liefert den Opcode zu einem Funktionsnamen.
	 *
	 * @param opcode der Funktionname
	 * @return den passenden Opcode
	 */
	private static final byte getOpcode(String opcode)
	{
		if (opcode.equalsIgnoreCase("load"))
			return 0;
		if (opcode.equalsIgnoreCase("store"))
			return 1;
		if (opcode.equalsIgnoreCase("jump"))
			return 2;
		if (opcode.equalsIgnoreCase("mult"))
			return 3;
		if (opcode.equalsIgnoreCase("decr"))
			return 4;
		if (opcode.equalsIgnoreCase("branchnz"))
			return 5;
		if (opcode.equalsIgnoreCase("return"))
			return 6;
		if (opcode.equalsIgnoreCase("nop"))
			return 7;
		if (opcode.equalsIgnoreCase("loadi"))
			return 8;

		throw new IllegalArgumentException("Operation not supported");
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private CodeLine[] codeLines; // nur lesender Zugriff
	private Instruction[] instructions;

	private volatile HashMap labelValues = new HashMap();
	private volatile HashMap labelQueue = new HashMap();

	/**
	 * Zunächst werden das Array mit den Codezeilen und ein zu füllendes Array mit den
	 * Ergebnis-Instruktionen als lokale Variablen gepeichert. Zusätzlich werden zwei HashMaps angelegt.
	 * Eine (labelValue) speichert zu einem gegebenen Labelnamen die passende Sprungadresse.
	 * Die zweite HashMap dient zur Synchronisation, welche nach folgendem Mechanismuß funktioniert:
	 * <p/>
	 * Soll ein Labelname durch eine Sprungadresse ersetzt werden, wird zunächst in der 1. HashMap
	 * nachgeschaut, oder zu dem Namen bereits die Adresse ermittelt und abgelegt wurde. Falls ja, wird die
	 * Ersetzung einfach vorgenommen. Falls die passende Adresse noch nicht ermittelt wurde, kommt die
	 * zweite HashMap als "Warteschlange" hinzu. In diese wird nun ein Object unter dem Labelnamen abgelegt
	 * und auf diesem wait aufgerufen in der Hoffnung, daß der zweite Thread irgendwann die passende
	 * Adresse errechnet und in der 1. HashMap abgelegt und dann auf diesem Objekt dann wieder notify
	 * aufruft (Was er auch tut).
	 * <p/>
	 * Der Konsument wartet also, bis der Lieferant die Adresse vorliegen hat und macht dann normal weiter.
	 * <p/>
	 * Die Methode erzeugt die entsprechenden Threads und startet sie.
	 * <p/>
	 * Hinweis: im Ernstfall sollte man threadsichere HashMaps verwenden, aber das würde hier über das
	 * eigentlich geforderte hinausgehen.
	 *
	 * @param codeLines
	 * @return
	 */
	public Instruction[] compile(CodeLine[] codeLines)
	{
		this.codeLines = codeLines;
		instructions = new Instruction[codeLines.length];

		Thread t1 = new Thread(new InstructionMaker());
		Thread t2 = new Thread(new LabelAdressCalculator());

		t1.start();
		t2.start();

		try
		{
			t1.join();
			t2.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		return instructions;
	}

	/**
	 * Die Klasse übersetzt CodeLines in Instructions. Muß dabei ein Label durch die konkrete Sprungadresse
	 * ersetzt werden, wird wie oben beschrieben vorgegegangen. Dieser Thread ist der einzige, der
	 * schreibend auf das Instructionsarray zugreift.
	 */
	private class InstructionMaker
	implements Runnable
	{

		public void run()
		{
			for (int i = 0; i < codeLines.length; i++)
			{
				CodeLine codeLine = codeLines[i];

				Instruction instruction = new Instruction();
				instruction.operation = getOpcode(codeLine.opcode);

				try
				{
					// Operand null?
					if (codeLine.operand == null)
						instruction.operand = 0;
					else // Operand Zahl?
						instruction.operand = Integer.parseInt(codeLine.operand);
				}
				catch (NumberFormatException e)
				{
					// Operand war keine Zahl und nicht null, also ein zu ersetzendes Label
					String label = codeLine.operand;

					System.out.println("Label gefunden: " + label);

					if (!labelValues.containsKey(codeLine.operand))
					{
						System.out.println("Adressse für '" + label + "' nicht gefunden.");

						final Object monitor = new Object();

						// Synchronisatiosarbeit (siehe oben)
						synchronized (monitor)
						{
							synchronized (labelQueue)
							{
								labelQueue.put(codeLine.operand, monitor);
							}

							try
							{
								System.out.println("Warte auf '" + label + "'");
								monitor.wait();
								System.out.println("Warten beendet: Adresse für '" + label + "' sollte" +
								                   " jetzt vorliegen.");

							}
							catch (InterruptedException e1)
							{
								e1.printStackTrace();
							}
						}
					}

					System.out.println("Adressse für '" + label + "' gefunden.");
					instruction.operand = ((Integer) (labelValues.get(codeLine.operand))).intValue();

				}
				finally
				{
					instructions[i] = instruction;
				}
			}
		}
	}

	/**
	 * Diese Klasse geht durch alle CodeLines lesend durch. sobald ein Label gefunden wurde, wird die
	 * passende Adresse in die erste HashMap geschrieben und anschließend auf einem evtl. vorhandenen
	 * Object in der Warteschlangen-HashMap unter diesem Labelnamen notify aufgerufen (einThread wartete
	 * also schon auf diese Berechnung).
	 */
	private class LabelAdressCalculator
	implements Runnable
	{

		public void run()
		{
			for (int i = 0; i < codeLines.length; i++)
			{
				CodeLine codeLine = codeLines[i];

				if (codeLine.label != null)
				{
					labelValues.put(codeLine.label, new Integer(4 * i));

					System.out.println("\tAdresse für '" + codeLine.label + "' wurde berechnet und " +
					                   "hinterlegt");

					if (labelQueue.get(codeLine.label) != null)
					{
						synchronized (labelQueue.get(codeLine.label))
						{
							System.out.println("\tWartesperre für '" + codeLine.label + "' wird " +
							                   "freigegeben");

							labelQueue.get(codeLine.label).notifyAll();
						}
					}
				}
			}
		}
	}
}