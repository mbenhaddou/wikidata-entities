import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


public class SearchResults {
	private List<SearchResult> searchResults = null;
	private List<SearchResult> cleanedResults = null;
	private HashSet<String> stopAfter = null;
	private HashMap<String, String> classestoRemove = null;
	private WDictionaries dics= null;
	public String Query;
	private List<Entity> entities= null;
	public SearchResults() throws URISyntaxException
	{
		dics = new WDictionaries();
		searchResults = new LinkedList<SearchResult>();
		cleanedResults = new LinkedList<SearchResult>();
		stopAfter = new HashSet<String>();
		classestoRemove = new HashMap<String, String>();
		classestoRemove.put("Q4167410", "Wikimedia Disambiguation Page");
		classestoRemove.put("Q17633526", "Wikinews article");
		classestoRemove.put("Q21286738", "duplicated page");
		classestoRemove.put("Q101352", "family name");
		classestoRemove.put("Q13442814", "Scientific article");
		
		stopAfter.add("Q19361238"); //wikidata metaclass
		stopAfter.add("Q19361238");
		stopAfter.add("Q24017465");//second order metaclass
		stopAfter.add("Q19361238");//third order meta class
		stopAfter.add("Q24027515"); //fourth order metaclass
		stopAfter.add("Q24027526"); //fixed order metaclass of higer order
		stopAfter.add("Q23958852"); //variable order metaclass
		stopAfter.add("Q23959932"); //fixed orderr metaclass
		stopAfter.add("Q502895"); //common name
	}
	

	public void addSearchResult(SearchResult searchResult) {
		searchResults.add(searchResult);
	}

	public List<SearchResult> getSearchResults() {
		return searchResults;
	}
		  
	public List<String> getSearchLabels() {
		List<String> labels = new ArrayList<String>(searchResults.size());
		for (SearchResult l : searchResults) {
		   labels.add(l.label);
		}
		return labels;
	}
	
	public List<Entity> getEntities() throws JsonParseException, JsonMappingException, JSONException, URISyntaxException, IOException
	{
		if(entities!=null)
			return entities;
		
		entities = new LinkedList<Entity>();
		Entity entity=null;
		for( SearchResult s : searchResults)
		{
			boolean classToRemove=false;
			WikidataEntity wikiEntity= WikidataAPI.getWikidataEntity(s.id);
			List<String> parents = wikiEntity.getParents();
			//remove candidates that are from unwanted categories
			if(parents != null)
				for(String parent : parents )
				{
					if(classestoRemove.containsKey(parent)) 
						classToRemove=true;
				}
			//remove candidates that have no parent and that are only articles on wikisource
			if(parents==null)
			{
			//	if(wikiEntity.label.equals(wikiEntity.wikisource))
					continue;
			}
			//Remove candidates without label
			if(wikiEntity.id.equals(wikiEntity.label)|| classToRemove)
				continue;
			
			boolean startRemoving = false;
			Iterator<Map.Entry<String,String>> iter = wikiEntity.getHierarchy().entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<String,String> entry = iter.next();
			  
				if(startRemoving)
				{
					iter.remove();
					continue;
				}
				if (stopAfter.contains(entry.getKey()))
				{
					startRemoving=true;
				}
			}
			if(wikiEntity.getParents()==null)
			{
				entity= new Entity(wikiEntity);
			}
			else 	if(wikiEntity.getParents().contains("Q5"))
			{
				entity =  new PersonEntity(wikiEntity);
			    ((PersonEntity) entity).OccupationLabel= dics.Occupations.get( ((PersonEntity) entity).OccupationId);
			}
			else
			{
				 entity= new Entity(wikiEntity);
	
			}
			if(entity.Label!=null)
			if(entity.Label.toLowerCase().equals(Query.toLowerCase()))
				entities.add(entity);
			else if(entity.getWikidataEntity().Aliases.contains(Query))
				entities.add(entity);
			else if(Arrays.asList(entity.Label.split(" ")).contains(Query))
				entities.add(entity);
			else{
				List<WDClaim> shortnames =entity.getWikidataEntity().getClaims().getClaimWithProperty("P1813");
				if(shortnames!=null)					
					for(WDClaim claim: shortnames)
						if(claim.Value.equals(Query))
						{
							entities.add(entity);
							break;
						}
			}
		}
		
		return entities;
	}
		  
}
