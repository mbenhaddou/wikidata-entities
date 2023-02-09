import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import utils.Pair;
public class PropToCategoryMapper {
	HashMap<String, Pair<String, String>> propertyMap = new HashMap<String, Pair<String, String>>();
	
	public PropToCategoryMapper() throws FileNotFoundException, IOException
	{
		String prpertiesFIlePath="data/wikidata_properties_per_entity.txt";
		
		try (BufferedReader br = new BufferedReader(new FileReader(prpertiesFIlePath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		     String[] array = line.split("\t");
		     Pair<String, String> p = new Pair<String, String>(array[0], array[1]);
		     propertyMap.put(array[3], p);
		    
		    }
		}
	}
	
	
	public Pair<String, String> getCategoriesForProperty(String propertyId)
	{		
		return propertyMap.get(propertyId);
	}

	
}
