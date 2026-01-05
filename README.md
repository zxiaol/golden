# 货架电商网站

一个基于Vue 3和Spring Boot的响应式电商网站，支持PC端和移动端。

## 技术栈

### 前端
- Vue 3
- Vue Router
- Pinia
- Element Plus
- Axios
- Vite

### 后端
- Spring Boot 3.1.5
- MyBatis
- MySQL
- Spring Security
- JWT

## 功能特性

- ✅ 用户注册/登录
- ✅ 商品浏览（列表、详情、分类筛选）
- ✅ 购物车管理
- ✅ 订单管理
- ✅ 商品评价
- ✅ 管理后台（商品管理、订单管理）
- ✅ 图片上传
- ✅ 响应式设计（支持PC和移动端）

## 项目结构

```
golden/
├── backend/          # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/golden/
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── service/       # 业务逻辑
│   │   │   │   ├── mapper/        # 数据访问
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── config/        # 配置类
│   │   │   │   └── util/         # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/       # MyBatis映射文件
│   │   │       ├── db/           # 数据库脚本
│   │   │       └── application.yml
│   └── pom.xml
└── frontend/        # Vue前端
    ├── src/
    │   ├── views/      # 页面组件
    │   ├── components/ # 公共组件
    │   ├── router/     # 路由配置
    │   ├── store/      # 状态管理
    │   ├── api/        # API接口
    │   └── App.vue
    └── package.json
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+

### 后端启动

1. 创建数据库
```sql
-- 执行 backend/src/main/resources/db/schema.sql
```

2. 修改配置文件
编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce?...
    username: your_username
    password: your_password
```

3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端服务将在 http://localhost:3000 启动

### 构建生产版本

前端构建：
```bash
cd frontend
npm run build
```

构建产物在 `frontend/dist` 目录

## API接口

### 认证相关
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/login` - 用户登录

### 用户相关
- GET `/api/user/profile` - 获取用户信息
- PUT `/api/user/profile` - 更新用户信息

### 商品相关
- GET `/api/products` - 商品列表
- GET `/api/products/{id}` - 商品详情
- POST `/api/products` - 创建商品（管理员）
- PUT `/api/products/{id}` - 更新商品（管理员）
- DELETE `/api/products/{id}` - 删除商品（管理员）

### 分类相关
- GET `/api/categories` - 分类列表

### 购物车相关
- GET `/api/cart` - 获取购物车
- POST `/api/cart` - 添加商品到购物车
- PUT `/api/cart/{id}` - 更新购物车商品数量
- DELETE `/api/cart/{id}` - 删除购物车商品

### 订单相关
- POST `/api/orders` - 创建订单
- GET `/api/orders` - 订单列表
- GET `/api/orders/{id}` - 订单详情
- PUT `/api/orders/{id}/status` - 更新订单状态

### 评价相关
- GET `/api/reviews/product/{productId}` - 获取商品评价
- POST `/api/reviews` - 提交评价

### 文件上传
- POST `/api/upload/image` - 上传图片

## 数据库表结构

- users - 用户表
- categories - 商品分类表
- products - 商品表
- cart_items - 购物车表
- orders - 订单表
- order_items - 订单项表
- reviews - 商品评价表

详细表结构请查看 `backend/src/main/resources/db/schema.sql`

## 注意事项

1. 文件上传路径配置在 `application.yml` 中的 `file.upload.path`，需要确保该目录存在且有写权限
2. JWT密钥配置在 `application.yml` 中的 `jwt.secret`，生产环境请修改为强密钥
3. 前端API请求通过代理转发到后端，配置在 `frontend/vite.config.js` 中

## 开发说明

- 后端使用MyBatis进行数据持久化，SQL映射文件在 `resources/mapper` 目录
- 前端使用Pinia进行状态管理，用户信息存储在 `store/user.js`
- 响应式设计使用Element Plus的栅格系统和CSS媒体查询实现

## License

MIT

