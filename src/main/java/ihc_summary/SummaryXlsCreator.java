package ihc_summary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/** class creates summary excel sheet from multiple data xls files
*/

public class SummaryXlsCreator {
	public static void main(String[] args) {
		String dirStr = "";
		Scanner scan = new Scanner(System.in);
		List<BioMarker> markers = null;
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet dataSheet = workbook.createSheet("Data");
		
		String cwd = System.getProperty("user.dir");  // get current working directory
		System.out.println("Path to biomarker directory:");
		System.out.print(cwd + "[Y/N]:");
		String pathCorrect;
		pathCorrect = scan.nextLine().trim().toLowerCase();
		 
		if (pathCorrect.equals("y")) {
			dirStr = cwd;
		}
		else if (pathCorrect.equals("n")) {  //user needs to provide the working directory with full path
			System.out.println("Please enter the full path to biomarker data directory: ");
			dirStr = scan.nextLine();
		}
		else {
			System.out.println("ERROR: Invalid input. Try again.");
			System.exit(0);
		}
		File dir = new File(dirStr);
		if (!dir.exists()) {  // Path does not exist
			System.out.println("ERROR: " + dirStr + " does not exist.");
			System.exit(0);
		}
		if (!dir.isDirectory()) {   // Path not a directory
			System.out.println("ERROR: " + dirStr + " is not a directory.");
			System.exit(0);
		}
		
		System.out.println("\nPlease choose the biomarker type: [1/2]");
		System.out.println("1. CD (CD3,CD4,CD8...)");
		System.out.println("2. PDL (CD68,PDL1...)");
		int typeChoice = scan.nextInt();

		if (typeChoice == 1) {
			markers = writeCDMarker(dataSheet, dir);
		}
		else if (typeChoice == 2) {
			markers = writePDLMarker(dataSheet, dir);
		}
		else {
			System.out.println("ERROR: Invalid selection.");
			System.exit(0);
		}
			
		// create separate summary sheets for density, percent, and h-score 
		HSSFSheet densitySheet = workbook.createSheet("Summary_Density");
		HSSFSheet percentSheet = workbook.createSheet("Summary_Percent");
		HSSFSheet hscoreSheet = workbook.createSheet("Summary_H-score");
		
		HSSFCellStyle fontStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		fontStyle.setFont(font);

		int rownum = 0;
		// creating the first row in each sheet
		Row rowDensity = densitySheet.createRow(rownum);
		Row rowPercent = percentSheet.createRow(rownum);
		Row rowHscore = hscoreSheet.createRow(rownum);
		rownum ++;
		
		// first row in density sheet
		Cell cellDensity = rowDensity.createCell(0);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("ID");
		cellDensity = rowDensity.createCell(1);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("MRN");
		cellDensity = rowDensity.createCell(2);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("Tissue Acc #");
		cellDensity = rowDensity.createCell(3);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("Density IM");
		cellDensity = rowDensity.createCell(4);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("Density CT");
		cellDensity = rowDensity.createCell(5);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("Density N");
		
		// first row in percent sheet
		Cell cellPercent = rowPercent.createCell(0);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("ID");
		cellPercent = rowPercent.createCell(1);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("MRN");
		cellPercent = rowPercent.createCell(2);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("Tissue Acc #");
		cellPercent = rowPercent.createCell(3);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("Percent IM");
		cellPercent = rowPercent.createCell(4);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("Percent CT");
		cellPercent = rowPercent.createCell(5);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("Percent N");
		
		// first row in percent sheet
		Cell cellHscore = rowHscore.createCell(0);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("ID");
		cellHscore = rowHscore.createCell(1);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("MRN");
		cellHscore = rowHscore.createCell(2);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("Tissue Acc #");
		cellHscore = rowHscore.createCell(3);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("Hscore IM");
		cellHscore = rowHscore.createCell(4);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("Hscore CT");
		cellHscore = rowHscore.createCell(5);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("Hscore N");
		

		double densityIMTotal = 0.0;
		double percentIMTotal = 0.0;
		double hscoreIMTotal = 0.0;
		double densityCTTotal = 0.0;
		double percentCTTotal = 0.0;
		double hscoreCTTotal = 0.0;
		double densityNormTotal = 0.0;
		double percentNormTotal = 0.0;
		double hscoreNormTotal = 0.0;
		int imNum = 0;
		int ctNum = 0;
		int normNum = 0;
		

		for (BioMarker m : markers) {
			BioMarker marker = null;
			if (m instanceof CDMarker) {
		        marker = (CDMarker)m;
			}
		    else if (m instanceof PDLMarker) {
		        marker = (PDLMarker)m;
		    }
			rowDensity = densitySheet.createRow(rownum);
			cellDensity = rowDensity.createCell(0);
			cellDensity.setCellValue(marker.getID());
			cellDensity = rowDensity.createCell(2);
			cellDensity.setCellValue(marker.getTissueAcc());
			
			
			rowPercent = percentSheet.createRow(rownum);
			cellPercent = rowPercent.createCell(0);
			cellPercent.setCellValue(marker.getID());
			cellPercent = rowPercent.createCell(2);
			cellPercent.setCellValue(marker.getTissueAcc());
			
			rowHscore = hscoreSheet.createRow(rownum);
			cellHscore = rowHscore.createCell(0);
			cellHscore.setCellValue(marker.getID());
			cellHscore = rowHscore.createCell(2);
			cellHscore.setCellValue(marker.getTissueAcc());
			rownum ++;

			if (marker.getImRows() != 0) {
				cellDensity = rowDensity.createCell(3);
				cellDensity.setCellValue(marker.getDensityIM());
				cellPercent = rowPercent.createCell(3);
				cellPercent.setCellValue(marker.getPercentIM());
				cellHscore = rowHscore.createCell(3);
				cellHscore.setCellValue(marker.getHscoreIM());
				densityIMTotal += marker.getDensityIM();
				percentIMTotal += marker.getPercentIM();
				hscoreIMTotal += marker.getHscoreIM();
				imNum ++;
			}
			if (marker.getCtRows() != 0) {
				cellDensity = rowDensity.createCell(4);
				cellDensity.setCellValue(marker.getDensityCT());
				cellPercent = rowPercent.createCell(4);
				cellPercent.setCellValue(marker.getPercentCT());
				cellHscore = rowHscore.createCell(4);
				cellHscore.setCellValue(marker.getHscoreCT());
				densityCTTotal += marker.getDensityCT();
				percentCTTotal += marker.getPercentCT();
				hscoreCTTotal += marker.getHscoreCT();
				ctNum ++;
			}
			if (marker.getNormRows() != 0) {
				cellDensity = rowDensity.createCell(5);
				cellDensity.setCellValue(marker.getDensityNorm());
				cellPercent = rowPercent.createCell(5);
				cellPercent.setCellValue(marker.getPercentNorm());
				cellHscore = rowHscore.createCell(5);
				cellHscore.setCellValue(marker.getHscoreNorm());
				densityNormTotal += marker.getDensityNorm();
				percentNormTotal += marker.getPercentNorm();
				hscoreNormTotal += marker.getHscoreNorm();
				normNum ++;
			}
		}
		
		// creating average row for each sheet
		rowDensity = densitySheet.createRow(rownum);
		cellDensity = rowDensity.createCell(0);
		cellDensity.setCellStyle(fontStyle);
		cellDensity.setCellValue("Average");
		rowPercent = percentSheet.createRow(rownum);
		cellPercent = rowPercent.createCell(0);
		cellPercent.setCellStyle(fontStyle);
		cellPercent.setCellValue("Average");
		rowHscore = hscoreSheet.createRow(rownum);
		cellHscore = rowHscore.createCell(0);
		cellHscore.setCellStyle(fontStyle);
		cellHscore.setCellValue("Average");
		if (imNum != 0) {
			cellDensity = rowDensity.createCell(3);
			cellDensity.setCellValue(densityIMTotal/imNum);
			cellPercent = rowPercent.createCell(3);
			cellPercent.setCellValue(percentIMTotal/imNum);
			cellHscore = rowHscore.createCell(3);
			cellHscore.setCellValue(hscoreIMTotal/imNum);
		}
		
		if (ctNum != 0) {
			cellDensity = rowDensity.createCell(4);
			cellDensity.setCellValue(densityCTTotal/ctNum);
			cellPercent = rowPercent.createCell(4);
			cellPercent.setCellValue(percentCTTotal/ctNum);
			cellHscore = rowHscore.createCell(4);
			cellHscore.setCellValue(hscoreCTTotal/ctNum);
		}
		
		if (normNum != 0) {
			cellDensity = rowDensity.createCell(5);
			cellDensity.setCellValue(densityNormTotal/normNum);
			cellPercent = rowPercent.createCell(5);
			cellPercent.setCellValue(percentNormTotal/normNum);
			cellHscore = rowHscore.createCell(5);
			cellHscore.setCellValue(hscoreNormTotal/normNum);
		}
		
		//write to excel file
        try {
        	File outfile = new File(dir, dir.getName() + "_summary.xls");
            FileOutputStream out = new FileOutputStream(outfile);
            workbook.write(out);
            out.close();
            System.out.println("\nExcel written successfully: " + outfile.getAbsolutePath());
             
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        scan.close();
	
	}
	
	public static List<BioMarker> writeCDMarker(HSSFSheet sheet, File dir) {
		List<BioMarker> cdMarkers = new ArrayList<BioMarker>();
		HSSFCellStyle fontStyle = sheet.getWorkbook().createCellStyle();
		HSSFFont font = sheet.getWorkbook().createFont();
		font.setBold(true);
		fontStyle.setFont(font);
		int rownum = 0;
		int cellnum = 0;
		Row row = sheet.createRow(rownum++);
		Cell cell = row.createCell(cellnum++);
		// write first row of sheet "Data"
		cell.setCellValue("ID");
		cell.setCellStyle(fontStyle);
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("MRN");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Tissue Acc #");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Region");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Region");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Length (um)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Area (um2)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Text");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(1+) Percent Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(0+) Percent Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Average Positive Intensity");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Average Negative Intensity");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(3+) Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(2+) Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(1+) Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(0+) Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Total Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Average Nuclear RGB Intensity");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Average Nuclear Size (Pixels)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Average Nuclear Size (um^2)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Area of Analysis (Pixels)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Area of Analysis (mm^2)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Percent Positive Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Intensity Score");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(3+) Percent Nuclei");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(2+) Percent Nuclei");
		cell = row.createCell(cellnum++);	
		cell = row.createCell(cellnum++);
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Density");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Percent");
		cell.setCellStyle(fontStyle);
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("H-score");
		
		
		int fileindex = 1;
		System.out.println("\nReading files:");
		// iterate all files in directory and only read ".xls" files
		for (File file : dir.listFiles()) {
			String fileName = file.getName();
//			if (fileName.toLowerCase().startsWith("cd") && fileName.endsWith(".xls") ) {
			if (fileName.endsWith(".txt")) {
				System.out.println(fileName);
				CDMarker marker = new CDMarker();
				marker.setID(Integer.toString(fileindex));
//				marker.readXls(dir.toString(), fileName);
				marker.readTxt(dir.toString(), fileName);
				rownum = writeCDData(sheet, rownum, fileindex, marker);
				cdMarkers.add(marker);
				fileindex ++;
			}
		}
		return cdMarkers;
	}
	
	// consolidate data from raw files to a single spreadsheet
	public static int writeCDData(HSSFSheet sheet, int rownum, int index, CDMarker marker) {
		int counter = 0;
		for (CDData data : marker.cdRows) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			Cell cell = row.createCell(cellnum);
			// first line of each type (IM, CT, norm)
			if (counter % 5 == 0) {
				if (counter == 0) {
					cell.setCellValue(marker.getID());
					cellnum = 2;
					cell = row.createCell(cellnum++);
					cell.setCellValue(marker.getTissueAcc());
					
					cell = row.createCell(cellnum++);
					if (marker.cdRows.size() == 5) 
						cell.setCellValue("CT");
					else
						cell.setCellValue("IM");
				} 
				else if (counter == 5) {
					cellnum = 3;
					cell = row.createCell(cellnum++);
					cell.setCellValue("CT");
				}
				else {
					cellnum = 3;
					cell = row.createCell(cellnum++);
					cell.setCellValue("Normal");
				}	
			}
			else {
				cellnum = 4;
			}
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getRegion());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getLength());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAreaUm());
			cell = row.createCell(cellnum++);
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getOnePctNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getZeroPctNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAvgPosIntensity());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAvgNegIntensity());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getThreeNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getTwoNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getOneNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getZeroNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getTotalNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAvgRGBIntensity());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAvgSizePix());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAvgSizeUm());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAreaAnalysisPix());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAreaAnalysisMm());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getPctPosNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getIntensityScore());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getThreePctNuclei());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getTwoPctNuclei());
			cellnum = cellnum + 2;
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getDensity());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getPercent());
			cell = row.createCell(cellnum);
			cell.setCellValue(data.getHScore());
			counter ++;
		}	
		rownum ++;
		return rownum;
	}

	public static List<BioMarker> writePDLMarker(HSSFSheet sheet, File dir) {
		List<BioMarker> pdlMarkers = new ArrayList<BioMarker>();
		HSSFCellStyle fontStyle = sheet.getWorkbook().createCellStyle();
		HSSFFont font = sheet.getWorkbook().createFont();
		font.setBold(true);
		fontStyle.setFont(font);
		int rownum = 0;
		int cellnum = 0;
		Row row = sheet.createRow(rownum++);
		Cell cell = row.createCell(cellnum++);
		// write first row of sheet "Data"
		cell.setCellValue("ID");
		cell.setCellStyle(fontStyle);
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("mrn");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Accession #");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Region");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Region");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Length (um)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Area (um2)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Area (mm2)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(0+) Percent Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Percent Complete");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Membrane Intensity");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(3+) Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(2+) Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(1+) Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(0+) Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Cells (Total)");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Complete Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Her2 Score");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(3+) Percent Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(2+) Percent Cells");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("(1+) Percent Cells");

		cell = row.createCell(cellnum++);
		cell = row.createCell(cellnum++);
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Density");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("Percent");
		cell = row.createCell(cellnum++);
		cell.setCellStyle(fontStyle);
		cell.setCellValue("H-score");
		
		
		int fileindex = 1;
		System.out.println("\nReading files: ");
		// iterate all files in directory and only read ".txt" files
		for (File file : dir.listFiles()) {
			String fileName = file.getName();
			if (fileName.endsWith(".txt")) {
				System.out.println(fileName);
				PDLMarker marker = new PDLMarker();
				marker.setID(Integer.toString(fileindex));
				marker.readTxt(dir.toString(), fileName);
				rownum = writePDLData(sheet, rownum, fileindex, marker);
				pdlMarkers.add(marker);
				fileindex ++;
			}
		}
		return pdlMarkers;
	}
	
	
	// consolidate data from raw files to a single spreadsheet
	public static int writePDLData(HSSFSheet sheet, int rownum, int index, PDLMarker marker) {
		int counter = 0;
		for (PDLData data : marker.pdlRows) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			Cell cell = row.createCell(cellnum);
			// first line of each type (IM, CT, norm)
			if (counter % 5 == 0) {
				if (counter == 0) {
					cell.setCellValue(marker.getID());
					cellnum = 2;
					cell = row.createCell(cellnum++);
					cell.setCellValue(marker.getTissueAcc());
					cell = row.createCell(cellnum++);
					cell.setCellValue("IM");
				} 
				else if (counter == 5) {
					cellnum = 3;
					cell = row.createCell(cellnum++);
					cell.setCellValue("CT");
				}
				else {
					cellnum = 3;
					cell = row.createCell(cellnum++);
					cell.setCellValue("Normal");
				}	
			}
			else {
				cellnum = 4;
			}
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getRegion());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getLength());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAreaUm());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getAreaMm());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getZeroPctCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getPctComplete());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getMemIntensity());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getThreeCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getTwoCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getOneCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getZeroCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getTotalCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getCompleteCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getHer2Score());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getThreePctCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getTwoPctCells());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getOnePctCells());

			cellnum = cellnum + 2;
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getDensity());
			cell = row.createCell(cellnum++);
			cell.setCellValue(data.getPercent());
			cell = row.createCell(cellnum);
			cell.setCellValue(data.getHScore());
			counter ++;
		}	
		rownum ++;
		return rownum;
	}
	
}