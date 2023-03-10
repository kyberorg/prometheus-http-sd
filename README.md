# Prometheus HTTP Service Discovery with UI

Simple prometheus service discovery with UI.

## Features
* Web UI to configure targets, labels, files
* It allows to add/edit targets without reloading Prometheus.
* Generating JSONs with targets on the fly
* Support managing targets in a structural way (by storing them in different files)
* Admin page to manage targets, labels, files (TBD)

## Install
There are 2 options how to install it: `systemd daemon` and `docker` (TDB)

### SystemD Daemon
#### Pre-requests 
* Java 17 (JRE)
* Database: build-in file-based HSQLDB or JDBC-compatible external database (Postgres, MySQL, MariaDB).

#### Installation
* First, you need an application directory. 
```shell
mkdir /opt/http-sd
cd /opt/http-sd
```
* Make Database directory (if you will use file-based HSQLDB)
```shell
mkdir /opt/http-sd/db
```
* Download JAR from [Releases](https://github.com/kyberorg/prometheus-http-sd/releases). 
  * `http-sd.jar` is universal JAR with DB Drivers included.
  * `http-sd-postgres.jar` is Postgres-specific version (only postgres driver included). 
* Create application configuration
```shell
  vim app-config.env
```
```
# Database (file-based HSQLDB)
DB_FOLDER=/opt/httpd-sd/db
DB_NAME=db-name
DB_USER=db-user-here
DB_PASSWORD=db-password-here
```
```
# Database (Postgres)
DB_HOST=db-host
DB_NAME=db-name
DB_SCHEMA=public-or-another-schema
DB_USER=db-user-here
DB_PASSWORD=db-password-here
```
* Add following lines to `app-config.env` to enable authorization
```
# Authorization (optional)
AUTH_ENABLED=true
AUTH_USER=my-user
AUTH_PASSWORD=my-password
```
* Add SystemD Daemon configuration
```shell
vim /etc/systemd/system/http-sd.service
```
```
[Unit]
Description=Prometheus HTTP Service Discovery
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
Restart=always
RestartSec=5s
WorkingDirectory=/opt/httpd-sd
ExecStart=java -jar http-sd.jar --spring.profiles.active=db #replace it with hsqldb,postgres,mysql or mariadb
EnvironmentFile=/opt/httpd-sd/app-config.env

[Install]
WantedBy=multi-user.target
```

* Start and enable it. First start might be a bit longer as it populates database.
```shell
systemctl daemon-reload
systemctl enable --now http-sd
```

* Finally, add it to `prometheus.yaml`
```
- job_name: 'my-job'
    metrics_path: "/probe"
    params:
      module: [https_monitor]
    http_sd_configs:
    - url: 'http://localhost:8080/targets.json'

```

## How to build ?
### With Docker

### Without Docker
* Build (with all drivers included)
```shell
 mvn clean package -P production
```

* Build (with Postgres Driver only)
```shell
 mvn clean package -P production, postgres, -all
```

