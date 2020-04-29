# COTrendGrapgh

data: https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/02-22-2020.csv 

0. data storage (Roman, Alex)
- database => aws(rds) => postgres ?

- er-diagram (Roman)
- DDL.sql    (Roman)
- table for last updateDate (Roman)

- populate retrospective data (java, node,js, python)  (Roman)

1. data processing (scheduling)

look for scheduling implementation or stay with http triggering. (Roman)

get_data(httpClient) -> parse(lib for csv parsing) -> filter -> persist(jdbc)  (Roman)

2. business logic (Sergii)
- controller 
- service (enricher, filter, validator, ...)
- model
- persistance (dao)

3. view (Sergii)
- js library for charts 
- create react app (js, jquery)

~ embedded server jetty

deploy:
? how to start app
? will we use docker for database
server - digital ocean (Serhii)

