
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonItemDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class WikidataAPI {
	
	static HttpClient client = new HttpClient();
	
	public static SearchResults searchEntity(String entity, int limit) throws URISyntaxException, JSONException, JsonParseException, JsonMappingException, IOException
	{
		SearchResults searchResults = new SearchResults();
		searchResults.Query=entity;
		//get search results
		String json = SearchEntities(entity, limit);
		
		//convert to JSON
		JSONObject obj = new JSONObject(json);		
		
		JSONArray arr = obj.getJSONArray("search");
		for (int i = 0; i < arr.length(); i++)					
			searchResults.addSearchResult(new Gson().fromJson(arr.getJSONObject(i).toString(), SearchResult.class));
		    
		

		return searchResults;
	}
	
	public static String SearchEntities(String searchTerm, int searchLimit) throws URISyntaxException
	{
		String lang="en";
		String limit=Integer.toString(searchLimit);
		
		
		

		

		   byte[] responseBody=null;
		    
		    URIBuilder uriBuilder = new URIBuilder();
		    uriBuilder.setScheme("https")
		            .setHost("www.wikidata.org")
		            .setPath("/w/api.php")
		            .addParameter("action", "wbsearchentities")
		            .addParameter("search", searchTerm)
		            .addParameter("language", lang)
		            .addParameter("limit", limit)
		            .addParameter("format", "json")
		            .addParameter("uselang", lang)
		            .addParameter("type", "item")     
		            .addParameter("continue", "0");
		    		
		    String uri = uriBuilder.build().toString();


		    
		    // Create a method instance.
		    GetMethod method = new GetMethod(uri);
		    
		    // Provide custom retry handler is necessary
		    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(3, false));

		    try {
		      // Execute the method.
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      // Read the response body.
		      responseBody = method.getResponseBody();
		      
		      
		    } catch (HttpException e) {
		        System.err.println("Fatal protocol violation: " + e.getMessage());
		        e.printStackTrace();
		      } catch (IOException e) {
		        System.err.println("Fatal transport error: " + e.getMessage());
		        e.printStackTrace();
		      } finally {
		        // Release the connection.
		        method.releaseConnection();
		      }
		    
		    return new String(responseBody, StandardCharsets.UTF_8);
		    
	}
	
	public static String GetEntityData(String entityId) throws URISyntaxException
	{
		String lang="en";

		
		   HttpClient client = new HttpClient();
		   byte[] responseBody=null;
		    
		    URIBuilder uriBuilder = new URIBuilder();
		    uriBuilder.setScheme("https")
		            .setHost("www.wikidata.org")
		            .setPath("/w/api.php")
		            .addParameter("action", "wbgetentities")
		            .addParameter("ids", entityId)
		            .addParameter("languages", lang)
		            .addParameter("redirects", "yes")
		            .addParameter("format", "json")
		            .addParameter("props", "sitelinks|descriptions|claims|datatype|aliases|labels");
		    		
		    String uri = uriBuilder.build().toString();


		    
		    // Create a method instance.
		    GetMethod method = new GetMethod(uri);
		    
		    // Provide custom retry handler is necessary
		    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(3, false));

		    try {
		      // Execute the method.
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      // Read the response body.
		      responseBody = method.getResponseBody();
		      
		      
		    } catch (HttpException e) {
		        System.err.println("Fatal protocol violation: " + e.getMessage());
		        e.printStackTrace();
		      } catch (IOException e) {
		        System.err.println("Fatal transport error: " + e.getMessage());
		        e.printStackTrace();
		      } finally {
		        // Release the connection.
		        method.releaseConnection();
		      }
		    
		    return new String(responseBody, StandardCharsets.UTF_8);
		    
	}

	
	public static WikidataEntity getWikidataEntity(String entityId) throws JSONException, URISyntaxException, JsonParseException, JsonMappingException, IOException{
		
	
		ObjectMapper mapper = new ObjectMapper(); 
	    
		WikidataEntity wikidataEntity = new WikidataEntity();
		wikidataEntity.id=entityId;
		String jsonStr=GetEntityData(entityId);
	   
		JsonNode root = mapper.readTree(jsonStr); 
	  
		JsonNode entities = root.path("entities");
		for (JsonNode entityNode : entities) {
			if (!entityNode.has("missing")) {
				JsonNode type=entityNode.get("type");
				wikidataEntity.type=type.toString().replace("\"", "");
				JsonNode label = null;
				if(entityNode.get("labels").get("en")!=null)
					label=entityNode.get("labels").get("en").get("value");
				if(label!=null)
					wikidataEntity.label=label.toString().replace("\"", "");;
				JsonNode descr=entityNode.get("descriptions").get("en");
				
				if(descr!= null)
				{
					descr=descr.get("value");
				    wikidataEntity.description= descr.toString().replace("\"", "");				
				}
				JsonNode aliases=entityNode.get("aliases").get("en");
				if(aliases!=null)
				{
					 for (final JsonNode objNode : aliases) {
						 wikidataEntity.Aliases.add(objNode.get("value").toString().replace("\"", ""));
					    }
				}
				
					
				 JsonNode claims=entityNode.get("claims");
				 
				 for(JsonNode claim: claims)
				 {
					
					 if (claim.isArray()) {
						    for (final JsonNode aclaim : claim) {
						    	String claimType=aclaim.get("type").toString().replace("\"", "");
						    	if(claimType.equals("statement"))
						    	{
						    		WDClaim wdclaim= new WDClaim();
						    		JsonNode mainsnak = aclaim.get("mainsnak");
						    		if(mainsnak.get("snaktype").toString().replace("\"", "").equals("value"))
						    		{
						    			
						    			wdclaim.Property=mainsnak.get("property").toString().replace("\"", "");
						    			wdclaim.DataType=mainsnak.get("datatype").toString().replace("\"", "");
						    			if(wdclaim.DataType.equals("wikibase-property")|| wdclaim.DataType.equals("external-id"))
						    				continue;
						    			JsonNode datavalueNode=mainsnak.get("datavalue");
						    			wdclaim.ValueType=datavalueNode.get("type").toString().replace("\"", "");
						    	//		System.out.println(mainsnak.get("datavalue").toString());
						    			if(wdclaim.ValueType.equals("wikibase-entityid"))
						    				wdclaim.Value=datavalueNode.get("value").get("id").toString().replace("\"", "");
						    			else if(wdclaim.ValueType.equals("monolingualtext"))
						    			{
						    				wdclaim.Value=datavalueNode.get("value").get("text").toString().replace("\"", "");
						    			}
						    			else{
						    				wdclaim.Value=datavalueNode.get("value").toString().replace("\"", "");
						    			}
						    			
						    			
						    		}
						    		else
						    			continue;
						    		JsonNode rank=aclaim.get("rank");
						    		if(rank!=null)
						    			wdclaim.ClaimRank=Rank.valueOf(rank.toString().toString().replace("\"", "").toUpperCase());
						    		wikidataEntity.getClaims().addClaim(wdclaim);
	
						    		}						        		
						    }					 
				 }
			}
			
			JsonNode sitelinks=entityNode.get("sitelinks");
			JsonNode wikiNode= sitelinks.get("enwiki");
			if(wikiNode!=null)
				wikidataEntity.wikipediaTitle=wikiNode.get("title").toString().replace("\"", "");

			JsonNode enwikisourceNode= sitelinks.get("enwikisource");
			if(enwikisourceNode!=null)
				wikidataEntity.wikisource=enwikisourceNode.get("title").toString().replace("\"", "");
			
		}
		
	  //      System.out.println(e.toString());

		}
		return wikidataEntity;
		
		
	}
	
	public static Map<String, String> getHierarchy(String entityId) throws URISyntaxException{
		
		String baseUrl="https://query.wikidata.org/sparql?format=json&query=";
		
		String SPARQL = String.format("SELECT ?item ?itemLabel {wd:%s wdt:P31* ?item SERVICE wikibase:label {bd:serviceParam wikibase:language \"en\".}}", entityId);
		
		
		  
	    URIBuilder uriBuilder = new URIBuilder();
	    uriBuilder.setScheme("https")
	            .setHost("query.wikidata.org")
	            .setPath("/sparql")
	            .addParameter("format", "json")
	            .addParameter("query", SPARQL);
	    
	    String uri = uriBuilder.build().toString();
	    
		 HttpClient client = new HttpClient();
		 GetMethod method = new GetMethod(uri);
		 byte[] responseBody=null;
		  try {
		      // Execute the method.
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      // Read the response body.
		      responseBody = method.getResponseBody();
		  } catch (HttpException e) {
		        System.err.println("Fatal protocol violation: " + e.getMessage());
		        e.printStackTrace();
		      } catch (IOException e) {
		        System.err.println("Fatal transport error: " + e.getMessage());
		        e.printStackTrace();
		      } finally {
		        // Release the connection.
		        method.releaseConnection();
		      }		
				
			String jsonResponse= new String(responseBody, StandardCharsets.UTF_8);
			
			ObjectMapper mapper = new ObjectMapper(); 
			JsonNode root = null;
			try {
				root = mapper.readTree(jsonResponse);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			
			JsonNode json =root.get("results");
			json=json.get("bindings");
			for (JsonNode node: json)
			{
				String[] keyValues=node.get("item").get("value").toString().split("/");
				String key=keyValues[keyValues.length-1];
				JsonNode value=node.get("itemLabel").get("value");
				
				map.put(key.replaceAll("\"", ""), value.toString().replaceAll("\"", ""));
			}
			
			return map;
		
	}
	
	public static Map<String, String> getInstances(String entityId) throws URISyntaxException{
		
		String baseUrl="https://query.wikidata.org/sparql?format=json&query=";
		
		String SPARQL = String.format("SELECT DISTINCT ?item ?itemLabel {?item  wdt:P31/wdt:P279*  wd:%s SERVICE wikibase:label {bd:serviceParam wikibase:language \"en\".}FILTER( EXISTS {?item rdfs:label ?lang_label. FILTER(LANG(?lang_label) = \"en\")})}", entityId);
		
		  
	    URIBuilder uriBuilder = new URIBuilder();
	    uriBuilder.setScheme("https")
	            .setHost("query.wikidata.org")
	            .setPath("/sparql")
	            .addParameter("format", "json")
	            .addParameter("query", SPARQL);
	    
	    String uri = uriBuilder.build().toString();
	    
		 HttpClient client = new HttpClient();
		 GetMethod method = new GetMethod(uri);
		 byte[] responseBody=null;
		  try {
		      // Execute the method.
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      // Read the response body.
		      responseBody = method.getResponseBody();
		  } catch (HttpException e) {
		        System.err.println("Fatal protocol violation: " + e.getMessage());
		        e.printStackTrace();
		      } catch (IOException e) {
		        System.err.println("Fatal transport error: " + e.getMessage());
		        e.printStackTrace();
		      } finally {
		        // Release the connection.
		        method.releaseConnection();
		      }		
				
			String jsonResponse= new String(responseBody, StandardCharsets.UTF_8);
			
			ObjectMapper mapper = new ObjectMapper(); 
			JsonNode root = null;
			try {
				root = mapper.readTree(jsonResponse);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			
			JsonNode json =root.get("results");
			json=json.get("bindings");
			for (JsonNode node: json)
			{
				String[] keyValues=node.get("item").get("value").toString().split("/");
				String key=keyValues[keyValues.length-1];
				JsonNode value=node.get("itemLabel").get("value");
				
				map.put(key.replaceAll("\"", ""), value.toString().replaceAll("\"", ""));
			}
			
			return map;
		
	}

	public static String getWikipediaSummary(String name) throws URISyntaxException, JsonProcessingException, IOException
	{
		 String lang="en";
//		 https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=Lewis%20Hamilton
		 byte[] responseBody=null;
		    
		    URIBuilder uriBuilder = new URIBuilder();
		    uriBuilder.setScheme("https")
		            .setHost(lang+".wikipedia.org")
		            .setPath("/w/api.php")
		            .addParameter("format", "json")
		            .addParameter("action", "query")
		            .addParameter("prop", "extracts")
		            .addParameter("exintro", "")
		            .addParameter("explaintext", "")
		            .addParameter("titles", name);
		    		
		    String uri = uriBuilder.build().toString();


		    
		    // Create a method instance.
		    GetMethod method = new GetMethod(uri);
		    
		    // Provide custom retry handler is necessary
		    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(3, false));

		    try {
		      // Execute the method.
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      // Read the response body.
		      responseBody = method.getResponseBody();
		      
		      
		    } catch (HttpException e) {
		        System.err.println("Fatal protocol violation: " + e.getMessage());
		        e.printStackTrace();
		      } catch (IOException e) {
		        System.err.println("Fatal transport error: " + e.getMessage());
		        e.printStackTrace();
		      } finally {
		        // Release the connection.
		        method.releaseConnection();
		      }
		    
		    String jsonStr= new String(responseBody, StandardCharsets.UTF_8);
		    
		    ObjectMapper mapper = new ObjectMapper(); 
		    JsonNode root = mapper.readTree(jsonStr); 
			  
			JsonNode query = root.path("query").get("pages").elements().next();

			return query.get("extract").toString();
	}

	public static String getEntitylabel(String entityId) throws URISyntaxException, JsonProcessingException, IOException
	{
		String lang="en";	
	   byte[] responseBody=null;
	    
	    URIBuilder uriBuilder = new URIBuilder();
	    uriBuilder.setScheme("https")
	            .setHost("www.wikidata.org")
	            .setPath("/w/api.php")
	            .addParameter("action", "wbgetentities")
	            .addParameter("ids", entityId)
	            .addParameter("languages", lang)
	            .addParameter("format", "json")
	            .addParameter("props", "labels");
	    		
	    String uri = uriBuilder.build().toString();


	    
	    // Create a method instance.
	    GetMethod method = new GetMethod(uri);
	    
	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));

	    try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        System.err.println("Method failed: " + method.getStatusLine());
	      }

	      // Read the response body.
	      responseBody = method.getResponseBody();
	      
	      
	    } catch (HttpException e) {
	        System.err.println("Fatal protocol violation: " + e.getMessage());
	        e.printStackTrace();
	      } catch (IOException e) {
	        System.err.println("Fatal transport error: " + e.getMessage());
	        e.printStackTrace();
	      } finally {
	        // Release the connection.
	        method.releaseConnection();
	      }
	    
	    String jsonStr= new String(responseBody, StandardCharsets.UTF_8);
	    
	    ObjectMapper mapper = new ObjectMapper(); 
	    
	    JsonNode root = mapper.readTree(jsonStr); 
		  
		return root.path("entities").get(entityId).get("labels").get("en").get("value").toString().replace("\"", "");

	}
}
