#!/bin/bash
sudo apt update && sudo apt upgrade -y
java_installed=$(java -version 2>&1)
if [["$java_installed"==*"not found"]]
    then
        echo "Cliente não possui java java instalado!"
    sudo apt install openjdk-17-jre -y
    else
        echo "Cliente possui java java instalado!"
        echo "Atualizando"
        sudo apt install openjdk-17-jre -y
fi
    # Add Docker's official GPG key:
    sudo apt-get update
    sudo apt-get install ca-certificates curl -y
    sudo install -m 0755 -d /etc/apt/keyrings
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
    echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update

    sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y
    sudo systemctl start docker 
    sudo systemctl enable docker
    sudo usermod -aG docker ubuntu
    sudo docker pull nogmaycon/maycon-datasight-mysql:1.0
    sudo docker stop datasight-mysql
    sudo docker rm datasight-mysql
    sudo docker run -d -p 3308:3306  --name datasight-mysql nogmaycon/maycon-datasight-mysql:1.0
    sudo docker pull nogmaycon/maycon-datasight-01:latest
    sudo docker stop maycon-datasight-01
    sudo docker rm maycon-datasight-01
    sudo docker run -d -p 8080:8080 --name springboot nogmaycon/maycon-datasight-01:latest
    
    cd Java/target
    sudo chmod 700 DataSight-1.0-SNAPSHOT-jar-with-dependencies.jar
    java -jar DataSight-1.0-SNAPSHOT-jar-with-dependencies.jar
