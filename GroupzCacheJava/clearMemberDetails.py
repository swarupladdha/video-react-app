from pymongo import MongoClient
from pymongo import cursor
client = MongoClient()
db = client.session
memberdetails=db.memberdetails

result = db.memberdetails.remove({})
print("Cleared all Data from memberdetails!")
