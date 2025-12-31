package com.javaaidev.usdafdc.mcp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.javaaidev.usdafdc.api.FdcApi;
import com.javaaidev.usdafdc.client.auth.ApiKeyAuth;
import com.javaaidev.usdafdc.mcp.cache.FileCacheService;
import com.javaaidev.usdafdc.mcp.tool.FoodNutrientTool;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import java.util.List;
import java.util.Objects;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  public ServerCapabilities.Builder serverCapabilitiesBuilder() {
    return ServerCapabilities.builder()
        .tools(true);
  }

  @Bean
  public FileCacheService fileCacheService() {
    return new FileCacheService();
  }

  @Bean
  public FdcApi fdcApi() {
    var fdcApi = new FdcApi();
    var auth = fdcApi.getApiClient().getAuthentication("ApiKeyAuth");
    if (auth instanceof ApiKeyAuth apiKeyAuth) {
      apiKeyAuth.setApiKey(
          Objects.requireNonNullElse(System.getenv("UDSA_FDC_API_KEY"), "DEMO_KEY"));
    }
    return fdcApi;
  }

  @Bean
  public ObjectMapper objectMapper() {
    return JsonMapper.builder()
        .addModules(JacksonUtils.instantiateAvailableModules())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
  }

  @Bean
  public FoodNutrientTool foodNutrientTool(FdcApi fdcApi, FileCacheService fileCacheService,
      ObjectMapper objectMapper) {
    return new FoodNutrientTool(fdcApi, fileCacheService, objectMapper);
  }

  @Bean
  public List<SyncToolSpecification> tools(FoodNutrientTool foodNutrientTool) {
    return List.of(
        foodNutrientTool.asTool()
    );
  }
}
