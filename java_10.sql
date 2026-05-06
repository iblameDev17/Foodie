CREATE DATABASE IF NOT EXISTS javaDemo;

USE javaDemo;

CREATE TABLE `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255)
);

INSERT INTO `user` (name, username, password) VALUES
('Dev Tayade', 'Dev', 'password123'),
('Jane Smith', 'jane', 'password456');

select * from user;

CREATE TABLE IF NOT EXISTS food_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE,
    image VARCHAR(500),
    description VARCHAR(500),
    is_veg BOOLEAN -- 1 for Veg, 0 for Non-Veg
);

TRUNCATE TABLE food_item;


INSERT INTO food_item (name, price, image, description, is_veg) VALUES
-- Main Course & Snacks
('Classic Cheeseburger', 249, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500', 'Juicy patty with melted cheddar and fresh veggies.', 0),
('Margherita Pizza', 399, 'https://images.unsplash.com/photo-1604068549290-dea0e4a305ca?w=500', 'Fresh mozzarella, basil, and tomato sauce.', 1),
('Spicy Chicken Wings', 299, 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=500', 'Crispy wings with our secret spice blend.', 0),
('Caesar Salad', 199, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500', 'Crisp romaine with parmesan and croutons.', 1),
('Grilled Fish Tacos', 349, 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=500', 'Seasoned grilled fish with cabbage slaw.', 0),
('Butter Chicken', 349, 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=500', 'Creamy tomato sauce with tender chicken pieces.', 0),
('Veggie Biryani', 279, 'https://images.unsplash.com/photo-1645112411341-6c4ee32510d8?w=500', 'Fragrant basmati rice with mixed vegetables.', 1),
('Paneer Tikka', 299, 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=500', 'Marinated paneer cubes grilled to perfection.', 1),
('Chicken Alfredo', 380, 'https://images.unsplash.com/photo-1645112411341-6c4ee32510d8?w=500', 'Creamy white sauce pasta with grilled chicken.', 0),
('Masala Dosa', 120, 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500', 'Crispy crepe filled with spicy potato mash.', 1),
('Hakka Noodles', 210, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500', 'Stir-fried noodles with crunchy vegetables.', 1),
('Mutton Seekh Kebab', 450, 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=500', 'Spiced minced mutton skewers.', 0),

-- Desserts & Ice Creams
('Chocolate Brownie', 149, 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=500', 'Rich chocolate fudge brownie with ice cream.', 1),
('New York Cheesecake', 299, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=500', 'Classic creamy cheesecake with berry compote.', 1),
('Vanilla Bean Gelato', 120, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=500', 'Smooth Italian vanilla ice cream.', 1),
('Belgian Waffle', 199, 'https://images.unsplash.com/photo-1562329265-95a6d7a63440?w=500', 'Warm waffle topped with Nutella and strawberries.', 1),
('Gulab Jamun (2pc)', 80, 'https://images.unsplash.com/photo-1589113182023-c9096238a8e3?w=500', 'Sweet milk dumplings soaked in saffron syrup.', 1),
('Red Velvet Cupcake', 99, 'https://images.unsplash.com/photo-1614707267537-b85aaf00c4b7?w=500', 'Soft cupcake with cream cheese frosting.', 1),
('Mango Sorbet', 130, 'https://images.unsplash.com/photo-1488900128323-21503983a07e?w=500', 'Refreshing vegan mango ice treat.', 1),
('Apple Pie', 180, 'https://images.unsplash.com/photo-1568571780765-9276ac8b75a2?w=500', 'Classic warm apple pie with cinnamon.', 1);

USE javaDemo;
ALTER TABLE user
ADD COLUMN address TEXT,
ADD COLUMN profile_pic VARCHAR(500) DEFAULT 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png';
ALTER TABLE user MODIFY COLUMN password VARCHAR(255);