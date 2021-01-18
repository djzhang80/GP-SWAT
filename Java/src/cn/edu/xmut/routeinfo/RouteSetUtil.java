package cn.edu.xmut.routeinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.crypto.provider.AESParameters;
import com.sun.org.apache.regexp.internal.recompile;

import cn.edu.xmut.util.UtilsFunctions;

/**
 * 
 */

public class RouteSetUtil {

	static ArrayList<String> lines = new ArrayList<String>();
	static ArrayList<String> rs = new ArrayList<String>();
	static String path = "E:\\projects\\p17\\";
	static int simulatecount = 2;

	public static void main(String[] args) {

		if (args.length < 2) {
			path = "E:\\projects\\p17\\";
			simulatecount = 4;
		} else {
			path = args[0];
			simulatecount = Integer.parseInt(args[1]);
		}

		BufferedReader in = null;
		Map<String, ArrayList> upstreamLookupTable = new HashMap<String, ArrayList>();
		try {
			in = new BufferedReader(new FileReader(path + "fig.fig"));
			String line;

			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				lines.add(line);

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

		//从最下游子流域开始

		for (int i = lines.size() - 1; i >= 0; i--) {
			String line = lines.get(i);
			if (line.startsWith("rout")) {
				//System.out.println(line);
				route(line, "-1", 0);
				System.out.print("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				break;

			}
		}



		//generate edges 
		try {
			File edgFile = new File(path + "stream.txt");
			FileWriter edgfileWriter = new FileWriter(edgFile);
			for (int k = 0; k < simulatecount; k++) {

				for (int i = 0; i < rs.size(); i++) {
					String[] tokens = rs.get(i).split(",");
					if (!tokens[1].startsWith("-1"))
						IOUtils.write(tokens[0] +UtilsFunctions.pad("0", k, 4)+ " " + tokens[1]+UtilsFunctions.pad("0", k, 4) + "\r\n",
								edgfileWriter);
				}
			}
			
			IOUtils.closeQuietly(edgfileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		computeUpstreamArea(rs);

		for (int i = 0; i < rs.size(); i++) {

			String[] tokens = rs.get(i).split(",");

			Double area = upstreamAreaMap.get(tokens[0]);
			if (area == null) {

				area = 0.0;

			}

			rs.set(i, rs.get(i) + "," + area);

		}
		
		//optimized graph
		
		for (int i = 0; i < rs.size(); i++) {

			String[] tokens = rs.get(i).split(",");

			for (int j = 0; j < rs.size(); j++) {
				String[] tokens2 = rs.get(i).split(",");
				
				if(tokens[1].equals(tokens2[0])) {
					
					rs.set(i, rs.get(i) + "," +	(Integer.parseInt(tokens2[2])-1));
					
				}


			}

		}
		
		
		
		

		try {
			File file4 = new File(path + "routeset.txt");
			FileWriter fileWriter = new FileWriter(file4);
			IOUtils.writeLines(rs, null, fileWriter);
			IOUtils.closeQuietly(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//generate vertices 
		try {
			File verFile = new File(path + "subbasin.txt");
			FileWriter verfileWriter = new FileWriter(verFile);

			for (int k = 0; k < simulatecount; k++) {

				for (int i = 0; i < rs.size(); i++) {
					String[] tokens = rs.get(i).split(",");
					int ustreamcount=0;
					for (int g = 0; g<rs.size(); g++) {
						String[] tokens2 = rs.get(g).split(",");
						if (tokens2[1].equals(tokens[0])) {
							ustreamcount++;
						}
					}
					IOUtils.write(tokens[0] + UtilsFunctions.pad("0", k, 4)
							+ " " + ustreamcount + "\r\n", verfileWriter);
				}

			}

			IOUtils.closeQuietly(verfileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static int route(String line, String k, int lv) {

		String tokens[] = line.split("\\s+");
		//if(k!="-1")
		//   System.out.println(tokens[3]+","+k);
		if (line.startsWith("routres")) {
			System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbb");
		}
		String lineString = fineCommandByPosition(tokens[4]);
		String[] tt = lineString.split("\\s+");
		int code = Integer.parseInt(tt[1]);
		if (code == 5)
			lv = add(lineString, tokens[3], lv);
		if (code == 2 || code == 3) {

			lv = route(lineString, tokens[3], lv);

		}
		lv++;
		//System.out.println(tokens[3] + "," + k + "," + lv);
		rs.add(tokens[3] + "," + k + "," + lv);
		return lv;

	}

	public static String fineCommandByPosition(String store) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String tokens[] = line.split("\\s+");
			if (tokens.length >= 3) {
				if (tokens[2].equals(store)) {
					return line;
				}
			}

		}
		return "";

	}

	public static int add(String line, String k, int lv) {
		int z = lv;
		int t1 = lv, t2 = lv, t3 = lv, t4 = lv;
		String tokens[] = line.split("\\s+");
		if (tokens.length >= 3) {
			String command1 = fineCommandByPosition(tokens[3]);
			if (command1.startsWith("add")) {
				t1 = add(command1, k, lv);
			} else if (command1.startsWith("rout")) {
				t3 = route(command1, k, lv);

			}
			String command2 = fineCommandByPosition(tokens[4]);
			if (command2.startsWith("add")) {
				t2 = add(command2, k, lv);
			} else if (command2.startsWith("rout")) {
				t4 = route(command2, k, lv);

			}
		}

		z = Math.max(Math.max(t1, t2), Math.max(t3, t4));

		return z;

	}

	public static HashMap<String, ArrayList<String>> upstreamMap = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, Double> upstreamAreaMap = new HashMap<String, Double>();

	public static void computeUpstreamArea(ArrayList<String> lines) {
		int maxlvl = lines.size();
		int tmpmaxlvl = 2;
		for (int lvl = 1; lvl < maxlvl; lvl++) {
			for (int i = 0; i < lines.size(); i++) {
				String[] tks = lines.get(i).split(",");
				int sublvl = Integer.parseInt(tks[2]);
				tmpmaxlvl = Math.max(tmpmaxlvl, sublvl);
				if (sublvl == lvl) {
					ArrayList<String> upstreams = upstreamMap.get(tks[1]);
					if (upstreams == null) {
						upstreams = new ArrayList<String>();
					}
					upstreams.add(tks[0]);
					ArrayList<String> indirectupstreams = upstreamMap
							.get(tks[0]);
					if (indirectupstreams != null)
						upstreams.addAll(indirectupstreams);
					upstreamMap.put(tks[1], upstreams);

					if (i == lines.size() - 1) {
						maxlvl = tmpmaxlvl;
					}
				}

			}
		}

		for (String line : lines) {

			String[] tks = line.split(",");

			ArrayList<String> upstreams = upstreamMap.get(tks[0]);
			double totalArea = 0;
			if (upstreams != null) {

				for (String upstream : upstreams) {

					String sub = UtilsFunctions.pad("0", upstream, 5,
							"0000.sub");
					double area = getSubbasinArea(sub);
					totalArea = totalArea + area;

				}
			}

			String sub = UtilsFunctions.pad("0", tks[0], 5, "0000.sub");
			double area = getSubbasinArea(sub);

			upstreamAreaMap.put(tks[0], totalArea + area);

		}

	}

	public static double getSubbasinArea(String sub) {
		ArrayList<String> infos = new ArrayList<String>();

		BufferedReader in = null;
		Map<String, ArrayList> upstreamLookupTable = new HashMap<String, ArrayList>();
		try {
			in = new BufferedReader(
					new FileReader(path + "modelbackup\\" + sub));
			String line;

			int k = 0;

			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				infos.add(line);
				k++;
				if (k > 2) {
					break;
				}

			}
			String tks[] = infos.get(1).split("\\|");
			return Double.parseDouble(tks[0].trim());
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

		return 0;
	}

}
