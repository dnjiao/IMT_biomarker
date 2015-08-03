package ihc_summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class FinalSummary {
	public static void main(String[] args) {
		// create summary workbook and spreadsheets
		HSSFWorkbook workbook = new HSSFWorkbook();
		String dirStr = "";
		Scanner scan = new Scanner(System.in); 
		String cwd = System.getProperty("user.dir");  // get current working directory
		System.out.println("Path to the directory with data files:");
		System.out.print(cwd + "[Y/N]:");
		String pathCorrect;
		pathCorrect = scan.nextLine().trim().toLowerCase();
		 
		if (pathCorrect.equals("y")) {
			dirStr = cwd;
		}	
		else if (pathCorrect.equals("n")) {  //user needs to provide the working directory with full path
			System.out.println("Please enter the full path to data directory: ");
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
		
		// get summary files from all folders in the current working directory
		List<File> summaryFiles = listSummaryXls(dirStr, "summary.xls");
	
		HashMap<String, SummaryRow> markerMap = new HashMap<String, SummaryRow>();
		List<String> markerList = new ArrayList<String>();
		// Loop through all summary files
		for (File file : summaryFiles) {
			try {
				String markerName = file.getParentFile().getName();
				markerMap = readSummary(markerMap, file, markerName);
				markerList.add(markerName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeSummarySheets(workbook, markerMap, markerList);
		
		// get current timestamp, part of filename
		DateTimeFormatter format = DateTimeFormat.forPattern("MMddyyyyHHmmss");
		DateTime current = new DateTime();
	    String dtStr = format.print(current);
		
	    // Finally write to the file
		try {
            FileOutputStream out = new FileOutputStream(new File(dir, dir.getName() + "_Summary_" + dtStr + ".xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");   
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
		scan.close();
	}
	
	// List all files with the name fileName in directory dirName (one level down)
	public static List<File> listSummaryXls(String dirName, String fileName) {
		File dir = new File(dirName);
		List<File> foundFiles = new ArrayList<File>();
		File[] files = dir.listFiles();
		for (File file: files) {
			// looking for all subdirectories in the current folder
			if (file.isDirectory()) {
				String fileStr = file.getName() + "_" + fileName;
				File[] subFiles = file.listFiles();
				// looking for certain file in each subdirectories.
				for (File subfile : subFiles) {
					// if file found, add to the return list
					if (subfile.isFile() && subfile.getName().equals(fileStr)) {
						foundFiles.add(subfile);
					}
				}
			}
		}
		return foundFiles;
	}
	
	public static HashMap<String, SummaryRow> readSummary(HashMap<String, SummaryRow> mapper, File file, String marker) throws IOException {
		SummaryRow summary = new SummaryRow();
		double[] markerValues;
		String id;
		String accession = null;

		FileInputStream fis = null;
		fis = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(fis);
		// open sheets to get the accessions numbers and values
		HSSFSheet dSheet = workbook.getSheetAt(1);
		HSSFSheet pSheet = workbook.getSheetAt(2);
		HSSFSheet hSheet = workbook.getSheetAt(3);
		
		Row dRow, pRow, hRow;
		// iterate on the rows
		Iterator<Row> rowIter = dSheet.rowIterator();
		
		while (rowIter.hasNext()) {
			dRow = rowIter.next();
			if (dRow.getRowNum() > 0) {
				markerValues = new double[9];
				int cellIndex = 3; // starting cell in density/percent/hscore sheet with values.
				if (!dRow.getCell(0).getStringCellValue().equals("Average")) {
					pRow = pSheet.getRow(dRow.getRowNum());
					hRow = hSheet.getRow(dRow.getRowNum());
					id = dRow.getCell(0).getStringCellValue();
//					mrn = Integer.parseInt(dRow.getCell(1).getStringCellValue());
					accession = dRow.getCell(2).getStringCellValue();
					for (int i = 0; i < 9; i++) {
						//density values i=0,1,2
						if (i < 3) {
							if (dRow.getCell(cellIndex) != null) 
								markerValues[i] = dRow.getCell(cellIndex).getNumericCellValue();
							else
								markerValues[i] = -1;	
							
						}
						// percent values i=3,4,5
						else if (i < 6) {
							if (i == 3)
								cellIndex = 3;
							if (pRow.getCell(cellIndex) != null)
								markerValues[i] = pRow.getCell(cellIndex).getNumericCellValue();
							else
								markerValues[i] = -1;
						}
						// hscore values i=6,7,8
						else {
							if (i == 6)
								cellIndex = 3;
							if (hRow.getCell(cellIndex) != null)
								markerValues[i] = hRow.getCell(cellIndex).getNumericCellValue();
							else
								markerValues[i] = -1;
						}
						cellIndex ++;
					}

					
					if (!mapper.containsKey(accession)) {   // if accession does not exist in mapper, create new key/value
						summary.setID(id);
//						summary.setMrn(mrn);
						summary.setValueMap(marker, markerValues);
						
					}
					else {     // if accession exists then update it
						summary = mapper.get(accession);
						summary.getValueMap().put(marker, markerValues);
					}
					
					mapper.put(accession, summary);
					summary = new SummaryRow();

				}
			}
		}
		
		workbook.close();
		return mapper;
	}
	
	@SuppressWarnings("deprecation")
	public static HSSFWorkbook writeSummarySheets(HSSFWorkbook workbook, HashMap<String, SummaryRow> mapper, List<String> list) {
		HSSFSheet dSheet = workbook.createSheet("Density");
		HSSFSheet pSheet = workbook.createSheet("Percent");
		HSSFSheet hSheet = workbook.createSheet("H-score");
		
		HSSFCellStyle alignStyle = workbook.createCellStyle();
		HSSFCellStyle fontStyle = workbook.createCellStyle();
		alignStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		alignStyle.setFont(font);
		fontStyle.setFont(font);
		
		// create first row in all 3 sheets
		
		Row dRow = dSheet.createRow(0);
		Row pRow = pSheet.createRow(0);
		Row hRow = hSheet.createRow(0);
		
		// write first rows
		Cell dCell = dRow.createCell(0);
		Cell pCell = pRow.createCell(0);
		Cell hCell = hRow.createCell(0);
		dCell.setCellValue("ID");
		pCell.setCellValue("ID");
		hCell.setCellValue("ID");
		dCell.setCellStyle(fontStyle);
		pCell.setCellStyle(fontStyle);
		hCell.setCellStyle(fontStyle);
		
		dCell = dRow.createCell(1);
		pCell = pRow.createCell(1);
		hCell = hRow.createCell(1);
		dCell.setCellValue("MRN");
		pCell.setCellValue("MRN");
		hCell.setCellValue("MRN");
		dCell.setCellStyle(fontStyle);
		pCell.setCellStyle(fontStyle);
		hCell.setCellStyle(fontStyle);
		dCell = dRow.createCell(2);
		pCell = pRow.createCell(2);
		hCell = hRow.createCell(2);
		dCell.setCellValue("Tissue Acc #");
		pCell.setCellValue("Tissue Acc #");
		hCell.setCellValue("Tissue Acc #");
		dCell.setCellStyle(fontStyle);
		pCell.setCellStyle(fontStyle);
		hCell.setCellStyle(fontStyle);
		dCell = dRow.createCell(3);
		pCell = pRow.createCell(3);
		hCell = hRow.createCell(3);
		dCell.setCellValue("Outside Acc #");
		pCell.setCellValue("Outside Acc #");
		hCell.setCellValue("Outside Acc #");
		dCell.setCellStyle(fontStyle);
		pCell.setCellStyle(fontStyle);
		hCell.setCellStyle(fontStyle);
		dCell = dRow.createCell(4);
		pCell = pRow.createCell(4);
		hCell = hRow.createCell(4);
		dCell.setCellValue("Protocol Acc #");
		pCell.setCellValue("Protocol Acc #");
		hCell.setCellValue("Protocol Acc #");
		dCell.setCellStyle(fontStyle);
		pCell.setCellStyle(fontStyle);
		hCell.setCellStyle(fontStyle);
		int cellnum = 5;
		for (int i = 0; i < list.size(); i++) {
			dCell = dRow.createCell(cellnum);
			pCell = pRow.createCell(cellnum);
			hCell = hRow.createCell(cellnum);
			dCell.setCellStyle(alignStyle);
			pCell.setCellStyle(alignStyle);
			hCell.setCellStyle(alignStyle);
			
			dCell.setCellValue(list.get(i));
			pCell.setCellValue(list.get(i));
			hCell.setCellValue(list.get(i));
			dSheet.addMergedRegion(new CellRangeAddress(0, 0, cellnum, cellnum + 2));
			pSheet.addMergedRegion(new CellRangeAddress(0, 0, cellnum, cellnum + 2));
			hSheet.addMergedRegion(new CellRangeAddress(0, 0, cellnum, cellnum + 2));
			cellnum += 3;
		}
		//write second rows with the keywords "im" "ct" and "norm"
		dRow = dSheet.createRow(1);
		pRow = pSheet.createRow(1);
		hRow = hSheet.createRow(1);
		cellnum = 5;
		for (int i = 0; i < list.size(); i++) {
			dCell = dRow.createCell(cellnum);
			pCell = pRow.createCell(cellnum);
			hCell = hRow.createCell(cellnum);
			dCell.setCellValue("Density IM");
			pCell.setCellValue("Percent IM");
			hCell.setCellValue("H-Score IM");
			dCell = dRow.createCell(cellnum + 1);
			pCell = pRow.createCell(cellnum + 1);
			hCell = hRow.createCell(cellnum + 1);
			dCell.setCellValue("Density CT");
			pCell.setCellValue("Percent CT");
			hCell.setCellValue("H-Score CT");
			dCell = dRow.createCell(cellnum + 2);
			pCell = pRow.createCell(cellnum + 2);
			hCell = hRow.createCell(cellnum + 2);
			dCell.setCellValue("Density N");
			pCell.setCellValue("Percent N");
			hCell.setCellValue("H-Score N");
			cellnum += 3;
		}

		//loop through mapper
		int rownum = 2;
		Iterator<String> mapIter = mapper.keySet().iterator();
		while (mapIter.hasNext()) {
			dRow = dSheet.createRow(rownum);
			pRow = pSheet.createRow(rownum);
			hRow = hSheet.createRow(rownum);
			String key = mapIter.next();
			
			SummaryRow line = new SummaryRow();
			line = (SummaryRow) mapper.get(key);
			dCell = dRow.createCell(0);
			pCell = pRow.createCell(0);
			hCell = hRow.createCell(0);
			dCell.setCellValue(line.getID());
			pCell.setCellValue(line.getID());
			hCell.setCellValue(line.getID());
			dCell = dRow.createCell(1);
			pCell = pRow.createCell(1);
			hCell = hRow.createCell(1);
			dCell.setCellValue(line.getMrn());
			pCell.setCellValue(line.getMrn());
			hCell.setCellValue(line.getMrn());
			dCell = dRow.createCell(2);
			pCell = pRow.createCell(2);
			hCell = hRow.createCell(2);
			dCell.setCellValue(key);
			pCell.setCellValue(key);
			hCell.setCellValue(key);
			dCell = dRow.createCell(3);
			pCell = pRow.createCell(3);
			hCell = hRow.createCell(3);
			dCell.setCellValue(line.getOutAcc());
			pCell.setCellValue(line.getOutAcc());
			hCell.setCellValue(line.getOutAcc());
			dCell = dRow.createCell(4);
			pCell = pRow.createCell(4);
			hCell = hRow.createCell(4);
			dCell.setCellValue(line.getProAcc());
			pCell.setCellValue(line.getProAcc());
			hCell.setCellValue(line.getProAcc());
			
			//iterate over the markers for each sample
			cellnum = 5;
			LinkedHashMap<String, double[]> valuemap = new LinkedHashMap<String, double[]>();
			valuemap = line.getValueMap();
			Iterator<String> valueIter = valuemap.keySet().iterator();
			// format double to one decimal
			HSSFDataFormat format = workbook.createDataFormat();
			HSSFCellStyle doublestyle = workbook.createCellStyle();
			doublestyle.setDataFormat(format.getFormat("0.0"));
			while (valueIter.hasNext()) {
				String marker = valueIter.next();
				int index = list.indexOf(marker); 
				if (Double.compare(-1, valuemap.get(marker)[0]) != 0)
				{   // im values
					dCell = dRow.createCell(cellnum + index * 3);
					pCell = pRow.createCell(cellnum + index * 3);
					hCell = hRow.createCell(cellnum + index * 3);
					dCell.setCellStyle(doublestyle);
					pCell.setCellStyle(doublestyle);
					hCell.setCellStyle(doublestyle);
					dCell.setCellValue(valuemap.get(marker)[0]);			
					pCell.setCellValue(valuemap.get(marker)[3]);
					hCell.setCellValue(valuemap.get(marker)[6]);
				}
				if (Double.compare(-1, valuemap.get(marker)[1]) != 0)
				{   // ct values
					dCell = dRow.createCell(cellnum + index * 3 + 1);
					pCell = pRow.createCell(cellnum + index * 3 + 1);
					hCell = hRow.createCell(cellnum + index * 3 + 1);
					dCell.setCellStyle(doublestyle);
					pCell.setCellStyle(doublestyle);
					hCell.setCellStyle(doublestyle);
					dCell.setCellValue(valuemap.get(marker)[1]);
					pCell.setCellValue(valuemap.get(marker)[4]);
					hCell.setCellValue(valuemap.get(marker)[7]);
				}
				if (Double.compare(-1, valuemap.get(marker)[2]) != 0)
				{     // norm values
					dCell = dRow.createCell(cellnum + index * 3 + 2);
					pCell = pRow.createCell(cellnum + index * 3 + 2);
					hCell = hRow.createCell(cellnum + index * 3 + 2);
					dCell.setCellStyle(doublestyle);
					pCell.setCellStyle(doublestyle);
					hCell.setCellStyle(doublestyle);
					dCell.setCellValue(valuemap.get(marker)[2]);
					pCell.setCellValue(valuemap.get(marker)[5]);
					hCell.setCellValue(valuemap.get(marker)[8]);
				}
			}
			rownum ++;
		}
		
		return workbook;
		
		
	}
}