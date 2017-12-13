# -*- coding: utf-8 -*-
"""
Created on Tue Dec 12 20:01:47 2017

@author: Eibhlín
"""
import os
import tornado.gen
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


class MainHandler(tornado.web.RequestHandler):

    def send_json_cors_headers(self):

        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')

    def returnData(self, data):
        self.send_json_cors_headers()
        self.write(json_encode(data))
        self.finish()

class OpenRepFile(MainHandler):

    def get(self, filename):
        FilePath = File_server_path + '\\' + filename

        output_file = open(FilePath, 'r')
        text = output_file.read()
        self.finish(text)



class CreateRepFile(MainHandler):

    def post(self, filename):
        fileinformation = str(self.request.body.decode('utf8'))
        FilePath = File_server_path + '\\' + filename
        output_file = open(FilePath, 'w')
        output_file.write(str(fileinformation))
        self.finish('File ' + filename + ' created on replication Server')


if __name__ == "__main__":
    File_server_path  = 'c:\\DistFileSystem\\RepFiles'
    port = 5556

    if not os.path.exists(File_server_path):
        os.makedirs(File_server_path)

    app = tornado.web.Application([
        #(r"/ShowFiles", ShowFiles)
         (r"/CreateRepFile/(.*)/", CreateRepFile)
       , (r"/OpenRepFile/(.*)/", OpenRepFile)

    ])

    app.listen(port)
    main_loop = tornado.ioloop.IOLoop.current()

    try:
        main_loop.start()
    finally:
        main_loop.stop()
        