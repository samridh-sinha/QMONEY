
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.Root;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectmapper = getObjectMapper();  
    File inputFile = resolveFileFromResources(args[0]); 
    Root[] root = objectmapper.readValue(inputFile, Root[].class);
    List<String> startDate = new ArrayList<>();   
    List<String> symbols = new ArrayList<>(); 
    for(Root r : root){
      symbols.add(r.getSymbol()); 
      startDate.add(r.getPurchaseDate());
    }  
    List<TotalReturnsDto> trdto = new ArrayList<>(); 
    for(int i=0;i<symbols.size();i++){
      String url = "https://api.tiingo.com/tiingo/daily/"+symbols.get(i)+"/prices?startDate="+startDate.get(i)+"&endDate="+args[1]+"&token=d024f195f4bf14d2e6975d6641266bfa5cd2e7d0"; 
      RestTemplate restTemplate = new RestTemplate(); 
      TiingoCandle[] tiingoCandle =  restTemplate.getForObject(url, TiingoCandle[].class);   
      int last = tiingoCandle.length-1;  
      TotalReturnsDto totalReturns = new TotalReturnsDto(null,0.0);  
       totalReturns.setClosingPrice(tiingoCandle[last].getClose()); 
       totalReturns.setSymbol(symbols.get(i)); 
       trdto.add(totalReturns); 

    }  
    Collections.sort(trdto, new Comparator<TotalReturnsDto>() {

      @Override
      public int compare(TotalReturnsDto arg0, TotalReturnsDto arg1) {
        
        if(arg0.getClosingPrice()==arg1.getClosingPrice()) 
          return 0; 
        else if(arg0.getClosingPrice()>arg1.getClosingPrice()) 
          return -1; 
        return 1;  
        
      }
    });
    List<String> sortedSymbol = new ArrayList<>();  
    List<String> prices = new ArrayList<>();
    for(TotalReturnsDto tr : trdto){
      sortedSymbol.add(tr.getSymbol()); 
      
    } 
  
    
     return sortedSymbol;
  } 

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException, JsonMappingException, JsonParseException {
    ObjectMapper object = getObjectMapper();  
    File inputFile = resolveFileFromResources(args[0]); 
    List<String> trades = object.readValue(inputFile,List.class); 
    ObjectMapper objectmapper = getObjectMapper();    
    Root[] root = objectmapper.readValue(inputFile, Root[].class);
    List<String> symbols = new ArrayList<>(); 
    for(Root r : root){
      symbols.add(r.getSymbol());
    }
    return symbols; 
    
  }  

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  } 
  
  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }
  
  
  
  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
       String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/samridh863-ME_QMONEY/qmoney/bin/main/trades.json";
       String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@2f9f7dcf";
       String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
       String lineNumberFromTestFileInStackTrace = "22"; 
       return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainReadQuotes(args));


  }


}

