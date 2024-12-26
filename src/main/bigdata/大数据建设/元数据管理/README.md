# 元数据管理




## 元数据管理平台
### 常见的开源平台
1. Apache Atlas：Apache Atlas是一个开源的大数据元数据管理和数据治理平台，旨在帮助组织收集、整理和管理数据的元数据信息。它提供了丰富的元数据模型和搜索功能，可以与各种数据存储和处理平台集成。

2. LinkedIn DataHub：LinkedIn DataHub是LinkedIn开源的元数据搜索和发现平台。它提供了一个集中式的元数据存储库，用于管理和浏览各种类型的数据集和数据资产的元数据信息。

3. Amundsen：Amundsen是Lyft开源的数据发现和元数据管理平台。它提供了一个用户友好的界面，使用户可以搜索、浏览和贡献数据集的元数据信息。Amundsen还支持与其他数据工具和平台的集成。

4. Metacat：Metacat是Netflix开源的数据发现和元数据管理平台。它提供了一个统一的接口来查找和浏览各种数据集的元数据信息，并支持与其他数据工具和服务的集成。



### OpenMetadata介绍
github地址：[https://github.com/open-metadata/OpenMetadata](https://github.com/open-metadata/OpenMetadata)
#### 
OpenMetadata 包括以下内容：
- **元数据模式**： 使用类型、实体和实体之间关系的模式定义元数据的核心抽象和词汇。这是开放元数据标准的基础。还支持具有自定义属性的实体和类型的可扩展性。
- **元数据存储**： 存储连接数据资产、用户和工具生成的元数据的元数据图。
- **元数据API** - 用于生成和使用基于用户界面模式以及工具、系统和服务集成构建的元数据。
- **摄取框架**： 用于集成工具并将元数据摄取到元数据存储的可插入框架，支持大约 55 个连接器。摄取框架支持众所周知的数据仓库，如 Google BigQuery、Snowflake、Amazon Redshift 和 Apache Hive；MySQL、Postgres、Oracle 和 MSSQL 等数据库；Tableau、Superset 和 Metabase 等仪表板服务；消息服务，如 Kafka、Redpanda；以及 Airflow、Glue、Fivetran、Dagster 等管道服务。
- **OpenMetadata用户界面**： 用户发现所有数据并就所有数据进行协作的单一位置。

#### 核心功能
- **数据协作**：通过活动源获取事件通知。使用 webhook 发送警报和通知。添加公告以通知团队即将发生的更改。添加任务以请求描述或术语表术语批准工作流程。添加用户提及并使用对话线程进行协作。
- **数据质量和分析器**： 标准化测试和数据质量元数据。将相关测试分组为测试套件。支持自定义SQL数据质量测试。有一个交互式仪表板可以深入了解详细信息。
- **数据血缘**： 支持丰富的列级沿袭。有效过滤查询以提取沿袭。根据需要手动编辑谱系，并使用无代码编辑器连接实体。
- **全面的角色和策略**： 处理复杂的访问控制用例和分层团队。
- **连接器**： 支持连接到各种数据库、仪表板、管道和消息传递服务的 55 个连接器。
- **术语表**： 添加受控词汇来描述组织内的重要概念和术语。添加词汇表、术语、标签、描述和审阅者。
- **数据安全**： 支持 Google、Okta、自定义 OIDC、Auth0、Azure、Amazon Cognito 和 OneLogin 作为 SSO 的身份提供商。此外，还支持 AWS SSO 和 Google 基于 SAML 的身份验证。

#### 安装
使用docker
```
# 环境要求 Python (version 3.7, 3.8 or 3.9)
python --version
# 环境要求 Docker (version 20.10.0 or greater)
docker --version
# 环境要求 Docker Compose (version v2.1.1 or greater)
docker compose version


# 创建python虚拟环境
conda create -n openmetadata_py39 python=3.9
# 激活python虚拟环境
conda activate openmetadata_py39
# 创建目录
mkdir openmetadata-docker && cd openmetadata-docker
# 安装openmetadata
pip install --upgrade "openmetadata-ingestion[docker]"
# 确认安装成功
metadata docker --help
# 启动
metadata docker --start
#启动postgre
metadata docker --start -db postgres
```

一切就绪后访问：http://localhost:8585

