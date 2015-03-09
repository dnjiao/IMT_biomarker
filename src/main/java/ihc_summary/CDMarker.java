package ihc_summary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class CDMarker implements BioMarker {
	String ID;
	int mrn;
	String accession;
	int rows;
	List<CDData> cdRows;
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
	
	public String getAccession() {
		return accession;
	}
	public void setAccession(String value) {
		accession = value;
	}
	
	public void readXls(String path, String filename) {
		cdRows = new ArrayList<CDData>();
		FileInputStream fis = null;
		try {
			File file = new File(path, filename);
			fis = new FileInputStream(file);
			accession = accessionFromFilename(filename);
			if (accession == null) {
				System.out.println("Invalid filename" + filename);
				System.exit(0);
			}
			// create a workbook from input excel file
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			// get the first sheet
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			Row row;
			Cell cell;
			
			// iterate on the rows
			Iterator<Row> rowIter = sheet.rowIterator();
			
			int rowCount = 0;
			int cellCount = 0;
			
			while (rowIter.hasNext()) {
				row = rowIter.next();
				if (rowCount > 0) {
					CDData cdData = new CDData();
					Iterator<Cell> cellIter = row.cellIterator();
					while (cellIter.hasNext()) {
						cell = cellIter.next();
						if (cellCount == 0) 
							cdData.setRegion(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 1)
							cdData.setLength(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 2)
							cdData.setAreaUm(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 4)
							cdData.setOnePctNuclei(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 5)
							cdData.setZeroPctNuclei(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 6)
							cdData.setAvgPosIntensity(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 7)
							cdData.setAvgNegIntensity(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 8)
							cdData.setThreeNuclei(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 9)
							cdData.setTwoNuclei(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 10)
							cdData.setOneNuclei(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 11)
							cdData.setZeroNuclei(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 12)
							cdData.setTotalNuclei(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 13)
							cdData.setAvgRGBIntensity(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 14)
							cdData.setAvgSizePix(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 15)
							cdData.setAvgSizeUm(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 16)
							cdData.setAreaAnalysisPix(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 17)
							cdData.setAreaAnalysisMm(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 18)
							cdData.setPctPosNuclei(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 19)
							cdData.setIntensityScore(Integer.parseInt(cell.getStringCellValue()));
						if (cellCount == 20)
							cdData.setThreePctNuclei(Double.parseDouble(cell.getStringCellValue()));
						if (cellCount == 21) {
							cdData.setTwoPctNuclei(Double.parseDouble(cell.getStringCellValue()));
							cdData.setDensity();
							cdData.setPercent();
							cdData.setHScore();
						}
						cellCount ++;
					}
					cdRows.add(cdData);
					cellCount = 0;
				}
				rowCount ++;	
			}
			rows = rowCount - 1;
			workbook.close(); 
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
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

		for (CDData element : cdRows) {
			if (arrayIndex < ctRows) {
				densityCTSum += element.getDensity();
				percentCTSum += element.getPercent();
				hscoreCTSum += element.getHScore();
				arrayIndex ++;
			}
			else if (arrayIndex < imRows + ctRows) {
				densityIMSum += element.getDensity();
				percentIMSum += element.getPercent();
				hscoreIMSum += element.getHScore();
				arrayIndex ++;
			}
			else if (arrayIndex < imRows + ctRows + normRows) {
				densityNormSum += element.getDensity();
				percentNormSum += element.getPercent();
				hscoreNormSum += element.getHScore();
				arrayIndex ++;				
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

	// read tab delimited text file
	public void readTxt(String path, String filename) {
		cdRows = new ArrayList<CDData>();
		File file = new File(path, filename);
		try {
			accession = accessionFromFilename(filename);
			if (accession == null) {
				System.out.println("Invalid filename" + filename);
				System.exit(0);
			}
			
			int rowCount = 0;
			// Create a buffered reader to read the file
			BufferedReader bReader = new BufferedReader(new FileReader(file));
			
			String line;
			
			// Looping the read block until all lines read.
			while ((line = bReader.readLine()) != null) {
				if (rowCount > 0) {
		            String datavalue[] = line.split("\t");
		            CDData cdData = new CDData();
		            cdData.setRegion(Integer.parseInt(datavalue[0]));
					cdData.setLength(Integer.parseInt(datavalue[1]));
					cdData.setAreaUm(Integer.parseInt(datavalue[2]));
					cdData.setOnePctNuclei(Double.parseDouble(datavalue[4]));
					cdData.setZeroPctNuclei(Double.parseDouble(datavalue[5]));
					cdData.setAvgPosIntensity(Double.parseDouble(datavalue[6]));
					cdData.setAvgNegIntensity(Double.parseDouble(datavalue[7]));
					cdData.setThreeNuclei(Integer.parseInt(datavalue[8]));
					cdData.setTwoNuclei(Integer.parseInt(datavalue[9]));
					cdData.setOneNuclei(Integer.parseInt(datavalue[10]));
					cdData.setZeroNuclei(Integer.parseInt(datavalue[11]));
					cdData.setTotalNuclei(Integer.parseInt(datavalue[12]));
					cdData.setAvgRGBIntensity(Double.parseDouble(datavalue[13]));
					cdData.setAvgSizePix(Double.parseDouble(datavalue[14]));
					cdData.setAvgSizeUm(Double.parseDouble(datavalue[15]));
					cdData.setAreaAnalysisPix(Integer.parseInt(datavalue[16]));
					cdData.setAreaAnalysisMm(Double.parseDouble(datavalue[17]));
					cdData.setPctPosNuclei(Double.parseDouble(datavalue[18]));
					cdData.setIntensityScore(Integer.parseInt(datavalue[19]));
					cdData.setThreePctNuclei(Double.parseDouble(datavalue[20]));
					cdData.setTwoPctNuclei(Double.parseDouble(datavalue[21]));
					cdData.setDensity();
					cdData.setPercent();
					cdData.setHScore();
					cdRows.add(cdData);
				}
				rowCount ++;
	        }
	        bReader.close();
	        rows = rowCount - 1;
	        System.out.println(rows);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
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
			for (CDData element : cdRows) {
				densityCTSum += element.getDensity();
				percentCTSum += element.getPercent();
				hscoreCTSum += element.getHScore();
				arrayIndex ++;
				System.out.println(densityCTSum + " " + percentCTSum + " " + hscoreCTSum);
			}
		}
		else {  // more than 5 rows; at IM + CT

			for (CDData element : cdRows) {
				if (arrayIndex < imRows) {
					densityIMSum += element.getDensity();
					percentIMSum += element.getPercent();
					hscoreIMSum += element.getHScore();
					arrayIndex ++;
					System.out.println(densityIMSum + " " + percentIMSum + " " + hscoreIMSum);
				}
				else if (arrayIndex < imRows + ctRows) {
					densityCTSum += element.getDensity();
					percentCTSum += element.getPercent();
					hscoreCTSum += element.getHScore();
					System.out.println(densityCTSum + " " + percentCTSum + " " + hscoreCTSum);
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
	public String accessionFromFilename(String name) {
		// extract accession # from filename
		String[] split_str = name.split(" ");
		String str = split_str[1];
		int pos = str.lastIndexOf(".");
		String accNum = str.substring(0, pos);
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