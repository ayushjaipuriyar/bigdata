FROM centos:7

# Install Java
RUN yum -y update && \
    yum -y install java-1.8.0-openjdk

# Install Hadoop
ADD https://downloads.apache.org/hadoop/common/hadoop-3.3.1/hadoop-3.3.1.tar.gz /opt/
RUN tar -xzvf /opt/hadoop-3.3.1.tar.gz -C /opt/ && \
    rm /opt/hadoop-3.3.1.tar.gz && \
    ln -s /opt/hadoop-3.3.1 /opt/hadoop

# Set environment variables
ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk
ENV HADOOP_HOME /opt/hadoop
ENV PATH $PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
