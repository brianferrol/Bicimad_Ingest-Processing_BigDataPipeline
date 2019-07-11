#!/usr/bin/env python
# coding: utf-8

# In[92]:


import requests
import json
import time
#from io import BytesIO

# Configuro API keys:

key = "???"
user = "???"
url = "https://rbdata.emtmadrid.es:8443/BiciMad/get_stations/{}/{}".format(user,key)


# In[93]:


# Función para guardar un fichero como json:

def writeToJSONFile(path, fileName, data):
    filePathNameWExt = "./" + path + "/" + fileName + ".json"
    with open(filePathNameWExt, 'w') as fp:
        json.dump(data, fp)


# In[94]:


# Función para hacer el get y guardar fichero:

def apiRequest():
    req = requests.get(url)
    sav = req.json()
    data = sav['data']
    eventTime = time.strftime("%Y%m%d%H%M")
    name = "bicimadAPI_"+eventTime
    if sav["code"]=="0":
        writeToJSONFile('./', name, data)
    else:
        apiRequest()


# In[95]:

while true:
    apiRequest()
    time.sleep(60)


# In[ ]:




