# Nascrawler

Nascrawler is a multi-purpose application that combines web crawling, database management and natural language processing. It is a work in progress that aims to be compatible with most domains of industry, although it favours such topics as food and costs of living. Its inception dates back to early 2026 as part of a college project. Given Ireland's rising prices at the time that had been affecting essential goods and services, the impetus to capture inflationary pressures was soon born. 

## Database

Nascrawler's database is powered by MySQL and requires setup when using the app for the first time. The user is expected to enter the host name, port number and database name. Upon success, the information will be saved locally for future use. It is worth noting that whenever an action that necessitates database access is scheduled, you will be prompted to key in your database credentials. To mitigate against unauthorised logins, the sensitive data is never stored on disk or allowed linger in memory. 
Additionally, there is a stripped-down table editor that lets users create a SQL table if they wish to avoid defining a complex schema. Column names, data types, primary keys and auto-increment attributes may be set. Apart from one app-specific oddity, the database operates as per convention. Namely, if a web crawler passes text to a table column that exceeds its character limit, the database manager will split it into two substrings i.e. rows. 

## Web Crawler

When the user navigates beyond the landing page to the main menu, they are met with a list of web crawlers available for deployment. If none is available, a new crawler may be programmed. Alternatively, existing crawlers can be edited. An automated web browser contains three key attributes: 1) name (for in-app reference); 2) user agent (an identifier that is conveyed to websites); 3) crawl delay (how long the crawler should wait before visiting the next page, for the sake of politeness and to avoid being banned the minimum recommended delay is 3 seconds).

The next section entitled "Scripting" explores how to operate a crawler in greater detail. 

## Scripting

To script a web crawler, you choose a command and enter an argument which may be either a URL or the output of a previous instruction. Commands can be added, edited and removed as one sees fit.
Currently, the program supports over 7 commands:

1) Visit - Directs the crawler to navigate to the specified URL
2) Search - Extracts HTML tags from the active web page
3) Text - Removes markup and accompanying syntax from the previous output
4) Attribute - Fetches values assigned to a particular HTML attribute (e.g., href)
5) Write - Writes the previous command's results to a text file whose name is specified as an argument
6) SQL - Executes a SQL statement formulated by the user
7) Python - Runs a Python file whereby its name is passed as a parameter

Note that some commands exhibit the same behaviour irrespective of whether you choose to operate on URLs or search results.
Moreover, to interleave intermediate output with SQL statements, use the below syntax (remove quotes):
@"Command Number"i or @"Command Number"s depending on whether you are subbing in integers or strings.

## Sentiment Analysis

Sentiment analysis is accessed from the dropdown menu adjacent to the crawler selector. Subsequently, the user may avail of two options. If they type in a phrase or sentence, they may run natural language processing by clicking the corresponding button. More importantly, the "Query Database" button enables them to peruse all the tables associated with the given database. Once the requisite credentials have been received, the list of tables will populate and the user will be able to choose which column to analyse. Note that only text-based columns can be tagged for analysis and that they must return to the previous menu to run the procedure.