from ToutiaoCrawler.utils.initMongo import MongoDB
from pymongo.errors import DuplicateKeyError

class ToutiaoPipeline(object):

    connection = MongoDB()

    def process_item(self, item, spider):

        table = self.connection.get_connection()
        try:
            inserted = table.insert_one(item)
            print '------' + inserted.inserted_id + '------'
        except DuplicateKeyError:
            print 'Data Exists'

        return item

    def close_spider(self, spider):
        self.connection = None
