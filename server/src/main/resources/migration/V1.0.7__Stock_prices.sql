create table stock_prices (
	id integer unsigned auto_increment primary key,
	date datetime,
	ticker varchar(64),
	price decimal(14,4)
);