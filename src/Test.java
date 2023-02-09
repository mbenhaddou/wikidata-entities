import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Test {

	public static void main(String[] args) throws URISyntaxException, JsonParseException, JsonMappingException, IOException, MediaWikiApiErrorException, JSONException {
		
	//	WDictionaries dics= new WDictionaries();
				
		String text="Newcastle takeover: After years of 'not-for-sale', Mike Ashley is ready to invite new investment";


		SearchResults newcastl= WikidataAPI.searchEntity("Manchester United", 10);
		SearchResults mike= WikidataAPI.searchEntity("Fergie", 10);
		
		for(Entity result : newcastl.getEntities())
			System.out.println(result.toString()+"\t"+result.Description+"\t"+result.getWikidataEntity().id);
		
		for(Entity result : mike.getEntities())
			System.out.println(result.toString()+"\t"+result.Description);
		
		
		for(Entity e1 : newcastl.getEntities())
		{
			for(Entity e2 : mike.getEntities())
			{
				System.out.println(e1.toString()+"//"+e2.toString()+":\t"+EntitySimilarity(e1, e2));
			}
		}
	}
	
	public static double EntitySimilarity(Entity e1, Entity e2)
	{
		int nbCommonProperties=0;
		int nbPropertiesE1=e1.getWikidataEntity().getClaims().filteredClaims().size();
		int nbPropertiesE2=e2.getWikidataEntity().getClaims().filteredClaims().size();
		
		List<String> e1_claims = new ArrayList<String>(e1.getWikidataEntity().getAllClaimValues());


		e1_claims.retainAll(e2.getWikidataEntity().getAllClaimValues());
		
		nbCommonProperties=e1_claims.size();
		
		System.out.println(e1_claims);
		return nbCommonProperties/(double)(nbPropertiesE1+nbPropertiesE2-nbCommonProperties);
		
	}
	
	
	
}
