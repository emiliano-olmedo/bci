CREATE TABLE IF NOT EXISTS users (
    ID UUID PRIMARY KEY,
    NAME VARCHAR(255),
    EMAIL VARCHAR(255) NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    CREATED_DATE TIMESTAMP NOT NULL,
    UPDATED_DATE TIMESTAMP NOT NULL,
    LAST_LOGIN TIMESTAMP NOT NULL,
    IS_ACTIVE BOOLEAN,
    TOKEN VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS phones (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID UUID NOT NULL,
    NUMBER VARCHAR(20),
    CITY_CODE VARCHAR(10),
    COUNTRY_CODE VARCHAR(10),
    FOREIGN KEY (USER_ID) REFERENCES users(ID)
);