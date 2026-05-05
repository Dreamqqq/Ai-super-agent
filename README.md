# Bai AI Agent - AI 智能体后端

基于 **Spring Boot 3.4 + Spring AI 1.1** 的 AI 智能体后端服务，支持多模型对话、RAG 检索增强生成、MCP 工具调用及 Manus 风格自主智能体。

---

## 技术栈

| 类别 | 技术 |
|------|------|
| **基础框架** | Spring Boot 3.4.4, Java 21 |
| **AI 框架** | Spring AI 1.1.2, Spring AI Alibaba 1.1.2 |
| **大模型** | 阿里云 DashScope（百练/灵积）、中转站 GPT-5.4、Ollama 本地模型 |
| **向量存储** | PostgreSQL + PGVector（HNSW 索引） |
| **RAG** | 文档加载、Token 级文本分割、多查询扩展、上下文增强 |
| **MCP** | Spring AI MCP Client（stdio 协议） |
| **文档生成** | iText 9.x（PDF 生成，含亚洲字体支持） |
| **API 文档** | SpringDoc OpenAPI + Knife4j |
| **工具库** | Lombok, Hutool, jsoup, Kryo 序列化 |

---

## 项目结构

```
src/main/java/com/bailou/aiagent/
├── BaiAiAgentApplication.java    # 应用入口
├── agent/                        # AI 智能体实现
│   ├── BaseAgent.java            # 抽象基类 — 状态管理与执行循环
│   ├── ReActAgent.java           # ReAct（推理+行动）模式智能体
│   ├── ToolCallAgent.java        # 工具调用智能体
│   └── BaiManus.java             # Manus 风格多步骤自主智能体
├── app/
│   └── LoveApp.java              # 「恋爱大师」AI 聊天应用
├── chatmemory/
│   └── FileBasedChatMemory.java  # 基于文件的会话记忆（Kryo 序列化）
├── config/
│   ├── ChatModelConfig.java      # 对话模型配置（DashScope/中转站/Ollama）
│   ├── EmbeddingModelConfig.java # 向量嵌入模型配置
│   └── CorsConfig.java           # 跨域配置
├── controller/
│   ├── AiController.java         # AI 对话 API（/api/ai/**）
│   └── HealthController.java     # 健康检查
├── rag/                          # RAG 检索增强生成
│   ├── LoveAppDocumentLoader.java             # Markdown 知识文档加载
│   ├── MyTokenTextSplitter.java               # 自定义 Token 文本分割器
│   ├── LoveAppContextualQueryAugmenterFactory.java  # 查询上下文增强
│   ├── LoveAppRagCloudAdvisorConfig.java      # Cloud RAG 顾问配置
│   ├── LoveAppRagCustomAdvisorFactory.java    # 自定义 RAG 顾问工厂
│   ├── QueryRewriter.java                     # 查询重写器
│   └── MyKeywordEnricher.java                 # 关键词增强
├── tools/                        # 工具箱
│   ├── ToolRegistration.java     # 工具自动注册（Spring AI ToolCallback）
│   ├── WebSearchTool.java        # 网页搜索
│   ├── WebScrapingTool.java      # 网页抓取
│   ├── FileOperationTool.java    # 文件读写
│   ├── PDFGenerationTool.java    # PDF 生成
│   ├── TerminalOperationTool.java # 终端命令执行
│   ├── TerminateTool.java        # 任务终止控制
│   └── ResourceDownloadTool.java # 资源下载
├── advisor/
│   ├── MyLoggerAdvisor.java      # 自定义日志顾问
│   └── ReReadingAdvisor.java     # 重读改进顾问
└── demo/                         # 示例代码
    ├── invoke/                   # 多方式调用示例（Spring AI / LangChain4j / HTTP / SDK）
    └── rag/                      # RAG 示例
```

---

## 核心功能

### 1. AI 智能体 (Agent)

采用 **ReAct（Reasoning + Acting）** 模式实现多步骤自主智能体：

| 智能体 | 说明 |
|--------|------|
| `BaseAgent` | 抽象基类，定义状态机（IDLE → RUNNING → FINISHED/ERROR）、执行循环、SSE 流式输出 |
| `ReActAgent` | 思考-行动-观察循环，支持工具调用与结果推理 |
| `ToolCallAgent` | 基于 Spring AI Tool Calling 的智能体 |
| `BaiManus` | Manus 风格智能体，可自主规划多步骤任务，支持流式 SSE 输出 |

### 2. 「恋爱大师」AI 聊天应用

- 基于 RAG 的知识库问答系统，加载恋爱关系 Markdown 知识文档（单身篇、恋爱篇、已婚篇）
- 支持 **同步调用**、**SSE 流式**、**SseEmitter** 多种输出模式
- 文件持久化会话记忆，支持多轮对话上下文

### 3. RAG 检索增强生成

```
文档加载 → Token 分割 → 向量化存储(PGVector) → 查询增强 → 相似度检索 → 上下文注入
```

- 支持 Markdown 文档自动加载与分割
- 多查询扩展：从原始问题生成多个子查询，提高检索召回率
- 上下文增强与查询重写
- PGVector HNSW 索引 + COSINE_DISTANCE 相似度计算

### 4. MCP 工具扩展

通过 Spring AI MCP Client 接入外部工具服务（如图片搜索），配置在 `mcp-servers.json` 中。

### 5. 内置工具集

| 工具 | 功能 |
|------|------|
| `WebSearchTool` | 网页搜索引擎 |
| `WebScrapingTool` | HTML 内容抓取与解析 |
| `FileOperationTool` | 本地文件读写操作 |
| `PDFGenerationTool` | PDF 文档生成（支持中文） |
| `TerminalOperationTool` | 终端命令执行 |
| `ResourceDownloadTool` | 网络资源下载 |

---

## 快速开始

### 环境要求

- **JDK 21+**
- **Maven 3.8+**
- **PostgreSQL** + PGVector 扩展（可选，用于 RAG 功能）

### 环境变量配置

```bash
export CHAT_API_KEY=your-chat-api-key       # 对话模型 API Key
export EMBEDDING_API_KEY=your-embedding-key  # 向量模型 API Key
export SEARCH_API_KEY=your-search-api-key   # 搜索 API Key
```

### 启动项目

```bash
# 克隆项目
git clone <repo-url>
cd bai-ai-agent-master

# 编译运行
mvn clean package -DskipTests
java -jar target/yu-ai-agent-0.0.1-SNAPSHOT.jar

# 或直接 Maven 运行
mvn spring-boot:run
```

默认启动端口：**8123**，上下文路径：**/api**

### API 文档

启动后访问 Swagger UI：

```
http://localhost:8123/api/swagger-ui.html
```

---

## API 接口

### AI 对话

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/ai/love_app/chat/sync` | 同步对话（恋爱大师） |
| GET | `/api/ai/love_app/chat/sse` | SSE 流式对话 |
| GET | `/api/ai/love_app/chat/server_sent_event` | ServerSentEvent 流式 |
| GET | `/api/ai/love_app/chat/sse_emitter` | SseEmitter 流式 |
| GET | `/api/ai/manus/chat` | Manus 超级智能体流式对话 |

**请求参数：** `message` - 用户消息，`chatId` - 会话标识

### 健康检查

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 健康检查 |

---

## 架构设计

```
┌─────────────────────────────────────────────────┐
│                   Controller 层                   │
│   AiController (SSE/Sync)  │  HealthController   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│                  Application 层                   │
│   LoveApp (RAG Chat)  │  BaiManus (Agent)        │
└──────┬────────────────────────────┬──────────────┘
       │                            │
┌──────▼────────┐    ┌──────────────▼─────────────┐
│   Agent 层     │    │        RAG 层               │
│ BaseAgent     │    │ DocumentLoader / Splitter  │
│ ReActAgent    │    │ QueryAugmenter / Rewriter  │
│ ToolCallAgent │    │ PGVector Store             │
└──────┬────────┘    └──────────────┬─────────────┘
       │                            │
┌──────▼────────────────────────────▼─────────────┐
│                  Infrastructure                   │
│  ChatModel │ EmbeddingModel │ MCP Client        │
│  ChatMemory │ Tools │ Advisors                  │
└─────────────────────────────────────────────────┘
```

---

## 配置说明

主配置文件：`application.yml`

```yaml
spring:
  ai:
    chat:
      api-key: ${CHAT_API_KEY}       # 对话模型密钥
      base-url: https://ai.huaibao.top  # 中转站地址
      model: gpt-5.4
    embedding:
      api-key: ${EMBEDDING_API_KEY}  # 向量模型密钥
      base-url: https://router.tumuer.me
      model: text-embedding-3-small
    mcp:
      client:
        stdio:
          servers-configuration: classpath:mcp-servers.json

server:
  port: 8123
  servlet:
    context-path: /api
```

- 本地开发使用 `application-local.yml` 覆盖生产配置
- 敏感信息通过环境变量注入，不在配置文件中硬编码

---

## 开发说明

- **排除自动配置**：项目手动排除了 DashScope 和 Ollama 的自动配置，通过 `ChatModelConfig` 手动装配，以便更灵活地管理模型路由
- **会话记忆**：使用 Kryo 序列化将会话历史持久化到本地文件，重启后不丢失对话上下文
- **文档分割**：自定义 `MyTokenTextSplitter` 实现 Token 级别的精确文本切割，而非简单的字符数切割
- **多模型支持**：可同时配置 DashScope、OpenAI 兼容中转站、Ollama 本地模型，按需切换

---

## Docker 部署

```bash
# 构建镜像
docker build -t bai-ai-agent .

# 运行容器
docker run -d \
  -p 8123:8123 \
  -e CHAT_API_KEY=your-key \
  -e EMBEDDING_API_KEY=your-key \
  -e SEARCH_API_KEY=your-key \
  bai-ai-agent
```
