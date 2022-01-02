// Generated by delombok at Mon May 27 20:39:51 CEST 2013
package org.um.feri.ears.statistic.true_skill;
/**
 * Parameters about the game for calculating the TrueSkill.
 */

public class GameInfo {
	private static final double defaultInitialMean = 25.0;
	private static final double defaultBeta = defaultInitialMean / 6.0;
	private static final double defaultDrawProbability = 0.1;
	private static final double defaultDynamicsFactor = defaultInitialMean / 300.0;
	private static final double defaultInitialStandardDeviation = defaultInitialMean / 3.0;
	private double initialMean;
	private double initialStandardDeviation;
	private double beta;
	private double dynamicsFactor;
	private double drawProbability;
	
	public GameInfo(double initialMean, double initialStandardDeviation, double beta, double dynamicFactor, double drawProbability) {
		
		this.initialMean = initialMean;
		this.initialStandardDeviation = initialStandardDeviation;
		this.beta = beta;
		this.dynamicsFactor = dynamicFactor;
		this.drawProbability = drawProbability;
	}
	
	public static GameInfo getDefaultGameInfo() {
		// We return a fresh copy since we have public setters that can mutate state
		return new GameInfo(defaultInitialMean, defaultInitialStandardDeviation, defaultBeta, defaultDynamicsFactor, defaultDrawProbability);
	}
	
	public Rating getDefaultRating() {
		return new Rating(initialMean, initialStandardDeviation);
	}
	
	@SuppressWarnings("all")
	public double getInitialMean() {
		return this.initialMean;
	}
	
	@SuppressWarnings("all")
	public double getInitialStandardDeviation() {
		return this.initialStandardDeviation;
	}
	
	@SuppressWarnings("all")
	public double getBeta() {
		return this.beta;
	}
	
	@SuppressWarnings("all")
	public double getDynamicsFactor() {
		return this.dynamicsFactor;
	}
	
	@SuppressWarnings("all")
	public double getDrawProbability() {
		return this.drawProbability;
	}
	
	@SuppressWarnings("all")
	public void setInitialMean(final double initialMean) {
		this.initialMean = initialMean;
	}
	
	@SuppressWarnings("all")
	public void setInitialStandardDeviation(final double initialStandardDeviation) {
		this.initialStandardDeviation = initialStandardDeviation;
	}
	
	@SuppressWarnings("all")
	public void setBeta(final double beta) {
		this.beta = beta;
	}
	
	@SuppressWarnings("all")
	public void setDynamicsFactor(final double dynamicsFactor) {
		this.dynamicsFactor = dynamicsFactor;
	}
	
	@SuppressWarnings("all")
	public void setDrawProbability(final double drawProbability) {
		this.drawProbability = drawProbability;
	}
	
	@Override
	@SuppressWarnings("all")
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof GameInfo)) return false;
		final GameInfo other = (GameInfo)o;
		if (!other.canEqual((Object)this)) return false;
		if (Double.compare(this.getInitialMean(), other.getInitialMean()) != 0) return false;
		if (Double.compare(this.getInitialStandardDeviation(), other.getInitialStandardDeviation()) != 0) return false;
		if (Double.compare(this.getBeta(), other.getBeta()) != 0) return false;
		if (Double.compare(this.getDynamicsFactor(), other.getDynamicsFactor()) != 0) return false;
		if (Double.compare(this.getDrawProbability(), other.getDrawProbability()) != 0) return false;
		return true;
	}
	
	@SuppressWarnings("all")
	public boolean canEqual(final Object other) {
		return other instanceof GameInfo;
	}
	
	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		final long $initialMean = Double.doubleToLongBits(this.getInitialMean());
		result = result * PRIME + (int)($initialMean >>> 32 ^ $initialMean);
		final long $initialStandardDeviation = Double.doubleToLongBits(this.getInitialStandardDeviation());
		result = result * PRIME + (int)($initialStandardDeviation >>> 32 ^ $initialStandardDeviation);
		final long $beta = Double.doubleToLongBits(this.getBeta());
		result = result * PRIME + (int)($beta >>> 32 ^ $beta);
		final long $dynamicsFactor = Double.doubleToLongBits(this.getDynamicsFactor());
		result = result * PRIME + (int)($dynamicsFactor >>> 32 ^ $dynamicsFactor);
		final long $drawProbability = Double.doubleToLongBits(this.getDrawProbability());
		result = result * PRIME + (int)($drawProbability >>> 32 ^ $drawProbability);
		return result;
	}
	
	@Override
	@SuppressWarnings("all")
	public String toString() {
		return "GameInfo(initialMean=" + this.getInitialMean() + ", initialStandardDeviation=" + this.getInitialStandardDeviation() + ", beta=" + this.getBeta() + ", dynamicsFactor=" + this.getDynamicsFactor() + ", drawProbability=" + this.getDrawProbability() + ")";
	}
}