package UberEats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ParseFiles {
	public void parseDrivers() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("drivers.txt"));

			String line = reader.readLine();
			while (line != null) {
				String[] arr = line.split("/");
				String[] arr2 = arr[1].split("-");

				// substituir prints por criacao da classe
				System.out.println(arr[0]);
				System.out.println(arr2[0]);
				System.out.println(arr2[1]);
				System.out.println(arr[2]);
				line = reader.readLine();
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseClients() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("clients.txt"));

			String line = reader.readLine();
			while (line != null) {
				String[] arr = line.split("/");
				String[] arr2 = arr[1].split("-");

				// substituir prints por criacao da classe
				System.out.println(arr[0]);
				System.out.println(arr2[0]);
				System.out.println(arr2[1]);
				System.out.println(arr[2]);
				System.out.println(arr[3]);
				line = reader.readLine();
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseRestaurants() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("restaurants.txt"));

			String line = reader.readLine();
			while (line != null) {
				String[] arr = line.split("/");
				String[] arr2 = arr[1].split("-");
				String[] arr3 = arr[3].split(";");

				// substituir prints por criacao da classe
				System.out.println(arr[0]);
				System.out.println(arr2[0]);
				System.out.println(arr2[1]);
				System.out.println(arr[2]);

				for (int i = 0; i < arr3.length; i++) {
					String[] temp = arr3[i].split("-");
					System.out.println(temp[0]);
					System.out.println(temp[1]);
				}
				
				line = reader.readLine();
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
