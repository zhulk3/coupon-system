# coupon-system
## 项目背景 
优惠券业务在电商和外卖等系统非常常见，用于激励用户消费。但是优惠券的种类非常繁多，不同商家可能会有不同的定制需求，所以本系统计划实现一个可以根据商家需求创建优惠券模版，并且用户可以领取之后消费结算的系统。系统采用Spring Cloud框架实现，这意味着系统不是一个单体服务，而是将功能进行拆分部署的多服务架构，这样的好处很多，比如服务实例的动态伸缩，熔等。

结合优惠券业务功能和微服务的特性，将系统拆分为五个模块，分别是服务发现模块、网关模块、优惠券模版模块、优惠券分发模块、优惠券结算模块。

## 业务思想
  - 优惠券模版微服务，根据营运人员设定的条件和描述，构造优惠券模版。为了防止优惠券超发，使用Set数据结果，利用Redis单线程的特性存储。借助Redis过期键，通过定时任务定期清理过期优惠券.
    

  - 优惠券券分发微服务，实现了根据用户id和优惠券状态查找用户优惠券记录，由于分发模块不维护模版信息，所以分发模块需要访问模版微服务获取模版信息，需要实现熔断兜底策略。此模块还需要根据优惠券领取限制，比对当前用户已经领取的优惠券，判断是否还能领取。
    ![方法gram](https://images.zhulk3.xyz/tech/ty.png)
    ![](https://images.zhulk3.xyz/tech/dist.png)
    ![](https://images.zhulk3.xyz/tech/dis2.png)
    

  - 结算微服务，校验需要使用的优惠券是否符合结算规则，对商品价格进行计算。优惠券是分类的，不同类的优惠券有不同的计算方法，比如是否可以组合使用，是满减还是立减。
    ![](https://images.zhulk3.xyz/tech/settle.png)
## 架构设计

  - zuul网关是服务入口，提供路由、验证授权、收集日志等通用功能。
  - Eureka是注册中心，其他服务在Eureka注册自身信息，供其他服务调用。
  - 优惠券模版微服务，对外暴露创建优惠券模版、查询模版信息等服务。
  - 优惠券分发微服务，提供领取、查询、结算等功能，部分功能需要依赖其他微服务完成。
  - 结算微服务，根据分发微服务传递的优惠券信息的结算规则，使用不同执行器进行结算，返回商品金额。

  ![structure](https://images.zhulk3.xyz/tech/struct.png)

## 组件使用

- Eureka 服务通常在不同的物理机器上部署，而且具备多个实例，所以为了其他服务能够知道本服务的IP和端口，就需要一个注册中心，各个微服务在启动以后向注册中心注册自身的服务信息，其他微服务从注册中心处获取此服务的相关信息。Spring Cloud 提供的组件Eureka就具备这样的功能。
  ![eureka](https://images.zhulk3.xyz/tech/eureka.png)
  

- Zuul 通常作为系统入口，提供路由、日志、授权验证等功能。
- Redis 高性能分布式缓存系统，比较支持String, List, Hash, Set, SortedSet等数据类型，而且所有的操作都是原子的。可以对key设置过期时间，超过时间以后就取不到这个值。
  ![redis](https://images.zhulk3.xyz/tech/reids.png)
  

- Kafka 基于发布-订阅的点对点消息系统
  ![kafka](https://images.zhulk3.xyz/tech/kafak.png)
  

- Hystrix 让客户端具备弹性，在上游微服务出现故障时，熔断可以减小被依赖服务的压力，而且客户端不必等待故障微服务的返回。

## 测试结果
功能测试符合预期，目前网关路由存在不能找到转发微服务的问题，需要解决。

## 未来展望
- 目前所有服务都是单实例部署，下一步尝试多实例部署服务。
- 添加用于权限模块，用于对用户身份校验。
- 提供前端页面，目前都是postman进行测试。