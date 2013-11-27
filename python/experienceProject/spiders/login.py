from scrapy.spider import BaseSpider
from scrapy.http import Request, HtmlResponse, FormRequest

class LoginSpider(BaseSpider):
    name = "login"
    start_urls = ['http://www.experienceproject.com/dologinhandler.php']
   
    	
 
    def parse(self, response):
        return FormRequest('http://www.experienceproject.com/dologinhandler.php', method='POST', formdata={'login_username':'trcexperiment@gmail.com', 'login_password':'trcexperiment123'}, callback=self.after_login, dont_filter=True)	

    def after_login(self, response):
        print response
        # check login succeed before going on
        if "trcexperiment" in response.body:
            self.log("Login failed", level=log.ERROR)
            return
        else :
            print "SUCCESS"
