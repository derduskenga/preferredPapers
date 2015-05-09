package models.orders;


import java.util.List;
import java.util.ArrayList;
import play.*;
import play.mvc.*;
import models.utility.Utilities;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import static play.data.Form.form;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Iterator;
import java.util.Set;

public class StaticData{
  public static Map<String,Boolean> getStyles(){
    Map<String,Boolean> styleMap = new HashMap<String,Boolean>();
    for(int i= 0; i<Utilities.STYLES.length;i++){
      styleMap.put(Utilities.STYLES[i],false);
    }
    return new TreeMap<String,Boolean>(styleMap);
  }
  
  public static Map<String,Boolean> getStylesForErrorForm(String style){
    Map<String,Boolean> styleMap = new HashMap<String,Boolean>();
    for(int i= 0; i<Utilities.STYLES.length;i++){
      if(Utilities.STYLES[i].equalsIgnoreCase(style)){
	 styleMap.put(Utilities.STYLES[i],true);
      }else{
	 styleMap.put(Utilities.STYLES[i],false);
      }
     
    }
    return new TreeMap<String,Boolean>(styleMap);
  }

  public static Map<String,Boolean> getLanguages(){
    Map<String,Boolean> languageMap = new HashMap<String,Boolean>();
    for(int i=0;i<Utilities.PROGRAMMING_LANGUAGES.length;i++){
      languageMap.put(Utilities.PROGRAMMING_LANGUAGES[i],false);
    }
    return new TreeMap<String,Boolean>(languageMap);
  }

  public static Map<String,Boolean> getDatabase(){
    Map<String,Boolean> databaseMap = new HashMap<String,Boolean>();
    for(int i=0;i<Utilities.DATABASE.length;i++){
      databaseMap.put(Utilities.DATABASE[i],false);
    }
    return new TreeMap<String,Boolean>(databaseMap);
  }

  public static Map<Integer,Boolean> getReferenceCount(){
    Map<Integer,Boolean> refMap = new HashMap<Integer,Boolean>();
    for(int i=1;i<=Utilities.NUMBER_OF_EFERENCES;i++){
      refMap.put(i,false);
    }
    return new TreeMap<Integer,Boolean>(refMap);
  }
}