package edu.asu.cse.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;


public class read_from_json_url {
	
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      //System.out.println(jsonText);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	
	public static void main(String[] args) throws IOException, JSONException {
	    JSONObject json = readJsonFromUrl("http://dbpedia.org/data/James_Dean.json");
	    //System.out.println(json.toString());
	    json = (JSONObject) json.get("http://dbpedia.org/resource/James_Dean");
	    System.out.println(json);
	    JSONArray arr = (JSONArray) json.get("http://dbpedia.org/property/almaMater");
	    //System.out.println(arr);
	    String name = (String) ((JSONObject) arr.get(0)).get("value");
	    //System.out.println(json.get("http://dbpedia.org/property/almaMater"));
	    //System.out.println(name);
	    
	    try{
		    HttpResponse<JsonNode> response = Unirest.post("https://twinword-lemmatizer1.p.mashape.com/extract/")
		    		.header("X-Mashape-Key", "r2iAzEK2ilmshMy6isqkHL9j8UiJp1XMo3ojsn1IMggY2xD7DK")
		    		.header("Content-Type", "application/x-www-form-urlencoded")
		    		.header("Accept", "application/json")
		    		.field("text", "When was the comet founded ?")
		    		.asJson();
		    //System.out.println(response.getBody());
		    }
		    
		    catch(Exception e){
		    	
		    	e.printStackTrace();
		    }
	    
	    
	    
	    
	  }

}
