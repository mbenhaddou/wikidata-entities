import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Entity {
	private WikidataEntity wikidataEntity =null;
	
	public String Label="";
	public String CategoryId="";
	public String CategoryLabel="";
	public String Description="";
	public String WikipediaSummary="";
	
	public Entity(WikidataEntity entity) throws JsonProcessingException, URISyntaxException, IOException
	{
		wikidataEntity=entity;
		Label=wikidataEntity.label;
		Description=wikidataEntity.description;
		
		if(wikidataEntity.getParents()!=null)
			CategoryId=wikidataEntity.getParents().get(0);
		CategoryLabel=wikidataEntity.getParentLabel();
		if(wikidataEntity.wikipediaTitle!=null)
			WikipediaSummary= WikidataAPI.getWikipediaSummary(wikidataEntity.wikipediaTitle);
	}
	
	public String toString()
	{
		return Label+" ("+CategoryLabel+")";
	}
	
	public WikidataEntity getWikidataEntity()
	{
		return wikidataEntity;
	}
}
