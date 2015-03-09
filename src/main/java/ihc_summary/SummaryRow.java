package ihc_summary;

import java.util.LinkedHashMap;

public class SummaryRow {
	int mrn;
	String ID;
	LinkedHashMap<String, double[]> valueMap;
	
	public void setMrn(int i) {
		mrn = i;
	}
	public int getMrn() {
		return mrn;
	}
	
	public void setID(String str) {
		ID = str;
	}
	public String getID() {
		return ID;
	}
	
	public void setValueMap(String str, double[] array) {
		valueMap = new LinkedHashMap<String, double[]>();
		valueMap.put(str, array);
	}
	
	public LinkedHashMap<String, double[]> getValueMap() {
		return valueMap;
	}
	
	
}