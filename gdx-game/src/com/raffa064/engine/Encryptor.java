package com.raffa064.engine;

import java.util.Base64;
import java.util.Random;

public class Encryptor {   
    public static String encrypt(String inputString, int key) {
		byte[] inputBytes = inputString.getBytes();
		byte[] output = new byte[inputBytes.length];
		
		Random random = new Random(key);
		
		byte A = (byte) (key & 0b1111);
		byte B = (byte) (key >> 4 & 0b1111);
		byte C = (byte) (key >> 8 & 0b1111);
		byte D = (byte) (key >> 12 & 0b1111);

		for (int i = 0; i < inputBytes.length; i++) {
			output[i] = inputBytes[i];
			output[i] ^= A;
			output[i] ^= B;
			output[i] ^= C;
			output[i] ^= D;
			
			if (random.nextBoolean()) {
				output[i]--;
			} else {
				output[i]++;
			}
		}
		
		return Base64.getEncoder().encodeToString(output);
	}
	
	public static String decrypt(String inputString, int key) {
		byte[] inputBytes = Base64.getDecoder().decode(inputString);
		byte[] output = new byte[inputBytes.length];

		Random random = new Random(key);

		byte A = (byte) (key & 0b1111);
		byte B = (byte) (key >> 4 & 0b1111);
		byte C = (byte) (key >> 8 & 0b1111);
		byte D = (byte) (key >> 12 & 0b1111);

		for (int i = 0; i < inputBytes.length; i++) {
			output[i] = inputBytes[i];
			
			if (random.nextBoolean()) {
				output[i]++;
			} else {
				output[i]--;
			}
			
			output[i] ^= A;
			output[i] ^= B;
			output[i] ^= C;
			output[i] ^= D;
		}

		String string = new String(output);
		return string;
	}
}
