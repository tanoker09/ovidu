#!/bin/bash

set -e

# The output of running this file can be typicaly found
# in the EC2 instance file /var/log/cloud-init-output.log

# Based on https://docs.openvidu.io/en/2.16.0/deployment/deploying-on-premises/

sudo apt update
sudo apt upgrade -y
sudo apt install -y mc htop silversearcher-ag vim

# Install Docker
sudo apt install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Install docker-compose
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Install OpenVidu
cd /opt && curl https://s3-eu-west-1.amazonaws.com/aws.openvidu.io/install_openvidu_2.16.0.sh | sudo bash

# Configure OpenVidu
DOMAIN_OR_PUBLIC_IP=
OPENVIDU_SECRET=
sudo sed -i "s/^DOMAIN_OR_PUBLIC_IP=.*/DOMAIN_OR_PUBLIC_IP=$DOMAIN_OR_PUBLIC_IP/" /opt/openvidu/.env
sudo sed -i "s/^OPENVIDU_SECRET=.*/OPENVIDU_SECRET=$OPENVIDU_SECRET/" /opt/openvidu/.env
sudo sed -i "s/^CERTIFICATE_TYPE=.*/CERTIFICATE_TYPE=letsencrypt/" /opt/openvidu/.env
sudo sed -i "s/^LETSENCRYPT_EMAIL=.*/LETSENCRYPT_EMAIL=noreply@nsalab.org/" /opt/openvidu/.env
sudo sed -i "s/^OPENVIDU_RECORDING=.*/OPENVIDU_RECORDING=true/" /opt/openvidu/.env
sudo sed -i "s/^OPENVIDU_RECORDING_PUBLIC_ACCESS=.*/OPENVIDU_RECORDING_PUBLIC_ACCESS=false/" /opt/openvidu/.env
sudo sed -i "s/^OPENVIDU_RECORDING_AUTOSTOP_TIMEOUT=.*/OPENVIDU_RECORDING_AUTOSTOP_TIMEOUT=1200000/" /opt/openvidu/.env

# Disable default videoconference application
cd /opt/openvidu && sudo mv docker-compose.override.yml docker-compose.override.yml.bak

# Start OpenVidu
cd /opt/openvidu && sudo docker-compose up -d
