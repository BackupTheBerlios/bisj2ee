
public class Event
{
	private int waiting = 0;

	public synchronized void SIGNAL()
	{
		waiting = Math.max(0, --waiting);
		notify();
	}

	public synchronized void WAIT()
	{
		try
		{
			waiting++;
			wait();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized int WAITING()
	{
		return waiting;
	}
}
