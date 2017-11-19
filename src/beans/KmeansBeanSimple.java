package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Kmeans全般を扱う予定のクラス 今のところデータ数とクラスタ数に依存しないように書いている
 * リストは使ってない、というか使いこなすスキルがない。2次元ががが 修正すべき項目：データをintからdoubleに
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
	public static int parseCsvLength(File file) {
		int counter = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.readLine() != null) {
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
	 * @return data[行-1][列]
	 */
	public static int[][] parseCsvData(File file, int dataNum, int headNum) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int raw = 0;// 現在何行目か
			int data[][] = new int[dataNum][headNum];// 配列ver、データ数と項目数が必要、ArrayList化するPG力なかった＾－＾
			while ((line = br.readLine()) != null) {
				if (raw > 0) {
					String[] dataElem = line.split(",", 0);
					int column = 0;// 現在何列目か

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

	/**
	 * データをランダムにクラスタ分けしたときに、 各クラスタのデータ数の差が1以下になるように振り分けた場合の 1クラスタに入るデータ数の上限値を求める
	 *
	 * @param data[][]
	 *            データ
	 * @param clusterNum
	 *            クラスタ数
	 * @param dataNum
	 *            データ数=csvLength-1
	 * @return 1クラスタに入るデータ数の上限値
	 */
	public static int calcClusterLimitCount(int clusterNum, int dataNum) {
		int clusterLimitCount = (int) Math.ceil(dataNum / clusterNum);
		return clusterLimitCount;
	}

	/**
	 * データをランダムにクラスタ分け 各クラスタのデータ数の差が1以下になるように振り分ける。
	 *
	 * @param data[][]
	 *            データ
	 * @param clusterNum
	 *            クラスタ数
	 * @param dataNum
	 *            データ数=csvLength-1
	 * @return claster[クラスタ番号]={所属データ番号のリスト}
	 */
	public static int[][] classifyRamdom(int[][] data, int clusterNum, int dataNum, int clusterLimitCount) {
		int tmprnd = 99;
		double rnd;
		// int clusterLimitCount = (int) Math.ceil(dataNum / clusterNum);
		int[][] cluster = new int[clusterNum][clusterLimitCount];
		int[] clusterCounter = new int[clusterNum];// 初期値0
		for (int i = 0; i < clusterNum; i++) {
			for (int j = 0; j < clusterLimitCount; j++) {
				cluster[i][j] = -1;// クラスタ[][]の中身初期値-1
			}
		}
		for (int i = 0; i < dataNum; i++) {
			rnd = Math.random();
			tmprnd = (int) Math.floor(rnd * clusterNum);
			if (clusterCounter[tmprnd] == clusterLimitCount) {
				i--;
				continue;
			} else {
				cluster[tmprnd][clusterCounter[tmprnd]] = i;
				clusterCounter[tmprnd]++;
			}
		}
		return cluster;
	}

	/**
	 * [未テスト]
	 * KKZで初期cog決定 データ間距離が最大値をとるデータペアをクラスタ数分用意する。 それらを初期cogとする。
	 *
	 * @param data[][]
	 *            データ
	 * @param clusterNum
	 *            クラスタ数
	 * @param dataNum
	 *            データ数=csvLength-1
	 * @return claster[クラスタ番号]={所属データ番号のリスト}
	 */
	public static int[][] classifyKKZ(int[][] data, int clusterNum, int dataNum, int headNum, int clusterLimitCount) {
		// int tmprnd = 99;
		// double rnd;
		// // int clusterLimitCount = (int) Math.ceil(dataNum / clusterNum);
		// 全データ間距離のMAXを求める
		double maxDist = 0;
		double tmpDist = 0;
		int tmpI = 0;
		int tmpJ = 0;
		int[][] cog = new int[clusterNum][headNum];
		for (int i = 0; i < dataNum; i++) {
			for (int j = 0; j < dataNum; j++) {
				for (int k = 0; k < headNum; k++) {
					tmpDist = Math.pow(data[i][k] - data[j][k], 2);
				}
				if (tmpDist > maxDist) {
					maxDist = tmpDist;
					tmpI = i;
					tmpJ = j;
				}
			}
		}
		for (int l = 0; l < headNum; l++) {
			cog[0][l] = data[tmpI][l];
			cog[1][l] = data[tmpJ][l];
		}

		int nowClusterNum = 2;

		// 求めたcog達から最も遠いデータ点を求めて新たなcogとする
		// 作りたいクラスタ数分回す
		tmpDist = 0;
		maxDist = 0;
		tmpJ = 0;
		for (int m = 0; m < clusterNum; m++) {
			for (int j = 0; j < dataNum; j++) {
				for (int i = 0; i < nowClusterNum; i++) {
					for (int k = 0; k < headNum; k++) {
						tmpDist = Math.pow(cog[i][k] - data[j][k], 2);
					}
					if (tmpDist > maxDist) {
						maxDist = tmpDist;
						tmpJ = j;
					}
				}
			}
			for (int l = 0; l < headNum; l++) {
				cog[nowClusterNum][l] = data[tmpJ][l];
			}
			nowClusterNum++;
		}
		return cog;
	}

	/**
	 * 各クラスタの重心を求める
	 *
	 * @param cluster[][]
	 *            クラスタ[クラスタ番号]={所属データの番号のリスト}
	 * @param data[][]
	 *            データ[データ番号][項目番号]
	 *
	 * @return 重心[クラスタ番号]={値1, 値2}
	 */
	public static int[][] calcCog(int clusterNum, int headNum, int[][] cluster, int[][] data, int clusterLimitCount) {
		int[][] cog = new int[clusterNum][headNum];
		int counter = 0;
		int j = 0;
		for (int i = 0; i < clusterNum; i++) {
			for (j = 0; j < headNum; j++) {
				counter = 0;
				for (int k = 0; k < clusterLimitCount; k++) {
					if (cluster[i][k] != -1) {
						cog[i][j] += data[cluster[i][k]][j];
						counter++;
					}
				}
				cog[i][j] = cog[i][j] / counter;
			}
		}
		return cog;
	}

	/**
	 * 各データと各重心を最適にマッチングし、 各クラスタに所属するデータリストを更新する。 収束判定未実装
	 *
	 * @param cog[][]
	 *            重心[クラスタ番号]={値1,値2}
	 * @param data[][]
	 *            データ[データ番号]={値1,値2}
	 * @param cluster[][]
	 *            クラスタ[クラスタ番号]={所属データの番号リスト}
	 * @param headNum
	 *            項目数
	 * @param clusterNum
	 *            クラスタ数
	 * @param dataNum
	 *            データ数
	 *
	 * @return クラスタ[クラスタ番号]={更新後の所属データの番号のリスト}
	 */
	public static int[][] updateCluster(int[][] cog, int[][] data, int headNum, int clusterNum, int dataNum) {
		double tmpdist = 0;
		double dist = Integer.MAX_VALUE;
		int updatedClusterNum = 99;// 更新後クラスタ番号
		int updatedCluster[][] = new int[clusterNum][dataNum];// 更新後クラスタ、ArrayListじゃないのでclusterNum個の枠を確保...
		int counter[] = new int[clusterNum];
		for (int i = 0; i < clusterNum; i++) {
			for (int j = 0; j < dataNum; j++) {
				updatedCluster[i][j] = -1;// 更新後クラスタの中身初期値=-1
			}
		}

		for (int k = 0; k < dataNum; k++) {
			for (int i = 0; i < clusterNum; i++) {
				for (int j = 0; j < headNum; j++) {
					tmpdist += Math.pow((cog[i][j] - data[k][j]), 2);
				}
				if (dist > tmpdist) {
					dist = tmpdist;
					updatedClusterNum = i;
				}
				tmpdist = 0;
			}
			updatedCluster[updatedClusterNum][counter[updatedClusterNum]] = k;
			counter[updatedClusterNum]++;
		}
		return updatedCluster;
	}

	/**
	 * 収束判定メソッド。Conv=Convergence:収束
	 * 初回収束はなしとする。初回のクラスタ配列と1回更新後のクラスタ配列のサイズが違うので比較できないため。
	 *
	 * @return 収束ならtrue、収束しないならfalse
	 */
	public static boolean judgeConv(int[][] cluster, int[][] updatedCluster, int clusterNum, int dataNum) {
		for (int i = 0; i < clusterNum; i++) {
			for (int j = 0; j < dataNum; j++) {
				if (cluster[i][j] != updatedCluster[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

}

class KmeansBeanSimpleTest {
	public static void main(String args[]) {
		int clusterNum = 2;
		File file = new File("C:\\Eclipse\\Eclipse_oxygen\\workspace\\WhoIsKmean\\csv\\test.csv");// 相対パスのがいい？
		int csvLength = KmeansBeanSimple.parseCsvLength(file);
		String[] head = KmeansBeanSimple.parseCsvHead(file);
		int[][] data = KmeansBeanSimple.parseCsvData(file, csvLength - 1, head.length);
		int clusterLimitCount = KmeansBeanSimple.calcClusterLimitCount(2, csvLength - 1);
		int[][] cluster = KmeansBeanSimple.classifyRamdom(data, clusterNum, csvLength - 1, clusterLimitCount);
		int loopCounter = 0;
		int[][] tmpCluster = null;
		while (true) {
			int[][] cog = KmeansBeanSimple.calcCog(clusterNum, head.length, cluster, data, clusterLimitCount);
			int[][] updatedCluster = KmeansBeanSimple.updateCluster(cog, data, head.length, clusterNum, csvLength - 1);
			if (loopCounter >= 1) {
				if (KmeansBeanSimple.judgeConv(tmpCluster, updatedCluster, clusterNum, csvLength - 1)) {
					System.out.println("収束!");
					break;
				}
			}
			tmpCluster = updatedCluster;
			loopCounter++;
		}
	}
}
