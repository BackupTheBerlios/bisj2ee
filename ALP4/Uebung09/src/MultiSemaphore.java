
import java.util.*;

public class MultiSemaphore
{
	private int permits;
	private Vector<Integer> waiting = new Vector<Integer>();

	public MultiSemaphore(int init)
	{
		permits = init;
	}

	public synchronized void P(int requestedPermits)
	{
		if (permits >= requestedPermits)
		{
			permits -= requestedPermits;
		}
		else
		{
			Integer request = new Integer(requestedPermits);
			waiting.add(request);

			try
			{
				request.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public synchronized void V(int releasedPermits)
	{
		permits += releasedPermits;

		while (waiting.size() > 0 && waiting.elementAt(0) <= permits)
		{
			Integer request = waiting.elementAt(0);
			waiting.removeElementAt(0);
			permits -= request;
			request.notify();
		}
	}

	public static void P(Set<MultiSemaphore> semas, int n)
	{
		synchronized (semas)
		{
			for (Iterator<MultiSemaphore> iterator = semas.iterator(); iterator.hasNext();)
			{
				iterator.next().P(n);
			}
		}
	}

	public static void V(Set<MultiSemaphore> semas, int n)
	{
		synchronized (semas)
		{
			for (Iterator<MultiSemaphore> iterator = semas.iterator(); iterator.hasNext();)
			{
				iterator.next().V(n);
			}
		}
	}
}
