package UberEats;

/*
 * class EcoSystem
 * 
 * - Responsavel por iniciar a aplicacao. Chama a classe que faz o parse dos dados e inicia os agentes. */

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class EcoSystem {
	
	static FileOutputStream writer;
	static String ss = "";

	public static void main(String[] args) {
		
		try {
			writer = new FileOutputStream("files/dataset.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		ContainerController mainContainer = rt.createMainContainer(p1);
		
		System.out.println("Generating data...");
		
		// fazer um for com 1000 iteracoes
		for(int i = 0; i < 1000; i++) {
			//System.out.println("ITE " +i+" -------------------------\n");
			
			caseGenerator gen = new caseGenerator(5,20,  5,15,  10,15);
			gen.generateFile();
			
			/*System.out.println("nClientes = " +gen.getClientes());
			System.out.println("nRests = " +gen.getRests());
			System.out.println("nDrivers = " +gen.getDrivers()+"\n");*/
		
		
			ParseFiles parseFiles = new ParseFiles();
		
			ArrayList<Drivers> drivers = new ArrayList<Drivers>();
			ArrayList<Client> clients = new ArrayList<Client>();
			ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		
			drivers = parseFiles.parseDrivers();
			clients = parseFiles.parseClients();
			restaurants = parseFiles.parseRestaurants();
			
			//try {
				//writer.write(String.valueOf(gen.getClientes()).getBytes());
				ss += gen.getClientes();
				
				int crit0=0;
				int crit1=0;
				int crit2=0;
				for(int j=0; j < clients.size();j++) {
					switch(clients.get(j).getCriterion()) 
			        { 
			            case "faster": 
			            	crit0++;
			                break; 
			            case "cheaper": 
			            	crit1++;
			                break; 
			            case "quality": 
			            	crit2++;
			                break;
			        }
				}
				
				float percCrit0 = ((float)crit0)/((float)gen.getClientes());
				float percCrit1 = ((float)crit1)/((float)gen.getClientes());
				float percCrit2 = ((float)crit2)/((float)gen.getClientes());
				
				ss += "\t";
				ss += percCrit0;
				ss += "\t";
				ss += percCrit1;
				ss += "\t";
				ss += percCrit2;
				
				/*writer.write("	".getBytes());
				writer.write(Float.toString(percCrit0).getBytes());
				writer.write("	".getBytes());
				writer.write(Float.toString(percCrit1).getBytes());
				writer.write("	".getBytes());
				writer.write(Float.toString(percCrit2).getBytes());*/
				
				ss += "\t";
				ss += restaurants.size();
				ss += "\t";
				
				ss += drivers.size();
				ss += "\t";
				
				/*writer.write("	".getBytes());
				writer.write(String.valueOf(restaurants.size()).getBytes());
				writer.write("	".getBytes());
				
				writer.write(String.valueOf(drivers.size()).getBytes());
				writer.write("	".getBytes());*/
				
				int q1 = 0;
				int q2 = 0;
				int q3 = 0;
				int q4 = 0;
				
				for(int j=0; j < clients.size();j++) {
					int quad = clients.get(j).calcQuad();
					switch(quad) {
					case 1:
						q1++;
						break;
					case 2:
						q2++;
						break;
					case 3:
						q3++;
						break;
					case 4:
						q4++;
						break;
					}
				}
				
				ss += q1;
				ss += "\t";
				ss += q2;
				ss += "\t";
				ss += q3;
				ss += "\t";
				ss += q4;
				ss += "\t";
				
				/*writer.write(String.valueOf(q1).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q2).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q3).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q4).getBytes());
				writer.write("	".getBytes());*/
				
				q1 = 0;
				q2 = 0;
				q3 = 0;
				q4 = 0;
				
				for(int j=0; j < restaurants.size();j++) {
					int quad = restaurants.get(j).calcQuad();
					switch(quad) {
					case 1:
						q1++;
						break;
					case 2:
						q2++;
						break;
					case 3:
						q3++;
						break;
					case 4:
						q4++;
						break;
					}
				}
				
				ss += q1;
				ss += "\t";
				ss += q2;
				ss += "\t";
				ss += q3;
				ss += "\t";
				ss += q4;
				ss += "\t";
				
				/*writer.write(String.valueOf(q1).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q2).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q3).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q4).getBytes());
				writer.write("	".getBytes());*/
				
				q1 = 0;
				q2 = 0;
				q3 = 0;
				q4 = 0;
				
				for(int j=0; j < drivers.size();j++) {
					int quad = drivers.get(j).calcQuad();
					switch(quad) {
					case 1:
						q1++;
						break;
					case 2:
						q2++;
						break;
					case 3:
						q3++;
						break;
					case 4:
						q4++;
						break;
					}
				}
				
				ss += q1;
				ss += "\t";
				ss += q2;
				ss += "\t";
				ss += q3;
				ss += "\t";
				ss += q4;
				ss += "\t\n"; // CATARINA: mete aqui entre o \t e \n a variavel dependente
				
				
				
				/*writer.write(String.valueOf(q1).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q2).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q3).getBytes());
				writer.write("	".getBytes());
				writer.write(String.valueOf(q4).getBytes());
				writer.write("	\n".getBytes());*/
				
			/*} catch (IOException e1) {
				e1.printStackTrace();
			}*/
				
				try {
					writer.write(ss.getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				ss = "";
			
			for(int j = 0; j < restaurants.size(); j++) {
				restaurants.get(j).setNumClients(gen.getClientes());
			}
			
			for(int j = 0; j < drivers.size(); j++) {
				drivers.get(j).setNumClients(gen.getClientes());
			}
		
			//System.out.println("NUMEROS DE PEDIDOS = " + clients.size());
		
			for(int j = 0; j < drivers.size(); j++) {
				AgentController ac0;
				try {
					ac0= mainContainer.acceptNewAgent(drivers.get(j).getDriverName(), drivers.get(j));
					ac0.start();
				}
				catch (StaleProxyException e) {
					e.printStackTrace();
				}		
			}
		
			for(int j = 0; j < restaurants.size(); j++) {
				AgentController ac0;
				try {
					ac0= mainContainer.acceptNewAgent(restaurants.get(j).getRestaurantName(), restaurants.get(j));
					ac0.start();
				}
				catch (StaleProxyException e) {
					e.printStackTrace();
				}		
			}
		
			for(int j=0; j < clients.size();j++) {
				AgentController ac0;
				try {
					ac0= mainContainer.acceptNewAgent(clients.get(j).getClientName(), clients.get(j));
					ac0.start();
				}
				catch (StaleProxyException e) {
					e.printStackTrace();
				}		
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}