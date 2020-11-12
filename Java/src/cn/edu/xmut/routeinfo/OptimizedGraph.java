/**
 * 
 */
package cn.edu.xmut.routeinfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptimizedGraph {

	static String path = "E:\\projects\\p17\\";
    public static void main(String[] args) {
        List<Integer[]> originalList = new ArrayList<>();
        
		BufferedReader in = null;
		Map<String, ArrayList> upstreamLookupTable = new HashMap<String, ArrayList>();
		try {
			in = new BufferedReader(new FileReader(path + "jjrouteset.txt"));
			String line;

			while ((line = in.readLine()) != null) {

				System.out.println(line);
				String[] tokens=line.split(",");
				
				int startPos=Integer.parseInt(tokens[2].trim());
				int endPos=Integer.parseInt(tokens[3].trim());
				Integer[] t=new Integer[(endPos-startPos+1)];
				
				for (int i = 0; i < (endPos-startPos+1); i++) {
					t[i]=startPos+i;
				}
				   originalList.add(t);
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
        
        System.out.println("");

        List<Integer[]> combineList = getCombineList(originalList);

        System.out.println("组成的新集合共有" + combineList.size() + "个数组");

        combineList.forEach(s-> {
            for (int i = 0; i < s.length; i++) {
        		System.out.print(s[i]+",");
			}
    		System.out.println();
        });
    }

    private static List<Integer[]> getCombineList(List<Integer[]> originalList) {
        int originalSize = originalList.size();
        int[] tempIndexArr = new int[originalSize];
        tempIndexArr[originalSize - 1] = -1;
        int[] tempLengthArr = new int[originalSize];
        for (int i = 0; i < originalSize ; i++) {
            tempLengthArr[i] = originalList.get(i).length;
        }

        List<Integer[]> combineList = new ArrayList<>();
        boolean completeFlag = false;

        while(!completeFlag) {
            int changeIndex = originalList.size() - 1;
            boolean isRightIndex = false;
            while (!isRightIndex) {
                tempIndexArr[changeIndex] += 1;
                if(tempIndexArr[changeIndex] >= tempLengthArr[changeIndex]) {
                    if(changeIndex == 0) {
                        isRightIndex = true;
                        completeFlag = true;
                    } else {
                        tempIndexArr[changeIndex--] = 0;
                    }
                } else {
                    isRightIndex = true;
                }
            }
            if(isRightIndex && !completeFlag) {
            	Integer[] newItem = new Integer[originalList.size()];
                for (int i = 0; i < originalList.size() ; i++) {
                    newItem[i] = originalList.get(i)[tempIndexArr[i]];
                }
                combineList.add(newItem);
            }
        }

        return combineList;
    }

}