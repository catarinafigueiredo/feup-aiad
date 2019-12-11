package UberEats;

import java.util.Random;

public class caseGenerator {
	
	private String fileName;
	
	private int nClientes;
	private int nRests;
	private int nDrivers;
	
	private int minClientes;
	private int minRests;
	private int minDrivers;
	
	private int maxClientes;
	private int maxRests;
	private int maxDrivers;
	
	public caseGenerator(int minC, int maxC, int minR, int maxR, int minD, int maxD) {
		this.fileName = "generatedCase.txt";
		
		minClientes=minC;
		minRests=minR;
		minDrivers=minD;
		
		maxClientes=maxC;
		maxRests=maxR;
		maxDrivers=maxD;
		
		nClientes = getRandomNumberInRange(minClientes, maxClientes);
		nRests = getRandomNumberInRange(minRests, maxRests);
		nDrivers = getRandomNumberInRange(minDrivers, maxDrivers);
	}
	
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public int getClientes() {
		return nClientes;
	}
	
	public int getRests() {
		return nRests;
	}
	
	public int getDrivers() {
		return nDrivers;
	}
	
}
