#!/bin/bash

# RBAC 系统启动脚本

APP_NAME=rbac-system-1.0.0.jar
APP_HOME=$(cd "$(dirname "$0")"; pwd)
LOG_DIR=$APP_HOME/logs
LOG_FILE=$LOG_DIR/output.log

# 创建日志目录
mkdir -p $LOG_DIR

cd $APP_HOME

# 检查是否已运行
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "应用已在运行，PID: $PID"
    exit 1
fi

echo "正在启动 RBAC 系统..."

# 启动应用
nohup java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=$LOG_DIR/heapdump.hprof \
     -Dfile.encoding=UTF-8 \
     -Duser.timezone=Asia/Shanghai \
     -jar $APP_NAME \
     --spring.profiles.active=prod \
     > $LOG_FILE 2>&1 &

echo "应用启动中，请稍候..."
sleep 5

# 检查是否启动成功
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "=========================================="
    echo "应用启动成功！"
    echo "PID: $PID"
    echo "访问地址: http://localhost:8080"
    echo "日志文件: $LOG_FILE"
    echo "=========================================="
    echo ""
    echo "查看日志: tail -f $LOG_FILE"
    echo "停止应用: ./stop.sh"
else
    echo "=========================================="
    echo "应用启动失败！"
    echo "请查看日志: $LOG_FILE"
    echo "=========================================="
    exit 1
fi
