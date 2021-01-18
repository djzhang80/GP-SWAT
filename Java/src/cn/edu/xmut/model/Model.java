package cn.edu.xmut.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import cn.edu.xmut.util.LockList;
import cn.edu.xmut.util.UtilsFunctions;

/**
 * 
 */

public class Model {

	static boolean initiated = false;

	static String basepath = null;

	static int cores = 0;
	
	static String os="Linux";

	static Map<String, ArrayList<String>> upstreamMap = new HashMap<String, ArrayList<String>>();

	public static void init() {

		BufferedReader in = null;
		try {
			InputStream inputStream = Model.class.getClassLoader()
					.getResourceAsStream("config.txt");
			in = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = in.readLine()) != null) {
				String[] tks = line.split("=");
				if (tks[0].trim().startsWith("basepath")) {
					basepath = tks[1].trim();
				}
				if (tks[0].trim().startsWith("cores")) {
					cores = Integer.parseInt(tks[1].trim());
				}
				
				if (tks[0].trim().startsWith("os")) {
					os = tks[1].trim();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		FileReader filereader;
		File file = new File(basepath + "routeset.txt");
		try {

			filereader = new FileReader(file);
			List<String> routeinfos = IOUtils.readLines(filereader);
			IOUtils.closeQuietly(filereader);
			for (int i = 0; i < routeinfos.size(); i++) {
				String[] tokens = routeinfos.get(i).split(",");

				if (!tokens[1].equals("-1")) {

					ArrayList<String> rs = (ArrayList<String>) upstreamMap
							.get(tokens[1]);
					if (rs == null) {
						rs = new ArrayList<String>();
					}
					rs.add(tokens[0]);
					upstreamMap.put(tokens[1], rs);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		File dir = new File(basepath + "repository");

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		};
		String[] childrens = dir.list(filter);

		for (int i = 0; i < childrens.length; i++) {

			File file2 = new File(basepath + "repository/" + childrens[i]);
			file2.delete();

		}

		for (int k = 0; k < cores; k++) {

			dir = new File(basepath + "models/model" + k);

			filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return !name.startsWith(".") && name.endsWith("pot");
				}
			};
			childrens = dir.list(filter);

			for (int i = 0; i < childrens.length; i++) {

				File file2 = new File(
						basepath + "models/model" + k + "/" + childrens[i]);
				file2.delete();

			}

		}

		initiated = true;

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void call(long sub_simulation) throws IOException {

		if (!initiated) {

			init();
		}

		int lInfo = -1;

		//1.get a free model
		while (true) {

			//TODO
			//System.out.println("cores:" + cores);
			//System.out.println("basepath:" + basepath);

			lInfo = LockList.getFreeModelNew(cores);

			if (lInfo != -1) {

				break;

			} else {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

		//2.run model 

		//2.1 prepare model configuration file

		int isub = (int) sub_simulation / 10000;
		int isim = (int) sub_simulation % 10000;
		//TODO
		//System.out.println("isub:" + isub);
		//System.out.println("isim:" + isim);

		String srcName = basepath + "wconfigfiles/"
				+ UtilsFunctions.pad("0", isub, 5)
				+ UtilsFunctions.pad("0", isim, 4) + ".fig";

		String dstName = basepath + "models/model" + lInfo + "/fig.fig";
		//TODO
		//System.out.println("dstName:=" + dstName);
		//System.out.println("srcName:=" + srcName);
		File myDelFile = new File(dstName);
		try {
			myDelFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileUtils.copyFile(new File(srcName),
				new File(dstName));
	

		//2.2 prepare point source files

		ArrayList<String> upstreams = upstreamMap.get(isub + "");
		//TODO
		//System.out.println("isub:" + isub);
		//System.out.println("2.2");

		//has upstreams
		if (upstreams != null) {

			for (int i = 0; i < upstreams.size(); i++) {

				String psName = UtilsFunctions.pad("0", upstreams.get(i), 5, "")
						+ UtilsFunctions.pad("0", isim, 4) + ".pot";

				String srcPSName = basepath + "repository/" + psName;
				String dstPSName = basepath + "models/model" + lInfo + "/"
						+ psName;
				File psFile = new File(dstPSName);
				if (!psFile.exists()) {
					FileUtils.copyFile(new File(srcPSName),
							new File(dstPSName));
					//TODO
					//System.out.println("dstPSName:=" + dstPSName);
					//System.out.println("srcPSName:=" + srcPSName);
				}
			}

		}
		//2.3 invoke model
		if(os.equals("Windows")) {
		UtilsFunctions.exeComandWindows(
				basepath + "models/model" + lInfo + "/invokeswat.bat>>"
						+ basepath + "models/model" + lInfo + "/log.txt");
		}else if(os.equals("Linux")){
			
		UtilsFunctions.exeComand(
				basepath + "models/model" + lInfo + "/invokeswat.sh>>"
						+ basepath + "models/model" + lInfo + "/log.txt");
		}
		//2.4 put generated point source file to a shared repository	
		String genPSName = UtilsFunctions.pad("0", isub, 5)
				+ UtilsFunctions.pad("0", isim, 4) + ".pot";
		String gendstPSName = basepath + "repository/" + genPSName;
		String gensrcPSName = basepath + "models/model" + lInfo + "/"
				+ genPSName;

		FileUtils.copyFile(new File(gensrcPSName), new File(gendstPSName));

		//TODO
		//System.out.println("gendstPSName:=" + gendstPSName);
		//System.out.println("gensrcPSName:=" + gensrcPSName);

		//3. reLeaseModel

		LockList.releaseModelNew(lInfo);

	}

}
