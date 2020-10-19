create table settings (
	id integer unsigned auto_increment primary key,
	name varchar(255),
	value varchar(255)
);

create table accounts (
	id integer unsigned auto_increment primary key,
	name varchar(255)
);

create table documents (
	id integer unsigned auto_increment primary key,
	date datetime,
	type varchar(255)
);

create table expense_items (
	id integer unsigned auto_increment primary key,
	name varchar(255)
);

create table income_items (
	id integer unsigned auto_increment primary key,
	name varchar(255)
);

create table persons (
	id integer unsigned auto_increment primary key,
	name varchar(255)
);

create table ticker_symbols (
	id integer unsigned auto_increment primary key,
	name varchar(255)
);

create table journal_entries (
	id integer unsigned auto_increment primary key,
	date datetime,
	code varchar(8),
	type varchar(8),
	amount decimal(14,4),
	account_id integer unsigned,
	person_id integer unsigned,
	currency varchar(8),
	currency_rate decimal(14,4),
	currency_amount decimal(14,4),
	price decimal(14,4),
	count integer,
	expense_item_id integer unsigned,
	income_item_id integer unsigned,
	ticker_symbol_id integer unsigned,
	document_id integer unsigned,
	product_type varchar(64),
	position integer,
	foreign key (account_id) references accounts(id),
	foreign key (person_id) references persons(id),
	foreign key (expense_item_id) references expense_items(id),
	foreign key (income_item_id) references income_items(id),
	foreign key (ticker_symbol_id) references ticker_symbols(id),
	foreign key (document_id) references documents(id)
);
