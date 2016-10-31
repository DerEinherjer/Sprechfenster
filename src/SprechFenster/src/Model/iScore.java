package Model;

public interface iScore extends Comparable<iScore>
{
	public iFencer getFencer();
	public int getWins();
	public int getHits();
	public int getGotHit();
	public int getHitDifference();
}
