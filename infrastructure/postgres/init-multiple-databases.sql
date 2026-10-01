-- This script runs automatically when the PostgreSQL container is created for the first time.
-- It ensures our notification-service has its own isolated datastore.

CREATE DATABASE notification_db;