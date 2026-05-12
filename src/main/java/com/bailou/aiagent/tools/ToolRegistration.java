package com.bailou.aiagent.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 集中的工具注册类
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Autowired(required = false)
    private ToolCallbackProvider toolCallbackProvider;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        
        ToolCallback[] manualTools = ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool
        );
        
        if (toolCallbackProvider == null) {
            return manualTools;
        }
        
        ToolCallback[] mcpTools = toolCallbackProvider.getToolCallbacks();
        
        ToolCallback[] allTools = new ToolCallback[manualTools.length + mcpTools.length];
        System.arraycopy(manualTools, 0, allTools, 0, manualTools.length);
        System.arraycopy(mcpTools, 0, allTools, manualTools.length, mcpTools.length);
        
        return allTools;
    }
}
