import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import cn.edu.xmut.model.Model;

/**
 * 
 */

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		
		try {
			Model.call(10001l);
			Model.call(20001l);
			Model.call(40001l);
			Model.call(30001l);
			Model.call(50001l);
			
			Model.call(10002l);
			Model.call(20002l);
			Model.call(40002l);
			Model.call(30002l);
			Model.call(50002l);
			
			
			Model.call(10000l);
			Model.call(20000l);
			Model.call(40000l);
			Model.call(30000l);
			Model.call(50000l);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
