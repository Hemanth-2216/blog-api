
-- Insert sample users
INSERT INTO users (username, email, password) VALUES
('john_doe', 'john@example.com', 'password123'),
('jane_smith', 'jane@example.com', 'securepass'),
('admin_user', 'admin@example.com', 'admin123');

-- Insert sample posts
INSERT INTO posts (title, content, author_id) VALUES
('First Blog Post', 'This is the content of the first blog post.', 1),
('Second Blog Post', 'Another interesting blog content goes here.', 2);

-- Insert sample comments
INSERT INTO comments (post_id, content, author_id) VALUES
(1, 'Great post!', 2),
(1, 'Thanks for sharing.', 3),
(2, 'Very informative.', 1);
