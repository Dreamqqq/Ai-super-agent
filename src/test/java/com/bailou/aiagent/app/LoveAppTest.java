package com.bailou.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpStdioClientProperties;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private McpStdioClientProperties mcpStdioClientProperties;

    @Resource(name = "mcpSyncClients")
    private List<Object> mcpSyncClients;

    @Test
    void testListMcpTools() {
        // 检查 MCP 工具是否被加载
        System.out.println("========== MCP 工具列表 ==========");
        var tools = toolCallbackProvider.getToolCallbacks();
        System.out.println("MCP 工具数量: " + tools.length);
        for (int i = 0; i < tools.length; i++) {
            System.out.println("工具 " + (i + 1) + ": " + tools[i].toString());
        }
        System.out.println("==============================");
        Assertions.assertNotNull(tools);
    }

    @Test
    void testListMcpRelatedBeans() {
        // 打印所有 MCP 相关的 Bean
        System.out.println("========== MCP 相关的 Bean ==========");
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanNames)
                .filter(name -> name.toLowerCase().contains("mcp"))
                .forEach(name -> {
                    Object bean = applicationContext.getBean(name);
                    System.out.println("Bean: " + name + " = " + bean.getClass().getName());
                });
        System.out.println("==============================");
    }

    @Test
    void testCheckMcpStdioClientProperties() {
        // 检查 MCP stdio 客户端配置
        System.out.println("========== MCP stdio 客户端配置 ==========");
        System.out.println("MCP stdio 客户端配置: " + mcpStdioClientProperties);
        System.out.println("servers-configuration: " + mcpStdioClientProperties.getServersConfiguration());
        System.out.println("==============================");
    }

    @Test
    void testCheckMcpSyncClients() {
        // 检查 MCP sync 客户端
        System.out.println("========== MCP sync 客户端 ==========");
        System.out.println("MCP sync 客户端数量: " + mcpSyncClients.size());
        for (int i = 0; i < mcpSyncClients.size(); i++) {
            System.out.println("客户端 " + (i + 1) + ": " + mcpSyncClients.get(i).toString());
        }
        System.out.println("==============================");
    }

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员白楼";
        String answer = loveApp.doChat(message, chatId);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是程序员白楼，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的樱花树下情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘芜湖七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
//        String message = "我的另一半居住在江西赣县区，请帮我找到 5 公里内合适的约会地点";
//        String answer =  loveApp.doChatWithMcp(message, chatId);
//        Assertions.assertNotNull(answer);
        // 测试图片搜索 MCP
        String message = "Help me search for some pictures of cats to make my partner happy.";
        String answer =  loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
