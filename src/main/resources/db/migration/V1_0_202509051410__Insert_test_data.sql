INSERT INTO "user" (id, name, date_of_birth, password) VALUES
(1, 'Иван Иванов', '1990-05-15', '$2b$12$P3m/GSzs3u8oIk17AfVMEeNO0Kdd2MHdLQCfcisda.nGmnZI/N/JS'),
(2, 'Петр Петров', '1985-11-23', '$2b$12$cXsdmk7fq3jYTe6MArs7wO/chTKKD6UK5sHzU6DWf.jRknRzhCoRe'),
(3, 'Мария Сидорова', '1995-02-10', '$2b$12$Pjz4pRlU6hmd/4TIxkLzoOQHEqV8p.UoLZNbW57cUu5ycjvT1BUNG'),
(4, 'Анна Козлова', '1988-07-30', '$2b$12$H924.8Q45wzlJ1V81mc04.uX3YOfC4sKgCSBJerVyhHg8ctDrbdPq'),
(5, 'Сергей Смирнов', '1993-09-17', '$2b$12$cye/AElH1.d1h1fUkU3QF.bHo15E733eQZos.CIQyMigKNWyyDUBO'),
(6, 'Ольга Новикова', '1991-12-05', '$2b$12$jbs6quSpa2eurgTtVUs41OZ5mqdJ4gdlgD0E4LK2Ic8mpGglOCqLu'),
(7, 'Дмитрий Волков', '1987-04-21', '$2b$12$q8fqKX0VeOAO0gMqDxE.z.8WeTM7QadgXvRVsKQ/9kthO8NWPEvou'),
(8, 'Елена Лебедева', '1994-08-12', '$2b$12$hGSV80i/lW2k57Ovp2xU6e/6N/ye9TbQtOxQvejPk61r8kVyTl7qW');

INSERT INTO email_data (user_id, email) VALUES
(1, 'ivanov@example.com'),
(1, 'ivanov.work@example.com'),
(2, 'petrov@example.com'),
(3, 'sidorova@example.com'),
(3, 'm.sidorova@example.com'),
(4, 'kozlova@example.com'),
(5, 'smirnov@example.com'),
(6, 'novikova@example.com'),
(7, 'volkov@example.com'),
(7, 'd.volkov@example.com'),
(8, 'lebedeva@example.com');

INSERT INTO phone_data (user_id, phone) VALUES
(1, '79151234567'),
(2, '79262345678'),
(2, '79262345679'),
(3, '79373456789'),
(4, '79484567890'),
(4, '79484567891'),
(5, '79595678901'),
(6, '79606789012'),
(6, '79606789013'),
(7, '79717890123'),
(8, '79828901234');

INSERT INTO account (user_id, balance, init_balance) VALUES
(1, 15000.50, 15000.50),
(2, 23000.75, 23000.75),
(3, 5000.00, 5000.00),
(4, 18000.25, 18000.25),
(5, 9000.90, 9000.90),
(6, 12000.30, 12000.30),
(7, 35000.60, 35000.60),
(8, 8000.45, 8000.45);

SELECT setval('user_id_seq', (SELECT MAX(id) FROM "user"));