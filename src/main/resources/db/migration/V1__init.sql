create table customers (
 id int primary key NOT NULL auto_increment,
 name VARCHAR(255)
);

create table orders (
 id int primary key NOT NULL auto_increment,
 description VARCHAR(255) NOT NULL,
 customer_id int NOT NULL,
 CONSTRAINT orders_customer_id_fk FOREIGN KEY (customer_id) REFERENCES customers (id)
);