
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Start
{
	/**
	 * Zunächst wird das Programm aus der Eingabedatei zeilenweise eingelesen. Und in ein CodeLine-Array
	 * geschrieben. Das Transformieren der Zeilen in  ihre Repräsentationsobjekte erfolgt in der Klasse
	 * {@link CodeLine}. Das Array wird einem Exemplar der Klasse MyAssembler zu parsen übergeben und
	 * anschließend wird das Ergebnis in einer Outputdatei (Filename angeben!) geschrieben.
	 *
	 * @param args Der Input und der Outputfile
	 */
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.err.println(usage());
			System.exit(-1);
		}

		//    --------|=|-----------|=| Einlesen der Daten |=|-----------|=|--------    \\

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(args[0]));
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Eingabedatei konnte nicht gefunden werden.");
			System.exit(-1);
		}

		Vector fileLines = new Vector();
		String singleLine;

		try
		{
			while ((singleLine = reader.readLine()) != null)
			{
				fileLines.add(singleLine);
			}
		}
		catch (IOException e)
		{
			System.err.println("Eingabedaten konnten nicht gelesen werden." +
			                   "\nEingabedaten korrupt");
			System.exit(-1);
		}

		CodeLine[] codeLines = new CodeLine[fileLines.size()];

		int i = 0;
		for (Iterator iterator = fileLines.iterator(); iterator.hasNext();)
		{
			codeLines[i++] = new CodeLine((String) iterator.next());
		}

		//    --------|=|-----------|=| Parsen der Daten |=|-----------|=|--------    \\

		Instruction[] instructions = new MyAssembler().compile(codeLines);

		//    --------|=|-----------|=| Ausgeben der Daten |=|-----------|=|--------    \\

		DecimalFormat format = new DecimalFormat("000");
		BufferedWriter out;

		try
		{
			out = new BufferedWriter(new FileWriter(args[1]));

			for (int j = 0; j < instructions.length; j++)
			{
				out.write(format.format(4 * j) + ":\t" + instructions[j]);
				out.newLine();
			}

			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			System.err.println("Fehler beim Schreiben der Ausgabedatei.");
		}
	}

	private static String usage()
	{
		return "usage: Uebung07.jar <Input-Filename> <Output-Filename>";
	}
}
