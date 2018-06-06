# service-alerts-backend

### Current Api's

#### GET ```/bad-services```

Gets a list of services with a status of bad from elasticsearch.

#### GET ```/bad-services-sms```

Gets a list of services with a status of bad from elasticsearch and sends an 
SMS message with an alert.

#### GET ```/tickets```

Gets Priority 1 or 2 tickets from elasticsearch since last time checked.

#### POST ```/message```

Sends an SMS message with the message sent.

Post body:
```{"message":"example message"}```

