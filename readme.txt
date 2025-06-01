http://localhost:8080/odata/Books

http://localhost:8080/odata/Books(1)

http://localhost:8080/odata/Books?$select=title

http://localhost:8080/odata/Books?$expand=credential,status

http://localhost:8080/odata/Books?$expand=credential&$filter=credential/bookGenre eq 'NonFiction'

http://localhost:8080/odata/Books?$orderby=title asc

http://localhost:8080/odata/Books?$select=title&$orderby=title asc

http://localhost:8080/odata/Books(1)/credential

http://localhost:8080/odata/Books(1)/status

http://localhost:8080/odata/Books?$expand=status&$filter=status/reservedStatus eq true

