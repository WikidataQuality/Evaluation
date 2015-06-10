import sys
import requests
import json

if len(sys.argv) != 2:
    sys.exit("Usage: getListOfItemIdsFor.py numericItemId")
item_id = sys.argv[1]
print("Querying wdq.wmflabs.org...")
items = json.loads(requests.get("http://wdq.wmflabs.org/api?q=claim[227] and claim[625]").text)['items']
f = open("itemIdsWithProperies%s.txt" % item_id.replace(",","_"), 'w')
i = 0
for item in items:
    s = str(item) + ';'
    f.write(s)
    i += 1
    if i % 1000 == 0:
        print(str(i))
f.close()