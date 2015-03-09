package ihc_summary;

public class PDLData {
	int region;
	int length;
	int areaUm;
	double areaMm;
	String text;
	double zeroPctCells;
	double pctComplete;
	double memIntensity;
	int threeCells;
	int twoCells;
	int oneCells;
	int zeroCells;
	int totalCells;
	int completeCells;
	double her2Score;
	double threePctCells;
	double twoPctCells;
	double onePctCells;
	double density;
	double percent;
	double hScore;
	
	public int getRegion() {
		return region;
	}
	public void setRegion(int value) {
		region = value;
	}
	
	public int getLength() {
		return length;
	}
	public void setLength(int value) {
		length = value;
	}
	
	public int getAreaUm() {
		return areaUm;
	}
	public void setAreaUm(int value) {
		areaUm = value;
	}
	
	public double getAreaMm() {
		return areaMm;
	}
	public void setAreaMm(double value) {
		areaMm = value;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String value) {
		text = value;
	}
	
	public double getZeroPctCells() {
		return zeroPctCells;
	}
	public void setZeroPctCells(double value) {
		zeroPctCells = value;
	}
	
	public double getPctComplete() {
		return pctComplete;
	}
	public void setPctComplete(double value) {
		pctComplete = value;
	}
	
	public double getMemIntensity() {
		return memIntensity;
	}
	public void setMemIntensity(double value) {
		memIntensity = value;
	}
	
	public int getThreeCells() {
		return threeCells;
	}
	public void setThreeCells(int value) {
		threeCells = value;
	}
	
	public int getTwoCells() {
		return twoCells;
	}
	public void setTwoCells(int value) {
		twoCells = value;
	}
	
	public int getOneCells() {
		return oneCells;
	}
	public void setOneCells(int value) {
		oneCells = value;
	}
	
	public int getZeroCells() {
		return zeroCells;
	}
	public void setZeroCells(int value) {
		zeroCells = value;
	}
	
	public int getTotalCells() {
		return totalCells;
	}
	public void setTotalCells(int value) {
		totalCells = value;
	}
	
	public int getCompleteCells() {
		return completeCells;
	}
	public void setCompleteCells(int value) {
		completeCells = value;
	}
	
	public double getHer2Score() {
		return her2Score;
	}
	public void setHer2Score(double value) {
		her2Score = value;
	}
	
	public double getThreePctCells() {
		return threePctCells;
	}
	public void setThreePctCells(double value) {
		threePctCells = value;
	}
	
	public double getTwoPctCells() {
		return twoPctCells;
	}
	public void setTwoPctCells(double value) {
		twoPctCells = value;
	}
	
	public double getOnePctCells() {
		return onePctCells;
	}
	public void setOnePctCells(double value) {
		onePctCells = value;
	}
	
	public double getDensity() {
		return density;
	}
	public void setDensity() {
		density = (twoCells + oneCells + zeroCells) / areaMm;
	}
	
	public double getPercent() {
		return percent;
	}
	public void setPercent() {
		percent = onePctCells + twoPctCells + threePctCells;
	}
	
	public double getHScore() {
		return hScore;
	}
	public void setHScore() {
		hScore = onePctCells + twoPctCells * 2 + threePctCells * 3;
	}
}

