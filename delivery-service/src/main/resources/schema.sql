-- SQL DDL for Delivery Service database
CREATE DATABASE IF NOT EXISTS delivery_db;
USE delivery_db;

CREATE TABLE IF NOT EXISTS deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    driver_name VARCHAR(255),
    status VARCHAR(50) NOT NULL
);
