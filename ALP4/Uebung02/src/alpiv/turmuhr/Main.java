package alpiv.turmuhr;


public class Main
{
	public static void main(String args[])
	{
		final Turmuhr uhr = TurmuhrFactory.createTurmuhr();
		try
		{
			uhr.setTime(10, 30, 45);
			uhr.highBell();
			Thread.sleep(1000);
			uhr.lowBell();
			Thread.sleep(2100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}
		System.exit(0);
	}
}
