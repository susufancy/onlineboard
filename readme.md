# readme

### 1. 环境要求

##### windows 10

PHP 7.4.12 (cli) (built: Oct 27 2020 17:18:47) ( ZTS Visual C++ 2017 x64 )

Java(TM) SE Runtime Environment (build 1.8.0_271-b09)

##### Linux:

PHP 7.2.24-0ubuntu0.18.04.7 (cli) (built: Oct  7 2020 15:24:25) ( NTS )
		J2SE-1.5 (OpenJDK 11.0.2)

##### Macos:

PHP 7.3.22-(cli) (built: Oct 30 2020 00:19:11) ( NTS )
		J2SE-1.5 (OpenJDK 11.0.2)	

以上为测试环境 PHP 和 Java 版本，理论上安装好 PHP 和 jre/jdk 并配置好环境变量即可

### 2. Quick Start

#### 编译源码执行:

测试环境：
		1、使用eclipse IDE新建Maven Project
		2、import工程文件夹 source_code/httpserver
		3、调整build path为J2SE-1.5 (OpenJDK 11.0.2)
		4、maven build并运行



####可执行文件：

##### Windows:

运行 `source_code/Server/start.bat` 即可

##### Macos&Linux:

```shell
cd source_code/Server
php server.php start
```

打开source_code/app.jar即可

#### 3. 文件说明

source code：源代码及可执行文件

- httpserver：http服务器源代码
- Server：php 后端服务器源代码和 Workerman 框架源代码
- Server/server.php：php 后端服务器源代码
- src：管理画板和用户画板html文件
- app.jar：http服务器可执行文件
- start.bat：windows命令行脚本，用于开启后端php服务器并运行 http 服务器提供画板网页

report：项目报告

slide：项目展示ppt

