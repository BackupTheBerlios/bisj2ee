
public class BooleanSemaphore
{
	private boolean resource = true;

	public synchronized void P()
	{
		if (!resource)
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		assert resource;
		resource = false;
	}

	public synchronized void V()
	{
		resource = true;
		notify();
	}
}
