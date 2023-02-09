import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonItemDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonStatement;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonValueSnak;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WDExtract {
	private static WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();

	public static WikidataEntity ExtractEntity(SearchResult entity) throws MediaWikiApiErrorException
	{
		WikidataEntity wikidataEntity= new WikidataEntity(entity);
		
		EntityDocument entityDoc= wbdf.getEntityDocument(entity.id);
		
		Map<String, List<JacksonStatement>>  claims = ((JacksonItemDocument) entityDoc).getJsonClaims();
		
		for (String key : claims.keySet()) {
			List<JacksonStatement> statements=claims.get(key);			

			JacksonValueSnak mainsnak = (JacksonValueSnak) statements.get(0).getMainsnak();
			
			WDClaim claim= new WDClaim();
			
			claim.Property=key;
			claim.Value=mainsnak.getValue().toString();
			claim.DataType=mainsnak.getDatatype();
			
			wikidataEntity.getClaims().addClaim(claim);

		}
		return wikidataEntity;
		
	}
	
	public static WikidataEntity ExtractEntity(String entity) throws MediaWikiApiErrorException
	{
		WikidataEntity wikidataEntity= new WikidataEntity();
		
		EntityDocument entityDoc= wbdf.getEntityDocument(entity);
		Map<String, List<JacksonStatement>>  claims = ((JacksonItemDocument) entityDoc).getJsonClaims();
		
		for (String key : claims.keySet()) {
			List<JacksonStatement> statements=claims.get(key);			

			JacksonValueSnak mainsnak = (JacksonValueSnak) statements.get(0).getMainsnak();
			
			WDClaim claim= new WDClaim();
			
			claim.Property=key;
			claim.Value=mainsnak.getValue().toString();
			claim.DataType=mainsnak.getDatatype();
			
			wikidataEntity.getClaims().addClaim(claim);

		}
		return wikidataEntity;
		
	}	
	
}
