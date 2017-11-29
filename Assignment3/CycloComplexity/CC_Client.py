import time
import tornado.web
from tornado.ioloop import IOLoop
from tornado import gen
from tornado.escape import json_encode, json_decode
import requests
from random import *
import os
from git import Repo
CCAddress = "http://localhost:4444"


#class MainHandler(tornado.web.RequestHandler):
#
#    def json_headers(self):
#        self.set_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, PUT')
#
#
#
#class CCHandler(MainHandler):
#    def get(self):
#        while True:
#            reply = requests.get(CCAddress)
#            job = reply.text
#            if job == 'Done':
#                break
#
#            msg = reply.text + 'completed.'
#
#            reply = requests.post('http://localhost:4444', data='{}'.format(msg))
#
#
#
#make_app = tornado.web.Application([
#    (r"/Start", CCHandler),
#])
#

def calculateComplexity(f):
    length_of_time = randint(1, 10)
    #time.sleep(length_of_time)
    print ('Complexity of file {} is {}'.format(f, length_of_time))
    return length_of_time

def update_files(repo_dir):
    files = []

    for (dirpath, dirnames, filenames) in os.walk(repo_dir):
        for filename in filenames:
            if '.py' in filename:
                dirpath = dirpath.replace("\\", "/")
                files.append(dirpath + '/' + filename)
    return files

    #cc_files = {file: None for file in self.files}
    
    
if __name__ == "__main__":
    # not using REST...yet(?)
    # application.listen(8887)
    # IOLoop.instance().start()

    #Setting up things
    ClientName = 'Client_{}'.format(randint(1, 200))
    print('This is {}'.format(ClientName))
    git_repo = "https://github.com/DLTK/DLTK"
    repo_dir = os.path.join("c:\\CycloComplx", ClientName)

    #create local copy of the repo
    if not os.path.exists(repo_dir):
        os.makedirs(repo_dir)

    # if repo root is empty, fill it
    if not os.listdir(repo_dir):
        Repo.clone_from(git_repo, repo_dir)
        repo = Repo(repo_dir)
    else:
        repo = Repo(repo_dir)




    #Ask server for work
    CCAddress = "http://localhost:4444"
    while True:
        # ask the server for a task
        response = requests.get(CCAddress)
        job = response.json()
        
        if job == '"Done"':  # deal with the double quotes
            break

        # CALCULATE THE COMPLEXITY
        print('Client {} is calculating {}'.format(ClientName, job))
        git = repo.git
        git.checkout(job)
        
        #get a lit of py files to calculate complexity
        files = update_files(repo_dir)
        
        totalComplexity = 0
        for f in files:
            totalComplexity += calculateComplexity(f)
            
        
        msg = 'The complexity in {} was completed by {}. Complexity value is: {}'.format(response.text, ClientName, totalComplexity)

        # post result to the server
        response = requests.post('http://localhost:4444', data='{}'.format(msg))



