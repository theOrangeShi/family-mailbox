#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
家庭留言箱服务器端
Family Mailbox Server API
"""

from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
import sqlite3
import os
import base64
from datetime import datetime
import uuid

app = Flask(__name__)
CORS(app)

# 配置
DATABASE = 'mailbox.db'
UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

def get_db():
    """获取数据库连接"""
    db = sqlite3.connect(DATABASE)
    db.row_factory = sqlite3.Row
    return db

def init_db():
    """初始化数据库"""
    db = get_db()
    db.execute('''
        CREATE TABLE IF NOT EXISTS messages (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            text TEXT NOT NULL,
            attachment_path TEXT,
            attachment_type TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    ''')
    db.commit()
    db.close()

@app.route('/api/messages', methods=['GET'])
def get_messages():
    """获取所有消息"""
    try:
        db = get_db()
        cursor = db.execute('SELECT id, text, attachment_path, attachment_type, created_at FROM messages ORDER BY id DESC')
        messages = []
        for row in cursor.fetchall():
            messages.append({
                'id': row['id'],
                'text': row['text'],
                'attachmentPath': row['attachment_path'],
                'attachmentType': row['attachment_type'],
                'createdAt': row['created_at']
            })
        db.close()
        return jsonify({'success': True, 'messages': messages})
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/messages', methods=['POST'])
def create_message():
    """创建新消息"""
    try:
        data = request.json
        text = data.get('text', '')
        attachment_path = None
        attachment_type = None
        
        # 处理附件（如果有）
        if 'attachment' in data and data['attachment']:
            attachment_data = data['attachment']
            attachment_type = data.get('attachmentType')
            
            # 生成唯一文件名
            file_ext = get_file_extension(attachment_type)
            filename = f"{uuid.uuid4()}{file_ext}"
            file_path = os.path.join(UPLOAD_FOLDER, filename)
            
            # 解码并保存文件
            file_content = base64.b64decode(attachment_data)
            with open(file_path, 'wb') as f:
                f.write(file_content)
            
            attachment_path = filename
        
        # 保存到数据库
        db = get_db()
        cursor = db.execute(
            'INSERT INTO messages (text, attachment_path, attachment_type) VALUES (?, ?, ?)',
            (text, attachment_path, attachment_type)
        )
        message_id = cursor.lastrowid
        db.commit()
        db.close()
        
        return jsonify({
            'success': True,
            'message': {
                'id': message_id,
                'text': text,
                'attachmentPath': attachment_path,
                'attachmentType': attachment_type
            }
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/attachments/<filename>', methods=['GET'])
def get_attachment(filename):
    """获取附件文件"""
    try:
        file_path = os.path.join(UPLOAD_FOLDER, filename)
        if os.path.exists(file_path):
            return send_file(file_path)
        else:
            return jsonify({'success': False, 'error': 'File not found'}), 404
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health_check():
    """健康检查"""
    return jsonify({'success': True, 'status': 'running'})

def get_file_extension(attachment_type):
    """根据附件类型获取文件扩展名"""
    ext_map = {
        'image': '.jpg',
        'video': '.mp4',
        'file': ''
    }
    return ext_map.get(attachment_type, '')

if __name__ == '__main__':
    init_db()
    # 生产环境建议使用 gunicorn 或 uwsgi
    # 使用端口 5001 避免与 macOS AirPlay 服务冲突
    app.run(host='0.0.0.0', port=5001, debug=True)



