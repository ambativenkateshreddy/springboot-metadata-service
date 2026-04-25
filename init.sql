-- Create tenant schemas
CREATE SCHEMA IF NOT EXISTS tenant_acme   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE SCHEMA IF NOT EXISTS tenant_globex CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE SCHEMA IF NOT EXISTS tenant_initech CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant privileges
GRANT ALL PRIVILEGES ON tenant_acme.*    TO 'root'@'%';
GRANT ALL PRIVILEGES ON tenant_globex.*  TO 'root'@'%';
GRANT ALL PRIVILEGES ON tenant_initech.* TO 'root'@'%';
FLUSH PRIVILEGES;
