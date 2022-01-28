create table currencies (
	id integer unsigned auto_increment primary key,
	name varchar(255)
);

insert into currencies (name) values
('RUB'),
('USD'),
('EUR')