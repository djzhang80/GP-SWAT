package cn.edu.xmut.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MultiProcessesLock {

	private RandomAccessFile raf;
	private FileChannel channel = null;
	private FileLock lock = null;

	public MultiProcessesLock(String filename) {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(filename, "rw");
			raf.seek(raf.length());
			channel = raf.getChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileLock lock() {
		try {
			return channel.tryLock();
		} catch (Exception e) {
			return null;
		}

	}

	public void releaseLock() {
		if (lock != null) {
			try {
				lock.release();
				lock = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
