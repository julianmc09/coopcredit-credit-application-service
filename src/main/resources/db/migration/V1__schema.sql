CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE affiliates (
    id VARCHAR(255) PRIMARY KEY,
    document VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    salary DECIMAL(19, 2) NOT NULL,
    affiliation_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE risk_evaluations (
    id VARCHAR(255) PRIMARY KEY,
    score INTEGER NOT NULL,
    risk_level VARCHAR(255) NOT NULL,
    detail TEXT NOT NULL,
    reason TEXT
);

CREATE TABLE credit_applications (
    id VARCHAR(255) PRIMARY KEY,
    requested_amount DECIMAL(19, 2) NOT NULL,
    term INTEGER NOT NULL,
    proposed_rate DECIMAL(19, 2) NOT NULL,
    application_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    affiliate_id VARCHAR(255) NOT NULL,
    risk_evaluation_id VARCHAR(255) UNIQUE,
    CONSTRAINT fk_credit_application_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates (id),
    CONSTRAINT fk_credit_application_risk_evaluation FOREIGN KEY (risk_evaluation_id) REFERENCES risk_evaluations (id)
);
