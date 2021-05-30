### 作业1 ###
由于官方提供的demo有些乱，所以按照样例自己实现了一个完整的Rpc Demo，并在作业要求的基础上做了一些改进 
- my-rpc-client模块为Rpc客户端框架，基于AOP实现API调用的拦截和远程调用，aop核心代码为com.geek.RpcClientProxy。  
底层通信采用netty客户端，序列化方式采用protostuff，核心代码为com.geek.RpcClient。目前netty客户端只支持单线程调用，有待改进。
- my-rpc-server模块为Rpc服务端框架。核心代码有com.geek.RpcServer，用于远程服务映射构建和netty服务的启动。远程服务映射是基于@RpcService注解在项目启动时获取到接口限定名和spring管理的业务实现类的映射关系。  
com.geek.RpcHandler作用是通过请求的接口限定名找到业务实现类，然后通过反射调用目标实现类。
- my-rpc-interface模块为客户端应用和服务端应用共享的远程API接口。
- my-rpc-client-app模块为客户端应用，核心代码为com.geek.ClientApplication，用于启动应用和远程api调用测试。
- my-rpc-server-app模块是服务端应用，com.geek.OrderServiceImpl是远程api真实的业务实现，com.geek.ServerApplication是应用启动入口。  

### 作业2 ###  
项目框架主要采用spring boot 2.3.11.RELEASE + Mybatis plus + apache.dubbo 2.7.7 + hmily 2.1.1  
实现的需求是A用户用美元账户的1美元汇到B用户的美元账户，来换取自己人民币账户7元的入账；B用户用人民币账户的7元汇到A用户的人民币账户，来换取自己美元账户的1美元。  
A用户的转出转入逻辑实现在banka服务中，B用户的转出转入逻辑实现在bankb服务中，中间业务协调者的逻辑在trade-center服务中。  
总体调用链为先是web层入口（因为API网关略去,所以trade-center放一起），然后web调用trade-center的本地service，本地service再先后调用banka和bankb的远程Api来完成业务。  
具体代码说明如下：
- 框架和业务所需的建表建库sql请参见dubbo-hmily.sql
- dubbo-hmily-banka-api模块为banka客户端和服务端共用的远程API
- dubbo-hmily-bankb-api模块为bankb客户端和服务端共用的远程API
- dubbo-hmily-banka-service模块为banka服务，核心代码为com.geek.capacity.BankAApiImpl，配置文件请见同模块下的application.yml和hmily.yml
- dubbo-hmily-bankb-service模块为bankb服务，核心代码为com.geek.capacity.BankBApiImpl，配置文件请见同模块下的application.yml和hmily.yml
- dubbo-hmily-trade-center模块为trade-center服务，核心代码为com.geek.TradeServiceImpl，配置文件请见同模块下的application.yml和hmily.yml
  
  请求url:http://localhost:8080/trade/exchange  
  请求方式: POST  
  请求体：  
  {  
	  "txId": 123456,  
    "fromUserId":1,  
	  "toUserId":2,  
	  "exchangeFund": 7,  
	  "exchangeRate":0.7  
}
