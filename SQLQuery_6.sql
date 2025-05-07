CREATE TABLE chapters (
    id varchar(64) NOT NULL,
    title nvarchar(255) NOT NULL,
    subject_id varchar(64) NOT NULL,
    order_index int NULL
);
GO

CREATE TABLE exams (
    id varchar(64) NOT NULL,
    student_id varchar(64) NOT NULL,
    chapter_id varchar(64) NOT NULL,
    score int NULL,
    submitted_at datetime NULL
);
GO

CREATE TABLE levels (
    id varchar(64) NOT NULL,
    student_id varchar(64) NOT NULL,
    chapter_id varchar(64) NOT NULL,
    level int NULL,
    current_exp int NULL,
    required_exp int NULL
);
GO

CREATE TABLE materials (
    id varchar(255) NOT NULL,
    title varchar(255) NOT NULL,
    pdfPath varchar(255) NULL,
    chapter_id varchar(64) NULL,
    type varchar(50) NOT NULL,
    vectorDbPath varchar(255) NULL
);
GO

CREATE TABLE questions (
    id varchar(64) NOT NULL,
    content nvarchar(max) NOT NULL,
    option_a nvarchar(max) NOT NULL,
    option_b nvarchar(max) NOT NULL,
    option_c nvarchar(max) NOT NULL,
    option_d nvarchar(max) NOT NULL,
    correct_option char(1) NOT NULL,
    student_answer char(1) NULL,
    exam_id varchar(64) NULL,
    explain nvarchar(max) NULL,
    difficulty int NULL
);
GO

CREATE TABLE subjects (
    id varchar(64) NOT NULL,
    name nvarchar(100) NOT NULL,
    description nvarchar(255) NULL
);
GO

CREATE TABLE users (
    id varchar(64) NOT NULL,
    name nvarchar(100) NOT NULL,
    email nvarchar(100) NOT NULL,
    role nvarchar(20) NOT NULL,
    password nvarchar(256) NOT NULL
);
GO

INSERT INTO subjects values (1, 'Operating System', 'OS FPT')
INSERT INTO chapters values (2, 'Chapter 2', 1, 2)
