import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WDClaims {
	private List<WDClaim> claims = null;


	public WDClaims()
	{
		claims = new LinkedList<WDClaim>();
	}
	
	public void addClaim(WDClaim claim) {
		claims.add(claim);
	}

	public List<WDClaim> getSearchResults() {
		return claims;
	}
		  
	public List<WDClaim> filteredClaims()
	{
		return claims;
		
	}
	
	public boolean ContainsProperty(String property)
	{
		for(WDClaim  c :claims)
		{
			if (c.Property.equals(property))
				return true;
		}
		return false;
	}
	
	public List<WDClaim> getClaimWithProperty(String property)
	{
		List<WDClaim> normalClaims = new ArrayList<WDClaim>();
		List<WDClaim> preferredClaims = new ArrayList<WDClaim>();
		List<WDClaim> bestClaims = new ArrayList<WDClaim>();
		for(WDClaim  c :claims)
		{
			if (c.Property.equals(property))
			{
				switch(c.ClaimRank)
				{
				case BEST:
					bestClaims.add(c);
					break;
				case PREFERRED:
					preferredClaims.add(c);
					break;
				case NORMAL:
					normalClaims.add(c);
					break;
				default:
					normalClaims.add(c);
					break;
				}
			}
				
		}
		
		if(!bestClaims.isEmpty())
			return bestClaims;
		else if(!preferredClaims.isEmpty())
			return preferredClaims;
		else if(!normalClaims.isEmpty())
			return normalClaims;
		else
		   return null;
	}
}
