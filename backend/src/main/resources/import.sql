INSERT INTO item(id, name, price, stock) VALUES (1, 'Cherry', 10, 10);
INSERT INTO item(id, name, price, stock) VALUES (2, 'Apple', 15, 15);
INSERT INTO item(id, name, price, stock) VALUES (3, 'Banana', 20, 20);
INSERT INTO item(id, name, price, stock) VALUES (4, 'Orange', 25, 5);
INSERT INTO item(id, name, price, stock) VALUES (5, 'Blueberry', 0.5, 200);
INSERT INTO item(id, name, price, stock) VALUES (6, 'Nuts', 1.25, 100);
INSERT INTO item(id, name, price, stock) VALUES (7, 'Peach', 4, 30);

INSERT INTO cart(id) VALUES (1);
INSERT INTO cart(id) VALUES (2);
INSERT INTO cart(id) VALUES (3);

INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (1, 1, 2);
INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (1, 2, 3);
INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (1, 3, 1);
INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (2, 4, 5);
INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (2, 5, 10);
INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (3, 6, 15);
INSERT INTO cartItem(cart_id, item_id, quantity) VALUES (3, 7, 2);

