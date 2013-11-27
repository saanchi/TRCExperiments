from scrapy.selector import HtmlXPathSelector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
from experienceProject.items import ExperienceprojectItem
from scrapy.utils.url import urljoin_rfc
from scrapy.http import Request
from scrapy.http import Request, HtmlResponse, FormRequest
import re

class ExperienceprojectSpider(CrawlSpider):
    name = 'experienceproject'
    start_urls = ['http://www.experienceproject.com/stories/topics/emotional-health','http://www.experienceproject.com/stories/topics/relationship-struggles','http://www.experienceproject.com/stories/topics/broken-hearts-and-betrayal','http://www.experienceproject.com/stories/topics/singledom','http://www.experienceproject.com/stories/topics/self-harm','http://www.experienceproject.com/stories/topics/phobias','http://www.experienceproject.com/stories/topics/drugs','http://www.experienceproject.com/stories/topics/regrets','http://www.experienceproject.com/stories/topics/addiction','http://www.experienceproject.com/stories/topics/loneliness','http://www.experienceproject.com/stories/topics/abuse','http://www.experienceproject.com/stories/topics/suicide','http://www.experienceproject.com/stories/topics/personality-disorder','http://www.experienceproject.com/stories/topics/bipolar-disorder','http://www.experienceproject.com/stories/topics/image-and-weight','http://www.experienceproject.com/stories/topics/memories','http://www.experienceproject.com/stories/topics/loss-of-a-pet','http://www.experienceproject.com/stories/topics/loss-of-a-child','http://www.experienceproject.com/stories/topics/loss-of-a-friend','http://www.experienceproject.com/stories/topics/loss-in-the-family','http://www.experienceproject.com/stories/topics/family-struggles','http://www.experienceproject.com/stories/topics/loss-to-suicide','http://www.experienceproject.com/stories/topics/pet-peeves','http://www.experienceproject.com/stories/topics/self-improvement','http://www.experienceproject.com/stories/topics/unemployment','http://www.experienceproject.com/stories/topics/ptsd']    

    def __init__(self) :
        response = FormRequest(url="http://www.experienceproject.com/dologin.php",
                    formdata={'login_username': 'trcexperiment@gmail.com', 'login_password': 'trcexperiment123'})
        print response

     
    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        sites = hxs.xpath('//a[contains(@href, "stories")]/@href')          
        url = ''
        pattern = re.compile('stories/[a-zA-Z\-]*/[0-9]*')        
        for site in sites:
            relative_url = site.extract()
            url = self._urljoin(response,relative_url)
            if  pattern.match(url) and url not in start_urls :
                print url
                yield Request(url, callback = self.parsetext)

    def parsetext(self, response):
        print url
        log = open("log.txt", "a")
        hxs = HtmlXPathSelector(response)
        items = []
        texts = hxs.xpath('//div[contains(@id,"story_div")]/text()').extract()
        text = ''.join(str(e) for e in texts)
        print text
        log.write(text)
        log.close()
        #return items

    def _urljoin(self, response, url):
        """Helper to convert relative urls to absolute"""
        return urljoin_rfc(response.url, url, response.encoding)
