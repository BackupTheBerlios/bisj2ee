
import java.text.DecimalFormat;

/**
 * Entspricht genau der Vorgabe, erweitert um eine hübsche Ausgabe.
 */
public class Instruction
{
	private static final DecimalFormat format = new DecimalFormat("000");

	byte operation;
	int operand;

	public String toString()
	{
		return format.format(operation) + ":\t" + format.format(operand);
	}
}
