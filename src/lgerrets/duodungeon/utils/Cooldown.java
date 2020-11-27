package lgerrets.duodungeon.utils;

public class Cooldown {
	private int cptMax;
	private int cpt;
	
	public Cooldown(int cptMax, boolean starts_ready)
	{
		this.cptMax = cptMax;
		if (starts_ready)
			this.cpt = 0;
		else
			this.cpt = this.cptMax;
	}
	
	public void tick()
	{
		if (cpt > 0)
			cpt -= 1;
	}
	
	public boolean isReady()
	{
		return (cpt <= 0);
	}
	
	public void reset()
	{
		cpt = cptMax;
	}
}
