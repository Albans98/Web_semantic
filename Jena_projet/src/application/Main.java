package application;
import java.lang.Runtime;
import java.util.List;
import java.util.Scanner;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import tools.JenaEngine;

import java.io.*;


/**
*
* @author Alban STEFF & Soumaya SABRY --- A4 -S8 IBO5
*/


public class Main {

	public static int getNumber(int max_value, Scanner reader) 
	{
		int n = -1;
		try {
			n = reader.nextInt();
			while(n > max_value || n < 0) {
				System.out.println("Wrong value, try again :");
				n = reader.nextInt();
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return n;
	}	
	public static void update()
	{
		try {
			Process p = Runtime.getRuntime().exec("python ./TempsReel.py", null , new File("..\\data"));
			System.out.print("Updating data ...... ");
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	public static void case_selection(Model model, Scanner reader, String NS, String city, String choice)
	{

		if(city == "Rennes") {
			System.out.println(JenaEngine.executeQueryFile(model,
					"data/query"+choice+"2.txt"));
		}
		else 
		{
			System.out.println(JenaEngine.executeQueryFileWithParameter(model,"data/query"+choice+"1.txt", city));
			
		}

		System.out.println("Write the ID of the station you want to get information about !");
		int n = getNumber(999999999, reader);
		String num = Integer.toString(n);
  		System.out.println("ID : " + num);
		JenaEngine.readDataType(model, NS, num, "name");
		JenaEngine.readDataType(model, NS, num, "status");
		JenaEngine.readDataType(model, NS, num, "available_docks");
		JenaEngine.readDataType(model, NS, num, "available_bikes");
		JenaEngine.readDataType(model, NS, num, "capacity");
		if(city != "Rennes") JenaEngine.readDataType(model, NS, num, "ville");
		else System.out.println("ville : Rennes\n");
	}
	public static void decalage(String value , String[] tab,int index)
	{
		for (int i = tab.length -1 ; i > index;i--)
		{
			tab[i]=tab[i-1];
		}
		tab[index]=value;
	}
	public static void decalage(double value, double[] tab, int index) {
		for (int i = tab.length -1 ; i > index;i--)
		{
			tab[i]=tab[i-1];
		}
		tab[index]=value;
		
	}
	public static void FunctionCorrdonate(Model model, Scanner reader, String NS)
	{
		System.out.print("Please,Entre your Latitude:");
		double lat = reader.nextDouble();
		System.out.print("Please,Entre your Longitude:");
		double lon = reader.nextDouble();
		List<Resource> stations= JenaEngine.getALLsatation(model,NS);
		String TopStation[] = new String[5];
		double TopStationDist[] = new double[5];
		for (int j = 0; j < TopStationDist.length; j++){TopStationDist[j]=100;}
		for (int i = 0; i < stations.size(); i++)
		{
			double temp= JenaEngine.getdistance(model, NS, lat, lon, stations.get(i));
			for (int j = 0; j < TopStationDist.length; j++)
			{
				if (temp < TopStationDist[j]){
					decalage(temp,TopStationDist,j);
					decalage(stations.get(i).getProperty(model.getProperty(NS+"name")).getString(),TopStation,j);
					break;
					}
			}
		}
		
		System.out.println("Here the top 5 near station");
		for (int j = 0; j < TopStation.length; j++)
		{
			System.out.println(j+1 +". '"+TopStation[j]+"' avec une distance de "+Math.round(TopStationDist[j]* 1000)/1000.0 +" m");
		}
		//JenaEngine.readDataType(model, NS, num, "coordinate");
		
	}

	public static void main(String[] args) {

		Scanner reader = new Scanner(System.in);
		System.out.println("Welcome to this bike rentals application !");
		String NS = "";
		int loop = 1;
		String villeLyon[] = {"Lyon"						,"OULLINS"							,"VILLEURBANNE"				,"VAULX-EN-VELIN"		,
							  "SAINT-GENIS-LAVAL"			,"CALUIRE-ET-CUIRE"					,"COLLONGES-AU-MONT-D'OR"	,"BRON"					,
							  "ECULLY"						,"SAINT-FONS","SAINT-PRIEST"		,"NEUVILLE-SUR-SAONE"		,"RILLIEUX-LA-PAPE"		,
							  "SAINT-DIDIER-AU-MONT-D'OR"	,"ALBIGNY-SUR-SAONE","PIERRE-BENITE","SAINT-CYR-AU-MONT-D'OR"	,"TASSIN-LA-DEMI-LUNE"	,
							  "FONTAINES-SUR-SAONE"			,"SAINTE-FOY-LES-LYON","LA MULATIERE","COUZON-AU-MONT-D'OR"		};
		
		String villeParis[]={ "Paris"  				,"Montreuil"           ,"Choisy-le-Roi"       ,"Neuilly-sur-Seine"   ,
				 			  "Levallois-Perret"    ,"Malakoff"            ,"Fontenay-sous-Bois"  ,"Vitry-sur-Seine"     ,
				 			  "Issy-les-Moulineaux" ,"Saint-Maurice"       ,"La Garenne-Colombes" ,"Montrouge"           ,
				 			  "Pantin"              ,"Boulogne-Billancourt","Suresnes"            ,"Vanves"              ,
				 			  "Bagneux"             ,"Gentilly"            ,"Clichy"              ,"Fontenay-aux-Roses"  ,
				 			  "Saint-Denis"         ,"Nogent-sur-Marne"    ,"Villejuif"           ,"Vincennes"           ,
				 			  "Romainville"         ,"Les Lilas"           ,"Chaville"            ,"Colombes"            ,
				 			  "Ivry-sur-Seine"      ,"Nanterre"            ,"Champigny-sur-Marne" ,"Rosny-sous-Bois"     ,
				 			  "Courbevoie"          ,"Aubervilliers"       ,"Bagnolet"            ,"Sceaux"              ,
				 			  "Clamart"             ,"Cachan"              ,"Maisons-Alfort"      ,"Charenton-le-Pont"   ,
				 			  "Le Prￃﾩ-Saint-Gervais","Alfortville"         ,"Joinville-le-Pont"   ,"Asniￃﾨres-sur-Seine"  ,
				 			  "Meudon"              ,"Arcueil"             ,"Saint-Mandￃﾩ"         ,"Saint-Cloud"         ,"Rueil-Malmaison"     };
		while(loop == 1)
		{   
			Model model = JenaEngine.readModel("../data/stations-velos.owl");
			NS = model.getNsPrefixURI("");
			
			//temps-reel
			update();
			
			JenaEngine.addRDF("../data/Paris.rdf", model);
			JenaEngine.addRDF("../data/Lyon.rdf", model);
			JenaEngine.addRDF("../data/Rennes.rdf", model);
			Model owlInferencedModel = JenaEngine.readInferencedModelFromRuleFile(model,
																"data/owlrules.txt");
			
			
			//Menu STRAT
			
			System.out.println("Here, you can have access to bike stations information");
			System.out.println("You want to 1 . choose your city  OR  2.write your coordinate");
			int n = getNumber(2, reader);
			if(n == 1) 
			{
				System.out.println("Please, choose your city ");
				System.out.println("1. Grand Paris    2. Grand Lyon    3. Rennes  ");
				
				n = getNumber(4, reader);
				String city = "";
				if(n == 1) 
				{
					for (int i = 0; i < villeParis.length; i++)
					{
						System.out.println( i+1 +". "+villeParis[i]);
					}
					n = getNumber(villeParis.length, reader);
					city = villeParis[n-1];
				}
				if(n == 2)
				{
					for (int i = 0; i < villeLyon.length; i++)
					{
						System.out.println( i+1 +". "+villeLyon[i]);
					}
					n = getNumber(villeLyon.length, reader);
					city = villeLyon[n-1];
				}
				if(n == 3) city = "Rennes";
				System.out.println(city);
				
				System.out.println("How do you want to display the data ?");
				System.out.println("1. No filter (ALL)    2. Filter by available docks    3. Filter by available bikes");
				
				n = getNumber(3, reader);
				switch(n) {
				case 1:
					case_selection(owlInferencedModel,reader,NS, city, "1");
					break;
				case 2:
					case_selection(owlInferencedModel,reader,NS, city, "2");
					break;
				case 3:
					case_selection(owlInferencedModel,reader,NS, city, "3");
					break;
				}
			}
			if(n == 2)
			{
				FunctionCorrdonate(owlInferencedModel,reader,NS);
			}
			System.out.println("Press 1 to start again or 2 to leave :");
			loop = getNumber(2, reader);
			System.out.println("-------------------------------------------------------------------------");
			System.out.println("-------------------------------------------------------------------------");
		}
		System.out.println("Thank you and goodbye !");
		System.out.println("-------------------------------------------------------------------------");
		reader.close();
		
	}
}
