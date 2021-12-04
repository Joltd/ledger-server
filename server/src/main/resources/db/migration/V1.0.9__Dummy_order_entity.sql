create table orders (
	id integer unsigned auto_increment primary key,
	name varchar(255),
	enabled bit(1),
	length integer,
	amount decimal(14,4),
	time datetime,
	date date,
	person_id integer unsigned,
	foreign key (person_id) references persons(id)
);
