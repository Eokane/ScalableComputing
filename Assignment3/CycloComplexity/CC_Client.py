import time
import tornado.web
from tornado.ioloop import IOLoop
from tornado import gen
from tornado.escape import json_encode, json_decode
import requests
from random import *

CCAddress = "http://localhost:4444"


class MainHandler(tornado.web.RequestHandler):

    def json_headers(self):
        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')



class CCHandler(MainHandler):
    def get(self):
        while True:
            reply = requests.get(CCAddress)
            job = reply.text
            if job == 'Done':
                break

            msg = reply.text + 'completed.'

            reply = requests.post('http://localhost:4444', data='{}'.format(msg))



make_app = tornado.web.Application([
    (r"/Start", CCHandler),
])


def Complexitycalc():
    length_of_time = randint(1, 10)
    time.sleep(length_of_time)
    return length_of_time


if __name__ == "__main__":
    # not using REST...yet(?)
    # application.listen(8887)
    # IOLoop.instance().start()

    ClientName = 'Client_{}'.format(randint(1, 200))
    print('This is {}'.format(ClientName))

    CCAddress = "http://localhost:4444"
    while True:
        # ask the server for a task
        response = requests.get(CCAddress)
        job = response.text
        if job == '"Done"':  # deal with the double quotes
            break

        # CALCULATE THE COMPLEXITY
        print('Client {} is calculating {}'.format(ClientName, job))
        c = Complexitycalc()

        msg = 'The complexity in {} was completed by {}. Complexity value is: {}'.format(response.text, ClientName, c)

        # post result to the server
        response = requests.post('http://localhost:4444', data='{}'.format(msg))



