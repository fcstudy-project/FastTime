CREATE TABLE certification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at    datetime              NULL,
    updated_at    datetime              NULL,
    deleted_at    datetime              NULL,
    bootcamp_name VARCHAR(255) NOT NULL,
    member_id BIGINT NOT NULL,
    image TEXT,
    content TEXT,
    withdrawal_reason TEXT,
    bootcamp_id BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'WITHDRAW') NOT NULL,
    rejection_reason TEXT,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (bootcamp_id) REFERENCES bootcamp(id)
    );
