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

START THE CLUSTER COCKROACH DATABASE

CHECK COCKROACH DONE IN FOLDER BIN
which cockroach

START THE CLUSTER COCKROACH DATABASE DONT FORGET DIRECTORY IN crdb

START FOR NODE 1 IN NEW TERMINAL 1
cd crdb
cockroach start --insecure --store=crdb-node-1 --listen-addr=localhost:26257 --http-addr=localhost:8081 --join=localhost:26257,localhost:26258,localhost:26259

START FOR NODE 2 IN NEW TERMINAL 2
cd crdb
cockroach start --insecure --store=crdb-node-2 --listen-addr=localhost:26258 --http-addr=localhost:8082 --join=localhost:26257,localhost:26258,localhost:26259

START FOR NODE 3 IN NEW TERMINAL 3
cd crdb
cockroach start --insecure --store=crdb-node-2 --listen-addr=localhost:26259 --http-addr=localhost:8083 --join=localhost:26257,localhost:26258,localhost:26259

INIT NODE CLUSTER IN NEW TERMINAL 4
cockroach init --insecure --host=localhost:26257

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

CREATE FILE FOR CONFIG.YAML
cd ztd
nano config.yaml

CREATE FILE FOR STEPS
cd ztd
sudo nano steps.yaml

EXPORT MASTERKEY FOR START ZITADEL
export ZITADEL_MASTERKEY="$(tr -dc A-Za-z0-9 </dev/urandom | head -c 32)"

SYNTAX FOR RUN ZITADEL WITH CONFIGURATION FILE WITH TLS OFF
zitadel start-from-init --config config-local.yaml --steps steps.yaml --masterkey "${ZITADEL_MASTERKEY}" --tlsMode disabled

LOGIN IN CONSOLE ZITADEL
username : root@zitadel.localhost
password : Password1!
```
