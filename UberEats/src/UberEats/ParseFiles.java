package UberEats;

/*
 * class ParseFiles
 * 
 * - Responsavel por extrair dados dos ficheiros. */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ParseFiles {
	
	public ArrayList<Drivers> parseDrivers() {
		
		ArrayList<Drivers> drivers = new ArrayList<Drivers>();
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader("files/cenariosTesteAiad/generatedCaseD.txt"));
			//reader = new BufferedReader(new FileReader("files/cenariosTesteAiad/drivers7.txt"));

			String line = reader.readLine();
			while (line != null) {
				String[] arr = line.split("/");
				String[] arr2 = arr[1].split("-");
				
				Drivers d= new Drivers(arr[0],Integer.parseInt(arr2[0]),Integer.parseInt(arr2[1]),Integer.parseInt(arr[2]));
				drivers.add(d);
				line = reader.readLine();
			}
			reader.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return drivers;
	}

	public ArrayList<Client> parseClients() {
		
		ArrayList<Client> clients = new ArrayList<Client>();
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader("files/cenariosTesteAiad/generatedCaseC.txt"));
			//reader = new BufferedReader(new FileReader("files/cenariosTesteAiad/clients7.txt"));

			String line = reader.readLine();
			while (line != null) {
				String[] arr = line.split("/");
				String[] arr2 = arr[1].split("-");
				
				Client c= new Client(arr[0],Integer.parseInt(arr2[0]),Integer.parseInt(arr2[1]),arr[2],arr[3],Integer.parseInt(arr[4]));
				clients.add(c);
				line = reader.readLine();
			}
			reader.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return clients;
	}

	public ArrayList<Restaurant> parseRestaurants() {
		
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader("files/cenariosTesteAiad/generatedCaseR.txt"));
			//reader = new BufferedReader(new FileReader("files/cenariosTesteAiad/restaurants7.txt"));

			String line = reader.readLine();
			while (line != null) {
				String[] arr = line.split("/");
				String[] arr2 = arr[1].split("-");
				String[] arr3 = arr[3].split(";");
				
				Hashtable hash= new Hashtable();
				
				for (int i = 0; i < arr3.length; i++) {
					String[] temp = arr3[i].split("-");
					hash.put(temp[0],new Integer(Integer.parseInt( temp[1])));
				}
				
				Restaurant c= new Restaurant(arr[0],Integer.parseInt(arr2[0]),Integer.parseInt(arr2[1]),Integer.parseInt(arr[2]),hash);
				restaurants.add(c);
				line = reader.readLine();
			}
			
			
			reader.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return restaurants;
	}

}