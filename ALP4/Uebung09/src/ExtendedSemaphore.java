
public class ExtendedSemaphore
{

	private BooleanSemaphore[] semaphoreRing;
	private int blockIndex = 0;
	private int releaseIndex = 0;
	private int distance = 0;

	public ExtendedSemaphore(int init)
	{

		semaphoreRing = new BooleanSemaphore[init];

		for (int i = 0; i < semaphoreRing.length; i++)
		{
			semaphoreRing[i] = new BooleanSemaphore();

		}
	}

	public synchronized void P(int n)
	{
		for (int i = 1; i <= n; i++)
		{
			semaphoreRing[blockIndex].P();
			blockIndex = (blockIndex + 1) % semaphoreRing.length;
			distance++;
		}

	}

	public synchronized void V(int n)
	{
		for (int i = 1; i <= n; i++)
		{
			if (distance <= 0) throw new IllegalArgumentException("Cannot release that much permits");

			semaphoreRing[releaseIndex].V();
			releaseIndex = (releaseIndex + 1) % semaphoreRing.length;
			distance--;
		}
	}
}
