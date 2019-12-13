package UberEats;

/*
 * class EcoSystem
 * 
 * - Responsavel por iniciar a aplicacao. Chama a classe que faz o parse dos dados e inicia os agentes. */

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import java.util.ArrayList;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class EcoSystem {

	public static void main(String[] args) {
		
		// fazer um for com 1000 iteracoes
		for(int i = 0; i < 1000; i++) {
			//System.out.println("ITE " +i+" -------------------------\n");
			
			caseGenerator gen = new caseGenerator(5,20,  5,15,  10,15);
			gen.generateFile();
			
			/*System.out.println("nClientes = " +gen.getClientes());
			System.out.println("nRests = " +gen.getRests());
			System.out.println("nDrivers = " +gen.getDrivers()+"\n");*/
		
		
		ParseFiles parseFiles = new ParseFiles();
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		ContainerController mainContainer = rt.createMainContainer(p1);
		
		ArrayList<Drivers> drivers = new ArrayList<Drivers>();
		ArrayList<Client> clients = new ArrayList<Client>();
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		
		drivers = parseFiles.parseDrivers();
		clients = parseFiles.parseClients();
		restaurants = parseFiles.parseRestaurants();
		
		System.out.println("NUMEROS DE PEDIDOS = " + clients.size());
		
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
		}}
	}
	
}