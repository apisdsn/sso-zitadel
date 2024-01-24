DROP DATABASE reimbursement;
CREATE DATABASE reimbursement;
USE reimbursement;

CREATE TABLE employees
(
    employee_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id    VARCHAR(50) NOT NULL,
    full_name    VARCHAR(50) NOT NULL,
    phone_number VARCHAR(50),
    email        VARCHAR(50) NOT NULL,
    company      VARCHAR(50) NOT NULL,
    position     VARCHAR(50) NOT NULL,
    gender       VARCHAR(20) NOT NULL,
    UNIQUE KEY clientId_unique (client_id),
    UNIQUE KEY email_unique (email)
) ENGINE InnoDB;

CREATE TABLE reimbursement
(
    reimbursement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id      BIGINT                     NOT NULL,
    approved_id      VARCHAR(50)  DEFAULT NULL,
    approved_name    VARCHAR(100) DEFAULT NULL,
    amount           DECIMAL(38, 2)             NOT NULL,
    activity         VARCHAR(100),
    type             VARCHAR(100),
    description      VARCHAR(255),
    status           BOOLEAN      DEFAULT FALSE NOT NULL,
    date_created     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    date_updated     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_reimbursement_clientId FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE InnoDB;

CREATE TABLE address
(
    address_id  VARCHAR(100) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    street      VARCHAR(100),
    city        VARCHAR(50),
    province    VARCHAR(50),
    country     VARCHAR(50),
    postal_code VARCHAR(10),
    CONSTRAINT FK_address_employeeId FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE InnoDB;

