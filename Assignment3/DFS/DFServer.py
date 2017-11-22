import os
import logging
from urllib.parse import unquote
import tornado.ioloop
import tornado.web
import tornado.gen
from tornado import options
from tornado.escape import json_encode, json_decode

#GLOBAL VARIABLES
#root file system folder folder. Will be created if doesnt exist. Inform ful path
FileServerRoot = ''

'''
BaseHandler Class; Inherits from tornado.web.RequestHandler
Used for common methods across all other Handlers
'''
class BaseHandler(tornado.web.RequestHandler):

    def send_json_cors_headers(self):
        self.set_header("Content-Type", "application/json;")
        self.set_header("Access-Control-Allow-Origin", "*")
        self.set_header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')

    def returnData(self, data):
        self.send_json_cors_headers()
        self.write(json_encode(data))
        self.finish()

