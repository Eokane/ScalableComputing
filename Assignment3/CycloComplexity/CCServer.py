import time
import tornado.web
from tornado.ioloop import IOLoop
from tornado import gen
from tornado.escape import json_encode, json_decode


#List of files to calculate the complexity
Files = ['file1', 'file2', 'file3', 'file4', 'file5','file6','file7','file8','file9','file10']

class MainHandler(tornado.web.RequestHandler):

    def json_headers(self):
        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')

    def returnData(self, data):
        self.json_headers()
        self.write(json_encode(data))
        self.finish()


class CCHandler(MainHandler):
    @tornado.web.asynchronous
    @gen.engine
    #Retreive the information (itertion); File1 to file5 are found on localhost:4444 then spits out done
    def get(self):
        while len(Files) == 0:
            job = 'Done'
        else:
            job = Files.pop(0)

        self.returnData(job)

    def post(self):
        msg = self.request.body
        print (msg)
        self.finish("Result Received")


make_app = tornado.web.Application([
    (r"/", CCHandler),
    ])

if __name__ == "__main__":
    make_app.listen(4444)
    IOLoop.instance().start()
