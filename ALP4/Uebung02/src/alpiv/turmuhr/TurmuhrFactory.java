package alpiv.turmuhr;

public class TurmuhrFactory
{
	public static Turmuhr createTurmuhr()
	{
		TurmuhrFrame turmF = new TurmuhrFrame();
		turmF.setVisible(true);
		return turmF;
	}
}
