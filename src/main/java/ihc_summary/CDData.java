package ihc_summary;

/** all fields for Aperio data file of CD type
 */

public class CDData {
	int region;
	double length;
	double areaUm;
	String text;
	double onePctNuclei;
	double zeroPctNuclei;
	double avgPosIntensity;
	double avgNegIntensity;
	int threeNuclei;
	int twoNuclei;
	int oneNuclei;
	int zeroNuclei;
	int totalNuclei;
	double avgRGBIntensity;
	double avgSizePix;
	double avgSizeUm;
	int areaAnalysisPix;
	double areaAnalysisMm;
	double pctPosNuclei;
	int intensityScore;
	double threePctNuclei;
	double twoPctNuclei;
	double density;
	double percent;
	double hScore;
	
	public int getRegion() {
		return region;
	}
	public void setRegion(int value) {
		region = value;
	}
	
	public double getLength() {
		return length;
	}
	public void setLength(double value) {
		length = value;
	}
	
	public double getAreaUm() {
		return areaUm;
	}
	public void setAreaUm(double value) {
		areaUm = value;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String value) {
		text = value;
	}
	
	public double getOnePctNuclei() {
		return onePctNuclei;
	}
	public void setOnePctNuclei(double d) {
		onePctNuclei = d;
	}
	
	public double getZeroPctNuclei() {
		return zeroPctNuclei;
	}
	public void setZeroPctNuclei(double value) {
		zeroPctNuclei = value;
	}
	
	public double getAvgPosIntensity() {
		return avgPosIntensity;
	}
	public void setAvgPosIntensity(double value) {
		avgPosIntensity = value;
	}
	
	public double getAvgNegIntensity() {
		return avgNegIntensity;
	}
	public void setAvgNegIntensity(double value) {
		avgNegIntensity = value;
	}
	
	public int getThreeNuclei() {
		return threeNuclei;
	}
	public void setThreeNuclei(int value) {
		threeNuclei = value;
	}
	
	public int getTwoNuclei() {
		return twoNuclei;
	}
	public void setTwoNuclei(int value) {
		twoNuclei = value;
	}
	
	public int getOneNuclei() {
		return oneNuclei;
	}
	public void setOneNuclei(int value) {
		oneNuclei = value;
	}
	
	public int getZeroNuclei() {
		return zeroNuclei;
	}
	public void setZeroNuclei(int value) {
		zeroNuclei = value;
	}
	
	public int getTotalNuclei() {
		return totalNuclei;
	}
	public void setTotalNuclei(int value) {
		totalNuclei = value;
	}
	
	public double getAvgRGBIntensity() {
		return avgRGBIntensity;
	}
	public void setAvgRGBIntensity(double value) {
		avgRGBIntensity = value;
	}
	
	public double getAvgSizePix() {
		return avgSizePix;
	}
	public void setAvgSizePix(double value) {
		avgSizePix = value;
	}
	
	public double getAvgSizeUm() {
		return avgSizeUm;
	}
	public void setAvgSizeUm(double value) {
		avgSizeUm = value;
	}
	
	public int getAreaAnalysisPix() {
		return areaAnalysisPix;
	}
	public void setAreaAnalysisPix(int value) {
		areaAnalysisPix = value;
	}
	
	public double getAreaAnalysisMm() {
		return areaAnalysisMm;
	}
	public void setAreaAnalysisMm(double value) {
		areaAnalysisMm = value;
	}
	
	public double getPctPosNuclei() {
		return pctPosNuclei;
	}
	public void setPctPosNuclei(double value) {
		pctPosNuclei = value;
	}
	
	public int getIntensityScore() {
		return intensityScore;
	}
	public void setIntensityScore(int value) {
		intensityScore = value;
	}
	
	public double getThreePctNuclei() {
		return threePctNuclei;
	}
	public void setThreePctNuclei(double value) {
		threePctNuclei = value;
	}
	
	public double getTwoPctNuclei() {
		return twoPctNuclei;
	}
	public void setTwoPctNuclei(double value) {
		twoPctNuclei = value;
	}
	
	public double getDensity() {
		return density;
	}
	public void setDensity() {
		density = (threeNuclei + twoNuclei + oneNuclei) / areaAnalysisMm;
	}
	
	public double getPercent() {
		return percent;
	}
	public void setPercent() {
		percent = onePctNuclei + twoPctNuclei + threePctNuclei;
	}
	
	public double getHScore() {
		return hScore;
	}
	public void setHScore() {
		hScore = onePctNuclei + twoPctNuclei * 2 + threePctNuclei * 3;
	}
}