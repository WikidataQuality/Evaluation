import sys
import requests
import json

if len(sys.argv) != 2:
    sys.exit("Usage: getListOfItemIdsFor.py numericItemId")
item_id = sys.argv[1]
print("Querying wdq.wmflabs.org...")
items = json.loads(requests.get("http://wdq.wmflabs.org/api?q=claim[31:" + item_id + "]").text)['items']
f = open("itemIdsForQ%s.txt" % item_id, 'w')
i = 0
for item in items:
    s = str(item) + ';'
    f.write(s)
    i += 1
    if i % 1000 == 0:
        print(str(i))
f.close()