



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

       