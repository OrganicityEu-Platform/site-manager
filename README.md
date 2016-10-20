# OrganiCity Facility Management Portal

A REST server and the associated web frontend for managing OrganiCity sites.


## Quick start

### 1 Build the project

```
git clone git@github.com:OrganicityEu-Platform/site-manager.git
cd manager
mvn clean install
```

### 2 Configure

Create an application.properties file with the following content (update it!)

```
# server configuration
organicity.server.protocol=http
organicity.server.host=localhost 
organicity.server.localport=80
organicity.server.exposedport=80

# OpenID client credentials, for getting roles on the OC perm server
organicity.backend.certificate=/home/me/cert.pem
organicity.backend.client-id=your_user_id
organicity.backend.client-password=your_user_password
```

### 3 Run your server !

java -jar target/site-manager-0.0.1-SNAPSHOT.jar

Browse `http://localhost:8080` (with this configuration file)


## Contributor start

Because we are using Spring, you will need:
 * [Spring Tool Suite IDE](https://spring.io/tools/sts/all)
 * [Lombok](https://projectlombok.org/download.html) just run the jar once to patch STS, and have the Lombok annotations recognized.
