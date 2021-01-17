package lgerrets.duodungeon.utils;

public class Cooldown {
	public int cptMax;
	public int cpt;
	
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
		this.tick(1);
	}
	
	public void tick(int delta)
	{
		if (cpt > 0)
			cpt -= delta;
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
