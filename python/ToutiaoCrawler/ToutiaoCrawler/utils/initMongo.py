import pymongo


class MongoDB(object):
    __table = None

    @classmethod
    def get_connection(cls):
        if cls.__table is None:

            mongo_client = pymongo.MongoClient('')
            db = mongo_client['spider']
            cls.__table = db['toutiao']

        return cls.__table
