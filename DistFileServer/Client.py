# -*- coding: utf-8 -*-
"""
Created on Thu Dec  7 21:19:19 2017

@author: Eibhlín
"""

import requests
import ClientCache as cache
from datetime import datetime 
import os 

FileServer = 'http://localhost:5555' 
localCache = cache.ClientCache()

def createFile(fileName):
    response = requests.post('{}/CreateFile/{}/'.format(FileServer, fileName), data="Line 1")
    print(response.text)

def listFiles():
    print ('Files on the server:')
    return requests.get("{}/ShowFiles".format(FileServer)).json()

def openFile(openFileName):
    if openFileName in localCache.cache:
        time = localCache.cache[openFileName]
        if (datetime.now() - time).seconds < 60: #cache is only valid for 60 seconds
            print ('File in local cache, not fetching from server')            
            return
        else:
            os.remove(openFileName)
            del localCache.cache[openFileName]

    file = requests.get('{}/OpenFile/{}/'.format(FileServer, openFileName)).text     
    local_copy = open(openFileName , "w") 
    local_copy.write(file)
    local_copy.close()
    
def changeFile(openFileName, line):
    local_copy = open(openFileName , "a") 
    local_copy.write(line)
    local_copy.close()


def saveFile(openFileName):
    #read the file again because it was closed
    local_copy = open(openFileName, "r")
    fileText = local_copy.read()
    response = requests.post('{}/CreateFile/{}/'.format(FileServer, openFileName), data=fileText)
    print(response.text)
    localCache.cache[openFileName] = datetime.now()


name = 'file2'

createFile(name)
listFiles()
openFile(name)
changeFile(name, 'new line2')
saveFile(name)
