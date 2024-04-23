# Database:

For this project, you need to create a database, which is named: advasco_test_datenbank

You need also a table, which is named: users


# Configurations in the application.properties file:

spring.application.name=advascotestproject

#This properties are used to connect to the database

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.url=jdbc:mysql://localhost:3306/advasco_test_datenbank

spring.datasource.username=root

spring.datasource.password=

#This properties are used to show the SQL queries in the console

spring.jpa.show-sql=true

#This properties is used to update the database each time by updating the models

spring.jpa.hibernate.ddl-auto=update
