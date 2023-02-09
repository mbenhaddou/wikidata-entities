import java.net.URISyntaxException;
import java.util.Map;

public class WDictionaries {
	
	public Map<String, String> Occupations =null;
	

	public WDictionaries() throws URISyntaxException
	{
		Occupations = WikidataAPI.getInstances("Q28640");	
	}	

}
