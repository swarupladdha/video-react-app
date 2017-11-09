from pymongo import MongoClient
from pymongo import cursor
client = MongoClient()
db = client.session
groupzdetails=db.groupzdetails

result = db.groupzdetails.remove({})
print("Cleared all Data from groupzdetails!")
