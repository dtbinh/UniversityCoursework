//we don't deal with stock
//we don't take any user input
import java.io.*;

public class Runner {

	public static void badInput(){ 
		System.out.println("\nPlease provide correct inputs");
	}

	public static void placeOrder(Database database) throws IOException {

		DistributionCentre tmp = null;
		boolean goodInputs = true;
		String sku = null, quantity = null, distc = null;
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(System.in));

		do { //gets product SKU with error checking
			if(!goodInputs)
				badInput();            
			System.out.println("\nAvailable Products:");
			System.out.println("\t(1020982648): Ariel");
			System.out.println("\t(1002350932): Pampers");
			System.out.println("\t(1023600676): Febreeze");
			System.out.println("\t(1043984673): Wella");
			System.out.println("\t(1043986790): Clairol");
			System.out.println("\t(1043980694): Head & Shoulders");
			System.out.println("\t(1043980535): Pantene");
			System.out.println("\t(1002374056): Olay");
			System.out.println("\t(1056700145): Crest");
			System.out.println("\t(1002670743): Vicks");
			System.out.println("\t(1048000457): Gillette Mach 3");
			System.out.println("\t(1048000458): Gillette Fusion");
			System.out.println("\t(1023100679): Duracell AA Batteries");
			System.out.println("\t(1035620983): Hugo Boss");
			System.out.println("\t(1035620560): Ghost");
			System.out.println("\t(1035620340): Lacoste");
			System.out.println("\t(1035620214): Max Factor");
			System.out.print("Select from the above SKU: ");
			sku = reader.readLine();
			goodInputs = database.checkSKU(sku);
		} while(!goodInputs);

		do { //gets quantity with error checking -- we assume that all input quantities are correct
			if(!goodInputs)
				badInput();
			System.out.print("\nInput quantity of pallets: ");
			quantity = reader.readLine();
			int tmp2 = 0;
			goodInputs = true;

			try {
				tmp2 = Integer.parseInt(quantity);
			} catch (Exception e) {
				goodInputs = false;
			}
			if(tmp2 < 1)
				goodInputs = false;


		} while(!goodInputs);

		do { //gets distribution centre with error checking
			if(!goodInputs)
				badInput();
			System.out.println("\nAvailable Distribution Centres:");
			for (int i = 0; i < Database.dc.size(); i++) {
				Database.dc.get(i).print();
			}
			System.out.print("\nInput Distribution Centre Code: ");
			distc = reader.readLine();
			tmp = Database.checkDC(distc);
			goodInputs = true;
			if (tmp == null) {
				goodInputs = false;
			}
		} while(!goodInputs);

		Order placeorder = new Order(sku, quantity, tmp);
		Alert alert = new Alert();
		alert.alertPlant(placeorder, Database.plant);
	
}

public static void checkOrder(Database database) {
	//Iterates through the plants, and prints the list of orders currently in the plant's queue

	for (int i = 0; i < 5; i++) {
		System.out.println("\n" + Database.plant.get(i).getName());
		Database.plant.get(i).print();
	}
	System.out.println("\n");
}

public static void minorAlert(Database database) throws IOException {
	//This function sends a minorAlert to a plant
	//A minorAlert is defined as an error sufficient to terminate the current production.

	boolean goodInputs = true;
	String input;
	BufferedReader reader = null;
	reader = new BufferedReader(new InputStreamReader(System.in));
	int inp = 0;
	int i = -1;
	//Get which plant the user wants, with error checking
	do {
		if(!goodInputs)
			badInput();

		System.out.println("\nAvailable Plants:");
		System.out.println("\t(811): London");
		System.out.println("\t(812): Manchester");
		System.out.println("\t(813): Newcastle");
		System.out.println("\t(814): Reading");
		System.out.println("\t(815): Seaton Deleval");
		System.out.print("Input Plant Code: ");
		input = reader.readLine();

		goodInputs = true;
		try {
			inp = Integer.parseInt(input);
		} catch (Exception e) {
			goodInputs = false;
		}
		if(inp < 811 || inp > 815)
			goodInputs = false;
	} while(!goodInputs);

	switch (inp) {
	case 811:
		i = 0;
		break;
	case 812:
		i = 1;
		break;
	case 813:
		i = 2;
		break;
	case 814:
		i = 3;
		break;
	case 815:
		i = 4;
		break;
	default:
		break;
	}

	if (i >= 0) {
		Alert alert = new Alert();
		alert.sendMinorAlert(Database.plant.get(i));
	}
	System.out.println();
}

public static void majorAlert(Database database) throws IOException {
	//This function sends a majorAlert to a plant
	//A majorAlert is defined as an error sufficient to terminate the current production. 

	boolean goodInputs = true;
	String input;
	BufferedReader reader = null;
	reader = new BufferedReader(new InputStreamReader(System.in));
	int inp = 0;
	int i = -1;

	//Get which plant the user wants, with error checking	
	do {
		if(!goodInputs)
			badInput();

		System.out.println("\nAvailable Plants:");
		System.out.println("\t(811): London");
		System.out.println("\t(812): Manchester");
		System.out.println("\t(813): Newcastle");
		System.out.println("\t(814): Reading");
		System.out.println("\t(815): Seaton Deleval");
		System.out.print("Input Plant Code: ");
		input = reader.readLine();

		goodInputs = true;
		try {
			inp = Integer.parseInt(input);
		} catch (Exception e) {
			goodInputs = false;
		}
		if(inp < 811 || inp > 815)
			goodInputs = false;
	} while(!goodInputs);

	switch (Integer.parseInt(input)) {//Choose which plant
	case 811:
		i = 0;
		break;
	case 812:
		i = 1;
		break;
	case 813:
		i = 2;
		break;
	case 814:
		i = 3;
		break;
	case 815:
		i = 4;
		break;
	default:
		break;
	}

	if (i >= 0) {
		//Create a new alert to the plant class and send a major alert to be used during the production
		Alert alert = new Alert();
		alert.sendMajorAlert(Database.plant.get(i));
		System.out.println("\n\tWe were forced to terminate the production at Plant: " + Database.plant.get(i).getName() + "\n");
	}
}

public static void menu(Database database) throws IOException {
	String input = null;
	BufferedReader reader = null;
	reader = new BufferedReader(new InputStreamReader(System.in));

	System.out.println("(1) Place an Order");
	System.out.println("(2) Check on Orders");
	System.out.println("(3) Send Minor Alert");
	System.out.println("(4) Send Major Alert");
	System.out.println("(5) Exit");

	System.out.print("Select an option: ");
	input = reader.readLine();

	if (input.equals("1")) {
		placeOrder(database);
	} else if (input.equals("2")) {
		checkOrder(database);
	} else if (input.equals("3")) {
		minorAlert(database);
	} else if (input.equals("4")) {
		majorAlert(database);
	} else if (input.equals("5")) {
		System.exit(0);
	} else {
		badInput();
		System.out.println();
	}
}

public static void main(String[] args) throws IOException {
	Database database = new Database();

	while (true) {
		menu(database); //calls menu until given input to exit
	}
}
}
