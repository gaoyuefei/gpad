FROM  registry-vecps-ns.gaccloud.com.cn/tenant-yfuu/devops_library:openjdk_8u201

#把部署包放到容器的/root目录
ADD gpad-auth/target/gpad-auth.jar /root
#把配置文件放到容器的/root目录
ADD gpad-auth/src/main/resources/bootstrap.yml /root
#修改编码
ENV LANG=C.UTF-8 \
    LANGUAGE=zh_CN.UTF-8 \
    LC_ALL=C.UTF-8
# 启动
ENTRYPOINT java -jar -Duser.timezone=GMT+08 -Dspring.config.location=/root/bootstrap.yml /root/gpad-auth.jar