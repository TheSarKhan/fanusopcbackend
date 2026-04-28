-- Test operator user (password: Fanus@2024)
INSERT INTO users (email, password, role, first_name, last_name, email_verified)
VALUES ('operator@fanus.az', '$2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6', 'OPERATOR', 'Test', 'Operator', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Test psychologist user (password: Fanus@2024)
INSERT INTO users (email, password, role, first_name, last_name, email_verified)
VALUES ('psixoloq@fanus.az', '$2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6', 'PSYCHOLOGIST', 'Test', 'Psixoloq', TRUE)
ON CONFLICT (email) DO NOTHING;
