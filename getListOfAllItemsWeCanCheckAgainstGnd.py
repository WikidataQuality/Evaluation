import sys
import requests
import json

print("Querying wdq.wmflabs.org...")
items = json.loads(requests.get("http://wdq.wmflabs.org/api?q=claim[227] and claim[19,20,21,22,25,26,39,40,50,106,410,569,570,625,1477]").text)['items']
f = open("itemIdsForAllItemsWeCanCheckAgainstGnd.txt", 'w')
i = 0
for item in items:
    s = str(item) + ';'
    f.write(s)
    i += 1
    if i % 1000 == 0:
        print(str(i))
f.close()