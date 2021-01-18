package cn.edu.xmut.util;

import java.io.IOException;

public class UtilsFunctions {
	public static int model_index = 1;

	static synchronized int getFreeModelIndex(int cores) {
		model_index = (model_index + 1) % cores;
		return model_index;
	}

	public static void println(String message) {

		try {
			String[] commands = new String[] { "/bin/bash", "-c",
					"echo '" + message + "'>>/home/application/debug1.out" };

			Process pr = java.lang.Runtime.getRuntime().exec(commands);
			pr.waitFor();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public  static void exeComand(String command) {
		try {
			String[] commands = new String[] { "/bin/bash", "-c",
					command + "&>>/home/application/debug.out" };
			Process pr = java.lang.Runtime.getRuntime().exec(commands);
			pr.waitFor();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public  static void exeComandWindows(String command) {
		try {
			
			//System.out.println(command);
			String[] commands = new String[] { "cmd", "/c",
					command };
			Process pr = java.lang.Runtime.getRuntime().exec(commands);
			pr.waitFor();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static String pad(String type, int input, int length) {
		String tString = input + "";
		int len = tString.length();
		for (int i = 0; i < length - len; i++) {
			tString = type + tString;
		}
		return tString;
	}

	public static String pad(String type, String input, int length,
			String posfix) {
		String tString = input + "";
		int len = tString.length();
		for (int i = 0; i < length - len; i++) {
			tString = type + tString;
		}
		return tString + posfix;
	}
}
