



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
  
  



# 公寓信息管理接口开发

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

