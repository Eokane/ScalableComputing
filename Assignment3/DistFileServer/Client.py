# -*- coding: utf-8 -*-
"""
Created on Thu Dec  7 21:19:19 2017

@author: Eibhlín
"""

import requests
import ClientCache as cache
from datetime import datetime 
import os 
import random

FileServer = 'http://localhost:5555' 
ReplicationServer = 'http://localhost:5556'
LockingServer = 'http://localhost:5557'
localCache = cache.ClientCache()

def checkIfLocked(fileName):
    response = requests.get('{}/Locked/{}/'.format(LockingServer, fileName))
    return response.text

def addLock(fileName):
    response = requests.post('{}/AddLock/{}/'.format(LockingServer, fileName))
    print (response.text)

def removeLock(fileName):
    response = requests.post('{}/RemoveLock/{}/'.format(LockingServer, fileName))
    print (response.text)

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

    if checkIfLocked(openFileName) == 'Yes':
        return 'File is locked - cant open'
    else:
        addLock(openFileName)
    
    #simulate here if file server is free, get the file from the replication server
    FileServerIsFree = random.uniform(0, 1) > 0.2
    if FileServerIsFree:
        print ('Getting File from the File Server')
        file = requests.get('{}/OpenFile/{}/'.format(FileServer, openFileName)).text
    else:
        print ('File Server Busy - Getting File from the Replication Server')
        file = requests.get('{}/OpenRepFile/{}/'.format(ReplicationServer, openFileName)).text
    
    local_copy = open(openFileName , "w") 
    local_copy.write(file)
    local_copy.close()
    
    return 'OK'
    
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
    removeLock(openFileName)

name = 'myFile'

createFile(name)
listFiles()
result = openFile(name)
if result != 'OK':
    print (result)
else:
    changeFile(name, 'new line')
    saveFile(name)
    
#checkIfLocked(name)
