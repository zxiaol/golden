# 前端部署说明

## 部署方式

### 方式一：Docker 部署（推荐）

#### 1. 使用项目根目录的 docker-compose.yml（包含前后端）

```bash
# 在项目根目录执行
docker-compose up -d

# 查看日志
docker-compose logs -f frontend

# 停止服务
docker-compose down
```

#### 2. 单独部署前端

```bash
cd frontend

# 构建镜像
docker build -t ecommerce-frontend:1.0.0 .

# 运行容器（需要后端服务在运行）
docker run -d \
  -p 80:80 \
  --name ecommerce-frontend \
  --network ecommerce-network \
  ecommerce-frontend:1.0.0
```

### 方式二：静态文件部署

#### 1. 构建生产版本

```bash
cd frontend

# 安装依赖
npm install

# 构建
npm run build

# 构建产物在 dist/ 目录
```

#### 2. 使用 Nginx 部署

将 `dist/` 目录的内容复制到 Nginx 的 html 目录：

```bash
# 复制构建产物
sudo cp -r dist/* /usr/share/nginx/html/

# 复制 Nginx 配置
sudo cp nginx.conf /etc/nginx/conf.d/ecommerce.conf

# 重启 Nginx
sudo nginx -t  # 测试配置
sudo systemctl restart nginx
```

#### 3. 使用其他静态文件服务器

- **Apache**: 配置虚拟主机，启用 mod_rewrite
- **Caddy**: 自动 HTTPS，配置简单
- **CDN**: 上传到 OSS/CDN（如阿里云 OSS、腾讯云 COS）

## 环境配置

### 开发环境

开发环境使用 Vite 的代理功能，API 请求会自动代理到后端：

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

### 生产环境

生产环境通过 Nginx 反向代理：

```nginx
location /api {
    proxy_pass http://backend:8080;
    # ... 其他配置
}
```

如果需要修改后端地址，编辑 `nginx.conf` 中的 `proxy_pass` 地址。

## 构建优化

### 1. 环境变量配置

可以创建 `.env.production` 文件配置生产环境变量：

```env
VITE_API_BASE_URL=https://api.example.com
```

然后在 `vite.config.js` 中使用：

```javascript
export default defineConfig({
  // ...
  define: {
    'process.env.VITE_API_BASE_URL': JSON.stringify(process.env.VITE_API_BASE_URL)
  }
})
```

### 2. 代码分割

Vite 默认会进行代码分割，可以通过配置优化：

```javascript
build: {
  rollupOptions: {
    output: {
      manualChunks: {
        'vendor': ['vue', 'vue-router', 'pinia'],
        'element-plus': ['element-plus']
      }
    }
  }
}
```

## 常见问题

### 1. 路由 404 问题

确保 Nginx 配置了 Vue Router 的 history 模式支持：

```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

### 2. API 请求跨域

生产环境通过 Nginx 代理解决，不需要配置 CORS。

### 3. 静态资源路径问题

如果部署在子路径下，需要配置 `base`：

```javascript
// vite.config.js
export default defineConfig({
  base: '/ecommerce/',  // 子路径
  // ...
})
```

## 性能优化建议

1. **启用 Gzip 压缩**（已在 nginx.conf 中配置）
2. **静态资源缓存**（已在 nginx.conf 中配置）
3. **CDN 加速**：将静态资源上传到 CDN
4. **图片优化**：使用 WebP 格式，懒加载
5. **代码分割**：按需加载路由组件

