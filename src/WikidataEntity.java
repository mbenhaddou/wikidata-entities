import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WikidataEntity extends SearchResult {

	private WDClaims claims= null;
	public List<String> Aliases= null;
	public String type;
	public Map<String, String> hierarchy= null;
	public String wikipediaTitle=null;
	public String wikisource=null;
	
	public WikidataEntity(SearchResult searchResult){
		 this.repository=searchResult.repository;
		 this.id=searchResult.id;
		 this.concepturi=searchResult.concepturi;
		 this.url=searchResult.url;
		 this.title=searchResult.title;
		 this.pageid=searchResult.pageid;
		 this.label=searchResult.label;
		 this.description=searchResult.description;
		 this.aliases=searchResult.aliases;
		 
		 claims = new  WDClaims ();
	}
	
	public WikidataEntity() {
		 claims = new  WDClaims ();
		 Aliases= new ArrayList<String>();
	}

	public WDClaims getClaims()
	{
		return claims;
	}
	public void addClaim(WDClaim claim)
	{
		claims.addClaim(claim);
	}
	
	public List<String> getParents()
	{
		List<String> parents = new ArrayList<String>();
		List<WDClaim> parentsClaims=  claims.getClaimWithProperty("P31");
		if(parentsClaims==null)
			parentsClaims=  claims.getClaimWithProperty("P279");
		if(parentsClaims==null)
			return null;
		if(claims!=null)
		{
			for(WDClaim claim : parentsClaims)
			{
				parents.add(claim.Value);
			}
		}
	
		if(parents.isEmpty())
			return null;
		return parents;
	}
	
	public Map<String, String> getHierarchy() throws URISyntaxException
	{
		if(hierarchy== null)
			hierarchy=WikidataAPI.getHierarchy(id);
		
		
		return hierarchy;
	}
	
	public  String getParentLabel() throws JsonProcessingException, URISyntaxException, IOException
	{
		List<String> parents=getParents();
		String parent = null;
		if(parents==null)
			return null;
		try {
			for(String p : parents)
			{
				parent=getHierarchy().get(p);
				if(parent!=null)
					break;
			}
		//	parent=getHierarchy().get(getParents().get(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(parent==null)
			parent=WikidataAPI.getEntitylabel(parents.get(0));
		return parent;
	}

	public List<String> getAllProperties()
	{
		List<String> properties= new ArrayList<String>();
		for(WDClaim claim: this.getClaims().filteredClaims())
			if(!properties.contains(claim.Property))
				properties.add(claim.Property);
		return properties;
	}
	
	public List<String> getAllClaimValues()
	{
		List<String> values= new ArrayList<String>();
		for(WDClaim claim: this.getClaims().filteredClaims())
			if(!values.contains(claim.Value))
				values.add(claim.Value);
		return values;
	}
}

