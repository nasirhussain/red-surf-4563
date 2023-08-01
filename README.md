# Springboot Application (2.3.0)

## OCRWS Water Supply

This is a simple application used in the [fly.io](https://github.com/nasirhussain/red-surf-4563)  documentation showing how to deploy a Springboot application using Flyctl's Dockerfile deployment option. [Reference](https://www.geeksforgeeks.org/how-to-dockerize-a-spring-boot-application-with-maven/)

* Right click on pom.xml and Run as -> maven clean, then -> maven install
* In terminal run, fly auth login
* For creating app and deploying app run, fly launch (It uses Dockerfile and creates a fly.toml file)
* For deploying application run, fly deploy (It will use values from fly.toml file)

### Local deploy docker
To run application locally using docker

* Right click on pom.xml and Run as -> maven clean, then -> maven install
* In terminal run, docker build -t [name:tag] . Eg. (docker build -t fly-app-ocrws/1.0 .)
* docker run -d -p [host_port]:[container_port] -–name [container_name] {image_id/image_tag} Eg. (docker run -d -p 5000:5000 --name dockerspringboot fly-app-ocrws/1.0)

### Local run
To run application locally, Main.java -> Run As -> Java Application

