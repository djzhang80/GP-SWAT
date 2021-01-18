/**
 * 
 */
package cn.edu.xmut.util;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

import cn.edu.xmut.model.Model;

public class LockList {

	static ArrayList<MultiProcessesLock> locklist;
	
	
	static boolean[] lockindicators={false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
	
	
	public static synchronized int getFreeModelNew(int cores) {
	
		for (int i = 0; i < cores; i++) {
			if (!lockindicators[i]) {
				lockindicators[i]=true;
				System.out.println("model "+i+ " is locked!");
				return i;
			}

		}
		return -1;
	}

	public static synchronized void releaseModelNew(int li) {

		System.out.println("model "+li+ " is unlocked!");
		lockindicators[li]=false;
		
	}

	public static synchronized LockInfo getFreeModel(int cores, String path) {
		LockInfo li = null;

		if (locklist == null) {
			locklist = new ArrayList<MultiProcessesLock>();
			for (int i = 0; i < cores; i++) {
				MultiProcessesLock mpLock = new MultiProcessesLock(
						path + "\\lock." + i);
				locklist.add(mpLock);
			}
		}

		for (int i = 0; i < locklist.size(); i++) {

			FileLock fLock = locklist.get(i).lock();

			if (fLock != null) {
				li = new LockInfo();
				li.lock = fLock;
				li.modelIndex = i;
				
				System.out.println("model "+li.modelIndex+ " is locked!");
				

				return li;
			}

		}

		return null;

	}

	public static void releaseModel(LockInfo li) {

		System.out.println("model "+li.modelIndex+ " is unlocked!");

		if (li.lock != null) {
			try {
				li.lock.release();
				li.lock = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
