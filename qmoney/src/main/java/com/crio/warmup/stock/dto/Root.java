package com.crio.warmup.stock.dto;

public class Root{ 
   
  private String symbol;
  private float quantity;
  private String tradeType;
  private String purchaseDate;
 
 
  // Getter Methods 
 
  public String getSymbol() {
   return symbol;
  }
 
  public float getQuantity() {
   return quantity;
  }
 
  public String getTradeType() {
   return tradeType;
  }
 
  public String getPurchaseDate() {
   return purchaseDate;
  }
 
  // Setter Methods 
 
  public void setSymbol(String symbol) {
   this.symbol = symbol;
  }
 
  public void setQuantity(float quantity) {
   this.quantity = quantity;
  }
 
  public void setTradeType(String tradeType) {
   this.tradeType = tradeType;
  }
 
  public void setPurchaseDate(String purchaseDate) {
   this.purchaseDate = purchaseDate;
  }


}

