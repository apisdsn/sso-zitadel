## Installation CockroachDB

Install CockroachDB with system operation Ubuntu 22.04 LTS

```bash
FOR ALL INSTALL ROOT ACCESS
sudo su root:root

CREATE DIRECTORY FOR COCKROACH DATABASE
mkdir crdb

CHANGE DIRECTORY TO crdb
cd crdb

DOWNLOAD COCKROACH DB
wget -qO- https://binaries.cockroachdb.com/cockroach-v23.1.13.linux-amd64.tgz | tar xvz && cp -i cockroach-v23.1.13.linux-amd64/cockroach /usr/local/bin/

CHECK COCKROACH DONE IN FOLDER BIN
which cockroach

CREATE FOLDER FOR CERTS
mkdir certs my-safe-directory

CREATE CERTIFICATE AUTHORITY
cockroach cert create-ca --certs-dir=certs --ca-key=my-safe-directory/ca.key

CREATE CERTIFICATE AND KEY PAIR FOR NODES
cockroach cert create-node localhost $(hostname) --certs-dir=certs --ca-key=my-safe-directory/ca.key

CREATE A CLIENT CERTIFICATE AND KEY PAIR FOR ROOT USER
cockroach cert create-client root --certs-dir=certs --ca-key=my-safe-directory/ca.key

CREATE A CLIENT CERTIFICATE AND KEY PAIR FOR mamangdasbor USER
cockroach cert create-client mamangdasbor --certs-dir=certs --ca-key=my-safe-directory/ca.key

START AND CERTIFICATE THE CLUSTER COCKROACH DATABASE DONT FORGET DIRECTORY IN crdb

START FOR NODE 1 IN NEW TERMINAL 1
cd crdb
cockroach start --certs-dir=certs --store=crdb-node-1 --listen-addr=localhost:26257 --http-addr=localhost:8081 --join=localhost:26257,localhost:26258,localhost:26259

START FOR NODE 2 IN NEW TERMINAL 2
cd crdb
cockroach start --certs-dir=certs --store=crdb-node-2 --listen-addr=localhost:26258 --http-addr=localhost:8082 --join=localhost:26257,localhost:26258,localhost:26259

START FOR NODE 3 IN NEW TERMINAL 3
cd crdb
cockroach start --certs-dir=certs --store=crdb-node-3 --listen-addr=localhost:26259 --http-addr=localhost:8083 --join=localhost:26257,localhost:26258,localhost:26259

INIT NODE CLUSTER IN NEW TERMINAL 4
cockroach init --certs-dir=certs --host=localhost:26257

USE SQL CONSOLE COCKROACH DATABASE IN TERMINAL 4
cockroach sql --certs-dir=certs --host=localhost:26257

USE SQL FOR ADD USER FOR ACCESS DB CONSOLE
CREATE USER mamangdasbor WITH PASSWORD ‘ayam1232.’;

USE SQL FOR GRANTS ROLES FOR USERNAME mamangdasbor
GRANT admin TO mamangdasbor;

AND EXIT TO SQL COCKROACH CONSOLE
\q

LOGIN FOR COCKROACHDB CONSOLE
username : mamangdasbor
password : ayam1232.
```

### INSTALL ZITADEL SINGLE SIGN ON

Install Zitadel with system operation Ubuntu 22.04 LTS

```bash
CREATE DIRECTORY FOR ZITADEL SINGLE SIGN ON
mkdir ztd
cd ztd

DOWNLOAD ZITADEL AND EXTRACT TO SERVER
LATEST=$(curl -i https://github.com/zitadel/zitadel/releases/latest | grep location: | cut -d '/' -f 8 | tr -d '\r'); ARCH=$(uname -m); case $ARCH in armv5*) ARCH="armv5";; armv6*) ARCH="armv6";; armv7*) ARCH="arm";; aarch64) ARCH="arm64";; x86) ARCH="386";; x86_64) ARCH="amd64";;  i686) ARCH="386";; i386) ARCH="386";; esac; wget -c https://github.com/zitadel/zitadel/releases/download/$LATEST/zitadel-linux-$ARCH.tar.gz -O - | tar -xz && sudo mv zitadel-linux-$ARCH/zitadel /usr/local/bin

CHECK ZITADEL DONE IN FOLDER BIN
which zitadel

CONFIGURATION FOR NGINX SSOI2DEV

ENABLE UFW
ufw allow ssh
ufw allow ‘Nginx Full’
ufw allow ‘Nginx HTTP’
ufw allow ‘Nginx HTTPS’
ufw enable

INSTALL NGINX REVERSE PORXT
apt install nginx

CREATE AND COPY CONFIGURATION NGINX
cd /etc/nginx/sites-available/

CREATE FILE FOR CONFIGURATION
nano ssoi2dev

COPY CONFIGURATION TO ssoi2dev
server {
        listen [::]:443 ssl http2;
        listen 443 ssl http2;
        server_name sso.i2dev.my.id;
        ssl_certificate /etc/letsencrypt/live/sso.i2dev.my.id/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/sso.i2dev.my.id/privkey.pem;
        ssl_ciphers EECDH+CHACHA20:EECDH+AES128:RSA+AES128:EECDH+AES256:RSA+AES256:EECDH+3DES:RSA+3DES:!MD5;
        access_log /var/log/nginx/access.log logger-json;
        error_log /var/log/nginx/error.log;
location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;

        grpc_pass grpc://localhost:8080;
        grpc_set_header Host $host:$server_port;
    }
}
AFTER PASTE CONFIGURATION AND SYMBOLIC LINK TO DIRECTORY
ln -s /etc/nginx/sites-available/ssoi2dev /etc/nginx/sites-enabled/

CREATE SSL CERTBOT FOR DOMAIN
apt install certbot python3-certbot-nginx
certbot --nginx -d sso.i2dev.my.id

TEST CONFIGURATION NGINX AND RESTART SERVICE NGINX
nginx -t
systemctl restart nginx

EXPORT MASTERKEY FOR START ZITADEL
export ZITADEL_MASTERKEY="$(tr -dc A-Za-z0-9 </dev/urandom | head -c 32)"

CREATE FILE FOR CONFIG.YAML
cd ztd
nano config.yaml

CREATE FILE FOR STEPS
cd ztd
nano steps.yaml

SYNTAX FOR RUN ZITADEL WITH CONFIGURATION FILE WITH TLS EXTERNAL
zitadel start-from-init --config config-deploy.yaml --steps steps.yaml --masterkey "${ZITADEL_MASTERKEY}" --tlsMode external

LOGIN IN CONSOLE ZITADEL
in the above username, replace localhost with your configured external domain, if any. e.g. with root@zitadel.sso.my.domain.tld
username : root@zitadel.sso.my.domain.tld
password : Password1!
```
