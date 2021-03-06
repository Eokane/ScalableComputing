import time
import tornado.web
from tornado.ioloop import IOLoop
from tornado import gen
from tornado.escape import json_encode, json_decode
from git import Repo
import os


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

        commits_tbd = master.commits
        if len(commits_tbd) == 0:
            self.returnData('Done')
        else:
            recent_commit = commits_tbd.pop(0)
            self.returnData("recent_commit.hexhsa")


    def post(self):
        msg = self.request.body

        complexity_number = msg['complexity_number']
        commit_number = msg['commit_number']
        print (complexity_number)

        master.complexity_map[commit_number] = complexity_number

        self.finish("Result Received")


make_app = tornado.web.Application([
    (r"/", CCHandler),
    ])


class Master:

    def __init__(self):

        self.gitrepo = "https://github.com/DLTK/DLTK"
        self.repo = None

        self.clone_repo()
        assert not self.repo.bare
        print(self.repo)

        self.commits = list(self.repo.iter_commits('master'))
        #foo = repo.iter_commits('master')
        for i in foo:
            print (i)
        print (self.commits)

        self.complexity_map = {}

    def clone_repo(self):


        repo_dir = './repo'

        # if repo root doesnt exists create it
        if not os.path.exists(repo_dir):
            os.makedirs(repo_dir)

        # if repo root is empty, fill it
        if not os.listdir(repo_dir):
            Repo.clone_from(self.gitrepo, repo_dir)
            self.repo = Repo(repo_dir)
        else:
            self.repo = Repo(repo_dir)



if __name__ == "__main__":

    master = Master()

    make_app.listen(4444)
    IOLoop.instance().start()
