rename table settings to old_settings;

create table settings (
	id integer unsigned auto_increment primary key,
	broker_id integer unsigned,
	broker_commission_expense_item_id integer unsigned,
	currency_reassessment_expense_item_id integer unsigned,
	currency_reassessment_income_item_id integer unsigned,
	currency_sale_expense_item_id integer unsigned,
	currency_sale_income_item_id integer unsigned,
	stock_reassessment_expense_item_id integer unsigned,
	stock_reassessment_income_item_id integer unsigned,
	stock_sale_expense_item_id integer unsigned,
	stock_sale_income_item_id integer unsigned,
	foreign key (broker_id) references persons(id),
	foreign key (broker_commission_expense_item_id) references expense_items(id),
	foreign key (currency_reassessment_expense_item_id) references expense_items(id),
	foreign key (currency_reassessment_income_item_id) references income_items(id),
	foreign key (currency_sale_expense_item_id) references expense_items(id),
	foreign key (currency_sale_income_item_id) references income_items(id),
	foreign key (stock_reassessment_expense_item_id) references expense_items(id),
	foreign key (stock_reassessment_income_item_id) references income_items(id),
	foreign key (stock_sale_expense_item_id) references expense_items(id),
	foreign key (stock_sale_income_item_id) references income_items(id)
);