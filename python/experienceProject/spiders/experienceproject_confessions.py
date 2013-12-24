from scrapy.selector import HtmlXPathSelector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
from experienceProject.items import ExperienceprojectItem
from scrapy.utils.url import urljoin_rfc
from scrapy.http import Request
from scrapy.http import Request, HtmlResponse, FormRequest
import re
# -*- coding: utf-8 -*-

class ExperienceprojectSpider(CrawlSpider):
    name = 'experienceproject'
    start_urls = ['http://www.experienceproject.com/confessions.php?ct=revenge','http://www.experienceproject.com/confessions.php?ct=venting','http://www.experienceproject.com/confessions.php?ct=embarrassing','http://www.experienceproject.com/confessions.php?ct=health','http://www.experienceproject.com/confessions.php?ct=family', 'http://www.experienceproject.com/confessions.php?ct=friend', 'http://www.experienceproject.com/confessions.php?ct=work', 'http://www.experienceproject.com/confessions.php?ct=school', 'http://www.experienceproject.com/confessions.php?ct=love', 'http://www.experienceproject.com/confessions.php?ct=other', 'http://www.experienceproject.com/confessions.php?ct=offtopic', 'http://www.experienceproject.com/confessions.php?reaction=sad', 'http://www.experienceproject.com/confessions.php?reaction=angry', 'http://www.experienceproject.com/confessions.php?ct=sympathetic' ]
   
    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        sites = hxs.xpath('//a/@href')          
        url = ''
        content_pattern = re.compile('.*confessions\.php\?cid=.*')
        next_pattern    = re.compile('.*confessions\.php\?.*ct=.*cn=') 
        for site in sites:
            relative_url = site.extract()
            url = self._urljoin(response,relative_url)
            if  content_pattern.match(url) :
                yield Request(url, callback = self.parsetext )
            if  next_pattern.match(url) :
                print url
                yield Request(url, callback = self.parse)

    def parsetext(self, response):
        print response
        log = open("log.txt", "a")
        hxs = HtmlXPathSelector(response)
        texts = hxs.xpath('//*[@id="confession_div"]/text()').extract()
        categories = hxs.xpath('//*[@id="main"]/div[1]/div/p[2]/span/a/text()').extract()
        category = u''.join(categories)
        text = u''.join(texts)
        log.write(category.encode('utf-8').strip() +"\t" + text.encode('utf-8').strip() + "\n" )
        log.close()

    def _urljoin(self, response, url):
        """Helper to convert relative urls to absolute"""
        return urljoin_rfc(response.url, url, response.encoding)
