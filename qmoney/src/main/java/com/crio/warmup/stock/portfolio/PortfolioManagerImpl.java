
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {



  RestTemplate restTemplate;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException { 
      String url = buildUri(symbol, from, to);
      TiingoCandle[] tiingocandle = restTemplate.getForObject(url, TiingoCandle[].class); 
      List<Candle> list = new ArrayList<>(); 

      for(TiingoCandle tc : tiingocandle){
        list.add(tc);
      } 
      
     return list;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) { 
      String startdate = startDate.toString(); 
      String enddate = endDate.toString();
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
            + "startDate="+startdate+"&endDate="+enddate+"&token=d024f195f4bf14d2e6975d6641266bfa5cd2e7d0"; 
      return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws JsonProcessingException
       {
    List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>(); 
    List<Candle> candleList = new ArrayList<>();
    for(PortfolioTrade pt : portfolioTrades){
      candleList = getStockQuote(pt.getSymbol(), pt.getPurchaseDate(), endDate); 
      int last = candleList.size()-1; 
      Double buyPrice = candleList.get(0).getOpen(); 
      Double sellPrice = candleList.get(last).getClose(); 
      Double totalReturn = (sellPrice-buyPrice)/buyPrice;  
      LocalDate startDate = pt.getPurchaseDate(); 
      Double years = ((double)ChronoUnit.DAYS.between(startDate,endDate))/365.0; 
      Double annualizedResturns = Math.pow((1+totalReturn), (1.0/years))-1.0;  
      AnnualizedReturn anRet = new AnnualizedReturn(pt.getSymbol(),annualizedResturns,totalReturn); 
      annualizedReturnsList.add(anRet);
      
    }
    Comparator<AnnualizedReturn> comp = getComparator(); 
    Collections.sort(annualizedReturnsList, comp);
    return annualizedReturnsList;
  }
}
