#!/bin/bash
# 本地开发环境启动脚本

echo "🚀 启动家庭留言箱服务器（本地调试模式）..."

# 进入服务器目录
cd "$(dirname "$0")"

# 使用 conda 环境运行
/opt/miniconda3/envs/family-mailbox/bin/python app.py


