# -*- coding: utf-8 -*-
"""
Created on Mon Dec  4 10:38:55 2017

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


#GLOBAL VARIABLES
file_server = None


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

class file_info_handler(MainHandler):

    def get(self,  file):
        logging.info('file_Handler - GET')
        msg = file_server.getFileMsg(file)
        print ('File {} - MSG: {}'.format(file, msg))
        self.returnData(msg)

class file_handler(MainHandler):

    def get(self,  file=None):
        logging.info('file_handler - GET')
        if file is None:
            self.returnData(os.listdir(file_server.File_server_path))
        else:
            self.returnData(file_server.getFile(file))

    def post(self, filename):
        fileinformation = str(self.request.body.decode('utf8')) 
        if file_server.checkIfExists(filename):
            logging.info('file_handler - POST: Update File')
            result = file_server.updateFile(filename, fileinformation)
        else:
            logging.info('FileHandler - POST: Create File')
            result = file_server.createFile(filename, fileinformation)

        self.finish(result)



class MainServer(object):

    def __init__(self, server_path, Force):
        if os.path.exists(server_path):  
            if Force:  
                shutil.rmtree(server_path) 
                os.makedirs(server_path) 
        else:
            os.makedirs(server_path)




class file_Server(MainServer):

    File_server_path = None

    def __init__(self, File_server_path, start):
        MainServer.__init__(self, File_server_path, start)
        self.File_server_path = File_server_path

    def getFileMsg(self, fname):
        fullFilePath = os.path.join(self.File_server_path, fname)
        hash_msg = hashlib.msg()
        with open(fullFilePath, "rb") as f:
            for part in iter(lambda: f.read(4096), b""):
                hash_msg.update(part)
        return hash_msg.hexdigest()

    def generateInternalFileName(self):
        return ''.join(random.choices(string.ascii_lowercase + string.digits, k=15))

    def checkIfExists(self, filename):
        p = pathlib.Path(os.path.join(self.File_server_path, filename))
        return p.is_file()

    def updateFile(self, filename, filecontent):
        fullFilePath = os.path.join(self.File_server_path, filename)

        with open(fullFilePath, 'w') as output_file:
            output_file.write(str(filecontent))

        return 'File {} updated'.format(filename)


    def createFile(self, filename, filecontent):
        internalFileName = self.generateInternalFileName()

        fullFilePath = os.path.join(File_server_path, internalFileName)
        output_file = open(fullFilePath, 'w')
        output_file.write(str(filecontent))
        result = {
            'filename' : filename,
            'internalFileName': internalFileName
        }
        return result

    def getFile(self, file):
        with open(os.path.join(self.File_server_path, file), 'r') as f:
            return f.read()

def make_app(File_server_path):
    file_server = None

    
    if File_server_path is not None:
        print ('This is a file server')
        file_server = file_server(File_server_path)



    return tornado.web.Application([
         (r"/Files", file_handler)
        ,(r"/Files/(.*)/", file_handler)
        ,(r"/Files/(.*)/create", file_handler)
        ,(r"/File/(.*)/getFileMsg", file_info_handler)

    ]), file_server
    
    
if __name__ == "__main__":
    
    
    File_server_path    = None
    

    File_server_path    = 'c:\\DistFileSystem\\FilesRoot'
    port = 5555



    app, file_server = make_app(File_server_path)

    app.listen(port)
    main_loop = tornado.ioloop.IOLoop.current()

    try:
        main_loop.start()
    finally:
        main_loop.stop()
