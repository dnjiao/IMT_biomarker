package ihc_summary;

public interface BioMarker {
	public String getID();
	public void setID(String value);	
	public int getMrn();
	public void setMrn(int value);	
	public String getTissueAcc();
	public void setTissueAcc(String tissueAcc);
	public String getOutsideAcc();
	public void setOutsideAcc(String outsideAcc);
	public String getProtocolAcc();
	public void setProtocolAcc(String protocolAcc);
	public void readTxt(String path, String filename);
	public String accessionFromFilename(String name);	
	public int getImRows();	
	public int getCtRows();	
	public int getNormRows();	
	public double getDensityIM();	
	public double getDensityCT();	
	public double getDensityNorm();	
	public double getPercentIM();	
	public double getPercentCT();	
	public double getPercentNorm();	
	public double getHscoreIM();	
	public double getHscoreCT();	
	public double getHscoreNorm();	
}