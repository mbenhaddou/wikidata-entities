public class WDClaim {

	public String Property;
	public String Value;
	public String DataType;
	public String ValueType;
	public Rank ClaimRank;
	public String getValue(){
		if(DataType.equals("wikibase-item")){
			String[] splited=Value.split("/");
			return splited[splited.length-1].split(" ")[0];
		}
		else
			return Value;
	}
	
	}
