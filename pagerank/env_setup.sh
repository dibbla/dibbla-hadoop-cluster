# This is the setup script for the environment. This can be quite helpful when we
# keep turning the containers on and off without persisting the data.

# The script should be executed with sudo permissions.

# Change yum mirror (optional)
sudo mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.bak
sudo wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
sudo yum makecache

# Install JDK
sudo yum update
sudo yum install java-1.8.0-openjdk-devel
javac -version