package me.naithantu.SimplePVP;

public class PlayerScore {

	String name = null;
	int kills = 0;
	int deaths = 0;
	int score = 0;
	int killStreak = 0;

	public PlayerScore(String name) {
		this.name = name;
	}

	public void addKill() {
		kills++;
		killStreak++;
	}

	public void addDeath() {
		deaths++;
	}

	public void addScore() {
		score++;
	}
	
	public void stopKillStreak() {
		killStreak = 0;
	}
	
	

	public int getKills() {
		return kills;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getKillStreak(){
		return killStreak;
	}
}
