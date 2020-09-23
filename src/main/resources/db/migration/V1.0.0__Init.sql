create table accounts (
	id integer identity primary key,
	name varchar(255)
);

create table documents (
	id integer identity primary key,
	date datetime
);

create table expense_items (
	id integer identity primary key,
	name varchar(255)
);

create table income_items (
	id integer identity primary key,
	name varchar(255)
);

create table persons (
	id integer identity primary key,
	name varchar(255)
);

create table ticker_symbols (
	id integer identity primary key,
	name varchar(255)
);

create table journal_entries (
	id integer identity primary key,
	date datetime,
	code varchar(8),
	type varchar(4),
	amount decimal(14,4),
	person_id integer,
	currency varchar(8),
	currency_rate decimal(14,4),
	currency_amount decimal(14,4),
	"count" integer,
	ticker_symbol_id integer,
	document_id integer,
	product_type varchar(64),
	foreign key (person_id) references persons(id),
	foreign key (ticker_symbol_id) references ticker_symbols(id),
	foreign key (document_id) references documents(id)
);

