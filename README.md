Payment Rule Engine 

# Overview
The Payment Rule Engine is a Java-based application built using Spring Boot. It processes payment transactions and routes them according to predefined rules


# Features
Process payment transactions based on customizable rules


# Prerequisites
. Java 17
. Maven 3.8.0 or higher
. An IDE (IntelliJ, Eclipse.)


# Getting Started
Clone the Repository

git clone https://github.com/MahaboobV/payment-rule-engine.git

cd payment-rule-engine

# Building the Application
Use Maven to build the project:
. mvn clean install


Configuration
The application uses an application.properties file for configuration. Here are some key settings

H2 Configuration

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.datasource.driver-class-name=org.h2.Driver

JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

H2 Console Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console


# API Endpoints
Load  Payment rules from JSON config file 
URL: /api/payments/loadRules

Method: 'GET'

Response:

"paymentRules Loaded"


Validate the Payment transaction request 

URL : /api/payments/evaluate
Method: 'POST'
Request Body 
{
    "customerType":"Non-Employee",
    "customerId":"77654",
    "location":"NORWAY",
    "amount":10001.0,
    "currency":"NOK",
    "paymentCardNetwork":"Mastercard",
    "cardType":"Debit Card",
}

Repone : 
{
    "customerId": "77654",
    "rulesApplicable": [
        "Authentication not required :Waive off 3DS for transaction : true",
        "Transaction amount exceeds the threshold amount: 10000.0, additional verification required"
    ],
    "amount": 10001.0,
    "additionalInfo": null,
    "location": "X",
    "paymentMethod": null,
    "customerType": "Non-Employee",
    "currency": "SEK"
}


