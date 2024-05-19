CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(15),
    password VARCHAR(20),
    email VARCHAR(30),
    phone_number VARCHAR(20),
    signature VARCHAR(40)
);