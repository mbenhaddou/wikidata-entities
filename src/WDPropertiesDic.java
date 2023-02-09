import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WDPropertiesDic {

	Map<String, String> propertyMap =null;
	public WDPropertiesDic(){
		
		propertyMap = new HashMap<String, String>();
		
		File file = new File("data/wikidata_properties.en");
		String content=null;
		try {
			content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ObjectMapper mapper = new ObjectMapper(); 
		JsonNode root = null;
		try {
			root = mapper.readTree(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonNode json =root.get("rows");
		
		for (JsonNode node: json)
		{
			JsonNode key=node.get(0);
			JsonNode value=node.get(1);
			
			propertyMap.put(key.toString().replaceAll("\"", ""), value.toString().replaceAll("\"", ""));
		}
			
		
	}
	

	public String getPropertyName(String property)
	{
		return propertyMap.get(property);
	}	
	public void updateData() throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		 HttpClient client = new HttpClient();
		 GetMethod method = new GetMethod("http://quarry.wmflabs.org/run/45013/output/1/json");
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
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("data/wikidata_properties.en"), "utf-8"))) {
	   writer.write(new String(responseBody, StandardCharsets.UTF_8));
	}
		
	}

}
