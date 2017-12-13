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
BaseHandler Class; Inherits from tornado.web.RequestHandler
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
        #create the file
        fileinformation = str(self.request.body.decode('utf8'))
        FilePath = File_server_path + '\\' + filename
        output_file = open(FilePath, 'w')
        output_file.write(str(fileinformation))

        #create the file on the replication server:
        response = requests.post('{}/CreateRepFile/{}/'.format(replicationServer, filename), data=fileinformation)
        print(response.text)

        self.finish('File ' + filename + ' created or updated')

class OpenFile(MainHandler):

    def get(self, filename):
        FilePath = File_server_path + '\\' + filename

        output_file = open(FilePath, 'r')
        text = output_file.read()
        self.finish(text)

    
if __name__ == "__main__":
    File_server_path  = 'c:\\DistFileSystem\\Files'
    port = 5555
    replicationServer = 'http://localhost:5556'

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