-- Fix admin password hash (V2 hash was truncated to 59 chars, must be 60)
UPDATE users
SET password = '$2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6'
WHERE email = 'admin@fanus.az';
