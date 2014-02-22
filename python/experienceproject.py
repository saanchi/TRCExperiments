from scrapy.selector import HtmlXPathSelector
from scrapy.contrib.spiders import CrawlSpider, Rule
from experienceProject.items import ExperienceprojectItem
from scrapy.http import Request
from scrapy.http import Request, HtmlResponse, FormRequest
import re
import json
import pdb
import time

class ExperienceprojectSpider(CrawlSpider):
    name = 'experienceproject'
    start_urls = ['http://www.experienceproject.com']
    category_list = ['emotional-health','relationship-struggles','broken-hearts-and-betrayal','singledom','self-harm','phobias','drugs','regrets','addiction','loneliness','abuse','suicide','personality-disorder','bipolar-disorder','image-and-weight','memories','loss-of-a-pet','loss-of-a-child','loss-of-a-friend','loss-in-the-family','family-struggles','loss-to-suicide','pet-peeves','self-improvement','unemployment','ptsd']
    category_count_map = {'emotional-health':1,'relationship-struggles':1,'broken-hearts-and-betrayal':1,'singledom':1,'self-harm':1,'phobias':1,'drugs':1,'regrets':1,'addiction':1,'loneliness':1,'abuse':1,'suicide':1,'personality-disorder':1,'bipolar-disorder':1,'image-and-weight':1,'memories':1,'loss-of-a-pet':1,'loss-of-a-child':1,'loss-of-a-friend':1,'loss-in-the-family':1,'family-struggles':1,'loss-to-suicide':1,'pet-peeves':1,'self-improvement':1,'unemployment':1,'ptsd':1} 
    #category_list=['health']    

    def start_requests(self):
        data = {'format':'json','isAjax':'true','limit':'20','order':'all'}
        for category in self.category_list :
            data['category'] = category
            data['offset']   = str(self.category_count_map[category])
            time.sleep(1)
            yield FormRequest( url="http://www.experienceproject.com/ajax/category/get-more-stories-by-category.php", callback=self.parse, formdata=data) 
 
    def parse(self, response):
        form_data = {'format':'json','isAjax':'true','limit':'20','order':'all'}
        data = json.loads( response.body)
        ## extract the category from url
        if data['nextHref'] :
            next_ref = data['nextHref']
        if next_ref :        
            split_list = data['nextHref'].split('/')
            for story in data['stories']:
                print "%s\t%s" % ( split_list[5].encode('utf-8'),  story['content'].encode('utf-8'))
            ## Form the request again
            if split_list[5] in self.category_list :
                form_data['category'] = split_list[5].encode('ascii')
                form_data['offset']   = str( self.category_count_map[split_list[5]]+1)
                self.category_count_map[split_list[5]] += 1  
                #print "**********************************************************"
                #print form_data
                #pdb.set_trace()
                time.sleep(1) 
                yield FormRequest( url="http://www.experienceproject.com/ajax/category/get-more-stories-by-category.php", callback=self.parse, formdata=form_data) 

