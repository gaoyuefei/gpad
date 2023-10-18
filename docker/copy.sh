#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20230706.sql ./mysql/db
cp ../sql/ry_config_20220929.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../gpad-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy gpad-gateway "
cp ../gpad-gateway/target/gpad-gateway.jar ./ruoyi/gateway/jar

echo "begin copy gpad-auth "
cp ../gpad-auth/target/gpad-auth.jar ./ruoyi/auth/jar

echo "begin copy gpad-visual "
cp ../gpad-visual/gpad-monitor/target/gpad-visual-monitor.jar  ./ruoyi/visual/monitor/jar

echo "begin copy gpad-modules-system "
cp ../gpad-modules/gpad-system/target/gpad-modules-system.jar ./ruoyi/modules/system/jar

echo "begin copy gpad-modules-file "
cp ../gpad-modules/gpad-file/target/gpad-modules-file.jar ./ruoyi/modules/file/jar

echo "begin copy gpad-modules-job "
cp ../gpad-modules/gpad-job/target/gpad-modules-job.jar ./ruoyi/modules/job/jar

echo "begin copy gpad-modules-gen "
cp ../gpad-modules/gpad-gen/target/gpad-modules-gen.jar ./ruoyi/modules/gen/jar

