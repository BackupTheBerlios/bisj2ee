package alpiv.turmuhr;

public class StartClock
{
	public static void main(String[] args)
	{
		// Erzeugt eine neue Turmuhr:
		Turmuhr turmuhr = new TurmuhrFactory().createTurmuhr();

		// Erzeugt einen neuen Uhrentreiber, der die Turmuhr steuert:
		// (pro Sekunde Realzeit werden 50 Sekunden simuliert)
		ClockDriver clockDriver = new ClockDriver(turmuhr, 50);

		// Startet den Treiber-Thread:
		clockDriver.start();
	}
}