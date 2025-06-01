http://localhost:8080/odata/Books

http://localhost:8080/odata/Books(1)

http://localhost:8080/odata/Books?$select=title

http://localhost:8080/odata/Books?$expand=Credential,Status

http://localhost:8080/odata/Books?$expand=Credential&$filter=Credential/bookGenre eq 'NonFiction'

http://localhost:8080/odata/Books?$orderby=title asc

http://localhost:8080/odata/Books?$select=title&$orderby=title asc

http://localhost:8080/odata/Books(1)/Credential

http://localhost:8080/odata/Books(1)/Status

http://localhost:8080/odata/Books?$expand=Status&$filter=Status/reservedStatus eq true

