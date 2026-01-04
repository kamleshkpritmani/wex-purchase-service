INSERT INTO users (id, username, password, role, enabled, account_locked) 
VALUES (
    1,
    'reviewer', 
    '$2a$10$EQ4rzC4847Bi8V3OjvPFCONPtAw6WjyR6sjXb/hBenmvdOQ71bodq', 
    'ROLE_USER', 
    true, 
    false
) ON CONFLICT (username) DO NOTHING;