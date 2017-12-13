# -*- coding: utf-8 -*-
"""
Created on Tues Dec 12 09:42:29 2017

@author: Eibhlín
"""
import tornado.web
import os
import tornado.gen
from tornado.escape import json_encode, json_decode

class MainHandler(tornado.web.RequestHandler):

    def send_json_cors_headers(self):

        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')

    def returnData(self, data):
        self.send_json_cors_headers()
        self.write(json_encode(data))
        self.finish()

class IsLocked(MainHandler):

    def get(self, filename):
        if filename in lockedFiles:
            self.finish('Yes')
        else:
            self.finish('No')


class AddLock(MainHandler):

    def post(self, filename):
        lockedFiles.append(filename)
        self.finish('Adding a lock on ' + filename)

class RemoveLock(MainHandler):

    def post(self, filename):
        lockedFiles.remove(filename)
        self.finish('Locked on ' + filename + ' removed')


if __name__ == "__main__":
    lockedFiles = []
    port = 5557

    app = tornado.web.Application([
         (r"/Locked/(.*)/", IsLocked)
       , (r"/AddLock/(.*)/", AddLock)
       , (r"/RemoveLock/(.*)/", RemoveLock)
    ])

    app.listen(port)
    main_loop = tornado.ioloop.IOLoop.current()

    try:
        main_loop.start()
    finally:
        main_loop.stop()
