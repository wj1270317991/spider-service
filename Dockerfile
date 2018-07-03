FROM java:8
ENV TZ="Asia/Shanghai"

WORKDIR /root
ADD service/target/spider.service-1.0.0-SNAPSHOT.jar /app.jar

RUN bash -c 'touch /app.jar ;touch /root/app-gc.log '
ENV JAVA_OPTS=""

CMD exec java -server -Xss256k $JAVA_OPTS \
  -XX:SurvivorRatio=10 \
  -XX:+UseConcMarkSweepGC  -XX:CMSMaxAbortablePrecleanTime=5000 -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80 \
  -XX:+UseCMSInitiatingOccupancyOnly \
  -XX:+DisableExplicitGC \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/root/app-java.hprof \
  -verbose:gc -Xloggc:/root/app-gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
  -Djava.awt.headless=true \
  -Dsun.net.client.defaultConnectTimeout=10000 \
  -Dsun.net.client.defaultReadTimeout=30000 \
  -Djava.security.egd=file:/dev/./urandom -jar /app.jar $*

