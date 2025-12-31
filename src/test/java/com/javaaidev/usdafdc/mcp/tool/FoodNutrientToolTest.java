package com.javaaidev.usdafdc.mcp.tool;

import static org.junit.jupiter.api.Assertions.fail;

import com.javaaidev.usdafdc.api.FdcApi;
import com.javaaidev.usdafdc.client.auth.ApiKeyAuth;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FoodNutrientToolTest {

  @Autowired
  FoodNutrientTool foodNutrientTool;

  @Test
  void testQueryFood() {
    try {
      var results = foodNutrientTool.getNutrient("Cheddar cheese");
      System.out.println(results);
    } catch (Exception e) {
      fail(e);
    }
  }

  private FdcApi createFdcApi() {
    var fdcApi = new FdcApi();
    var auth = fdcApi.getApiClient().getAuthentication("ApiKeyAuth");
    if (auth instanceof ApiKeyAuth apiKeyAuth) {
      apiKeyAuth.setApiKey(
          Objects.requireNonNullElse(System.getenv("UDSA_FDC_API_KEY"), "DEMO_KEY"));
    }
    return fdcApi;
  }
}