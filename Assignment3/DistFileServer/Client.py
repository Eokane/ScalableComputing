# -*- coding: utf-8 -*-
"""
Created on Thu Dec  7 21:19:19 2017

@author: Eibhlín
"""

import requests


FileServer = 'http://localhost:5555' 

############################ Create File
fileName = 'file1'
response = requests.post('{}/CreateFile/{}/'.format(FileServer, fileName), data="Line 1")
print(response.text)



############################ list files
print ('Files on the server:')
requests.get("{}/ShowFiles".format(FileServer)).json()


############################ Open 1 File
#this is the name of the file you want to open:
openFileName = 'file1'
file = requests.get('{}/OpenFile/{}/'.format(FileServer, openFileName)).text 

local_copy = open(openFileName , "w") 
local_copy.write(file)

############################ Change the file
local_copy.write('\nChanging the file')
local_copy.close()

############################ Send it back to the server:
#read the file again because it was closed
local_copy = open(openFileName, "r")
fileText = local_copy.read()
response = requests.post('{}/CreateFile/{}/'.format(FileServer, openFileName), data=fileText)