import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class PersonEntity extends Entity {
	
	public String OccupationId="";
	public String OccupationLabel="";

	public PersonEntity(WikidataEntity entity) throws JsonProcessingException, URISyntaxException, IOException {
		super(entity);
		
		OccupationId=entity.getClaims().getClaimWithProperty("P106").get(0).Value;
		
	}
	
	public String toString()
	{
		return Label+" ("+OccupationLabel+")";
	}

}
