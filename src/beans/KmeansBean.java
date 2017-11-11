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
 *
 * @author azarashi
 */

/**
 * Kmeans全般を扱う予定のクラス
 *
 * @author azarashi
 */
public class KmeansBean {

	private static String[] header = new String[] { "data1", "data2" };

	/**
	 * csvファイルの1行目の項目名をリスト化
	 *
	 * @param file
	 *            CSVファイル
	 * @return 項目名のリスト
	 */
	static List<String[]> opencsvToStringArray(File file) {
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"));
			return reader.readAll();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * csvファイルのデータをリスト化
	 *
	 * @param file
	 *            CSVファイル
	 * @return データのリスト
	 */
	static List<DTO> opencsvToBean(File file) {
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"), ',', '"', 1);
			ColumnPositionMappingStrategy<DTO> strat = new ColumnPositionMappingStrategy<DTO>();
			strat.setType(DTO.class);
			strat.setColumnMapping(header);
			CsvToBean<DTO> csv = new CsvToBean<DTO>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

class KmeansBeanTest {
	public static void main(String args[]) {
		File file = new File("C:\\Eclipse\\Eclipse_oxygen\\workspace\\WhoIsKmean\\csv\\test.csv");
		KmeansBean.opencsvToStringArray(file);
		KmeansBean.opencsvToBean(file);
//		List axisName = new ArrayList();
//		axisName = KmeansBean.opencsvToStringArray(file);
//		List<DTO> data = new ArrayList();
//		data = KmeansBean.opencsvToBean(file);
//		System.out.println(axisName);
//		System.out.println(data);

	}

}
