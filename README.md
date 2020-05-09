# LocalAuth [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Is a forward authentication api and front end service.

## Usage
**Program arguments**
* <port> 8080
* <proxy_path> /auth
* <jdbc_url> jdbc:mysql://192.168.1.125:3306/local_auth?user=<USERNAME>&password=<PASSWORD>&useSSL=false&allowPublicKeyRetrieval=true
* <domain> localhost

## Building
```bash
docker build --no-cache --build-arg SSH_FILE=ssh_private.txt --build-arg HOST='s/$HOST_URL/sub.domain.net/g' -t localauth:localauth .
```