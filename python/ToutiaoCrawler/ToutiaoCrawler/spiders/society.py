# -*- coding: utf-8 -*-
import scrapy
import time
from ToutiaoCrawler.utils.ascpGenerator import get_as_cp
import random
from ToutiaoCrawler.utils.user_agents import get_user_agents
from ToutiaoCrawler.utils.cookie import get_cookies
import json


class SocietySpider(scrapy.Spider):
    name = 'society'

    count = 0
    timestamp = 0

    allowed_domains = ['toutiao.com']
    baseUrl = 'https://m.toutiao.com/list/?tag='
    tag = 'news_society'
    # tag = 'news_hot'
    params = '&ac=wap&count=20&format=json_raw'
    as_param = '&as='
    cp_param = '&cp='
    max_behot_time = '&max_behot_time='

    as_cp = get_as_cp(round(time.time()))

    start_urls = baseUrl + tag + params + as_param + as_cp['as'] + cp_param + as_cp['cp'] + max_behot_time + str(int(time.time()))

    no_time_url = baseUrl + tag + params + as_param + as_cp['as'] + cp_param + as_cp['cp'] + max_behot_time

    print start_urls

    headers = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
        'Host': 'm.toutiao.com',
        'User-Agent': random.choice(get_user_agents()),
        'Cookie': 'tt_webid=' + get_cookies()
    }

    def start_requests(self):
        print 'start'
        yield scrapy.Request(url=self.start_urls, callback=self.parse, headers=self.headers)

    def parse(self, response):
        # print response.body
        data = (json.loads(response.body))['data']
        self.count += int((json.loads(response.body))['return_count'])
        for each in data:
            print each['title']
            each['_id'] = each['item_id']
            self.timestamp = each['behot_time']
            yield each

        as_cp = get_as_cp(self.timestamp)
        url = self.baseUrl + self.tag + self.params + self.as_param + as_cp['as'] + self.cp_param + as_cp['cp'] + self.max_behot_time + str(
            int(self.timestamp))

        yield scrapy.Request(url=url, callback=self.parse, headers=self.headers)
