Ejecución:
- Clonar ropositorio: https://github.com/emiliano-olmedo/bci

- Configurar entorno: Java JDK 11, Maven.

- Para levantar en local, en la terminal:
mvn spring-boot:run

- Acceso a BBDD H2: 

Driver Class:	org.h2.Driver
JDBC URL:    	jdbc:h2:mem:userdb
User Name:   	SA
Password:   	(deja este campo vacío)


Diagrama de componentes:

+-----------------------+
|   Client Application  |
+-----------+-----------+
|
v
+-----------+-----------+
| Authentication Service |
+-----------+-----------+
| - User Repository      |
| - Token Filter         |
+-----------+-----------+
|
v
+-----------+-----------+
|       User Service     |
+-----------+-----------+
| - JwtUtil              |
+-----------+-----------+
|
v
+-----------+-----------+
|        Database        |
+-----------------------+

Diagrama de secuencia:

Client                       Authentication Service   User Repository         JWT Filter            Database
|                           |                        |                       |                     |                     |
| -----> /authenticate      |                        |                       |                     |                     |
|       (email, password)   |                        |                       |                     |                     |
|                           |                        |                       |                     |                     |
|                           |                        |                       |                     |                     |
|                           |-----> findByEmail()    |                       |                     |                     |
|                           |       (email)          | -----> User           |                     |                     |
|                           |                        |       (User)          |                     |                     |
|                           |<----- User             |                       |                     |                     |
|                           |                        |                       |                     |                     |
|                           |-----> authenticate()   |                       |                     |                     |
|                           |      (email, password) |                       |                     |                     |
|                           |                        |                       |                     |                     |
|                           |<----- success          |                       |                     |                     |
|                           |                        |                       |                     |                     |
|                           |-----> generateToken()  |                       |                     |                     |
|                           |       (UserDetails)    |                       |                     |                     |
|                           |                        |                       |-----> Token         |                     |
|                           |                        |                       |<----- Token         |                     |
|                           |<----- Token            |                       |                     |                     |
| <----- Token              |                        |                       |                     |                     |
|                           |                        |                       |                     |                     |
