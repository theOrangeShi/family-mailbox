#!/bin/bash

# 家庭留言箱 - VPS远程部署脚本
# 使用方法: ./deploy_to_vps.sh [用户名] [SSH密钥路径]

# 配置
VPS_IP="YOUR_VPS_IP"
DOMAIN="YOUR_DOMAIN"
VPS_USER="YOUR_USERNAME"  # 默认使用root，可通过参数指定
SSH_KEY="YOUR_SSH_KEY_PATH"  # SSH密钥路径（必需）
REMOTE_PATH="/home/$VPS_USER/family-mailbox"

# 检查SSH密钥参数
if [ -z "$SSH_KEY" ]; then
    echo "❌ 错误: 缺少SSH密钥路径参数"
    echo ""
    echo "使用方法:"
    echo "  ./deploy_to_vps.sh [用户名] [SSH密钥路径]"
    echo ""
    echo "示例:"
    echo "  ./deploy_to_vps.sh root ~/.ssh/id_rsa"
    echo "  ./deploy_to_vps.sh ubuntu /path/to/your/key.pem"
    echo ""
    exit 1
fi

# 检查密钥文件是否存在
if [ ! -f "$SSH_KEY" ]; then
    echo "❌ 错误: SSH密钥文件不存在: $SSH_KEY"
    exit 1
fi

echo "=========================================="
echo "  家庭留言箱 VPS 部署脚本"
echo "=========================================="
echo "VPS IP: $VPS_IP"
echo "域名: $DOMAIN"
echo "用户: $VPS_USER"
echo "SSH密钥: $SSH_KEY"
echo "远程路径: $REMOTE_PATH"
echo "=========================================="
echo ""

# 1. 创建远程目录并上传服务器代码
echo "📦 [1/6] 创建目录并上传服务器代码..."

# 先创建远程目录
ssh -i "$SSH_KEY" "$VPS_USER@$VPS_IP" "mkdir -p $REMOTE_PATH"

if [ $? -ne 0 ]; then
    echo "❌ 创建目录失败！请检查SSH连接和权限"
    exit 1
fi

# 上传代码
scp -i "$SSH_KEY" -r server "$VPS_USER@$VPS_IP:$REMOTE_PATH/"

if [ $? -ne 0 ]; then
    echo "❌ 上传失败！请检查SSH连接"
    exit 1
fi

echo "✅ 代码上传成功"
echo ""

# 2. 在VPS上安装依赖并启动服务
echo "🔧 [2/6] 安装依赖并初始化..."
ssh -i "$SSH_KEY" "$VPS_USER@$VPS_IP" bash << ENDSSH
    set -e  # 遇到错误立即退出
    
    cd $REMOTE_PATH/server || exit 1
    
    # 检查并安装Python相关包
    echo "检查 Python 环境..."
    if ! dpkg -l | grep -q python3-venv; then
        echo "安装 Python3 和相关包..."
        sudo apt update
        sudo apt install -y python3 python3-pip python3-venv
    fi
    
    # 创建虚拟环境
    echo "创建虚拟环境..."
    python3 -m venv venv
    
    # 激活虚拟环境并安装依赖
    echo "安装依赖包..."
    source venv/bin/activate
    pip install --upgrade pip
    pip install -r requirements.txt
    
    echo "✅ 依赖安装完成"
ENDSSH

echo ""

# 3. 创建systemd服务
echo "🔧 [3/6] 配置系统服务..."
ssh -i "$SSH_KEY" "$VPS_USER@$VPS_IP" bash << ENDSSH
    sudo tee /etc/systemd/system/family-mailbox.service > /dev/null << 'EOF'
[Unit]
Description=Family Mailbox Server
After=network.target

[Service]
Type=simple
User=$VPS_USER
WorkingDirectory=$REMOTE_PATH/server
Environment="PATH=$REMOTE_PATH/server/venv/bin"
ExecStart=$REMOTE_PATH/server/venv/bin/gunicorn -w 4 -b 0.0.0.0:5001 app:app
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

    sudo systemctl daemon-reload
    sudo systemctl enable family-mailbox
    sudo systemctl start family-mailbox
    
    echo "✅ 服务已启动"
ENDSSH

echo ""

# 4. 配置防火墙
echo "🔒 [4/6] 配置防火墙..."
ssh -i "$SSH_KEY" "$VPS_USER@$VPS_IP" << 'ENDSSH'
    # 检查防火墙
    if command -v ufw &> /dev/null; then
        sudo ufw allow 80/tcp
        sudo ufw allow 443/tcp
        sudo ufw allow 5001/tcp
        echo "✅ UFW防火墙规则已添加"
    elif command -v firewall-cmd &> /dev/null; then
        sudo firewall-cmd --permanent --add-service=http
        sudo firewall-cmd --permanent --add-service=https
        sudo firewall-cmd --permanent --add-port=5001/tcp
        sudo firewall-cmd --reload
        echo "✅ Firewalld防火墙规则已添加"
    else
        echo "⚠️  未检测到防火墙，请手动配置开放端口 80, 443, 5001"
    fi
ENDSSH

echo ""

# 5. 安装Nginx
echo "🌐 [5/6] 配置Nginx反向代理..."
ssh -i "$SSH_KEY" "$VPS_USER@$VPS_IP" bash << ENDSSH
    # 安装Nginx
    if ! command -v nginx &> /dev/null; then
        echo "安装 Nginx..."
        sudo apt update
        sudo apt install -y nginx
    fi
    
    # 创建Nginx配置
    sudo tee /etc/nginx/sites-available/family-mailbox > /dev/null << 'EOF'
server {
    listen 80;
    server_name $DOMAIN;

    location / {
        proxy_pass http://127.0.0.1:5001;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # 增加超时时间
        proxy_connect_timeout 300;
        proxy_send_timeout 300;
        proxy_read_timeout 300;
    }

    # 增加上传文件大小限制
    client_max_body_size 100M;
}
EOF

    # 启用配置
    sudo ln -sf /etc/nginx/sites-available/family-mailbox /etc/nginx/sites-enabled/
    sudo rm -f /etc/nginx/sites-enabled/default
    
    # 测试配置
    sudo nginx -t
    sudo systemctl restart nginx
    
    echo "✅ Nginx配置完成"
ENDSSH

echo ""

# 6. 配置HTTPS
echo "🔐 [6/6] 配置HTTPS证书..."
ssh -i "$SSH_KEY" "$VPS_USER@$VPS_IP" bash << ENDSSH
    # 安装Certbot
    if ! command -v certbot &> /dev/null; then
        echo "安装 Certbot..."
        sudo apt update
        sudo apt install -y certbot python3-certbot-nginx
    fi
    
    # 获取SSL证书
    echo "正在申请SSL证书..."
    sudo certbot --nginx -d $DOMAIN --non-interactive --agree-tos --email admin@$DOMAIN --redirect
    
    if [ \$? -eq 0 ]; then
        echo "✅ HTTPS证书配置成功"
    else
        echo "⚠️  HTTPS配置失败，请手动运行: sudo certbot --nginx -d $DOMAIN"
    fi
ENDSSH

echo ""
echo "=========================================="
echo "🎉 部署完成！"
echo "=========================================="
echo ""
echo "服务器地址："
echo "  HTTP:  http://$DOMAIN"
echo "  HTTPS: https://$DOMAIN"
echo ""
echo "管理命令："
echo "  查看状态: ssh -i $SSH_KEY $VPS_USER@$VPS_IP 'sudo systemctl status family-mailbox'"
echo "  查看日志: ssh -i $SSH_KEY $VPS_USER@$VPS_IP 'sudo journalctl -u family-mailbox -f'"
echo "  重启服务: ssh -i $SSH_KEY $VPS_USER@$VPS_IP 'sudo systemctl restart family-mailbox'"
echo ""
echo "测试连接："
echo "  curl https://$DOMAIN/api/health"
echo ""
echo "=========================================="

