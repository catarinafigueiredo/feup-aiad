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
		
		//System.out.println("Inicializacao dos Agentes --------------------------------");
		
		for(int i = 0; i < drivers.size(); i++) {
			AgentController ac0;
			try {
				ac0= mainContainer.acceptNewAgent(drivers.get(i).getDriverName(), drivers.get(i));
				ac0.start();
			}
			catch (StaleProxyException e) {
				e.printStackTrace();
			}		
		}
		
		for(int i = 0; i < restaurants.size(); i++) {
			AgentController ac0;
			try {
				ac0= mainContainer.acceptNewAgent(restaurants.get(i).getRestaurantName(), restaurants.get(i));
				ac0.start();
			}
			catch (StaleProxyException e) {
				e.printStackTrace();
			}		
		}
		
		for(int i=0; i < clients.size();i++) {
			AgentController ac0;
			try {
				ac0= mainContainer.acceptNewAgent(clients.get(i).getClientName(), clients.get(i));
				ac0.start();
			}
			catch (StaleProxyException e) {
				e.printStackTrace();
			}		
		}
	}
	
}