/**
 * 
 */
package cn.edu.xmut.configfile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.edu.xmut.util.*;
import sun.tools.jar.resources.jar;

import org.apache.commons.io.IOUtils;

public class FigFileUtil {

	static Map<String, String> lookuptableMap = new HashMap<String, String>();
	static String path = "E:\\projects\\p17\\";
	static int simulatecount = 1;

	public static void main(String[] args) {
		if (args.length < 2) {
			path = "E:\\projects\\p17\\";
			simulatecount=4;
		} else {
			path = args[0];
			simulatecount = Integer.parseInt(args[1]);
		}

		FileReader fig_filereader;
		File fig_file = new File(path + "fig.fig");
		try {
			fig_filereader = new FileReader(fig_file);
			List<String> configs = IOUtils.readLines(fig_filereader);

			for (int i = 0; i < configs.size(); i++) {
				if (configs.get(i).startsWith("subbasin")) {
					String line = configs.get(i);
					String ww[] = line.split("\\s+");
					String line2 = configs.get(i + 1);
					String old = line2.substring(line2.indexOf("0"),
							8 + line2.indexOf("0") + 1);
					lookuptableMap.put(ww[3], old);

				}
			}
			System.out.println(lookuptableMap.toString());

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//-------------------------生成新旧子流域的映身表结束--------------------

		Map<String, ArrayList<String>> upstreamMap = new HashMap<String, ArrayList<String>>();
		//prepare fig.fig files for all subbasin

		Map<String, String> upstreamAreaMap = new HashMap<String, String>();
		
		FileReader filereader;
		File file = new File(path + "routeset.txt");
		try {
			filereader = new FileReader(file);
			List<String> routeinfos = IOUtils.readLines(filereader);
			IOUtils.closeQuietly(filereader);
			for (int i = 0; i < routeinfos.size(); i++) {
				String[] tokens = routeinfos.get(i).split(",");
				
				
				upstreamAreaMap.put(tokens[0],tokens[3]);

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

			String subbasin_p1 = "subbasin       1     1     1";
			String subbasin_p2 = "          xxxxxxxxx.sub";
			String rec_p1 = "recday        10hhhhhhffffff            xxxxxx";
			String rec_p2 = "          xxxxxxxxx.pot";
			String add = "add            5aaaaaabbbbbbcccccc";
			String route_p1 = "route          2aaaaaa     1bbbbbb";
			String route_p2 = "          xxxxxxxxx.rtexxxxxxxxx.swq";
			String save_p1 = "save           9aaaaaa     1     0     0";
			String save_p2 = "          xxxxxxxxx.pot  ";
			String finish = "finish         0";
			
			
			for (int z=0; z<simulatecount; z++) {
				
			String simulation=UtilsFunctions.pad("0", z, 4);
			
			for (int j = 0; j < routeinfos.size(); j++) {

				String[] tks = routeinfos.get(j).split(",");
				String csub = tks[0];
				ArrayList upsubs = upstreamMap.get(csub);
				String sub = UtilsFunctions.pad("0", csub, 5, simulation);
				String oldsub = lookuptableMap.get(csub);

				ArrayList<String> contents = new ArrayList<String>();
				contents.add(subbasin_p1);
				contents.add(subbasin_p2.replace("xxxxxxxxx", oldsub));
				int count = 0;

				if (upsubs != null)
					count = upsubs.size();

				int k = count + 1;
				int g = k + 1;

				if (count > 0) {
					for (int i = 0; i < count; i++) {
						String tupstream = (String) upsubs.get(i);
						String line1 = rec_p1
								.replace("hhhhhh",
										UtilsFunctions.pad(" ", i + 2, 6))
								.replace("ffffff",
										UtilsFunctions.pad(" ", i + 1, 6))
								.replace("xxxxxx", UtilsFunctions.pad(" ", upstreamAreaMap.get(tupstream), 6,""))	;
					
						String line2 = rec_p2.replace("xxxxxxxxx",
								UtilsFunctions.pad("0", tupstream, 5, simulation));
						contents.add(line1);
						contents.add(line2);
					}

					for (int i = 0; i < upsubs.size(); i++) {
						String line1 = add
								.replace("cccccc",
										UtilsFunctions.pad(" ", i + 1, 6))
								.replace("bbbbbb",
										UtilsFunctions.pad(" ", k, 6))
								.replace("aaaaaa",
										UtilsFunctions.pad(" ", g, 6));
						contents.add(line1);
						k++;
						g++;
					}

				}
				//String route_p1="route          2aaaaaa     1bbbbbb";
				//String route_p2="          xxxxxxxxx.rtexxxxxxxxx.swq";

				String l1 = route_p1
						.replace("aaaaaa", UtilsFunctions.pad(" ", g, 6))
						.replace("bbbbbb", UtilsFunctions.pad(" ", k, 6));
				String l2 = route_p2.replaceAll("xxxxxxxxx", oldsub);
				contents.add(l1);
				contents.add(l2);

				String line1 = save_p1.replace("aaaaaa",
						UtilsFunctions.pad(" ", g, 6));
				String line2 = save_p2.replace("xxxxxxxxx", sub);

				contents.add(line1);
				contents.add(line2);
				contents.add(finish);

				FileWriter fileWriter;
				File file4 = new File(path + "wconfigfiles/" + sub + ".fig");
				fileWriter = new FileWriter(file4);
				IOUtils.writeLines(contents, null, fileWriter);
				IOUtils.closeQuietly(fileWriter);

			}
		  }

		} catch (

		IOException e) {
			e.printStackTrace();
		}
	}

}
