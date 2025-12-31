package com.javaaidev.usdafdc.mcp.tool;

public class NoFoodFoundException extends RuntimeException {

  public NoFoodFoundException(String query) {
    super("No food found for query: " + query);
  }
}
