package de.peterkossek.jdup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5Comparation extends ComparationMethod {

	public MD5Comparation() {
		super("MD5 Comparation");
	}

	private static final String MD5 = "MD5";

	@Override
	public boolean filesEqual(File fileA, File fileB) throws IOException, NoSuchAlgorithmException {
		FileInputStream aIS = new FileInputStream(fileA);
		FileInputStream bIS = new FileInputStream(fileB);
		MessageDigest aMD5 = MessageDigest.getInstance(MD5);
		DigestInputStream aDIS = new DigestInputStream(aIS, aMD5);
		byte[] buffer = new byte[1024];
		while (aDIS.read(buffer) != -1) {}
		byte[] aDigest = aMD5.digest();
		aDIS.close();
		System.out.println(fileA+" MD5 = "+Arrays.toString(aDigest));
		
		MessageDigest bMD5 = MessageDigest.getInstance(MD5);
		DigestInputStream bDIS = new DigestInputStream(bIS, bMD5);
		buffer = new byte[1024];
		while (bDIS.read(buffer) != -1) {}
		byte[] bDigest = bMD5.digest();
		bDIS.close();
		System.out.println(fileB+" MD5 = "+Arrays.toString(bDigest));
		
		boolean equal = true;
		for (int i=0; i<aDigest.length; i++) {
			if (aDigest[i] != bDigest[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}
	
}
