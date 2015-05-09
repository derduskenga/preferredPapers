package models.orders;

import java.util.Map;
import java.util.HashMap;


public class TempStore{
  //fields
  private static Map<Map<Long,String>,Boolean> documentDeadlines;
  private static Map<Map<Long,String>,Boolean> documentSubjects;
  private static Map<Map<Long,String>,Boolean> numberOfUnits;
 
  public TempStore(){}
  //setters
  public static void setDocumentDeadlines(Map<Map<Long,String>,Boolean> docDeadlines){
      documentDeadlines =  docDeadlines;
  }
  
   public static void setDocumentSubjects(Map<Map<Long,String>,Boolean> docSubjects){
      documentSubjects =  docSubjects;
  }
  //map structure
  //map(map(select value, select string),select label).
  public static void setnumberOfUnitsMap(Map<Map<Long,String>,Boolean> numOfUnits){
    numberOfUnits = numOfUnits;
  }
  
  //getters
  public static Map<Map<Long,String>,Boolean> getDocumentDeadlines(){
    return documentDeadlines;
  }
  
   public static Map<Map<Long,String>,Boolean> getDocumentSubjects(){
    return documentSubjects;
  }
  
  public static Map<Map<Long,String>,Boolean> getNumberOfUnits(){
    return numberOfUnits;
  }
}