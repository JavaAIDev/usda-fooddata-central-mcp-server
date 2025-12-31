# USDA FoodData Central MCP Server

[![build](https://github.com/JavaAIDev/usda-fooddata-central-mcp/actions/workflows/build.yaml/badge.svg)](https://github.com/JavaAIDev/usda-fooddata-central-mcp/actions/workflows/build.yaml)

Use environment variable `UDSA_FDC_API_KEY` to provide an API key. The default `DEMO_KEY` is used when not provided.

The config below starts the server using a container image.

```json
{
  "mcpServers": {
    "amap": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "-e",
        "UDSA_FDC_API_KEY=YOUR_AMAP_API_KEY",
        "ghcr.io/javaaidev/usda-fooddata-central-mcp-server:1.0.0"
      ]
    }
  }
}
```