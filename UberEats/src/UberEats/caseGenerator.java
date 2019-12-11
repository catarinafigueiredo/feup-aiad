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
	
	private String[] ementa = {"pizza", "lasanha", "massa", "prego no prato", "hamburguer", "francesinha"};
	
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
			int food = getRandomNumberInRange(0,5);
			
			String lineC = "C"+(i+1)+"/"+xC+"-"+yC+"/"+ementa[food]+"/";
			
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
		for(int i = 0; i < nRests; i++) {
			
			int xR = getRandomNumberInRange(1,99);
			int yR = getRandomNumberInRange(1,99);
			
			int rat = getRandomNumberInRange(1,5);
			
			String lineR = "R"+(i+1)+"/"+xR+"-"+yR+"/"+rat+"/";
			
			for(int j = 0; j<ementa.length;j++) {
				if(j==ementa.length-1) {
					lineR+=ementa[j]+"-"+getRandomNumberInRange(5,13)+"\n";
				}
				else {
					lineR+=ementa[j]+"-"+getRandomNumberInRange(5,13)+";";
				}
			}
			
			try {
				this.writerR.write(lineR.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// GERAR DRIVERS
		for(int i = 0; i < nDrivers; i++) {
			
			int xD = getRandomNumberInRange(1,99);
			int yD = getRandomNumberInRange(1,99);
			
			int initTemp = getRandomNumberInRange(0,50);
			
			String lineD = "D"+(i+1)+"/"+xD+"-"+yD+"/"+initTemp+"\n";
			
			try {
				this.writerD.write(lineD.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
