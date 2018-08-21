class HttpProxyMiddleware(object):

    def process_request(self, request, spider):
        request.meta['proxy'] = 'http://111.47.192.141:8888'
