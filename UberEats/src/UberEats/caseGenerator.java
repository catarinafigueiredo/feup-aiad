package UberEats;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class caseGenerator {
	
	private String filePathName;
	
	private int nClientes;
	private int nRests;
	private int nDrivers;
	
	private int minClientes;
	private int minRests;
	private int minDrivers;
	
	private int maxClientes;
	private int maxRests;
	private int maxDrivers;
	
	FileOutputStream writerC;
	FileOutputStream writerR;
	FileOutputStream writerD;
	
	public caseGenerator(int minC, int maxC, int minR, int maxR, int minD, int maxD) {
		this.filePathName = "files/cenariosTesteAiad/generatedCase";
		
		try {
			this.writerC = new FileOutputStream(filePathName+"C.txt");
			this.writerR = new FileOutputStream(filePathName+"R.txt");
			this.writerD = new FileOutputStream(filePathName+"D.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
	
	public void generateFile() {
		// GERAR CLIENTES
		int time = 0;
		for(int i = 0; i < nClientes; i++) {
			time += 50;
			
			int xC = getRandomNumberInRange(1,99);
			int yC = getRandomNumberInRange(1,99);
			
			int crit = getRandomNumberInRange(0,2);
			
			String lineC = "C"+(i+1)+"/"+xC+"-"+yC+"/"+"food"+"/";
			
			switch(crit) {
			case 0:
				lineC+="faster";
				break;
			case 1:
				lineC+="cheaper";
				break;
			case 2:
				lineC+="quality";
				break;
			}
			
			lineC+="/"+time+"\n";
			
			try {
				this.writerC.write(lineC.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// GERAR RESTAURANTES
		// GERAR DRIVERS
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
