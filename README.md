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

Para una correcta visualización de los diagramas, se recomienda abrir archivo README.md en IDE.

Diagrama de componentes:

+-------------------------------------+
|             AuthController          |
|-------------------------------------|
| - authService : AuthService         |
|-------------------------------------|
| + signup(request: UserRequest)      |
| + login(authorizationHeader: token)|
+-------------------------------------+
            | (calls)
            v
+-------------------------------------+
|              AuthService            |
|-------------------------------------|
| - jwtUtil : JwtUtil                 |
| - userDetailsService : UserDetailsService |
| - userRepository : UserRepository   |
| - passwordEncoder : PasswordEncoder |
|-------------------------------------|
| + signup(request: UserRequest)      |
| + processLogin(token: String)       |
|-------------------------------------+
            | (calls)
            v
+-------------------------------------+
|           CustomUserDetailsService  |
|-------------------------------------|
| - userRepository : UserRepository   |
|-------------------------------------|
| + loadUserByUsername(username: email) |
+-------------------------------------+


Diagramas de secuencia:

signup

+-------------------+      +-----------------+      +------------------------+
|    AuthController |      |   AuthService   |      |  UserRepository        |
+-------------------+      +-----------------+      +------------------------+
         |                        |                           |
         | 1. signup(request)     |                           |
         |----------------------->|                           |
         |                        | 2. validatePassword()     |
         |                        |-------------------------->|
         |                        |                           |
         |                        | 3. validateEmail()        |
         |                        |-------------------------->|
         |                        |                           |
         |                        | 4. checkEmailExists()     |
         |                        |-------------------------->|
         |                        |                           |
         |                        | 5. userRepository.save()  |
         |                        |-------------------------->|
         |                        |                           |
         |                        | 6. generateToken()        |
         |                        |-------------------------->|
         |                        |                           |
         |                        | 7. return UserResponse    |
         |<-----------------------|                           |
         |                        |                           |
         | 8. return Response     |                           |
         |----------------------->|                           |
         |                        |                           |

login

+-------------------+      +-----------------+      +------------------------+      +-----------------------+
|    AuthController |      |   AuthService   |      |CustomUserDetailsService|      |  JwtUtil              |
+-------------------+      +-----------------+      +------------------------+      +-----------------------+
         |                        |                           |                            |
         | 1. login(token)        |                           |                            |
         |----------------------->|                           |                            |
         |                        | 2. extractUsername()      |                            |
         |                        |-------------------------->|
         |                        |                           |                            |
         |                        | 3. validateToken()        |                            |
         |                        |-------------------------->|
         |                        |                           | 4. loadUserByUsername()    |
         |                        |                           |--------------------------> |
         |                        |                           |                            |
         |                        | 5. findByEmail()          |                            |
         |                        |-------------------------->|
         |                        |                           |                            |
         |                        | 6. generateToken()        |                            |
         |                        |-------------------------->|
         |                        |                           |                            |
         |                        | 7. return UserResponse    |                            |
         |<-----------------------|                           |                            |
         |                        |                           |                            |
         | 8. return Response     |                           |                            |
         |----------------------->|                           |                            |
         |                        |                           |                            |
