# Spring skeleton

## Requirements

To use the application, the following setup is needed:
- Java 8 installed
- Maven installed
- A MySQL database called `springskeleton`
## Usage
To run the application, use the following command filling the two arguments with your credentials:
```batch
cd spring-skeleton
mvn spring-boot:run -Drun.arguments="--jasypt.encryptor.password={THE PASSWORD},--front-end.url={FRONT END URL}"
```
**API URL: http://localhost:8080/api**

The API endpoints are documented with Swagger, as it's described in [API functionality](https://github.com/sergiobanegas/spring-skeleton#api-functionality) section.


To execute all tests, run the command below:
```batch
cd spring-skeleton
mvn clean test -Dtest=springskeleton.AllTests -Dspring.profiles.active=test -Djasypt.encryptor.password={THE PASSWORD} -Dfront-end.url={FRONT END URL}
```
How to execute all integration tests:
```batch
cd spring-skeleton
mvn clean test -Dtest=springskeleton.AllIntegrationTests -Dspring.profiles.active=test -Djasypt.encryptor.password={THE PASSWORD} -Dfront-end.url={FRONT END URL}
```
How to execute all unit tests:
```batch
cd spring-skeleton
mvn clean test -Dtest=springskeleton.AllUnitTests
```
Example of executing an integration test:
```batch
cd spring-skeleton
mvn clean test -Dtest=springskeleton.service.UserServiceIT -Dspring.profiles.active=test -Djasypt.encryptor.password={THE PASSWORD} -Dfront-end.url={FRONT END URL}
```
Example of executing an unit test:
```batch
cd spring-skeleton
mvn clean test -Dtest=springskeleton.controller.CommentControllerUT
```
## Configuration
This application sends emails to verify some operations. In order to set the main application email, these two properties of the file `spring.skeleton/src/main/resources/application.yml` have to be changed:
```yml
mail:
  username: YOUR EMAIL (with permission to send emails in an application)
  password: ENC(ENCODED PASSWORD)
```
As the application uses **jasypt**, you need to encode your email password with a secret key. This key must be passed as an argument (jasypt.encryptor.password) when running the application. More information at http://www.jasypt.org/cli.html

The default admin user has the following credentials:
```yml
user: admin@gmail.com
password: admin
```
Once this is changed, you can run the application with the key and front-end url:
```
-Djasypt.encryptor.password=the key used for encrypt the email password

-Dfront-end.url=the front-end url, by default http://localhost:3000
```
## Functionality

### Technical functionality
- **API REST:** the application uses REST controllers as the only entry point.
- **Authentication**: the app uses **Spring Security** together with  **JWT** to control the user session. The application is stateless as it doesn't store any information of the user session, only in a cookie inside the JWT. This way, the front-end doesn't have to worry about the JWT expiration, as the browser does it out of the box. Apart from this cookie, it uses another cookie with non sensitive information to help the front-end getting the user roles and language.
- **Roles**: an user can have two roles: admin or user. The admin can access to some functionality that the user doesn't. The roles are controlled thanks to the JWT.
- **Email service**: the application uses java mail to send emails to the user. The emails are mainly used to confirm different operations, making the app more secure.
- **Testing**: the application contains both integration tests and unit tests. In order to test the application with real data, each time a test is executed, a seeder service fills the database with fake data (located  in `spring.skeleton/src/test/resources/data-for-testing.yml`) and clear the data after. The tests use an **h2** database, meaning that the real database (MySQL) won't be altered after the execution of these tests.
- **Batch process**: the application contains a batch process that is executed every day at 00:00am and removes all the unconfirmed users and the unconfirmed tokens for email changes.
- **Logging**: the app uses **log4j2**.
- **i18n**: the application is implemented with multi-language functionality. Thanks to resource bundle, depending on the user language it displays messages in english, spanish, japanese or french. 
By default, the application will return the messages in the language of the user browser (Accept-language header), but the user can change the language manually. If the supported language list doesn't contain the browser language, the messages are shown in english. The language is stored in the JWT.
- **Advices**: the application handles all the exceptions the services throw thanks to the advices. They convert the exception to a wrapper complying with the contract with the client.
- **Validators**: custom validators to check if the request fields are correct.
- **Swagger**: in order to test and document the different endpoints, the server uses Swagger. The url is the following: http://localhost:8080/api/swagger-ui.html#/
### API functionality
- **Sign up**: The user can sign up filling the following credentials: email, password, name and gender. The user will receive an email that contains a link to confirm his account.
- **Sign in**: The user can log in using his email and password.
- **Password recovery**. If the user doesn't remember the password he can reset it by introducing his email. He will receive an email that contains a link to set the new password.
- **Edit account info**. The user can edit his name, password, email (with a secured email based confirmation), name, avatar, language and gender.
- **Delete account**: to delete his account, he will receive a confirmation email.
-  **Admin users**: the administrator can admin users: 
    + Get the full list of users paginated
    + See the info of an specific user
    + Delete an user
- **Comments**: this functionality is implemented to include it in future applications that start from this skeleton. Although the comment can have infinite comment childs, a comment doesn't specify a parent. That means that the comment functionality can be inserted in different future entities, for example: forum posts, blog entries, restaurants, fly offers...
## Architecture
The application uses the following structure:
- **Config**: contains all the configuration (properties, spring security, persistence config...).
- **REST controllers**: receive the data and validates it thanks to hibernate validators and custom annotations.
- **Services**: receive the wrapper from the controller and make the business logic using the different DAOs. This layer is the one that throws exceptions.
- **Batch processes**: are executed every so often and interact with the DAOs to make automated operations.
- **DAOs**: implement JPARepository and interact with the database thanks to the entities annotated with the javax entity annotation.
- **Model**: contains all the javax entities. They are used as DTOs for operating with the database.
- **Utils**: components used as utilities, independent from the business logic (services, DTOs and DAOs).


#### Application made by Sergio Banegas for learning purposes
