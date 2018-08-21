import scrapy


class ToutiaoItem(scrapy.Item):

    abstract = scrapy.Field()

    behot_time = scrapy.Field()

    datetime = scrapy.Field()

    item_id = scrapy.Field()

    keywords = scrapy.Field()

    publish_time = scrapy.Field()

    source = scrapy.Field()

    tag = scrapy.Field()

    title = scrapy.Field()

    url = scrapy.Field()
