# -*- coding: utf-8 -*-
"""
Created on Thu Dec  7 21:16:46 2017

@author: Eibhlín
"""
import os
import logging
import random
from urllib.parse import unquote
import tornado.ioloop
import tornado.web
import tornado.gen
from tornado import options
from tornado.escape import json_encode, json_decode
import shelve
import datetime
import shutil
import time
import string
import random
import pathlib
import hashlib
import argparse
import requests

'''
MainHandler Class; Inherits from tornado.web.RequestHandler
Used for common methods across all other Handlers
'''
class MainHandler(tornado.web.RequestHandler):

    def send_json_cors_headers(self):

        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')

    def returnData(self, data):
        self.send_json_cors_headers()
        self.write(json_encode(data))
        self.finish()

class ShowFiles(MainHandler):

    def get(self,  file=None):
        self.returnData(os.listdir(File_server_path))

        class showFiles(MainHandler):
            def get(self, file=None):
                self.returnData(os.listdir(File_server_path))

class CreateFile(MainHandler):

    def post(self, filename):
        fileinformation = str(self.request.body.decode('utf8'))
        FilePath = File_server_path + '\\' + filename
        output_file = open(FilePath, 'w')
        output_file.write(str(fileinformation))
        self.finish('File ' + filename + ' created')

class OpenFile(MainHandler):

    def get(self, filename):
        FilePath = File_server_path + '\\' + filename

        output_file = open(FilePath, 'r')
        text = output_file.read()
        self.finish(text)

#
# class file_Server():
#
#     File_server_path = None
#
#     def __init__(self, File_server_path, start):
#
#         self.File_server_path = File_server_path
#
#     def getFileMsg(self, fname):
#         fullFilePath = os.path.join(self.File_server_path, fname)
#         hash_msg = hashlib.msg()
#         with open(fullFilePath, "rb") as f:
#             for part in iter(lambda: f.read(4096), b""):
#                 hash_msg.update(part)
#         return hash_msg.hexdigest()
#
#     def generateInternalFileName(self):
#         return ''.join(random.choices(string.ascii_lowercase + string.digits, k=15))
#
#     def checkIfExists(self, filename):
#         p = pathlib.Path(os.path.join(self.File_server_path, filename))
#         return p.is_file()
#
#     def updateFile(self, filename, filecontent):
#         fullFilePath = os.path.join(self.File_server_path, filename)
#
#         with open(fullFilePath, 'w') as output_file:
#             output_file.write(str(filecontent))
#
#         return 'File {} updated'.format(filename)
#
#
#     def createFile(self, filename, filecontent):
#         internalFileName = self.generateInternalFileName()
#
#         fullFilePath = os.path.join(File_server_path, internalFileName)
#         output_file = open(fullFilePath, 'w')
#         output_file.write(str(filecontent))
#         result = {
#             'filename' : filename,
#             'internalFileName': internalFileName
#         }
#         return result
#
#     def getFile(self, file):
#         with open(os.path.join(self.File_server_path, file), 'r') as f:
#             return f.read()
#

    
if __name__ == "__main__":
    File_server_path  = 'c:\\DistFileSystem\\Files'
    port = 5555

    if not os.path.exists(File_server_path):
        os.makedirs(File_server_path)

    app = tornado.web.Application([
        (r"/ShowFiles", ShowFiles)
        , (r"/CreateFile/(.*)/", CreateFile)
        , (r"/OpenFile/(.*)/", OpenFile)

    ])

    app.listen(port)
    main_loop = tornado.ioloop.IOLoop.current()

    try:
        main_loop.start()
    finally:
        main_loop.stop()