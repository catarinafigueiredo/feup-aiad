package UberEats;
// deve ler os ficheiros de clientes restaurants e drivers

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import java.util.ArrayList;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class EcoSystem {
	
	//private ContainerController mainContainer;
	
	/*private ArrayList<Drivers> drivers;
	private ArrayList<Client> clients;
	private ArrayList<Restaurant> restaurants;*/

	public static void main(String[] args) {
		ParseFiles parseFiles= new ParseFiles();
		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		//p1.setParameter(...);
		ContainerController mainContainer = rt.createMainContainer(p1);
		
		//Profile p2 = new ProfileImpl();
		//p2.setParameter(...);
		//ContainerController container = rt.createAgentContainer(p2);

		
		ArrayList<Drivers> drivers = new ArrayList<Drivers>();
		ArrayList<Client> clients= new ArrayList<Client>();
		ArrayList<Restaurant> restaurants= new ArrayList<Restaurant>();
		//fazer for por cada agente 
		drivers=parseFiles.parseDrivers();
		clients=parseFiles.parseClients();
		restaurants=parseFiles.parseRestaurants();
		
		
		for(int i=0; i < drivers.size();i++) {
			AgentController ac0;
			try {
				ac0= mainContainer.acceptNewAgent(drivers.get(i).getDriverName(), drivers.get(i));
				ac0.start();
			}catch (StaleProxyException e) {
				e.printStackTrace();
			}		
		}
		for(int i=0; i < restaurants.size();i++) {
			AgentController ac0;
			try {
				ac0= mainContainer.acceptNewAgent(restaurants.get(i).getRestaurantName(), restaurants.get(i));
				ac0.start();
			}catch (StaleProxyException e) {
				e.printStackTrace();
			}		
		}
		for(int i=0; i < clients.size();i++) {
			AgentController ac0;
			try {
				ac0= mainContainer.acceptNewAgent(clients.get(i).getClientName(), clients.get(i));
				ac0.start();
			}catch (StaleProxyException e) {
				e.printStackTrace();
			}		
		}
		
		
		/*AgentController ac1;
		try {
			
			ac1 = mainContainer.acceptNewAgent("name1", new Agent()); // por o tipo de agente
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		Object[] agentArgs = new Object[0];
		AgentController ac2;
		try {
			ac2 = container.createNewAgent("name2", "jade.core.Agent", agentArgs);
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}*/
	}

}


