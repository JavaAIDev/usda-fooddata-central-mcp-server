package com.javaaidev.usdafdc.mcp.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaaidev.usdafdc.api.FdcApi;
import com.javaaidev.usdafdc.client.ApiException;
import com.javaaidev.usdafdc.mcp.cache.FileCacheService;
import com.javaaidev.usdafdc.model.AbridgedFoodNutrient;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FoodNutrientTool {

  private final FdcApi fdcApi;
  private final FileCacheService fileCacheService;
  private final ObjectMapper objectMapper;

  public FoodNutrientTool(FdcApi fdcApi, FileCacheService fileCacheService,
      ObjectMapper objectMapper) {
    this.fdcApi = fdcApi;
    this.fileCacheService = fileCacheService;
    this.objectMapper = objectMapper;
  }

  public SyncToolSpecification asTool() {
    return SyncToolSpecification.builder()
        .tool(Tool.builder()
            .name("GetFoodNutrient")
            .description("Get nutrients of food by name")
            .inputSchema(new JsonSchema(
                "object",
                Map.of(
                    "query",
                    new JsonSchema("string", Map.of(), List.of(), false, Map.of(), Map.of())
                ),
                List.of("query"),
                false,
                Map.of(),
                Map.of()
            ))
            .build())
        .callHandler((exchange, request) -> {
          var query = (String) request.arguments().get("query");
          try {
            return CallToolResult.builder()
                .addTextContent(objectMapper.writeValueAsString(getNutrient(query)))
                .build();
          } catch (Exception e) {
            return CallToolResult.builder()
                .addTextContent("Failed to get nutrients, error is " + e.getMessage())
                .isError(true)
                .build();
          }
        })
        .build();
  }

  public FoodNutrient getNutrient(String foodName) throws Exception {
    return fileCacheService.runWithCache("nutrition", new Object[]{foodName}, FoodNutrient.class,
        () -> doGetNutrient(foodName));
  }

  private FoodNutrient doGetNutrient(String foodName) throws ApiException {
    var searchResult = fdcApi.getFoodsSearch(foodName, List.of("Branded", "Foundation"), 1, 0,
        null, null, null);
    if (searchResult == null) {
      throw new NoFoodFoundException(foodName);
    }
    var foods = searchResult.getFoods();
    if (foods == null || foods.isEmpty()) {
      throw new NoFoodFoundException(foodName);
    }
    var nutrients = Objects.requireNonNullElse(foods.getFirst().getFoodNutrients(),
        List.<AbridgedFoodNutrient>of());
    return new FoodNutrient(nutrients.stream().map(
        nutrient -> new Nutrient(nutrient.getNutrientId(), nutrient.getNutrientNumber(),
            nutrient.getNutrientName(), nutrient.getUnitName(),
            nutrient.getValue())).toList());
  }
}
