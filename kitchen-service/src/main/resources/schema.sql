-- SQL DDL for Kitchen Service database
CREATE DATABASE IF NOT EXISTS kitchen_db;
USE kitchen_db;

CREATE TABLE IF NOT EXISTS kitchen_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);
