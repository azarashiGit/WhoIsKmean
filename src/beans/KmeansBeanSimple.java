package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Kmeans全般を扱う予定のクラス
 *
 * @author azarashi
 */
public class KmeansBeanSimple {

	/**
	 * csvファイルの行数を返す
	 *
	 * @param file
	 *            CSVファイル
	 * @return CSVファイルの行数
	 */
	public static int parseCsvLength(File file){
		int counter=0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.readLine()!= null) {
				counter++;
			}
			br.close();
			return counter;
		} catch (IOException e) {
			System.out.println(e);
			return 0;
		}
	}
	/**
	 * csvファイルの1行目の項目名をリスト化
	 *
	 * @param file
	 *            CSVファイル
	 * @return 項目名の配列
	 */
	public static String[] parseCsvHead(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			String[] axisName = line.split(",", 0); // 行をカンマ区切りで配列に変換
			br.close();
			return axisName;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	/**
	 * csvファイルのデータ部分をリスト化
	 *
	 * @param file
	 *            CSVファイル
	 * @param dataNum
	 *            データ数
	 * @param headNum
	 *            データ項目数
	 * @return データ
	 */
	public static int[][] parseCsvData(File file, int dataNum, int headNum) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int raw = 0;//現在何行目か
			int data[][] = new int[dataNum][headNum];// 配列ver、データ数と項目数が必要、ArrayList化するPG力なかった＾－＾
			while ((line = br.readLine()) != null) {
				if (raw > 0) {
					String[] dataElem = line.split(",", 0);
					int column = 0;//現在何列目か

					for (String elem : dataElem) {
						data[raw - 1][column] = Integer.parseInt(elem);
						column++;
					}
				}
				raw++;
			}
			br.close();
			return data;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}
}

class KmeansBeanSimpleTest {
	public static void main(String args[]) {
		File file = new File("C:\\Eclipse\\Eclipse_oxygen\\workspace\\WhoIsKmean\\csv\\test.csv");//相対パスのがいい？
		int csvLength=KmeansBeanSimple.parseCsvLength(file);
		String[] head=KmeansBeanSimple.parseCsvHead(file);
		int[][] data=KmeansBeanSimple.parseCsvData(file, csvLength-1, head.length);
	}
}