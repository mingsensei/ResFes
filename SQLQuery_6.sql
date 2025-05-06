-- Bảng USERS
CREATE TABLE users (
                       id VARCHAR(64) PRIMARY KEY,
                       name NVARCHAR(100) NOT NULL,
                       email NVARCHAR(100) NOT NULL UNIQUE,
                       role NVARCHAR(20) NOT NULL,
                       password NVARCHAR(256) NOT NULL
);

-- Bảng SUBJECTS
CREATE TABLE subjects (
                          id VARCHAR(64) PRIMARY KEY,
                          name NVARCHAR(100) NOT NULL,
                          description NVARCHAR(255)
);

-- BẢNG CHAPTERS
CREATE TABLE chapters (
                          id VARCHAR(64) PRIMARY KEY,
                          title NVARCHAR(255) NOT NULL,
                          subject_id VARCHAR(64) NOT NULL,
                          order_index INT,
                          FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- BẢNG MATERIALS
CREATE TABLE materials (
                           id VARCHAR(64) PRIMARY KEY,
                           title NVARCHAR(255) NOT NULL,
                           content NVARCHAR(MAX),
                           chapter_id VARCHAR(64) NOT NULL,
                           type NVARCHAR(50),
                           FOREIGN KEY (chapter_id) REFERENCES chapters(id)
);

-- BẢNG EXAMS
CREATE TABLE exams (
                       id VARCHAR(64) PRIMARY KEY,
                       student_id VARCHAR(64) NOT NULL,
                       subject_id VARCHAR(64) NOT NULL,
                       score INT,
                       submitted_at DATETIME,
                       FOREIGN KEY (student_id) REFERENCES users(id),
                       FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- ✅ BẢNG QUESTIONS (đã có explain)
CREATE TABLE questions (
                           id VARCHAR(64) PRIMARY KEY,
                           content NVARCHAR(MAX) NOT NULL,
                           option_a NVARCHAR(255),
                           option_b NVARCHAR(255),
                           option_c NVARCHAR(255),
                           option_d NVARCHAR(255),
                           correct_option NVARCHAR(1),
                           student_answer NVARCHAR(1),
                           exam_id VARCHAR(64) NOT NULL,
                           explain NVARCHAR(MAX),
                           FOREIGN KEY (exam_id) REFERENCES exams(id)
);

-- BẢNG LEVELS
CREATE TABLE levels (
                        id VARCHAR(64) PRIMARY KEY,
                        student_id VARCHAR(64) NOT NULL,
                        subject_id VARCHAR(64) NOT NULL,
                        level INT DEFAULT 1,
                        current_exp INT DEFAULT 0,
                        required_exp INT DEFAULT 100,
                        FOREIGN KEY (student_id) REFERENCES users(id),
                        FOREIGN KEY (subject_id) REFERENCES subjects(id)
);
