create table tinkoff_tarifs (
	id integer unsigned auto_increment primary key,
	date datetime,
	account_id integer unsigned,
	tariff varchar(255),
	foreign key (account_id) references accounts(id)
);