package de.peterkossek.jdup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ByteComparation extends ComparationMethod {

	public ByteComparation() {
		super("Bytewise Comparation");
	}

	@Override
	public boolean filesEqual(File fileA, File fileB) throws IOException, NoSuchAlgorithmException {
		FileInputStream aIS = new FileInputStream(fileA);
		FileInputStream bIS = new FileInputStream(fileB);
		boolean equal = true;
		int aByte;
		int bByte;
		do {
			aByte = aIS.read();
			bByte = bIS.read();
			if ((aByte != bByte)) {
				equal = false;
				break;
			}
		} while (aByte != -1 && bByte != -1);
		aIS.close();
		bIS.close();
		return equal;
	}

}
