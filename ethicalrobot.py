from protego import Protego
from datetime import datetime, timedelta
from usp.tree import sitemap_tree_for_homepage
from bs4 import BeautifulSoup
import json
import re
import random
import requests
import time
# https://proxyscrape.com/blog/the-easy-way-to-crawl-siteemaps-with-python
# https://pypi.org/project/Protego/
# https://www.systemdesignhandbook.com/guides/design-a-web-crawler-system-design/
# https://requests.readthedocs.io/en/latest/api/
# https://www.datacamp.com/tutorial/python-trim
# https://docs.python.org/3/library/json.html
robotstxt = """
User-agent: *
Disallow: /admin/
Disallow: /django-admin/
Disallow: /documents/
Disallow: /search/
Disallow: /*/search/
Disallow: /cuardaigh/
Disallow: /*/cuardaigh/
# Blocks numerous Irish search pages
Disallow: /*?*
Allow: /static/

# Point to sitemap
Sitemap: https://www.gov.ie/sitemap.xml"""
rp = Protego.parse(robotstxt)
myUserAgent = "NAME_LASTNAME/National College of Ireland/x00000000@student.ncirl.ie/Research purposes only."
print(rp.can_fetch("https://www.gov.ie", myUserAgent))
print(rp.can_fetch("https://www.gov.ie/en/search/?q=", myUserAgent))

cutoffDate = datetime.now() - timedelta(days=1)
siteMapFile = open("sitemap.txt", "w")
url = "https://www.gov.ie/en"
tree = sitemap_tree_for_homepage(url)
articeList = ()

headers = {"User-Agent": myUserAgent}
session = requests.Session()
session.headers.update(headers)

for page in tree.all_pages():
    if (page.last_modified == None):
        continue

    if (page.last_modified >= cutoffDate):
        print(page.url)
        print("Can fetch: " + str(rp.can_fetch(page.url, myUserAgent)))
        # Add delay
        time.sleep(3.5 + random.uniform(0.1, 1.5))
        try:
            print("Fetching data...")
            response = session.get(page.url, timeout=10, allow_redirects=True)
            if response.status_code == 200:
                #print(response.content)
                soup = BeautifulSoup(response.content, 'html.parser')
                parsedData = soup.find_all("p", attrs={"data-block-key": re.compile('.*')})
                metadata = soup.find("script", attrs={"id": "matomo-data"})
                data = metadata.text
                json_data = json.loads(data)

                #parsedData = re.sub('<*>', '', str(soup))
                #trimmed_text = ' '.join(trimmed_text.split())
                print(str(page.url) + "\n" + str(json_data["govie_publisher_title"]) + "\n")
                for tag in parsedData:
                    print(tag.text + "\n")

                siteMapFile.write(str(page.url) + "\n" + str(json_data["govie_publisher_title"]) + "\n")
                for tag in parsedData:
                    siteMapFile.write(tag.text + "\n")

                article = [json_data["govie_publisher_title"], tag]

        except Exception as ex:
            print("Error: " + str(ex))

# Start fetching data from the extracted URLs
