package aufgabe01;

class MaxComputation
{
	private static int myMax = 0;

	public static int getMax()
	{
		return myMax;
	}

	public static void compute(int[] field)
	{
		for ( int i = 0; i < field.length; i++ )
		{
			if ( field[i] > myMax )
			{
				/*try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}*/

				myMax = field[i];
			}
		}
	}
}
