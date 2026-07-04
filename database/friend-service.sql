-- Friend service schema
CREATE TABLE IF NOT EXISTS friend (
  id INT AUTO_INCREMENT PRIMARY KEY,
  follower_id INT,
  following_id INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
