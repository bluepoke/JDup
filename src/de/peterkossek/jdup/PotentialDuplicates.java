package de.peterkossek.jdup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PotentialDuplicates {

	private static final String MD5 = "MD5";
	File source;
	Set<File> duplicates = new HashSet<File>();
	public static final int COMPARE_BYTES = 0;
	public static final int COMPARE_MD5 = 1;
	
	public PotentialDuplicates(File source) {
		this.source = source;
	}
	
	public void addPotentialDuplicates(List<File> files) {
		duplicates.addAll(files);
	}
	
	public void checkDuplicates(int method) throws IOException, NoSuchAlgorithmException {
		Iterator<File> iterator = duplicates.iterator();
		while (iterator.hasNext()) {
			File duplicate = iterator.next();
			System.out.println("Comparing "+source+" to "+duplicate);
			FileInputStream sourceIS = new FileInputStream(source);
			FileInputStream dupIS = new FileInputStream(duplicate);
			
			if (method == COMPARE_BYTES) {
				while (true) {
					int s = sourceIS.read();
					int d = dupIS.read();
					if (s != d) {
						iterator.remove();
						System.out.println(source+" differs from "+duplicate);
						break;
					}
					if (s==-1 || d==-1)
						break;
				}
			} else if (method == COMPARE_MD5) {
				MessageDigest sourceMD5 = MessageDigest.getInstance(MD5);
				DigestInputStream sourceDIS = new DigestInputStream(sourceIS, sourceMD5);
				byte[] buffer = new byte[1024];
				while (sourceDIS.read(buffer) != -1) {}
				byte[] sourceDigest = sourceMD5.digest();
				System.out.println(source+" MD5 = "+Arrays.toString(sourceDigest));
				
				MessageDigest dupMD5 = MessageDigest.getInstance(MD5);
				DigestInputStream dupDIS = new DigestInputStream(dupIS, dupMD5);
				buffer = new byte[1024];
				while (dupDIS.read(buffer) != -1) {}
				byte[] dupDigest = dupMD5.digest();
				System.out.println(duplicate+" MD5 = "+Arrays.toString(dupDigest));
				
				for (int i=0; i<sourceDigest.length; i++) {
					if (sourceDigest[i] != dupDigest[i]) {
						iterator.remove();
						System.out.println(source+" differs from "+duplicate);
						break;
					}
				}
			}
			
			sourceIS.close();
			dupIS.close();
		}
	}

	public boolean hasDuplicates() {
		return !duplicates.isEmpty();
	}
	
	@Override
	public String toString() {
		return source+" "+duplicates;
	}
}
