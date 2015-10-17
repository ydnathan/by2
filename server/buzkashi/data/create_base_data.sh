#!/usr/bin/env bash

# create a company
# curl -H "Content-Type: application/x-www-form-urlencoded" -X POST -d 'company_name=flipkart&email_domain=flipkart.com' http://localhost:8080/companies/add
# curl -H "Content-Type: application/x-www-form-urlencoded" -X POST -d 'company_name=InMobi&email_domain=gmail.com' http://localhost:8080/companies/add

# create a company office
# curl -H "Content-Type: application/x-www-form-urlencoded" -X POST -d 'company_id=1&office_name=PW, Cessna&address_text=Pardhanani Willshire, Cessna Business Park, Kadubessarnahalli, Bangalore &address_lat_lon=12.932891,77.683627&city=Bangalore&state=Karnataka' http://localhost:8080/company_offices/add
# curl -H "Content-Type: application/x-www-form-urlencoded" -X POST -d 'company_id=1&office_name=Mantri Commercio&address_text=Kariyammana Agrahara, Bellandur, Bangalore &address_lat_lon=12.932891,77.683627&city=Bangalore&state=Karnataka' http://localhost:8080/company_offices/add
# curl -H "Content-Type: application/x-www-form-urlencoded" -X POST -d 'company_id=2&office_name=Cessna&address_text=7th Floor, Delta Block, Embassy Tech Square, Marathahalli - Sarjapur Outer Ring Rd, Bengaluru&address_lat_lon=12.932891,77.683627&city=Bangalore&state=Karnataka' http://localhost:8080/company_offices/add


# GET APIs
# curl http://localhost:8080/company_offices/search?company=1
# curl http://localhost:8080/companies/all