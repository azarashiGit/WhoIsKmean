package beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
/**
 * csvをparseする。
 * https://qiita.com/satio_koibuti/items/e90a9e30db289ac1b1f0参照
 * @author azarashi
 */

/**
 * parseして得たデータの型を決めるクラス。
 * @author azarashi
 */
class DTO {
	private Integer data1, data2;

	public Integer getData1() {
		return data1;
	}

	public void setData1(Integer data1) {
		this.data1 = data1;
	}

	public Integer getData2() {
		return data2;
	}

	public void setData2(Integer data2) {
		this.data2 = data2;
	}
}

/**
 * Kmeans全般を扱う予定のクラス
 * @author azarashi
 */
public class Kmean {

	final String[] HEADER = new String[] { "data1", "data2" };

	/**
	 * csvファイルの1行目の項目名をリスト化
	 * @param file CSVファイル
	 * @return 項目名のリスト
	 */
	List<String[]> opencsvToStringArray(File file) {
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"));
			return reader.readAll();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * csvファイルのデータをリスト化
	 * @param file CSVファイル
	 * @return データのリスト
	 */
	List<DTO> opencsvToBean(File file) {
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"), ',', '"', 1);
			ColumnPositionMappingStrategy<DTO> strat = new ColumnPositionMappingStrategy<DTO>();
			strat.setType(DTO.class);
			strat.setColumnMapping(HEADER);
			CsvToBean<DTO> csv = new CsvToBean<DTO>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
