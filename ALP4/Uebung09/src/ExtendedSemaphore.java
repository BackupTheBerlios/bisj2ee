
/**
 * Implementation einer Semaphoren mit voreinstellbarer Anzahl von permits, bvasierend auf boolschen Semaphoren.
 */
public class ExtendedSemaphore
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private BooleanSemaphore[] semaphoreRing;

	private int lastBlocked = -1;
	private int firstBlocked = 0;
	private int distance = 0;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Konstruktoren                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Diese erweiterte Semaphore verwendet zum vergeben der unterschiedlichen Anzahl von Permits ein Array von
	 * boolschen Semaphoren. Die Größe dieses Feldes entspricht der Maximalzahl der permits. Das Array wird durch
	 * modulo-indexierung wie ein Ring verwendet. Es gibt zwei Indizes, welche den Anfang und das Ende desjenigen
	 * Bereiches im Feld begrenzen, in dem sich nur gesperrte Semaphoren befinden.
	 * <p/>
	 * Bei jeder Anfrage nach n permits wird der geperrte Bereich um n Semaphoren am hinteren Ende erweitert, bei
	 * Freigabe von n permits werden am vorderen Ende n Semaphoren entsperrt. Die Indizes werden entsprechend
	 * verschoben.
	 * <p/>
	 * Dabei gibt es zwei Möglichkeiten, wie sich die Zeiger überholen können:
	 * <p/>
	 * Der hintere Index überholt den vorderen, wenn mehr permits als maximal möglich angefordert werden. In diesem
	 * Fall tritt genau das gewünschte Verhalten einer Semaphore auf: Da die zu sperrenden Semaphoren bereits gesperrt
	 * sind, wird der aktuelle Thread solange schlafen gelegt, bis wieder Semaphoren frei sind (und zwar genau soviele
	 * wie noch fehlen).
	 * <p/>
	 * Der vordere Index überholt den hinteren, wenn mehr permits freigegeben werden als vorher angefordert wurden.
	 * Dieser Fall ist bei dieser Implementierung nicht erlaubt und wird mit einer Exception quittiert. Da bei
	 * zyklischer Verwendung der Array nicht einfach durch <= festgelstellt werden kann, welcher index wirklich vor dem
	 * anderen ist, wird der reale Abstand der Zeiger in einer Instanzvariablen ({@link #distance}) gehalten. Ist er 0
	 * oder kleiner, dürfen keine V() Operationen aufgerufen werden.
	 *
	 * @param init Anzahl der maximal zu vergebenen permits
	 */
	public ExtendedSemaphore(int init)
	{

		semaphoreRing = new BooleanSemaphore[init];

		for (int i = 0; i < semaphoreRing.length; i++)
		{
			semaphoreRing[i] = new BooleanSemaphore();

		}
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Schleife mit n Wiederholungen:
	 * Modulo-Inkrementieren des Index
	 * Sperren der aktuellen Semaphore
	 * Inkrementieren des Anstands
	 *
	 * @param n Anzahl der angeforderten permits
	 * @throws InterruptedException
	 */
	public synchronized void P(int n)
	throws InterruptedException
	{
		for (int i = 1; i <= n; i++)
		{
			lastBlocked = (lastBlocked + 1) % semaphoreRing.length;
			semaphoreRing[lastBlocked].P();
			distance++;
		}

	}

	/**
	 * Nach der Überprüfung, ob die Anzahl der freizugebenen permits zulässig ist:
	 * Schleife mit n Wiederholungen:
	 * Entsperren der aktuellen Semaphore
	 * Modulo-Dekrementieren des Index
	 * Dekrementieren des Anstands
	 *
	 * @param n Anzahl der freizugebenen permits
	 */
	public synchronized void V(int n)
	{
		if (distance < n) throw new IllegalArgumentException("Cannot release that much permits");

		for (int i = 1; i <= n; i++)
		{
			semaphoreRing[firstBlocked].V();
			firstBlocked = (firstBlocked + 1) % semaphoreRing.length;
			distance--;
		}
	}
}
