## 项目大致

## 移动端

## 管理端

## 技术框架

vue3 +springboot3 前后端分离项目。

技术要求

前端

* vue3
* nginx

后端

* Springboot
* SpringMvc
* Mybatis +Mybatis Plus

存储

* mysql
* Mino
* redis

## 数据库设计

详见lease.sql	

1. 创建数据库

```sql
CREATE DATABASE lease CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. **导入数据库脚本**

   将资料中的`lease.sql`脚本导入`lease`数据库，其中包含了建表语句和少量的原始数据。

## 开发环境

* centos7

  * 静态ip

    * NAT模式下，虚拟机的IP地址，默认情况下是由虚拟DHCP服务自动分配的，虚拟机的IP地址不固定，这对于SSH远程访问也不友好，所以通常情况下，我们不使用虚拟DHCP服务，而是手动为虚拟机配置静态的IP地址。具体操作如下。

      * 确定网络配置文件

        在终端输入如下命令`ifconfig`命令，查看当前的网络接口，其中ens33是目前的以太网接口，故其配置文件为`vim /etc/sysconfig/network-scripts/ifcfg-ens33`。

      * 修改配置文件

        * ```shell
          TYPE=Ethernet
          PROXY_METHOD=none
          BROWSER_ONLY=no
          BOOTPROTO=dhcp
          DEFROUTE=yes
          IPV4_FAILURE_FATAL=no
          IPV6INIT=yes
          IPV6_AUTOCONF=yes
          IPV6_DEFROUTE=yes
          IPV6_FAILURE_FATAL=no
          IPV6_ADDR_GEN_MODE=stable-privacy
          NAME=ens33
          UUID=bf788989-e095-4e58-865a-e6b07248a514
          DEVICE=ens33
          ONBOOT=yes
          ```

        * 需要修改的内容如下

          - 修改`BOOTPROTO`参数为`static`

          - 修改`ONBOOT`参数为`yes`

          - 增加如下内容

          - ```shell
            IPADDR=192.168.10.100
            NETMASK=255.255.255.0
            GATEWAY=192.168.10.2
            DNS1=192.168.10.2
            ```

            ![image-20240709131021461](images/readme.assets/image-20240709131021461.png)

            在子网ip的范围选择一个没使用的ip给centos7。

            ![image-20240709131233647](images/readme.assets/image-20240709131233647.png)

            网关ip，充当dns1。

  * 关闭防火墙

    * ```shell
      #关闭防火墙 
      systemctl stop firewalld
      
      #禁止防火墙开机自启
      systemctl disable firewalld
      ```

      

  * 关闭selinux

    * SELinux，全称为Security-Enhanced Linux，是一种用于Linux操作系统的安全增强功能，为保证后序部署阶段的Nginx能够正常工作，此处关闭SELinux功能。
    * 修改seliunx配置文件
      * 打开配置文件  vim /etc/selinux/config
      * 修改内容如下  SELINUX=disabled
      * 重启 

* 部署mysql

  * **安装MySQL yum库**

    - **下载yum库**

      下载地址为https://dev.mysql.com/downloads/repo/yum/。需要根据操作系统选择相应版本，Centos7需选择`mysql80-community-release-el7-9.noarch.rpm`。

      执行以下命令可直接下载到服务器

      ```bash
      wget https://dev.mysql.com/get/mysql80-community-release-el7-9.noarch.rpm
      ```

     - **安装yum库**

       在上述`rpm`文件所在路径执行如下命令

       ```bash
       rpm -ivh mysql80-community-release-el7-9.noarch.rpm
       ```

     - **配置国内镜像**

       修改`/etc/yum.repo.d/mysql-community.repo`文件中的`[mysql80-community]`中的`baseUrl`参数，修改内容如下：

       ```ini
       [mysql80-community]
       name=MySQL 8.0 Community Server
       baseurl=https://mirrors.tuna.tsinghua.edu.cn/mysql/yum/mysql-8.0-community-el7-$basearch/
       enabled=1
       gpgcheck=1
       gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql-2022
              file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql
       ```


  2. **安装MySQL**

     执行如下命令安装MySQL

     ```bash
     yum install -y mysql-community-server
     ```

     # 报错

     ![image-20240708213243524](images/readme.assets/image-20240708213243524.png)

     改用,原因是centos7不维护了 和mysql8 不匹配

     ```shell
     yum install mysql-server --nogpgcheck
     ```

     

  3. **启动MySQL**

     执行如下命令启动MySQL服务

       ```bash
     systemctl start mysqld
       ```

       执行以下命令查看MySQL运行状态

       ```bash
     systemctl status mysqld
       ```


  4. **root用户相关配置**

     - **查看root用户初始密码**

       MySQL启动后会将root用户的初始密码写入日志，通过以下命令可以获取密码

       ```bash
       cat /var/log/mysqld.log | grep password
       ```

      - **使用初始密码登录**

        执行以下命令登录MySQL

        ```bash
        mysql -uroot -p'password'
        ```

     - **修改root用户密码**

       ```bash
       alter user 'root'@'localhost' identified by 'Ls021223.';
       ```

       **注意**：MySQL默认安装了[validate_password](https://dev.mysql.com/doc/refman/8.0/en/validate-password.html) 插件，默认情况下，要求密码要包含大写字母、小写字母、数字和特殊符号，且密码长度最小为8。若需设置简单密码，可禁用该插件，或调整该插件的密码强度级别。

     - **授予root用户远程登录权限**

       ```bash
       CREATE USER 'root'@'%' IDENTIFIED BY 'Ls021223.';
       GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
       FLUSH PRIVILEGES;
       ```

*  部署redis

  * 1.安装redis

    * 下载redis所在yum仓库

      * Redis所在的仓库为**remi-release**，下载地址为：http://rpms.famillecollet.com/enterprise/remi-release-7.rpm，可使用如下命令直接下载到服务器 

        * ```shell
          wget http://rpms.famillecollet.com/enterprise/remi-release-7.rpm
          ```

          

      * **安装yum仓库**

        执行如下命令进行安装

        ```shell
        rpm -ivh remi-release-7.rpm
        ```

    * 安装redis

      ```shell
         yum --enablerepo=remi -y install redis-7.2.5
      ```

  * 2.配置redis远程访问

    * Redis服务默认只允许本地访问，若需要进行远程访问，需要做出以下配置

      * ```shell
        vim /etc/redis/redis.conf
        ```

        修改如下

      * ```shell
        #监听所有网络接口，默认只监听localhost
        bind 0.0.0.0
        
        #关闭保护模式，默认开启。开始保护模式后，远程访问必须进行认证后才能访问。
        protected-mode no
        ```

  * 3. 启动redis

       ```shell
       systemctl start redis
       systemctl status redis
       systemctl enable redis
       ```

       

* 部署minio

  * 获取安装包

    * 下载地址如下：https://dl.min.io/server/minio/release/linux-amd64/archive/minio-20230809233022.0.0.x86_64.rpm，通过以下命令可直接将安装包下载至服务器

    * ```shell
      wget https://dl.min.io/server/minio/release/linux-amd64/archive/minio-20230809233022.0.0.x86_64.rpm
      ```

  * 安装Minio

    * ```shell
      rpm -ivh minio-20230809233022.0.0.x86_64.rpm
      ```

  * 启动Minio 看看有没有安装成功

    * ```shell
      minio server ./test
      ```

  * **集成Systemd**

    * **Systemd概述**

      `Systemd`是一个广泛应用于Linux系统的系统初始化和服务管理器，其可以管理系统中的各种服务和进程，包括启动、停止和重启服务，除此之外，其还可以监测各服务的运行状态，并在服务异常退出时，自动拉起服务，以保证服务的稳定性。系统自带的防火墙服务`firewalld`，我们自己安装的`mysqld`和`redis`均是由`Systemd`进行管理的，此处将MinIO服务也交给Systemd管理。

    * **编写MinIO服务配置文件**

      Systemd所管理的服务需要由一个配置文件进行描述，这些配置文件均位于`/etc/systemd/system/`或者`/usr/lib/systemd/system/`（redis和myql）目录下，下面创建MinIO服务的配置文件。

      执行以下命令创建并打开`minio.service`文件

      ```shell
      vim /etc/systemd/system/minio.service
      ```

      内容如下，具体可参考MinIO[官方文档](https://min.io/docs/minio/linux/operations/install-deploy-manage/deploy-minio-single-node-single-drive.html#create-the-systemd-service-file)。

      ```ini
      [Unit]
      Description=MinIO
      Documentation=https://min.io/docs/minio/linux/index.html
      Wants=network-online.target
      After=network-online.target
      AssertFileIsExecutable=/usr/local/bin/minio
      
      [Service]
      WorkingDirectory=/usr/local
      ProtectProc=invisible
      EnvironmentFile=-/etc/default/minio
      ExecStartPre=/bin/bash -c "if [ -z \"${MINIO_VOLUMES}\" ]; then echo \"Variable MINIO_VOLUMES not set in /etc/default/minio\"; exit 1; fi"
      ExecStart=/usr/local/bin/minio server $MINIO_OPTS $MINIO_VOLUMES
      Restart=always
      LimitNOFILE=65536
      TasksMax=infinity
      TimeoutStopSec=infinity
      SendSIGKILL=no
      
      [Install]
      WantedBy=multi-user.target
      ```

      **注意**：

      重点关注上述文件中的以下内容即可

      - `EnvironmentFile`，该文件中可配置MinIO服务所需的各项参数
      - `ExecStart`，该参数用于配置MinIO服务的启动命令，其中`$MINIO_OPTS`、`$MINIO_VOLUMES`，均引用于`EnvironmentFile`中的变量。
        - `MINIO_OPTS`用于配置MinIO服务的启动选项，可省略不配置。
        - `MINIO_VOLUMES`用于配置MinIO服务的数据存储路径。
      - `Restart`，表示自动重启

    * **编写`EnvironmentFile`文件**

      执行以下命令创建并打开`/etc/default/minio`文件

      ```shell
      vim /etc/default/minio
      ```

      内容如下，具体可参考[官方文档](https://min.io/docs/minio/linux/operations/install-deploy-manage/deploy-minio-single-node-single-drive.html#create-the-environment-variable-file)。

      ```ini
      MINIO_ROOT_USER=minioadmin
      MINIO_ROOT_PASSWORD=minioadmin
      MINIO_VOLUMES=/data
      MINIO_OPTS="--console-address :9001"
      ```

      *注意**

      - `MINIO_ROOT_USER`和`MINIO_ROOT_PASSWORD`为用于访问MinIO的用户名和密码，**密码长度至少8位**。

      - `MINIO_VOLUMES`用于指定数据存储路径，需确保指定的路径是存在的，可执行以下命令创建该路径。

        ```bash
        mkdir /data
        ```

      - `MINIO_OPTS`中的`console-address`,用于指定管理页面的地址。

  * 启动Minio

    * ```shell
      systemctl start minio
      systemctl status minio
      systemctl enable minio
      ```

    * **访问MinIO管理页面**

      管理页面的访问地址为：`http://192.168.10.101:9001`

      **注意**：

      `ip`需要根据实际情况做出修改
  
  



## 公寓信息管理接口开发

### 房间支付方式接口开发

![image-20240711125803829](images/README.assets/image-20240711125803829.png)

#### 1 查询所有的支付方式

* 由于数据做数据分析会有用，删除并不是真正的删除，做逻辑上的删除。PayMentType表有一个字段来表示是否删除。
* 查询的时候，就不要查询已经逻辑删除的数据。所以每次查询都要在sql后添加where 语句，过滤掉逻辑删除的。但是每次查询都要这样，很繁琐。可以使用mybaits-Plus 提供的逻辑删除。

**第一种方式**

​	修改配置文件如下

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: isDeleted   #全局逻辑删除的实体字段名    除了配置文件，也可以用@TableLogic实现逻辑删除功能
      logic-delete-value: 1    #逻辑已经删除值（默认为1）
      logic-not-delete-value: 0 #逻辑未删除值（默认为0）
```

**第二种方式**

 添加注解@TableLogic 。

**tips**：

* 开启mybaits-plus 的逻辑删除后，只对自动注入的sql有效，手写的sql无效。
* 开启逻辑删除后，使用mp的删除方法，也会变成逻辑删除。delete语句，变为update语句。

#### 2 保存或者更新支付方式

 问题： 前端更新或保存数据，一般都不会填写更新时间或者保存时间。，所以这样问题可以这样解决，后端接受到前端传来的json对象，手动set时间。但是每次都set时间，很繁琐。可以使用mp的自动注入。

* 确定注入实际如下  fill的使用

```java
 @TableField(value = "create_time",fill = FieldFill.INSERT)
 private Date createTime;
```

* 确定注入什么.需要实现MetaObjectHander如下

```java
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

    }
}
```

#### 3 根据id删除支付方式

* 由于开启了逻辑删除，删除操作变成了更新操作。逻辑意义上的删除。



### 房间租期管理

#### 1. 查询所有租期

![image-20240711130011146](images/README.assets/image-20240711130011146.png)

注意：已经实现了逻辑删除。在查找的时候使用mp的动态sql会自动做where语句。

#### 2.保存或更新租期

![image-20240711130914735](images/README.assets/image-20240711130914735.png)

注意：保存和修改

保存：前端传json对象，不传id就是保存

修改：前端传json对象，传id就是修改， 在mp代理的动态sql是先根据id查看信息，然后在update更新信息.

#### 3. 根据id删除租期

![image-20240711130914735](images/README.assets/image-20240711130914735.png)



点击删除，前端获取执行的对象，传json给后端，后端用@RequestBody 接受json并变为对象。由于开启了逻辑删除，所有的删除语句就变成了更新语句.

###  标签管理

![image-20240711140755419](images/README.assets/image-20240711140755419.png)

#### 1. （根据类型）查询标签列表

 类型是可选的。可以传可以不传。@RequestParam(required = false) 

传参时发现错误。

![image-20240711141508055](images/README.assets/image-20240711141508055.png)

![image-20240711141538850](images/README.assets/image-20240711141538850.png)

```java
 Failed to convert value of type 'java.lang.String' to required type 'com.ls.lease.model.enums.ItemType'; Failed to convert from type [java.lang.String] to type [@org.springframework.web.bind.annotation.RequestParam com.ls.lease.model.enums.ItemType] for value '1']
```

类型转化失败。String转化为ItemType类型失败

上述type参数，在数据库是tinyint，实体类中是`ItemType`枚举类型，前端中是json。

具体过程涉及类型转化如下

* **请求流程**

![image-20240711142129518](images/README.assets/image-20240711142129518.png)

**说明**

- SpringMVC中的`WebDataBinder`组件负责将HTTP的请求参数绑定到Controller方法的参数，并实现参数类型的转换。
- Mybatis中的`TypeHandler`用于处理Java中的实体对象与数据库之间的数据类型转换。



* **响应流程**

![image-20240711142219876](images/README.assets/image-20240711142219876.png)

**说明**

- SpringMVC中的`HTTPMessageConverter`组件负责将Controller方法的返回值（Java对象）转换为HTTP响应体中的JSON字符串，或者将请求体中的JSON字符串转换为Controller方法中的参数（Java对象），例如下一个接口**保存或更新标签信息**

![image-20240711142341070](images/README.assets/image-20240711142341070.png)

下面介绍一下每个环节的类型转换原理

- **WebDataBinder枚举类型转换**

  `WebDataBinder`依赖于[`Converter`](https://docs.spring.io/spring-framework/reference/core/validation/convert.html)实现类型转换，若Controller方法声明的`@RequestParam`参数的类型不是`String`，`WebDataBinder`就会自动进行数据类型转换。SpringMVC提供了常用类型的转换器，例如`String`到`Integer`、`String`到`Date`，`String`到`Boolean`等等，其中也包括`String`到枚举类型，但是`String`到枚举类型的默认转换规则是根据实例名称（"APARTMENT"）转换为枚举对象实例（ItemType.APARTMENT）。若想实现`code`属性到枚举对象实例的转换，需要自定义`Converter`，代码如下，具体内容可参考[官方文档](https://docs.spring.io/spring-framework/reference/core/validation/convert.html#core-convert-Converter-API)。

- 自定义转换类型

  - 1. 实现Converter接口  

    ```java
    /**
     * 自定义 Converter 方式
     * 实现Converter接口 import org.springframework.core.convert.converter.Converter;
     */
    //在ioc中注册，方便使用
    @Component
    public class StringToItemType implements Converter<String,ItemType> {
    
        @Override
        public ItemType convert(String code) {
            //如果新增枚举实例就新增一个if
    //        if ("1".equals(code)){
    //            return ItemType.APARTMENT;
    //        } else if ("2".equals(code)) {
    //            return ItemType.ROOM;
    //        }
    
            //用枚举类型的静态方法，获取所有枚举类型的实例
            ItemType[] values = ItemType.values();
    
            //遍历枚举类型实例，如果枚举类型的实例的code等于参数code，就返回这个枚举实例
            for (ItemType value : values) {
                if (value.getCode().equals(Integer.valueOf(code))){
                    return value;
                }
            }
            throw new IllegalArgumentException("code:" + code  +"非法");
        }
    }
    ```

    2. 在SpringMVC中注册自定义转换类

       ```java
       @Configuration
       public class WebMvcConfiguration implements WebMvcConfigurer {
       
           //添加自定义Converter
           @Autowired
           private StringToItemType stringToItemType;
           @Override
           public void addFormatters(FormatterRegistry registry) {
               registry.addConverter(stringToItemType);
           }
       }
       
       ```

       这样WebDateBind就能够转化String到枚举。（Springmvc虽然提供了String->枚举类型，但是不能满足外面的要求，Spring提供的自动转化是根据string的内容找枚举实例，我们需要根据string的内容对应的序号获取枚举类型）。

#### 2. 更新或报错标签

#### 3. 删除标签操作

###  自定义类型转换工厂

 在标签管理中由于WebDatabind不能按照我们的要求将String 转换为枚举类，我们自定义了一个转换类。并在webmvc中注册。

但是我们有很多的枚举类型都需要考虑类型转换这个问题，按照上述思路，我们需要为每个枚举类型都定义一个Converter，并且每个Converter的转换逻辑都完全相同，针对这种情况，我们使用[`ConverterFactory`](https://docs.spring.io/spring-framework/reference/core/validation/convert.html#core-convert-ConverterFactory-SPI)接口更为合适，这个接口可以将同一个转换逻辑应用到一个接口的所有实现类，因此我们可以定义一个`BaseEnum`接口，然后另所有的枚举类都实现该接口，然后就可以自定义`ConverterFactory`，集中编写各枚举类的转换逻辑了。具体实现如下：

```java
public interface BaseEnum {
    Integer getCode();
    String getName();
}
```

```java
@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new Converter<String, T>() {
            @Override
            public T convert(String source) {

                for (T enumConstant : targetType.getEnumConstants()) {
                    if (enumConstant.getCode().equals(Integer.valueOf(source))) {
                        return enumConstant;
                    }
                }
                throw new IllegalArgumentException("非法的枚举值:" + source);
            }
        };
    }
}
```

### **TypeHandler枚举类型转换**

Mybatis预置的`TypeHandler`可以处理常用的数据类型转换，例如`String`、`Integer`、`Date`等等，其中也包含枚举类型，但是枚举类型的默认转换规则是枚举对象实例（ItemType.APARTMENT）和实例名称（"APARTMENT"）相互映射。若想实现`code`属性到枚举对象实例的相互映射，需要自定义`TypeHandler`。

不过MybatisPlus提供了一个[通用的处理枚举类型的TypeHandler](https://baomidou.com/pages/8390a4/)。其使用十分简单，只需在`ItemType`枚举类的`code`属性上增加一个注解`@EnumValue`，Mybatis-Plus便可完成从`ItemType`对象到`code`属性之间的相互映射，具体配置如下。

```java
public enum ItemType {

    APARTMENT(1, "公寓"),
    ROOM(2, "房间");

    @EnumValue
    private Integer code;
    private String name;

    ItemType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

### **HTTPMessageConverter枚举类型转换**

`HttpMessageConverter`依赖于Json序列化框架（默认使用Jackson）。其对枚举类型的默认处理规则也是枚举对象实例（ItemType.APARTMENT）和实例名称（"APARTMENT"）相互映射。不过其提供了一个注解`@JsonValue`，同样只需在`ItemType`枚举类的`code`属性上增加一个注解`@JsonValue`，Jackson便可完成从`ItemType`对象到`code`属性之间的互相映射。具体配置如下，详细信息可参考Jackson[官方文档](https://fasterxml.github.io/jackson-annotations/javadoc/2.8/com/fasterxml/jackson/annotation/JsonValue.html)。

```java
public enum ItemType {

    APARTMENT(1, "公寓"),
    ROOM(2, "房间");

    @EnumValue
  	@JsonValue
    private Integer code;
    private String name;

    ItemType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

### 为什么使用枚举后，类型转换这么多麻烦还要用枚举，而不直接用integer表示状态。

1. **类型安全**：
   使用枚举类型可以确保状态的取值只能是预定义的值之一，这避免了使用整数时可能出现的错误值。例如，如果你使用整数来表示订单的状态（如1表示待支付，2表示已支付），那么代码中的任何错误都可能导致使用无效的整数值（如3或-1），这可能会导致运行时错误或逻辑错误。而枚举类型强制类型安全，确保状态值只能是定义好的那几个。
2. **清晰易读**：
   枚举类型的命名通常比整数更具描述性，使得代码更加清晰易懂。例如，使用`OrderStatus.PENDING_PAYMENT`比使用整数`1`来表示待支付状态要直观得多。这不仅提高了代码的可读性，也方便了团队成员之间的理解和协作。
3. **易于扩展和维护**：
   随着业务逻辑的发展，可能需要添加新的状态或修改现有状态。使用枚举类型可以很容易地通过添加新的枚举常量或修改现有常量来实现，而不需要修改大量使用这些状态的代码。相比之下，如果使用整数表示状态，则可能需要在多处代码中更新状态值，这既繁琐又容易出错。
4. **支持方法和属性**：
   枚举类型可以包含方法和属性，这使得它们能够包含比简单整数更多的信息。例如，你可以为每个枚举常量添加一个描述性字符串，或者定义一个方法来获取与该状态相关的特定信息。这些功能对于表示复杂的状态逻辑非常有用。
5. **支持`switch`语句**：
   在Java中，使用枚举类型作为`switch`语句的表达式可以使代码更加清晰和易于维护。从Java 7开始，`switch`语句还支持字符串，但枚举类型通常仍然是更好的选择，因为它们提供了更强的类型安全性和更清晰的语义。
6. **可序列化且单例**：
   枚举类型是自动可序列化的，并且它们的每个实例在JVM中都是唯一的（即单例）。这意味着你可以安全地将枚举类型的实例作为序列化对象传输，而无需担心它们的唯一性或序列化问题。

综上所述，使用枚举类型来表示状态是Java开发中一种更加安全、清晰、易于扩展和维护的方式。



### 配套管理开发

![image-20240711172040893](images/README.assets/image-20240711172040893.png)

![image-20240711172137155](images/README.assets/image-20240711172137155.png)

![image-20240711172214279](images/README.assets/image-20240711172214279.png)

配套信息的开发接口。

#### 1. （根据类型）查询配套信息

#### 2. 新增或修改配套信息

#### 3.根据id删除配套信息

### 房间基本属性管理

![image-20240712101111856](images/README.assets/image-20240712101111856.png)

表结构

![image-20240712101313586](images/README.assets/image-20240712101313586.png)

#### 1. 查询所有属性值以及对应的属性名称。

涉及多表查询，mybaitsPlus不能满足我们的需求，就自定义sql。

controller层调用service层接口方法，service层接口方法实现（调用dao层），dao层绑定mapper.xml编写sql语句。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ls.lease.web.admin.mapper.AttrKeyMapper">
    <resultMap id="AttrKeyVoMap" type="com.ls.lease.web.admin.vo.attr.AttrKeyVo">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <collection property="attrValueList" ofType="com.ls.lease.model.entity.AttrValue">
            <id property="id" column="attr_value_id"/>
            <result property="name" column="attr_value_name"/>
            <result property="attrKeyId" column="attr_key_id"/>
        </collection>

    </resultMap>

    <select id="listAttrInfo" resultMap="AttrKeyVoMap">
        select k.id,
               k.name,
               v.id attr_value_id,
               v.name attr_value_name,
               v.attr_key_id
        from attr_key k
                 join attr_value v
                      on k.id = v.attr_key_id
        where k.is_deleted = 0
          and v.is_deleted = 0;

    </select>
</mapper>
```

回顾一下：

* 两表查询，外键放在多方，一对多，多对一，看需求，本次需求显然是一对多。因为要涉及量表查询后数据的保存，所以定义了新的VO。新的Vo应该保证传给前端的json的格式{"code":xx,"msg" : xxx,"data": {}}。应主要关注于data 的格式。本次查询data应该是list（属性值）套list(属性名称)。
* 自定义sql，表的join，以及自定义映射，在自定义表的二级映射时，如果数据时集合使用collection标签。
* idea中sql语句提示，要idea连接数据库，然后在mapper.xml中简历新session。

上述代码有bug，这种情况下

​	属性值下面没有对应的属性名，join连接不会保存属性值信息，也就无法映射，在前端就收不到这个空属性名的属性值。

​	可以改用left join 连接

JOIN（内连接）

+ **基本概念**：`JOIN`（或称为内连接）会返回两个或多个表中匹配的行。如果在一个表中有行与另一个表中的行基于连接条件匹配，那么这些行会被包括在结果集中。如果某行在一个表中没有匹配的行，那么该行就不会出现在结果集中。
+ **用途**：当你只想要那些在两个或多个表中都有匹配的行时，使用 `JOIN`。

 LEFT JOIN（左连接）

+ **基本概念**：`LEFT JOIN`（或称为左外连接）会返回左表（`LEFT JOIN` 语句中指定的第一个表）的所有行，即使右表中没有匹配的行。如果右表中没有匹配的行，则结果中这些行的右表部分将包含 NULL。
+ **用途**：当你想要从左表中选择所有的行，并且仅当它们在右表中也有匹配时，才选择右表中的行，这时可以使用 `LEFT JOIN`。这常用于想要保留左表中的所有记录，同时获取与右表匹配的信息（如果有的话）的场景。

发现还是不能显示k的数据（k对应v为空）。left join 会保存左表的所有，那么为什么会没有呢？

​	这是因为我们使用了逻辑删除，sql语句的后的过滤条件，

```sql
where k.is_deleted = 0
          and v.is_deleted = 0;
```

由于右边对应左表的值时空的，即右表中没有匹配的行，则结果中这些行的右表部分将包含 NULL。就不满足逻辑删除的v.is_deleted = 0的条件，自然就没有该数据。

一般我们对于使用逻辑删除，left join 的连接，过滤条件发生变化，将 v.is_deleted = 0。移动到表连接的过程，如下。

```sql
 select k.id,
               k.name,
               v.id attr_value_id,
               v.name attr_value_name,
               v.attr_key_id
        from attr_key k
                left join attr_value v
                      on k.id = v.attr_key_id  and v.is_deleted = 0
        where k.is_deleted = 0
```



#### 2. 保存或更新属性值

#### 3. 保存或更新属性名

#### 4. 根据id删除属性名

删除属性名的同时，应该删除属性名下所有的属性值

#### 5. 根据id删除属性

### 杂费信息管理

![image-20240712113620079](images/README.assets/image-20240712113620079.png)

对应的表

![image-20240712113902583](images/README.assets/image-20240712113902583.png)

#### 1. 查询所有的杂费名称，以及对应的杂费值

实体对象设计：返回杂费名的集合，杂费名的集合中包含杂费值的集合。可以为这个对象设计个新类型，表示多表查询的结果接收类。

该表只需继承杂费名，添加list<杂费值>即可。

业务实现：mybatisplus不能实现多表查询，只能自定义sql。controller层调用service接口方法，serviceimp负责实现，serviceimp中调用dao层方法。在对应的mapper.xml文件编写sql。编写sql的时候注意逻辑删除。

#### 2. 更新或保存杂费名

#### 3. 更新或保存杂费值

#### 4. 根据id删除杂费名

 删除杂费名的同时删除杂费名对应的杂费值

#### 5. 根据id删除属性值



### 地区信息管理

![image-20240712135608437](images/README.assets/image-20240712135608437.png)

#### 1. 查询省份信息

#### 2. 根据省份id查询城市id

#### 3. 根据城市id查询区域信息



### 图片上传管理

**1. 图片上传流程**

下图展示了新增房间或公寓时，上传图片的流程。

![image-20240712161425579](images/README.assets/image-20240712161425579.png)

2. 配置Minio 

   1. 引入Minio Maven依赖

      ```xml
      <dependency>
          <groupId>io.minio</groupId>
          <artifactId>minio</artifactId>
      </dependency>
      ```

   2. 配置Minio相关参数

   在`application.yml`中配置Minio的`endpoint`、`accessKey`、`secretKey`、`bucketName`等参数

   ```yaml
   minio:
     endpoint: http://<hostname>:<port>
     access-key: <access-key>
     secret-key: <secret-key>
     bucket-name: <bucket-name>
   ```

   3. 创建配置参数类,（也可以直接用@Value注入）

   ```java
   /**
    * 配置参数类
    * @ConfigurationProperties(prefix = "minio")
    * 根据类的属性自动映射，yaml文件中minio下的属性值。
    * 注意要在在配置类上注册
    *
    */
   
   @Data
   @ConfigurationProperties(prefix = "minio")
   public class MinioProperties{
        private String endpoint;
        private String accessKey;
        private String secretKey;
   
        private String bucketName;
   
   }
   
   ```

   4. 创建配置类

   ```java
   @Configuration
   //@EnableConfigurationProperties(MinioProperties.class) //注册
   @ConfigurationPropertiesScan("com.ls.lease.common.minio")  //注册 配置参数类
   public class MinioConfiguration {
   
       @Autowired
       private MinioProperties minioProperties;
       @Bean
       public MinioClient minioClient(){
           MinioClient minioClient = MinioClient.builder()
                   .endpoint(minioProperties.getEndpoint())
                   .credentials(minioProperties.getAccessKey(), minioProperties.getAccessKey())
                   .build();
   
           return minioClient;
       }
   }
   ```

   至此minio配置完成。

   上传图片实现。

   controller层调用service接口，接口实现逻辑。

   编写service代码

   ```java
   /**
    * 图片上传业务
    */
   @Service
   public class FileServiceImpl implements FileService {
       @Autowired
       private MinioClient minioClient;
       @Autowired
       private MinioProperties properties;
   
       @Override
       public String upload(MultipartFile file) {
           try {
               //判断桶是否存在
               boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                       .bucket(properties.getBucketName())
                       .build()
               );
   
               if (!bucketExists) {
                   //创建桶
                   minioClient.makeBucket(MakeBucketArgs.builder()
                           .bucket(properties.getBucketName())
                           .build()
                   );
                   //设置桶的访问权限
                   minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                           .bucket(properties.getBucketName())
                           .config(createBucketPolicyConfig(properties.getBucketName()))
                           .build());
               }
   //            minioClient.uploadObject() 上传本地图片
               //设置文件名
               String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
               //上传文件
               minioClient.putObject(PutObjectArgs.builder()
                       .bucket(properties.getBucketName())
                       .stream(file.getInputStream(), file.getSize(), -1)
                       .object(filename)
                       .contentType(file.getContentType())//设置响应体，内容类型，不然这种处理方式时字符流，浏览器访问字符流的文件会下载，而不是查看。
                       .build()
               );
               //拼接url
   //            String url = properties.getEndpoint() +"/"+properties.getBucketName()+"/"+filename;
               String url = String.join("/", properties.getEndpoint(), properties.getBucketName(), filename);
   
               return url;
           } catch (Exception e) {
               e.printStackTrace();
           }
           return null;
       }
   
       private String createBucketPolicyConfig(String bucketName) {
   
           return """
                   {
                     "Statement" : [ {
                       "Action" : "s3:GetObject",
                       "Effect" : "Allow",
                       "Principal" : "*",
                       "Resource" : "arn:aws:s3:::%s/*"
                     } ],
                     "Version" : "2012-10-17"
                   }
                   """.formatted(bucketName);
       }
   }
   ```

   需要注意的是：

   * 设置响应体，内容类型，不然这种处理方式时字符流，浏览器访问字符流的文件会下载，而不是查看。

   ​	

   处理异常的巧妙的解决办法：try{}catch(){}

   * 在处理异常时，我们可以将service层的异常全部抛出在controller解决，try{}catch(){},捕获所有异常返回失败就行。而不是根据service的返回结果判断是否出现异常。
   * 如下例子

   ```java
   @Tag(name = "文件管理")
   @RequestMapping("/admin/file")
   @RestController
   public class FileUploadController {
   
       @Autowired
       private FileService fileService;
   
       @Operation(summary = "上传文件")
       @PostMapping("/upload")
       public Result<String> upload(@RequestParam MultipartFile file) {
           try {
               String url = fileService.upload(file);
               return Result.ok(url);
           } catch (Exception e) {
               return Result.fail();
           }
       }
   }
   ```

   #### 全局异常处理器

   由于处理异常的逻辑都是一样的，异常全都抛出在controller层处理，controller层trycatch处理，每个controller接口几乎都要这样处理异常。每个接口都要写这个逻辑，就显得臃肿，SpringMvc提供了全局异常处理功能,可以将所有处理异常的逻辑集中起来，进而统一处理所有异常，使代码更容易维护。

   具体用法如下，详细信息可参考[官方文档](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html)：

   在**common模块**中创建`com.ls.lease.common.exception.GlobalExceptionHandler`类，内容如下

   ```java
   /**
    * springmvc 提供了全局异常处理功能
    * `@ControllerAdvice`用于声明处理全局Controller方法异常的类
    * `@ExceptionHandler`用于声明处理异常的方法，`value`属性用于声明该方法处理的异常类型
    * `@ResponseBody`表示将方法的返回值作为HTTP的响应体
    */
   
   @ControllerAdvice
   public class GlobalExceptionHandler {
   
       @ExceptionHandler(Exception.class)
       @ResponseBody
       public Result error(Exception e){
           e.printStackTrace();
           return Result.fail();
       }
   }
   ```

   

###  公寓管理

表结构

![image-20240714145551065](images/README.assets/image-20240714145551065.png)
前端传参

  ![image-20240714150348784](images/README.assets/image-20240714150348784.png)

前端传来的json,其中经度纬度,是调用高德地图的接口,通过详细地址获得.

后端接收json的类

```java
@Schema(description = "公寓信息")
@Data
public class ApartmentSubmitVo extends ApartmentInfo {

    @Schema(description="公寓配套id")
    private List<Long> facilityInfoIds;

    @Schema(description="公寓标签id")
    private List<Long> labelIds;

    @Schema(description="公寓杂费值id")
    private List<Long> feeValueIds;

    @Schema(description="公寓图片id")
    private List<GraphVo> graphVoList;

}
@Schema(description = "公寓信息表")
@TableName(value = "apartment_info")
@Data
public class ApartmentInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公寓名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "公寓介绍")
    @TableField(value = "introduction")
    private String introduction;

    @Schema(description = "所处区域id")
    @TableField(value = "district_id")
    private Long districtId;

    @Schema(description = "所处区域名称")
    @TableField(value = "district_name")
    private String districtName;

    @Schema(description = "所处城市id")
    @TableField(value = "city_id")
    private Long cityId;

    @Schema(description = "所处城市名称")
    @TableField(value = "city_name")
    private String cityName;

    @Schema(description = "所处省份id")
    @TableField(value = "province_id")
    private Long provinceId;

    @Schema(description = "所处区域名称")
    @TableField(value = "province_name")
    private String provinceName;

    @Schema(description = "详细地址")
    @TableField(value = "address_detail")
    private String addressDetail;

    @Schema(description = "经度")
    @TableField(value = "latitude")
    private String latitude;

    @Schema(description = "纬度")
    @TableField(value = "longitude")
    private String longitude;

    @Schema(description = "公寓前台电话")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "是否发布")
    @TableField(value = "is_release")
    private ReleaseStatus isRelease;

}
```



#### 1. 保存或更新公寓信息

逻辑,前端通过http请求发送带有公寓信息的json串,后端定义一个符合前端请求json格式的接收类.接收前端传来的参数.这里并不能直接调用mybatisplus的方法,因为没有这个定义vo交给mybatis-plus.也不能直接调用`ApartmentInfoService`因为vo所附加的信息不能够操作.



更新操作总体思路如下

![image-20240714152853818](images/README.assets/image-20240714152853818.png)

![image-20240714152904618](images/README.assets/image-20240714152904618.png)

修改公寓信息,删除里面的一个图片,怎么操作?,json串的是剩余的数据,而不是要改变的数据,那么怎么删除公寓里面那张图片呢?

答:可以先删除公寓里面图片的信息,然后在保存json串的信息就可以完成修改.

同样适用于修改公寓信息里面的公寓配套,公寓标签,公寓杂费.

![image-20240714153241758](images/README.assets/image-20240714153241758.png)

这里我们要先理清楚逻辑,**如果前端传来了id就是更新操作**.**如果没传**id**就是保存操作**.

* 更新操作逻辑:  先删除在保存
  * 更新ApartmentInfo信息,可以用mp对应的service方法save
  * 对于其他字段需要我们自己写更新逻辑 (facilityInfoIds,labelIds,feeValueIds,graphVoList)
    * 更新公寓配套 `facilityInfoIds`
      * 先删除公寓id所对应的配套信息id
      * 在保存前端传来了配套信息id
    * 更新公寓标签 `labelIds`
      * 先删除公寓id对应的配套信息id
      * 在保存
    * 等等
  * 可以先删除需要更新的，在调用sava方法保存。

```java
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;
    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;
    @Autowired
    private GraphInfoService graphInfoService;

    @Override
    public void saveOrUpdateapart(ApartmentSubmitVo apartmentSubmitVo) {
        //调用父方法保存apartment基本信息
        super.saveOrUpdate(apartmentSubmitVo);
        //手动处理其他信息即Vo新增的信息facilityInfoIds,labelIds,feeValueIds,graphVoList
        //保存操作直接保存，更新操作，所以先删除公寓所对应的信息，在保存参数的信息。
        Boolean is_update = apartmentSubmitVo.getId() != null;
        if(is_update){
            //删除 对应的信息
            //删除修改公寓所公寓配套信息
            LambdaQueryWrapper<ApartmentFacility> apartmentFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<ApartmentFacility> eq = apartmentFacilityLambdaQueryWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(eq);
            //删除 修改公寓所对应公寓标签信息
            LambdaQueryWrapper<ApartmentLabel> apartmentLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apartmentLabelLambdaQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            apartmentLabelService.remove(apartmentLabelLambdaQueryWrapper);
            //删除公寓杂费信息
            LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apartmentFeeValueLambdaQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(apartmentFeeValueLambdaQueryWrapper);

            //删除图片信息
            LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId())
                    .eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoService.remove(graphInfoLambdaQueryWrapper);
        }
        //无论是保存还是更细操作，都要保存

        /**
         * 保存公寓配套信息
         */
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)){
            //savabatch要求ApartmentFacility 泛型参数  Long -》ApartmentFacility
            ArrayList<ApartmentFacility> facilityArrayList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityInfoId);
                facilityArrayList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(facilityArrayList);
        }


        /**
         * 保存图片信息
         */
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
            // graphInfoService.saveBatch(); 要求参数的集合泛型是<GraphInfo>,但是图片的信息是GraphVo
            //类型转换为info
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setName(graphVo.getName());
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }

        /**
         * 保存标签信息
         */
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)){
            ArrayList<ApartmentLabel> labelArrayList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                labelArrayList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(labelArrayList);

        }
        /**
         * 保存公寓杂费
         */
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)){
            ArrayList<ApartmentFeeValue> feeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                feeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(feeValueList);
        }
    }
}
```

* 为什么引入service，而不引入mapper，因为service层的保存方法，可以保存list，mapper则不行
* savabatch（）的参数要求是被调用的类型的泛型的集合。不符合要自己构造。在传参.

​	

#### 2. 根据条件，分页查询公寓列表(复杂sql值得学习)

![image-20240715133826891](images/README.assets/image-20240715133826891.png)

前端传参查询 当前页数，和页数大小，还有查询条件。

后端接收前端的参数，当前页数和大小直接使用注解@RequestParam接收参数，查询条件，可以封装成一个类去接收命名为queryvo，

这里由于是get请求，所以没有请求体，即不能使用@RequetBody接收，而是直接根据前端传参的name和实体的属性一致接收。

后端返回给前端页面上展示的所有内容。

由于没有一个类可以完整的显示上面的内容，我们自定义Vo类可以继承基本类，在添加上没有的字段即可。

由于是分页操作，我们可以自己写limit，但是麻烦，可以利用mp的分页插件

* 分页插件配置

  * 在mp的配置类添加分页插件

  ```java
   @Bean
      public MybatisPlusInterceptor mybatisPlusInterceptor() {
          MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
          interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
          return interceptor;
      }
  ```

* 分页插件的使用，看mp教程

编写Controller层

```java
@Operation(summary = "根据条件分页查询公寓列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        Page<ApartmentItemVo> apartmentItemVoPage = new Page<>(current, size);
        IPage<ApartmentItemVo> result = apartmentInfoService.pageItem(apartmentItemVoPage,queryVo);
        return Result.ok(result);
    }
```

编写service层

编写接口，编写实现类

```java
IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> apartmentItemVoPage, ApartmentQueryVo queryVo);

@Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> apartmentItemVoPage, ApartmentQueryVo queryVo) {

        return  mapper.pageItem(apartmentItemVoPage,queryVo);
    }
```

mapper层，创建接口，自定义sql

（根据 省id, 市id ,区id）动态查询，公寓列表，查询到除了房间总数和空闲房间数的其他字段

房间总数，可以根据公寓id查询roominfo表，过滤条件是逻辑删除和房间是否发布，查询的结果分为一组，计算数量

剩余房间数，可以根据房间的签约状态，筛选除已经签约的房间，并分为一组，计算数量，总数-该数就是剩余房间数。

将上述三个查询，看成子sql，左连接查询结果

* 左连接会导致左表匹配右表，右表没有值的话，会置为null，在计算房间剩余数的时候的计算可能会出问题，比如总数是10，签约房间（0）做左连接查询因为查询的公寓没有已经签约的房间会即已经签约数为0，都是空房间，左连接会null填充，导致10-null。正数和null的运算都是null。就会导致结果错误，所以要特判一下左连接查询的时候数量为null就是0.sql如下

```xml
<select id="pageItem" resultType="com.ls.lease.web.admin.vo.apartment.ApartmentItemVo">

    select
        ai.id,
        ai.name,
        ai.introduction,
        ai.district_id,
        ai.district_name,
        ai.city_id,
        ai.city_name,
        ai.province_id,
        ai.province_name,
        ai.address_detail,
        ai.latitude,
        ai.longitude,
        ai.phone,
        ai.is_release,
        ifnull(tc.cnt,0) total_room_count,
        ifnull(tc.cnt,0) - ifnull(cc.cnt,0) free_room_count
        from
        (select id,
                name,
                introduction,
                district_id,
                district_name,
                city_id,
                city_name,
                province_id,
                province_name,
                address_detail,
                latitude,
                longitude,
                phone,
                is_release
         from apartment_info
        <where>
            is_deleted=0
            <if test="queryVo.provinceId != null">
                and province_id=#{queryVo.provinceId}
            </if>
            <if test="queryVo.cityId != null">
                and city_id=#{queryVo.cityId}
            </if>
            <if test="queryVo.districtId != null">
                and district_id=#{queryVo.districtId}
            </if>
        </where>
        )
        ai
        left join

        (select
        apartment_id,
        count(*) cnt
        from room_info
        where is_deleted = 0
        and is_release = 1
        group by apartment_id)
        tc
        on ai.id = tc.apartment_id
        left join

        (select
        apartment_id,
        count(*) cnt
        from lease_agreement
        where is_deleted = 0
        and status in (2,5)
        group by apartment_id)
        cc
        on ai.id=cc.apartment_id
    </select>
```

需要补充的是，测试的时候,标红部分，只穿一个查询条件，其他的要删掉。不方便测试，可以添加如下配置

![image-20240715141642476](images/README.assets/image-20240715141642476.png)

```yaml
springdoc:
  default-flat-param-object: true
```

![image-20240715141812427](images/README.assets/image-20240715141812427.png)

#### 3. 根据ID获取公寓详细信息

点击编辑公寓，如下内容

![image-20240715175752742](images/README.assets/image-20240715175752742.png)

这个就是根据id查询公寓详细信息的接口。根据原型，即上述页面的内容，定义一个返回给前端的vo。为什么不用更新或者删除公寓信息的vo，因为那个vo（保存或者删除公寓信息）是前端传给后端的，著需要传入对应的id，后端就能查到。

本vo是前端请求后端，只有公寓id，后端即使发送给前端对应的图片id，前端也不能查找数据库，所以要定义本次vo。

 实现逻辑：

​	先根据id查公寓的基本信息。格外的信息，如公寓配套列表，标签列表，杂费列表，图片列表。单独处理

* 处理图片列表

  * 表结构![image-20240715181014720](images/README.assets/image-20240715181014720.png)
  * 根据公寓id和图片类型 查图片信息表 获得图片列表
* 处理配套信息列表

  * 根据公寓id查公寓-配套关系，得到配套id列表，根据查到的配套id列表查配套信息。
* 处理标签信息列表

  * 根据公寓id查公寓-标签关系，得到对应的标签id列表，再根据标签id列表查标签信息。
* 处理杂费值信息列表
  * 根据公寓id查公寓-杂费值关系，得到对应的杂费值id列表。将杂费值和杂费名join，利用得到的杂费值id列表查询join的表。注意查到的字段需要处理，fee_key_name

对得到的结果进行封装，封装成需要的对象。可以new 一个，然后set，但是属性太多，比较麻烦，而且代码臃肿

可以使用BeanUtils---》spring提供的工具类，可以将一个类复制成另一个类型。

第一个参数时资源类，第二个参数时目标类，两个类型可以没有任何关系，只要属性一致。

```java
  BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
```

再单独set额外信息。

tips： 再编写imp时，引入mapper时，要考虑多变查询到底谁是主体，引入主体更方便操作。



#### 4.根据ID删除公寓信息

逻辑：删除公寓基本信息，同时删除图片，配套，标签，杂费。

基本信息直接调用通用service删除就行。

* 删除图片
  * 根据公寓id和属性，操作图片信息表，删除图片信息。
* 删除配套信息
  * 根据公寓id，操作公寓-配套关系表，删除该公寓所对应的配套id
* 删除标签信息
  * 同删除配套信息逻辑
* 删除杂费值信息
  * 删除公寓-杂费关系



补充：由于公寓下会包含房间信息，因此在删除公寓时最好先判断一下该公寓下是否存在房间信息，若存在，则提醒用户先删除房间信息后再删除公寓信息.

通用mapper提供了selectCount方法，查询有几条数据。

```java
LambdaQueryWrapper<RoomInfo> roomQueryWrapper = new LambdaQueryWrapper<>();
roomQueryWrapper.eq(RoomInfo::getApartmentId, id);
Long count = roomInfoMapper.selectCount(roomQueryWrapper);
if (count > 0) {
    //直接为前端返回如下响应：先删除房间信息再删除公寓信息
}
```

想要直接为前端返回响应，可利用前边配置的全局异常处理功能（此处直接抛出异常，全局异常处理器捕获到异常后，便会直接为前端返回响应结果）。

但是抛出的没有具体信息怎么办，exception（"message"）参数传递错误信息。没有状态码怎么办。自定义异常，扩展属性。

```java
/**
 * 自定义异常处理 扩展RuntimeException
 */
@Data
public class LeaseException extends RuntimeException{
    private  Integer code;

    public LeaseException(Integer code,String message){
        super(message);//父类字段不能直接修改，但是可以调用方法
        this.code = code;
    }
}
```

再全局异常处理器中配置处理该异常

```java
/**
 * springmvc 提供了全局异常处理功能
 * `@ControllerAdvice`用于声明处理全局Controller方法异常的类
 * `@ExceptionHandler`用于声明处理异常的方法，`value`属性用于声明该方法处理的异常类型
 * `@ResponseBody`表示将方法的返回值作为HTTP的响应体
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result error(LeaseException e){

        e.printStackTrace();
        return Result.fail(e.getCode(),e.getMessage());
    }
}
```

要确保Result.fail()有该类型的构造器

```java
    public static <T> Result<T> fail(Integer code,String message) {
        Result<T> result = build(null);//data数据
        result.setCode(code);
        result.setMessage(message);
        return  result;
    }
```

将异常和异常信息定义为枚举类的实例，可读性，以及约束更好。

```java
  LambdaQueryWrapper<RoomInfo> roomInfoQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoQueryWrapper.eq(RoomInfo::getApartmentId,id);
        Long count = roomInfoMapper.selectCount(roomInfoQueryWrapper);
        if (count > 0){
            //提示有房间不要删除。
            throw  new LeaseException(ResultCodeEnum.DELETE_ERROR.getCode(),ResultCodeEnum.DELETE_ERROR.getMessage());
        }
```

代码太长了，我们可以直接传一个枚举类型，创建一个枚举类型的构造器。

```java
/**
 * 自定义异常处理 扩展RuntimeException
 */
@Data
public class LeaseException extends RuntimeException{
    private  Integer code;

    public LeaseException(Integer code,String message){
        super(message);//父类字段不能直接修改，但是可以调用方法
        this.code = code;
    }

    public LeaseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code=resultCodeEnum.getCode();
    }
}
```



#### 5.根据公寓id修改公寓状态

* 注意状态是枚举类型
  * 前端向后端发请求，请求参数类型都是String类型，不过由于springmvc中的webDatabinder，加上@RequestParam,可以将类型自动转化为后端接收的类型。不过对于枚举类型而说，WebDatabindhandler自动转化类型的原则是，前端传来的内容和，枚举实列的name相互映射。不过一般都是前后端规定数字表示状态，所以要自定义转换类型。前面业务已经定义了自定义转换类工厂规则。实现ConversetFact 重现converter方法。
  * 服务器向数据库操作，mybatis也提供了Java中实体对象和数据库之间数据类型自动转换，TypeHandler。比如Interger类到int,也提供了枚举类行的转换。不过默认是根据服务器传来的内容，匹配数据库枚举实例的名字，来匹配枚举类型。我们也要自定义枚举类型的转换规则，不过mp提供了注解@EnumValue。

#### 6. 根据区县id查询公寓列表

### 房间管理（未实现）



## 租赁管理

### 看房预约管理

![image-20240716140345675](images/README.assets/image-20240716140345675.png)

 #### 1.根据id修改预约状态

```java
    @Operation(summary = "根据id更新预约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam AppointmentStatus status) {
        LambdaUpdateWrapper<ViewAppointment> viewAppointmentUpdateWrapper = new LambdaUpdateWrapper<>();
        viewAppointmentUpdateWrapper.eq(ViewAppointment::getId,id)
                .set(ViewAppointment::getAppointmentStatus,status);
        viewAppointmentService.update(viewAppointmentUpdateWrapper);
        return Result.ok();
    }
```

#### 2.根据条件分页查询看房预约信息

**查看请求和响应的数据结构**

- **请求数据结构**

  - `current`和`size`为分页相关参数，分别表示**当前所处页面**和**每个页面的记录数**。

  - `AppointmentQueryVo`为看房预约的查询条件，详细结构如下：

  - ```java
    @Data
    @Schema(description = "预约看房查询实体")
    public class AppointmentQueryVo {
    
        @Schema(description="预约公寓所在省份")
        private Long provinceId;
    
        @Schema(description="预约公寓所在城市")
        private Long cityId;
    
        @Schema(description="预约公寓所在区")
        private Long districtId;
    
        @Schema(description="预约公寓所在公寓")
        private Long apartmentId;
    
        @Schema(description="预约用户姓名")
        private String name;
    
        @Schema(description="预约用户手机号码")
        private String phone;
    }
    ```

- 响应数据结构

  - ```java
    @Data
    @Schema(description = "预约看房信息")
    public class AppointmentVo extends ViewAppointment {
    
        @Schema(description = "预约公寓信息")
        private ApartmentInfo apartmentInfo;
    }
    ```

编写逻辑，利用分页插件，进行分页，自定义方法根据条件分页查询，返回响应类型的数据结构。实现自定义方法。

涉及两个表，一个ViewAppointment表，根据条件查询看房预约信息，根据看房预约的id再查询apartment_info表，再进行自定义映射要求的结果

```xml
<resultMap id="AppointmentVoMap" type="com.ls.lease.web.admin.vo.appointment.AppointmentVo" autoMapping="true">
        <id property="id" column="id"/>
        <association property="apartmentInfo" javaType="com.ls.lease.model.entity.ApartmentInfo" autoMapping="true">
            <id property="id" column="ai_id"/>
            <result property="name" column="ai_name"/>
            <result property="phone" column="ai_phone"/>
        </association>

    </resultMap>
    <select id="selectByQueeyVo" resultMap="AppointmentVoMap">
        select va.id,
               va.user_id,
               va.name,
               va.phone,
               va.apartment_id,
               va.appointment_time,
               va.additional_info,
               va.appointment_status,
               ai.id ai_id,
               ai.name ai_name,
               ai.introduction,
               ai.district_id,
               ai.district_name,
               ai.city_id,
               ai.city_name,
               ai.province_id,
               ai.province_name,
               ai.address_detail,
               ai.latitude,
               ai.longitude,
               ai.phone ai_phone,
               ai.is_release
        from view_appointment va
                 left join apartment_info ai
                           on va.apartment_id = ai.id and ai.is_deleted = 0

        <where>
            va.is_deleted = 0
            <if test="queryVo.provinceId != null">
                and ai.province_id =#{queryVo.provinceId}
            </if>
            <if test="queryVo.cityId != null">
                and ai.city_id = #{queryVo.cityId}
            </if>
            <if test="queryVo.districtId != null">
                and ai.district_id = #{queryVo.districtId}
            </if>
            <if test="queryVo.apartmentId != null">
                and ai.id =#{queryVo.apartmentId}
            </if>
            <if test="queryVo.name != null and queryVo.name != ''" >
                and va.name like concat('%',#{queryVo.name},'%')
            </if>
            <if test="queryVo.phone != null and queryVo.phone != ''">
                and va.phone like concat('%',#{queryVo.phone},'%')
            </if>
        </where>
    </select>
```

* concat拼接字符串  like模糊查询。

发现一个问题，在服务端响应请求端的时间格式不是我们想要的，这是前端在收到后端实体对象，json框架序列化的结果，怎么设置这个格式呢？

![image-20240716164232416](images/README.assets/image-20240716164232416.png)

**知识点**：

`ViewAppointment`实体类中的`appointmentTime`字段为`Date`类型，`Date`类型的字段在序列化成JSON字符串时，需要考虑两个点，分别是**格式**和**时区**。本项目使用JSON序列化框架为Jackson，具体配置如下

- **格式**

  格式可按照字段单独配置，也可全局配置，下面分别介绍

  - **单独配置**

    在指定字段增加`@JsonFormat`注解，如下

    ```java
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date appointmentTime;
    ```

  - **全局配置**

    在`application.yml`中增加如下内容

    ```yml
    spring:
      jackson:
        date-format: yyyy-MM-dd HH:mm:ss
    ```

- **时区** 

  时区要和

  时区同样可按照字段单独配置，也可全局配置，下面分别介绍

  - **单独配置**

    在指定字段增加`@JsonFormat`注解，如下

    ```java
    @JsonFormat(timezone = "GMT+8")
    private Date appointmentTime;
    ```

  - **全局配置**

    ```yml
    spring:
      jackson:
        time-zone: GMT+8
    ```

推荐格式按照字段单独配置，时区全局配置。

时区要和数据库一致，那么怎么看数据库时区呢？

进入mysql客户端

```sql
show variables like 'time_zone';
```

![image-20240716172928464](images/README.assets/image-20240716172928464.png)

显示时区是和系统有关

退出mysql客服端查看系统时区

```shell
timedatectl
```

![image-20240716173114175](images/README.assets/image-20240716173114175.png)

+800 就是东八区 



### 租约管理

#### 1.保存或更新租约信息

 查看请求数据结构，查看响应数据结构 都是在一张表里面。直接调用通用service即可

#### 2.根据条件分页查询租约列表

* 查看请求和响应数据结构

  * 请求数据结构

    * `current`和`size`为分页相关参数，分别表示**当前所处页面**和**每个页面的记录数**。

    * `AgreementQueryVo`为公寓的查询条件，详细结构如下：

    * ```java
      @Data
      @Schema(description = "租约查询实体")
      public class AgreementQueryVo {
      
          @Schema(description = "公寓所处省份id")
          private Long provinceId;
      
          @Schema(description = "公寓所处城市id")
          private Long cityId;
      
          @Schema(description = "公寓所处区域id")
          private Long districtId;
      
          @Schema(description = "公寓id")
          private Long apartmentId;
      
          @Schema(description = "房间号")
          private String roomNumber;
      
          @Schema(description = "用户姓名")
          private String name;
      
          @Schema(description = "用户手机号码")
          private String phone;
      }
      ```

  * 响应数据结构

    * ```java
      @Data
      @Schema(description = "租约信息")
      public class AgreementVo extends LeaseAgreement {
      
          @Schema(description = "签约公寓信息")
          private ApartmentInfo apartmentInfo;
      
          @Schema(description = "签约房间信息")
          private RoomInfo roomInfo;
      
          @Schema(description = "支付方式")
          private PaymentType paymentType;
      
          @Schema(description = "租期")
          private LeaseTerm leaseTerm;
      }
      ```

    * ```java
      @Schema(description = "租约信息表")
      @TableName(value = "lease_agreement")
      @Data
      public class LeaseAgreement extends BaseEntity {
      
          private static final long serialVersionUID = 1L;
      
          @Schema(description = "承租人手机号码")
          @TableField(value = "phone")
          private String phone;
      
          @Schema(description = "承租人姓名")
          @TableField(value = "name")
          private String name;
      
          @Schema(description = "承租人身份证号码")
          @TableField(value = "identification_number")
          private String identificationNumber;
      
          @Schema(description = "签约公寓id")
          @TableField(value = "apartment_id")
          private Long apartmentId;
      
          @Schema(description = "签约房间id")
          @TableField(value = "room_id")
          private Long roomId;
      
          @Schema(description = "租约开始日期")
          @JsonFormat(pattern = "yyyy-MM-dd")
          @TableField(value = "lease_start_date")
          private Date leaseStartDate;
      
          @Schema(description = "租约结束日期")
          @TableField(value = "lease_end_date")
          @JsonFormat(pattern = "yyyy-MM-dd")
          private Date leaseEndDate;
      
          @Schema(description = "租期id")
          @TableField(value = "lease_term_id")
          private Long leaseTermId;
      
          @Schema(description = "租金（元/月）")
          @TableField(value = "rent")
          private BigDecimal rent;
      
          @Schema(description = "押金（元）")
          @TableField(value = "deposit")
          private BigDecimal deposit;
      
          @Schema(description = "支付类型id")
          @TableField(value = "payment_type_id")
          private Long paymentTypeId;
      
          @Schema(description = "租约状态")
          @TableField(value = "status")
          private LeaseStatus status;
      
          @Schema(description = "租约来源")
          @TableField(value = "source_type")
          private LeaseSourceType sourceType;
      
          @Schema(description = "备注信息")
          @TableField(value = "additional_info")
          private String additionalInfo;
      
      }
      ```

  * 联表查询，主表即leaseagrement表，没有省市区id，根据条件查询需要省市区，所以连接apartment_info表，这次连接满足了条件查询需要的内容，同时满足了响应的数据结构需要的内容。

    然后连接room_info表获得签约房间信息，然后连接支付方式表，获得支付方式信息，然后连接租期表。



controller

```java
  @Operation(summary = "根据条件分页查询租约列表")
    @GetMapping("page")
    public Result<IPage<AgreementVo>> page(@RequestParam long current, @RequestParam long size, AgreementQueryVo queryVo) {
        Page<AgreementVo> page = new Page<>(current, size);
        IPage<AgreementVo> result = leaseAgreementService.pageAgreementVo(page,queryVo);
        return Result.ok(result);
    }
```

service

```java
public interface LeaseAgreementService extends IService<LeaseAgreement> {

    IPage<AgreementVo> pageAgreementVo(Page<AgreementVo> page, AgreementQueryVo queryVo);
}

```

mapper

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ls.lease.web.admin.mapper.LeaseAgreementMapper">
       <resultMap id="pageAgreementVoMap" type="com.ls.lease.web.admin.vo.agreement.AgreementVo" autoMapping="true">
              <id property="id" column="id"/>

              <association property="apartmentInfo" javaType="com.ls.lease.model.entity.ApartmentInfo" autoMapping="true">
                     <id property="id" column="aiId"/>
                     <result property="name" column="aiName"/>
                     <result property="phone" column="aiPhone"/>
              </association>
              <association property="roomInfo" javaType="com.ls.lease.model.entity.RoomInfo" autoMapping="true">
                     <id property="id" column="riId"/>
                     <result property="apartmentId" column="riApartmentId"/>
                     <result property="isRelease" column="riIsRelease"/>
              </association>
              <association property="paymentType" javaType="com.ls.lease.model.entity.PaymentType" autoMapping="true">
                     <id property="id" column="ptId"/>
                     <result property="name" column="PtName"/>
              </association>

              <association property="leaseTerm" javaType="com.ls.lease.model.entity.LeaseTerm" autoMapping="true">
                     <id property="id" column="ltId"/>
              </association>
       </resultMap>

    <select id="pageAgreementVo" resultMap="pageAgreementVoMap">
        select la.id,
               la.phone,
               la.name,
               la.identification_number,
               la.apartment_id,
               la.room_id,
               la.lease_start_date,
               la.lease_end_date,
               la.lease_term_id,
               la.rent,
               la.deposit,
               la.payment_type_id,
               la.status,
               la.source_type,
               la.additional_info,
               ai.id ai_id,
               ai.name ai_name,
               ai.introduction,
               ai.district_id,
               ai.district_name,
               ai.city_id,
               ai.city_name,
               ai.province_id,
               ai.province_name,
               ai.address_detail,
               ai.latitude,
               ai.longitude,
               ai.phone ai_phone,
               ai.is_release,
               ri.id ri_id,
               ri.room_number,
               ri.rent,
               ri.apartment_id ri_apartment_id,
               ri.is_release ri_is_release,
               pt.id pt_id,
               pt.name pt_name,
               pt.pay_month_count,
               pt.additional_info,
               lt.id lt_id,
               lt.month_count,
               lt.unit
        from lease_agreement la
                 left join apartment_info ai
                           on la.apartment_id = ai.id and ai.is_deleted = 0
                 left join room_info ri
                           on la.room_id = ri.id and ri.is_deleted = 0
                 left join payment_type pt
                           on la.payment_type_id = pt.id and pt.is_deleted = 0
                 left join lease_term lt
                           on la.lease_term_id = lt.id and lt.is_deleted = 0

           <where>
                  la.is_deleted=0
                  <if test="queryVo.provinceId !=null">
                       and  ai.province_id =#{queryVo.provinceId}
                  </if>
                  <if test="queryVo.cityId !=null">
                        and ai.city_id =#{queryVo.cityId}
                  </if>
                  <if test="queryVo.districtId !=null">
                        and ai.district_id =#{queryVo.districtId}
                  </if>
                  <if test="queryVo.apartmentId !=null">
                        and  la.apartment_id =#{queryVo.apartmentId}
                  </if>
                  <if test="queryVo.roomNumber !=null and queryVo.roomNumber !=''">
                        and ri.room_number like concat('%',#{queryVo.roomNumber},'%')
                  </if>
                  <if test="queryVo.name !=null and queryVo.name !=''">
                        and la.name like concat('%',#{queryVo.name},'%')
                  </if>
                  <if test="queryVo.phone !=null and queryVo.phone != ''">
                        and la.phone like concat('%',#{queryVo.phone},'%')
                  </if>
           </where>

    </select>
</mapper>

```



#### 3.根据id查询租约信息

因为是查询一个结果，可以直接查，查完在封装。

#### 4.根据id删除租约信息

只涉及一张表，通用service直接删

#### 5.根据id更新租约状态

 updatewrapper。 状态是枚举类型，前面已经定义了映射规则。



#### 6.定时检查租约状态

 定时检查租约信息，功能如何实现。

spring中提供了定时方法

* 启用Spring Boot定时任务

在SpringBoot启动类上增加`@EnableScheduling`注解，如下

```java
@SpringBootApplication
@EnableScheduling
public class AdminWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
    }
}
```

* 编写定时逻辑

```java
@Component
public class ScheduledTasks {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLeaseStatus() {

        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        Date now = new Date();
        updateWrapper.le(LeaseAgreement::getLeaseEndDate, now);
        updateWrapper.eq(LeaseAgreement::getStatus, LeaseStatus.SIGNED);
        updateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING);

        leaseAgreementService.update(updateWrapper);
    }
}
```

**知识点**:

SpringBoot中的cron表达式语法如下

```ini
  ┌───────────── second (0-59)
  │ ┌───────────── minute (0 - 59)
  │ │ ┌───────────── hour (0 - 23)
  │ │ │ ┌───────────── day of the month (1 - 31)
  │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
  │ │ │ │ │ ┌───────────── day of the week (0 - 7)
  │ │ │ │ │ │          (0 or 7 is Sunday, or MON-SUN)
  │ │ │ │ │ │
  * * * * * *
```

思路： 请用spring框架中的定时方法，方法的逻辑是没天00：00：00 检查在租状态的租约信息的租约结束日期。如果小于检查时日期就更新租约信息。

```java
@Component
public class ScheduleTask {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * * ")
    public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> leaseAgreementUpdateWrapper = new LambdaUpdateWrapper<>();
        leaseAgreementUpdateWrapper.le(LeaseAgreement::getLeaseEndDate, new Date())
                .in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING)
                .set(LeaseAgreement::getStatus, LeaseStatus.EXPIRED);
        leaseAgreementService.update(leaseAgreementUpdateWrapper);
    }
}
```

### 用户管理

![image-20240717102831091](images/README.assets/image-20240717102831091.png)

#### 1.根据条件分页查询用户列表

查看请求和响应数据结构

* 请求数据
  * `current`和`size`为分页相关参数，分别表示**当前所处页面**和**每个页面的记录数**。
  * `UserInfoQueryVo`为用户的查询条件，详细结构如下：

```java
@Schema(description = "用户信息查询实体")
@Data
public class UserInfoQueryVo {

    @Schema(description = "用户手机号码")
    private String phone;

    @Schema(description = "用户账号状态")
    private BaseStatus status;
}
```

* 响应数据
  * 单个用户表

实现思路是，controller掉service，servic调mapper，自定义mapper，动态sql，并且手机号模糊查询，状态精确查询。

```xml
 <select id="pageUserInfo" resultType="com.ls.lease.model.entity.UserInfo">
        select id,
               phone,
               avatar_url,
               nickname,
               status
        from user_info

        <where>
            is_deleted = 0
            <if test="queryVo.phone !=null and queryVo.phone != ''">
                and phone like concat('%',#{queryVo.phone},'%')
            </if>
            <if test="queryVo.status != null and queryVo.status !=''">
                and status = #{queryVo.status}
            </if>
        </where>

    </select>
```

但是测试时，前端传来状态值，发生报错.

```java
Error querying database.  Cause: java.lang.IllegalArgumentException: invalid comparison: com.ls.lease.model.enums.BaseStatus and java.lang.String
Cause: java.lang.IllegalArgumentException: invalid comparison: com.ls.lease.model.enums.BaseStatus and java.lang.String
```

这个错误信息表明在数据库查询过程中，你试图将一个枚举类型（`com.ls.lease.model.enums.BaseStatus`）与一个字符串（`java.lang.String`）进行比较，但这是不允许的，因为它们的类型不匹配。

sql的逻辑错了，枚举类不可能是空字符串。更改动态sql

```sql
select id,
               phone,
               avatar_url,
               nickname,
               status
        from user_info

        <where>
            is_deleted = 0
            <if test="queryVo.phone !=null and queryVo.phone != ''">
                and phone like concat('%',#{queryVo.phone},'%')
            </if>
            <if test="queryVo.status != null">
                and status = #{queryVo.status}
            </if>
        </where>

```

需要注意的是，status是int类型，status实是枚举类型，他们是怎么可以相比较的。

这里是mybatis的Typhandler，`EnumTypeHandler`是MyBatis默认的枚举转换器，它将枚举实例的`name()`方法返回的字符串作为值存入数据库，并从数据库中读取字符串后通过`Enum.valueOf(Class<T> enumType, String name)`方法转换回枚举实例。但是我们是通过code映射的，怎么办呢？可以自定义Tyhandler的映射规则。

不过MybatisPlus提供了一个[通用的处理枚举类型的TypeHandler](https://baomidou.com/pages/8390a4/)。其使用十分简单，只需在枚举类的`code`属性上增加一个注解`@EnumValue`，Mybatis-Plus便可完成从`枚举`对象到`code`属性之间的相互映射。

```java
public enum BaseStatus implements BaseEnum {

    ENABLE(1, "正常"),

    DISABLE(0, "禁用");

    @EnumValue
    @JsonValue
    private Integer code;

    private String name;

    BaseStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

```



#### 2.根据用户id更新账号状态



## 系统管理

![image-20240717121237200](images/README.assets/image-20240717121237200.png)

### 岗位管理

#### 1.分页查询岗位信息

#### 2.保存或更新岗位信息

#### 3.根据id删除岗位信息

#### 4.获取全部岗位列表

#### 5.根据id获取岗位信息

#### 6.根据id修改岗位状态





### 后台用户管理
![image-20240717140318493](images/README.assets/image-20240717140318493.png)

点击编辑,有个回显功能,即根据id查询后台用户信息.

![image-20240717140414788](images/README.assets/image-20240717140414788.png)

后台用户管理的接口

```java
@Tag(name = "后台用户信息管理")
@RestController
@RequestMapping("/admin/system/user")
public class SystemUserController {

    @Autowired
    SystemUserService service;
}
```





#### 1.分页查询后端用户信息

查看请求数据和响应数据

* **请求的数据结构**

  - `current`和`size`为分页相关参数，分别表示**当前所处页面**和**每个页面的记录数**。
  - `SystemUserQueryVo`为房间的查询条件，详细结构如下：

  ```java
  @Data
  @Schema(description = "员工查询实体")
  public class SystemUserQueryVo {
  
      @Schema(description= "员工姓名")
      private String name;
  
      @Schema(description= "手机号码")
      private String phone;
  }
  ```

* 响应的数据结构

  ```java
  @Data
  @Schema(description = "后台管理系统用户基本信息实体")
  public class SystemUserItemVo extends SystemUser {
  
      @Schema(description = "岗位名称")
      @TableField(value = "post_name")
      private String postName;
  }
  ```

* controller

```java
    @Operation(summary = "根据条件分页查询后台用户列表")
    @GetMapping("page")
    public Result<IPage<SystemUserItemVo>> page(@RequestParam long current, @RequestParam long size, SystemUserQueryVo queryVo) {
        Page<SystemUser> page = new Page<>(current, size);
        IPage<SystemUserItemVo> result =  systemUserService.selectSysUserpage(page,queryVo);
        return Result.ok(result);
    }
```

* service

```java
 IPage<SystemUserItemVo> selectSysUserpage(Page<SystemUser> page, SystemUserQueryVo queryVo);
@Service

public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>
        implements SystemUserService {
    
    @Autowired
    private SystemUserMapper mapper;

    @Override
    public IPage<SystemUserItemVo> selectSysUserpage(Page<SystemUser> page, SystemUserQueryVo queryVo) {
        return mapper.selectSysUserpage(page,queryVo);
    }
}
```

* mapper

```java
 IPage<SystemUserItemVo> selectSysUserpage(Page<SystemUser> page, SystemUserQueryVo queryVo);
 <select id="selectSysUserpage" resultType="com.ls.lease.web.admin.vo.system.user.SystemUserItemVo">
        select u.id,
               u.username,
               u.name,
               u.type,
               u.phone,
               u.avatar_url,
               u.additional_info,
               u.post_id,
               u.status,
               p.name post_name

        from system_user u
                 left join system_post p
                           on u.post_id = p.id and p.is_deleted=0

        <where>
            u.is_deleted=0
            <if test="queryVo.name != null and queryVo.name != ''">
                and username like concat('%',#{queryVo.name},'%')
            </if>
            <if test="queryVo.phone != null and queryVo.phone != ''" >
                and phone like  concat('%',#{queryVo.phone},'%')
            </if>
        </where>

    </select>
```



#### 2. 根据ID查询后台用户信息

* 查看请求和响应数据个格数

  * 请求数据格式 用户id

  * 响应数据格式

  * ```java'
    @Data
    @Schema(description = "后台管理系统用户基本信息实体")
    public class SystemUserItemVo extends SystemUser {
    
        @Schema(description = "岗位名称")
        @TableField(value = "post_name")
        private String postName;
    }
    ```

  * 和分页查询的返回数据结构一样,不过上个是个list,这个只有一个结果.

* 思路

  * 涉及多表,但是结果只有一个,可以在代码层面上查寻两次,封装结果,返回.

* controller

```java
    @Operation(summary = "根据ID查询后台用户信息")
    @GetMapping("getById")
    public Result<SystemUserItemVo> getById(@RequestParam Long id) {
        SystemUserItemVo result = systemUserService.getSysUSerById(id);
        return Result.ok(result);
    }

```

* service

```java
  SystemUserItemVo getSysUSerById(Long id);
   @Override
    public SystemUserItemVo getSysUSerById(Long id) {
        SystemUser systemUser = mapper.selectById(id);
        SystemPost systemPost = systemPostMapper.selectById(systemUser.getPostId());
        SystemUserItemVo systemUserItemVo = new SystemUserItemVo();
        BeanUtils.copyProperties(systemUser,systemUserItemVo);
        systemUserItemVo.setPostName(systemPost.getName());

        return systemUserItemVo;
    }
```

* 测试
* ![image-20240717152844251](images/README.assets/image-20240717152844251.png)

不应该显示密码,我们自定义sql的时候,可以写sql的时候不查该字段.那么使用通用mapper后怎么处理呢,

很简单在密码字段上加上注解如下

```java
    @TableField(value = "password",select = false)
    private String password;
```



#### 3. 保存或更新后台用户信息

单表-直接通用service

* controller

```java
    @Operation(summary = "保存或更新后台用户信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemUser systemUser) {

        if(systemUser.getPassword() != null){
            String md5password = DigestUtils.md5Hex(systemUser.getPassword()); //如果密码为空进行md5处理会异常
            systemUser.setPassword(md5password);
        }
        systemUserService.saveOrUpdate(systemUser);
        return Result.ok();
    }
```

需要注意的是,在处理密码时,一般都不会将明文存储在数据库中.

**知识点**：

- **密码处理**

  用户的密码通常不会直接以明文的形式保存到数据库中，而是会先经过处理，然后将处理之后得到的"密文"保存到数据库，这样能够降低数据库泄漏导致的用户账号安全问题。

  密码通常会使用一些单向函数进行处理，如下图所示

  ![image-20240717155458960](images/README.assets/image-20240717155458960.png)

  常用于处理密码的单向函数（算法）有MD5、SHA-256等，**Apache Commons**提供了一个工具类`DigestUtils`，其中就包含上述算法的实现。

  使用该工具类需引入`commons-codec`依赖，在**common模块**的pom.xml中增加如下内容

  ```xml
  <dependency>
      <groupId>commons-codec</groupId>
      <artifactId>commons-codec</artifactId>
  </dependency>
  ```

  ```java
    String md5password = DigestUtils.md5Hex(systemUser.getPassword()); 
  ```

  需要注意的是,在更新操作时,传来的密码值时null,那么执行通用方法,会将密码更新成null码?

  答案是不会的,

  * **Mybatis-Plus update strategy**

  使用Mybatis-Plus提供的更新方法时，若实体中的字段为`null`，默认情况下，最终生成的update语句中，不会包含该字段。若想改变默认行为，可做以下配置。	

  * 全局配置

  在`application.yml`中配置如下参数

  ```xml
  mybatis-plus:
    global-config:
      db-config:
        update-strategy: <strategy>
  ```

  **注**：上述`<strategy>`可选值有：`ignore`、`not_null`、`not_empty`、`never`，默认值为`not_null`

  - `ignore`：忽略空值判断，不管字段是否为空，都会进行更新
  - `not_null`：进行非空判断，字段非空才会进行判断
  - `not_empty`：进行非空判断，并进行非空串（""）判断，主要针对字符串类型
  - `never`：从不进行更新，不管该字段为何值，都不更新

  

  * 局部配置

    在实体类中的具体字段通过`@TableField`注解进行配置，如下：

    ```java
    @Schema(description = "密码")
    @TableField(value = "password", updateStrategy = FieldStrategy.NOT_EMPTY)
    private String password;
    ```

    

#### 4. 判断后台用户名是否可用

要求用户名不能重复

当输入的用户名不和数据库里面的用户名重复在可用

```java
    @Operation(summary = "判断后台用户名是否可用")
    @GetMapping("isUserNameAvailable")
    public Result<Boolean> isUsernameExists(@RequestParam String username) {
        LambdaQueryWrapper<SystemUser> systemUserQueryWrapper = new LambdaQueryWrapper<>();
        systemUserQueryWrapper.eq(SystemUser::getUsername,username);
        long count = systemUserService.count(systemUserQueryWrapper);

        return Result.ok(count ==0);
    }
```



#### 5. 根据ID删除后台用户信息

```java
    @DeleteMapping("deleteById")
    @Operation(summary = "根据ID删除后台用户信息")
    public Result removeById(@RequestParam Long id) {
        systemUserService.removeById(id);
        return Result.ok();
    }
```

#### 6. 根据ID修改后台用户状态

* 请求数据结构  id 和状态

controller

```java'
    @Operation(summary = "根据ID修改后台用户状态")
    @PostMapping("updateStatusByUserId")
    public Result updateStatusByUserId(@RequestParam Long id, @RequestParam BaseStatus status) {

        LambdaUpdateWrapper<SystemUser> systemUserUpdateWrapper = new LambdaUpdateWrapper<>();

        systemUserUpdateWrapper.eq(SystemUser::getId,id)
                        .set(SystemUser::getStatus,status);
        systemUserService.update(systemUserUpdateWrapper);
        return Result.ok();
    }
```



## 登录管理

### Session和Token认证

基于**Session**的认证和基于**Token**的认证是两种常见的用户身份认证方案，它们在实现机制、安全性、适用场景等方面各有特点。以下是对这两种认证方案的详细解析：
* 一基于Session的认证
  * 认证流程
    * 用户输入账号和密码进行登录。
    
    * 服务器验证用户信息，如果验证通过，则在服务端生成用户相关的数据保存在Session中（当前会话）。
    
    * 服务器将Session ID发送给客户端，并存储在客户端的Cookie中。
    
    * 客户端后续请求时，会带上Session ID，服务器通过验证Session ID来确认用户的身份和会话状态。
  
    * 当用户退出系统或Session过期销毁时，客户端的Session ID也随之失效。
  
* 优点
  * 实现简单，易于理解和维护。
  * Session信息存储在服务器端，相对安全。
* 缺点

  * 服务器需要维护大量的Session信息，增加了服务器的存储负担。
  * Session ID存储在客户端的Cookie中，如果Cookie被窃取，则存在安全风险。
  * Session认证通常依赖于客户端的Cookie，无状态的服务器无法直接识别用户身份。
* 适用场景
  * 适用于用户数量相对较少、服务器资源相对充足的应用场景。

  * 适用于需要频繁进行状态保持的应用场景，如Web应用中的用户登录状态保持。
*  二、基于Token的认证
*  认证流程
     *  用户输入账号和密码进行登录。
     *  服务器验证用户信息，如果验证通过，则生成一个Token（通常包含用户信息、过期时间等）并发送给客户端。
     *  客户端将Token存储在本地（LocalStorage等），并在后续请求中带上Token。
     *  服务器通过验证Token来确认用户的身份和权限。
     *  Token过期后，用户需要重新登录以获取新的Token。
* 优点

  + 服务器不需要存储Token信息，减轻了服务器的存储负担。
  + Token可以存储在客户端的多种存储介质中，提高了灵活性。
  + Token支持跨域认证，适用于分布式系统。

* 缺点

  + Token的验证需要服务器进行额外的计算，可能会增加服务器的处理负担。

* 适用场景
  + 适用于用户数量较多、服务器资源有限的应用场景。
  + 适用于需要跨域认证、分布式系统的应用场景。
  + 适用于对安全性要求较高、需要频繁进行身份验证的应用场景。

总结

  基于Session的认证和基于Token的认证各有优缺点，在选择认证方案时需要根据实际应用场景和需求进行综合考虑。例如，在需要频繁进行状态保持且用户数量相对较少的应用场景中，可以选择基于Session的认证；而在用户数量较多、需要跨域认证或分布式系统的应用场景中，则更适合选择基于Token的认证。



### Token详细

我们所说的Token，通常指**JWT**（JSON Web TOKEN）。JWT是一种轻量级的安全传输方式，用于在两个实体之间传递信息，通常用于身份验证和信息传递。

JWT是一个字符串，如下图所示，该字符串由三部分组成，三部分由`.`分隔。三个部分分别被称为

- `header`（头部）
- `payload`（负载）
- `signature`（签名）

![image-20240717180715253](images/README.assets/image-20240717180715253.png)

各部分的作用如下

* **Header（头部）**

  Header部分是由一个JSON对象经过`base64url`编码得到的，这个JSON对象用于保存JWT 的类型（`typ`）、签名算法（`alg`）等元信息，例如

  ```json
  {
    "alg": "HS256",
    "typ": "JWT"
  }
  ```

* **Payload（负载）**
  也称为 Claims（声明），也是由一个JSON对象经过`base64url`编码得到的，用于保存要传递的具体信息。JWT规范定义了7个官方字段，如下：

  - iss (issuer)：签发人
  - exp (expiration time)：过期时间
  - sub (subject)：主题
  - aud (audience)：受众
  - nbf (Not Before)：生效时间
  - iat (Issued At)：签发时间
  - jti (JWT ID)：编号

  除此之外，我们还可以自定义任何字段，例如
  
  ```json
  {
    "sub": "1234567890",
    "name": "John Doe",
    "iat": 1516239022
  }
  ```
  
* **Signature（签名）**
  
  由头部、负载和秘钥一起经过（header中指定的签名算法）计算得到的一个字符串，用于防止消息被篡改。

### 登录流程

后台管理系统的登录流程如下图所示

![image-20240719115547968](images/README.assets/image-20240719115547968.png)

根据上述登录流程，可分析出，登录管理共需三个接口，分别是**获取图形验证码**、**登录**、**获取登录用户个人信息**，除此之外，我们还需为所有受保护的接口增加验证JWT合法性的逻辑，这一功能可通过`HandlerInterceptor`来实现。

### 登录接口开发

#### 1. 获取图形验证码

* 查看响应数据

```java
@Data
@Schema(description = "图像验证码")
@AllArgsConstructor
public class CaptchaVo {

    @Schema(description="验证码图片信息")
    private String image;

    @Schema(description="验证码key")
    private String key;
}
```

  需要注意的是String image， 是传的图片的信息，是图片经过base64url编码获得得字符串。这个字符串，在html中可用直接用image标签解析。
* **配置所需依赖**

  * **验证码生成工具**

    本项目使用开源的验证码生成工具**EasyCaptcha**，其支持多种类型的验证码，例如gif、中文、算术等，并且简单易用，具体内容可参考其[官方文档](https://gitee.com/ele-admin/EasyCaptcha)。

    在**common模块**的pom.xml文件中增加如下内容

    ```xml
    <dependency>
        <groupId>com.github.whvcse</groupId>
        <artifactId>easy-captcha</artifactId>
    </dependency>
    ```

  * **Redis**

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
  ```

  在`application.yml`中增加如下配置

  ```yaml
  spring:
    data:
      redis:
        host: <hostname>
        port: <port>
        database: 0
  ```

* **controller**

  ```java
  @Operation(summary = "获取图形验证码")
  @GetMapping("login/captcha")
  public Result<CaptchaVo> getCaptcha() {
      CaptchaVo captcha = service.getCaptcha();
      return Result.ok(captcha);
  }
  ```

* **service**

  ```java
  @Service
  public class LoginServiceImpl implements LoginService {
  
      @Autowired
      private StringRedisTemplate redisTemplate;
  
      /**
       * 生成验证码图片和uuid
       * 加入redis 缓存 uuid， 验证码得值
       * 返回给前端uuid 和验证码图片
       * @return
       */
      @Override
      public CaptchaVo getCaptcha() {
  
          //获取验证码
          SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
  
          // 获取验证码得值，转小写，忽略大小写不同
          String code = specCaptcha.text().toLowerCase();
  
          //生成key
  //        String key = "admin:login" + UUID.randomUUID();
          //统一前缀,创建个类，
          String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();
  
  
          //加入redis缓存
          //统一管理
  //        redisTemplate.opsForValue().set(key,code,60, TimeUnit.SECONDS);
          redisTemplate.opsForValue().set(key,code,RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
  
  
  
          //图片转为字符传   base64编码
          String picture01 = specCaptcha.toBase64();
          return new CaptchaVo(picture01,key);
      }
  }
  
  ```

#### 2.登录接口

  - **录校验逻辑**

    用户登录的校验逻辑分为三个主要步骤，分别是**校验验证码**，**校验用户状态**和**校验密码**，具体逻辑如下

    - 前端发送`username`、`password`、`captchaKey`、`captchaCode`请求登录。
    - 判断`captchaCode`是否为空，若为空，则直接响应`验证码为空`；若不为空进行下一步判断。
    - 根据`captchaKey`从Redis中查询之前保存的`code`，若查询出来的`code`为空，则直接响应`验证码已过期`；若不为空进行下一步判断。
    - 比较`captchaCode`和`code`，若不相同，则直接响应`验证码不正确`；若相同则进行下一步判断。
    - 根据`username`查询数据库，若查询结果为空，则直接响应`账号不存在`；若不为空则进行下一步判断。
    - 查看用户状态，判断是否被禁用，若禁用，则直接响应`账号被禁`；若未被禁用，则进行下一步判断。
    - 比对`password`和数据库中查询的密码，若不一致，则直接响应`账号或密码错误`，若一致则进行入最后一步。
    - 创建JWT，并响应给浏览器。
    
- 请求数据结构

  ```java
  @Data
  @Schema(description = "后台管理系统登录信息")
  public class LoginVo {
  
      @Schema(description="用户名")
      private String username;
  
      @Schema(description="密码")
      private String password;
  
      @Schema(description="验证码key")
      private String captchaKey;
  
      @Schema(description="验证码code")
      private String captchaCode;
  }
  ```

- 响应数据结构 

  String 类型得  JWT

- **配置所需依赖**

  登录接口需要为登录成功的用户创建并返回JWT，本项目使用开源的JWT工具**Java-JWT**，配置如下，具体内容可参考[官方文档](https://github.com/jwtk/jjwt/tree/0.11.2)。

  ```xml
  <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-api</artifactId>
  </dependency>
  
  <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-impl</artifactId>
      <scope>runtime</scope>
  </dependency>
  
  <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-jackson</artifactId>
      <scope>runtime</scope>
  </dependency>
  ```

- 创建jwt工具类

  ```java
  public class JwtUtil {
      private static long tokenExpiration = 60 * 60 * 1000L;
      private static SecretKey secretKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());
  
      public static String createToken(Long userId, String username) {
          String token = Jwts.builder().
                  setSubject("USER_INFO").
                  setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).
                  claim("userId", userId).
                  claim("username", username).
                  signWith(secretKey, SignatureAlgorithm.HS256).
                  compact();
          return token;
      }
  }
  ```

- controller

  ```java
      @Operation(summary = "登录")
      @PostMapping("login")
      public Result<String> login(@RequestBody LoginVo loginVo) {
          String result = service.login(loginVo);
          return Result.ok(result);
      }
  ```

- service

  ```java
   @Override
      public String login(LoginVo loginVo) {
          //判断`captchaCode`是否为空，若为空，则直接响应`验证码为空`；若不为空进行下一步判断。
          if(loginVo.getCaptchaCode() == null){
              throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
          }
          //`captchaKey`从Redis中查询之前保存的`code`，若查询出来的`code`为空，则直接响应`验证码已过期`；若不为空进行下一步判断。
          String code = redisTemplate.opsForValue().get(loginVo.getCaptchaKey());
          if (code ==null){
              throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
          }
          //比较`captchaCode`和`code`，若不相同，则直接响应`验证码不正确`；若相同则进行下一步判断。
          if (!code.equals(loginVo.getCaptchaCode().toLowerCase())){
              throw  new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
          }
          //根据`username`查询数据库，若查询结果为空，则直接响应`账号不存在`；若不为空则进行下一步判断。
          LambdaQueryWrapper<SystemUser> systemUserQueryWrapper = new LambdaQueryWrapper<>();
          systemUserQueryWrapper.eq(SystemUser::getUsername,loginVo.getUsername());
          SystemUser systemUser = systemUserMapper.selectOne(systemUserQueryWrapper);
          if (systemUser == null){
              throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
          }
          //查看用户状态，判断是否被禁用，若禁用，则直接响应`账号被禁`；若未被禁用，则进行下一步判断。
          if (systemUser.getStatus()== BaseStatus.DISABLE){
              throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
  
          }
          //比对`password`和数据库中查询的密码，若不一致，则直接响应`账号或密码错误`，若一致则进行入最后一步。
          if (!systemUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword()))){
              throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
          }
  
  
          return JwtUtil.createToken(systemUser.getId(),systemUser.getUsername());
      }
  ```

  测试发现

  ![image-20240719171509764](images/README.assets/image-20240719171509764.png)

![image-20240719171538370](images/README.assets/image-20240719171538370.png)

空指针异常，没有password，这是因为我们之前设置得不查找密码字段，使用myabtisplus。要想查密码怎么办就自定义sql呗。

补充

校验jwt

我们需要为所有受保护的接口增加校验JWT合法性的逻辑。具体实现如下

```java
public class JwtUtil {
    private static long tokenExpiration = 60 * 60 * 1000L;
    private static SecretKey secretKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());

    public static String createToken(Long userId, String username) {
        String token = Jwts.builder().
                setSubject("USER_INFO").
                setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).
                claim("userId", userId).
                claim("username", username).
                signWith(secretKey, SignatureAlgorithm.HS256).
                compact();
        return token;
    }

    /**
     * 校验token
     * @param token
     */
    public static void parseToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
        }catch (ExpiredJwtException e){
            throw  new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        }catch (JwtException e){
            throw  new LeaseException(ResultCodeEnum.TOKEN_INVALID);

        }
    }
    
}
```

配置拦截器，内容如下，有关`HanderInterceptor`的相关内容，可参考[官方文档](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/handlermapping-interceptor.html)。

```java
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access-token");
        JwtUtil.parseToken(token);
        return true;
    }
}
```

**注意**：

我们约定，前端登录后，后续请求都将JWT，放置于HTTP请求的Header中，其Header的key为`access-token`。

**注册HandlerInterceptor**

在WebMvcConfiguration`中增加如下内容

```java
@Autowired
private AuthenticationInterceptor authenticationInterceptor;

@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login/**");
    }
```

**Knife4j配置**

在增加上述拦截器后，为方便继续调试其他接口，可以获取一个长期有效的Token，将其配置到Knife4j的全局参数中，如下图所示。

![image-20240719181018509](images/README.assets/image-20240719181018509.png)

#### 3. 获得登录用户信息

* **查看请求和响应的数据结构**

  * **响应的数据结构**

    ```java
    @Schema(description = "员工基本信息")
    @Data
    public class SystemUserInfoVo {
    
        @Schema(description = "用户姓名")
        private String name;
    
        @Schema(description = "用户头像")
        private String avatarUrl;
    }
    ```

  * **请求的数据结构**

    按理说，前端若想获取当前登录用户的个人信息，需要传递当前用户的`id`到后端进行查询。但是由于请求中携带的JWT中就包含了当前登录用户的`id`，故请求个人信息时，就无需再传递`id`。

* **修改`JwtUtil`中的`parseToken`方法**

  由于需要从Jwt中获取用户`id`，因此需要为`parseToken` 方法增加返回值，如下

  ```java
  public static Claims parseToken(String token){
  
      if (token==null){
          throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
      }
  
      try{
          JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
          return jwtParser.parseClaimsJws(token).getBody();
      }catch (ExpiredJwtException e){
          throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
      }catch (JwtException e){
          throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
      }  
  }
  ```

* controller

  ```java
      @Operation(summary = "获取登陆用户个人信息")
      @GetMapping("info")
      public Result<SystemUserInfoVo> info(@RequestHeader("access-token") String token) {
          //下面两行可以拿到用户id，但是这样就解析了两遍token，拦截器解析一次，这里又解析一次。
          Claims claims = JwtUtil.parseToken(token);
          Long userid = claims.get("userId", Long.class);
          SystemUserInfoVo result = service.getLoginUserByID(userid);
  
          return Result.ok(result);
      }
  }
  ```

* service

  ```java
      /**
       * 根据用户id获得用户信息
       * @param userid
       * @return
       */
      @Override
      public SystemUserInfoVo getLoginUserByID(Long userid) {
          SystemUser systemUser = systemUserMapper.selectById(userid);
  
          SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
          systemUserInfoVo.setAvatarUrl(systemUser.getAvatarUrl());
          systemUserInfoVo.setName(systemUser.getUsername());
          return systemUserInfoVo;
      }
  ```

  上述controller可以实现功能，但是有点小问题，因为我们配置了拦截器，所以会在拦截器，那里校验一次token，在controller为了得到token的数据，又校验一次。

  * 我们可以这样做
    * 在拦截器中，配置拦截路径，排除该路径的请求。
    * 在拦截器中获得token，并保存下来传递给请求。

第一种只需要在webconfig中的拦截器配置中，使用exculed就行

第二种，需要使用 ThreadLocal

> **ThreadLocal概述**
>
> ThreadLocal的主要作用是为每个使用它的线程提供一个独立的变量副本，使每个线程都可以操作自己的变量，而不会互相干扰，其用法如下图所示。
>
> ![image-20240720092642590](images/README.assets/image-20240720092642590.png)

创建线程工具类

```java
public class LoginUserHolder {
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    public static void setLoginUser(LoginUser loginUser) {
        threadLocal.set(loginUser);
    }

    public static LoginUser getLoginUser() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
```

LocalThread处理的对象

```java
@Data
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private String username;
}
```

修改`AuthenticationInterceptor`拦截器

```java
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access-token");

        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("UserId", Long.class);
        String username = claims.get("username", String.class);

        LoginUserHolder.setLoginUser(new LoginUser(userId,username));

        return  true;

    }

    /**
     * 清除线程信息，避免内存泄露
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
```

* 注意，请注意，虽然 `ThreadLocal` 很有用，但它也可能导致内存泄漏，特别是在使用线程池时。因为 `ThreadLocal` 变量是与线程关联的，所以如果线程被重用（如在线程池中），而 `ThreadLocal` 变量未被清除，那么这些变量就会一直存在，占用内存。因此，在使用完 `ThreadLocal` 变量后，应该总是调用 `remove()` 方法来清除它。，spingmvc就是使用线程池.



## 用户端开发

## 项目初始化

* Springboot配置

  ```yaml
  server:
    port: 8081
  ```

  创建启动类

  ```java
  @SpringBootApplication
  @MapperScan("com.ls.lease.web.app.mapper")
  public class AppWebApplication {
      public static void main(String[] args) {
          SpringApplication.run(AppWebApplication.class,args);
      }
  }
  
  ```

  

* Mybatis-Plus配置

  ```yaml
  spring:
    datasource:
      type: com.zaxxer.hikari.HikariDataSource
      url: jdbc:mysql://192.168.231.100:3306/lease?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2b8
      username: root
      password: Ls021223.
      hikari:
        connection-test-query: SELECT 1 # 自动检测连接
        connection-timeout: 60000 #数据库连接超时时间,默认30秒
        idle-timeout: 500000 #空闲连接存活最大时间，默认600000（10分钟）
        max-lifetime: 540000 #此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认1800000即30分钟
        maximum-pool-size: 12 #连接池最大连接数，默认是10
        minimum-idle: 10 #最小空闲连接数量
        pool-name: SPHHikariPool # 连接池名称
    jackson:
      time-zone: GMT+8
  #用于打印框架生成的sql语句，便于调试
  mybatis-plus:
    configuration:
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl 
  ```

* Knife4j配置

  在**web-app模块**下创建`com.ls.lease.web.app.custom.config.Knife4jConfiguration`类，内容如下：

  ```java
  @Configuration
  public class Knife4jConfiguration {
  
      @Bean
      public OpenAPI customOpenAPI() {
          return new OpenAPI()
                  .info(new Info()
                          .title("APP接口")
                          .version("1.0")
                          .description("用户端APP接口")
                          .termsOfService("http://doc.xiaominfo.com")
                          .license(new License().name("Apache 2.0")
                                  .url("http://doc.xiaominfo.com")));
      }
      
  
      @Bean
      public GroupedOpenApi loginAPI() {
          return GroupedOpenApi.builder().group("登录信息").
                  pathsToMatch("/app/login/**", "/app/info").
                  build();
      }
  
      @Bean
      public GroupedOpenApi personAPI() {
          return GroupedOpenApi.builder().group("个人信息").
                  pathsToMatch(
                          "/app/history/**",
                          "/app/appointment/**",
                          "/app/agreement/**"
                  ).
                  build();
      }
  
      @Bean
      public GroupedOpenApi lookForRoomAPI() {
          return GroupedOpenApi.builder().group("找房信息").
                  pathsToMatch(
                          "/app/apartment/**",
                          "/app/room/**",
                          "/app/payment/**",
                          "/app/region/**",
                          "/app/term/**"
                  ).
                  build();
      }
  }
  ```

  yaml新增如下

  ```yaml
  #方便接口测试-对于参数是对象来说，有的属性传有的属性不传值   
  springdoc:
    default-flat-param-object: true    
  ```

* 启动测试

  ![image-20240720114023330](images/README.assets/image-20240720114023330.png)

![image-20240720114032223](images/README.assets/image-20240720114032223.png)

创建minio失败，原因是，创建minio需要参数，我们配置minio的时候参数是从配置文件中获取的，我们可以在配置文件中添加参数，以便创建minio，但是该服务不需要使用minio服务，我们可以有另一种解决方法。

由于**common模块**中配置了**MinioClient**这个Bean，并且**web-app模块**依赖于**common模块**，因此在启动**AppWebApplication**时，SpringBoot会创建一个MinioClient实例，但是由于**web-app模块**的application.yml文件中并未提供MinioClient所需的参数（**web-app模块**暂时不需要使用MinioClient），因此MinioClient实例的创建会失败。

为解决该问题，可以为MinioClient的配置类增加一个条件注解`@ConditionalOnProperty`，如下，该注解表达的含义是只有当`minio.endpoint`属性存在时，该配置类才会生效。

```java
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(name = "minio.endpoint")
public class MinioConfiguration {

    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(properties.getEndpoint()).credentials(properties.getAccessKey(), properties.getSecretKey()).build();
    }
}
```

启动成功