package ihc_summary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PDLMarker implements BioMarker {
	String ID;
	int mrn;
	String tissueAcc;
	String outsideAcc;
	String protocolAcc;
	int rows;
	List<PDLData> pdlRows;
	int imRows = 0;
	int ctRows = 0;
	int normRows = 0;
	double densityIM = 0.0;
	double percentIM = 0.0;
	double hscoreIM = 0.0;
	double densityCT = 0.0;
	double percentCT = 0.0;
	double hscoreCT = 0.0;
	double densityNorm = 0.0;
	double percentNorm = 0.0;
	double hscoreNorm = 0.0;
	
	public String getID() {
		return ID;
	}
	public void setID(String value) {
		ID = value;
	}
	
	public int getMrn() {
		return mrn;
	}
	public void setMrn(int value) {
		mrn = value;
	}
	

	
	public String getTissueAcc() {
		return tissueAcc;
	}
	public void setTissueAcc(String tissueAcc) {
		this.tissueAcc = tissueAcc;
	}
	public String getOutsideAcc() {
		return outsideAcc;
	}
	public void setOutsideAcc(String outsideAcc) {
		this.outsideAcc = outsideAcc;
	}
	public String getProtocolAcc() {
		return protocolAcc;
	}
	public void setProtocolAcc(String protocolAcc) {
		this.protocolAcc = protocolAcc;
	}
	// read tab delimited text file
	public void readTxt(String path, String filename) {
		pdlRows = new ArrayList<PDLData>();
		File file = new File(path, filename);
		try {
			tissueAcc = accessionFromFilename(filename);
			if (tissueAcc == null) {
				System.out.println("Invalid filename" + filename);
				System.exit(0);
			}
			
			int rowCount = 0;
			// Create a buffered reader to read the file
			BufferedReader bReader = new BufferedReader(new FileReader(file));
			
			String line;
			
			// Looping the read block until all lines read.
			while ((line = bReader.readLine()) != null) {
				String datavalue[] = line.split("\t");
				if (datavalue.length == 17 && !datavalue[0].equals("Region")) {	            
		            PDLData pdlData = new PDLData();
		            pdlData.setRegion(Integer.parseInt(datavalue[0]));
					pdlData.setLength(Double.parseDouble(datavalue[1]));
					double area = Double.parseDouble(datavalue[2]);
					pdlData.setAreaUm(area);
					
					// converting area from UM^2 to MM ^2
					pdlData.setAreaMm(area / 1000000);
	
					pdlData.setZeroPctCells(Double.parseDouble(datavalue[4]));
					pdlData.setPctComplete(Double.parseDouble(datavalue[5]));
					pdlData.setMemIntensity(Double.parseDouble(datavalue[6]));
					pdlData.setThreeCells(Integer.parseInt(datavalue[7]));
					pdlData.setTwoCells(Integer.parseInt(datavalue[8]));
					pdlData.setOneCells(Integer.parseInt(datavalue[9]));
					pdlData.setZeroCells(Integer.parseInt(datavalue[10]));
					pdlData.setTotalCells(Integer.parseInt(datavalue[11]));
					pdlData.setCompleteCells(Integer.parseInt(datavalue[12]));
					pdlData.setHer2Score(Integer.parseInt(datavalue[13]));
					pdlData.setThreePctCells(Double.parseDouble(datavalue[14]));
					pdlData.setTwoPctCells(Double.parseDouble(datavalue[15]));
					pdlData.setOnePctCells(Double.parseDouble(datavalue[16]));
					pdlData.setDensity();
					pdlData.setPercent();
					pdlData.setHScore();
					pdlRows.add(pdlData);
					rowCount ++;
				}
				
	        }
	        bReader.close();
	        rows = rowCount;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
			
		// get the number of rows for three types of regions: IM, CT, normal
		if (rows == 5) {
			imRows = 0;
			ctRows = 5;
			normRows = 0;
		}
		else if (rows == 10) {
			imRows = 5;
			ctRows = 5;
			normRows = 0;
		}
		else if (rows == 15) {
			imRows = 5;
			ctRows = 5;
			normRows = 5;
		}
		else {
			System.out.println("Error: incorrect number of rows in " + filename);
			System.exit(0);
		}
		
		int arrayIndex = 0;
		double densityIMSum = 0.0;
		double percentIMSum = 0.0;
		double hscoreIMSum = 0.0;
		double densityCTSum = 0.0;
		double percentCTSum = 0.0;
		double hscoreCTSum = 0.0;
		double densityNormSum = 0.0;
		double percentNormSum = 0.0;
		double hscoreNormSum = 0.0;

		if (imRows == 0) {  // only 5 rows (all CT)
			for (PDLData element : pdlRows) {
				densityCTSum += element.getDensity();
				percentCTSum += element.getPercent();
				hscoreCTSum += element.getHScore();
				arrayIndex ++;
			}
		}
		else {  // more than 5 rows; at IM + CT

			for (PDLData element : pdlRows) {
				if (arrayIndex < imRows) {
					densityIMSum += element.getDensity();
					percentIMSum += element.getPercent();
					hscoreIMSum += element.getHScore();
					arrayIndex ++;
				}
				else if (arrayIndex < imRows + ctRows) {
					densityCTSum += element.getDensity();
					percentCTSum += element.getPercent();
					hscoreCTSum += element.getHScore();
					arrayIndex ++;
				}
				else if (arrayIndex < imRows + ctRows + normRows) {
					densityNormSum += element.getDensity();
					percentNormSum += element.getPercent();
					hscoreNormSum += element.getHScore();
					arrayIndex ++;				
				}
			}
		}
		if (imRows != 0) {
			densityIM = densityIMSum / imRows;
			percentIM = percentIMSum / imRows;
			hscoreIM = hscoreIMSum / imRows;
		}
		if (ctRows != 0) {
			densityCT = densityCTSum / ctRows;
			percentCT = percentCTSum / ctRows;
			hscoreCT = hscoreCTSum / ctRows;
		}
		if (normRows != 0) {
			densityNorm = densityNormSum / normRows;
			percentNorm = percentNormSum / normRows;
			hscoreNorm = hscoreNormSum / normRows;
		}
		
	}
	
	public String accessionFromFilename(String str) {
		// extract accession # from filename
		int begin = 0;
		int end = str.lastIndexOf(".");
		if (str.contains(" ")) {
			begin = str.lastIndexOf(" ") + 1;
		}
		String accNum = str.substring(begin, end);
		char c = accNum.charAt(0);
		// if the first character is a letter type
		// return the string as accession number
		if (Character.isLetter(c)) {
			return accNum;
		}
		else {
			return null;
		}
	}
	
	public int getImRows() {
		return imRows;
	}
	
	public int getCtRows() {
		return ctRows;
	}
	
	public int getNormRows() {
		return normRows;
	}
	
	public double getDensityIM() {
		return densityIM;
	}
	
	public double getDensityCT() {
		return densityCT;
	}
	
	public double getDensityNorm() {
		return densityNorm;
	}
	
	public double getPercentIM() {
		return percentIM;
	}
	
	public double getPercentCT() {
		return percentCT;
	}
	
	public double getPercentNorm() {
		return percentNorm;
	}
	
	public double getHscoreIM() {
		return hscoreIM;
	}
	
	public double getHscoreCT() {
		return hscoreCT;
	}
	
	public double getHscoreNorm() {
		return hscoreNorm;
	}

	
}