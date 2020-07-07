## 项目结构介绍
#### auth
security 鉴权配置
#### config
* 基础bean生成
* 全局异常处理
* 静态字符串定义
#### dto
* 传输层对象
#### filter
拦截器
* AuthenticationRetrieveFilter 用户解析拦截
* ResourceAccessFilter 资源权限拦截
####  resource
接口权限判断工具
* user 用户权限控制 基于资源 code 码 (存储于 redis 中)
* client 资源权限控制 基于请求路径 (存储于数据库中)
#### util
工具类
* RouteUtils 用于从请求中获取服务名
* RSAUtils 用户解析 Rsa 公钥于私钥
