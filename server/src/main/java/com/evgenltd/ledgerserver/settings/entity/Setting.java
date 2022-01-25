package com.evgenltd.ledgerserver.settings.entity;

import com.evgenltd.ledgerserver.base.entity.ExpenseItem;
import com.evgenltd.ledgerserver.base.entity.IncomeItem;
import com.evgenltd.ledgerserver.base.entity.Person;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "broker_id")
    private Person broker;

    @ManyToOne
    @JoinColumn(name = "broker_commission_expense_item_id")
    private ExpenseItem brokerCommissionExpenseItem;

    @ManyToOne
    @JoinColumn(name = "currency_reassessment_expense_item_id")
    private ExpenseItem currencyReassessmentExpenseItem;

    @ManyToOne
    @JoinColumn(name = "currency_reassessment_income_item_id")
    private IncomeItem currencyReassessmentIncomeItem;

    @ManyToOne
    @JoinColumn(name = "currency_sale_expense_item_id")
    private ExpenseItem currencySaleExpenseItem;

    @ManyToOne
    @JoinColumn(name = "currency_sale_income_item_id")
    private IncomeItem currencySaleIncomeItem;

    @ManyToOne
    @JoinColumn(name = "stock_reassessment_expense_item_id")
    private ExpenseItem stockReassessmentExpenseItem;

    @ManyToOne
    @JoinColumn(name = "stock_reassessment_income_item_id")
    private IncomeItem stockReassessmentIncomeItem;

    @ManyToOne
    @JoinColumn(name = "stock_sale_expense_item_id")
    private ExpenseItem stockSaleExpenseItem;

    @ManyToOne
    @JoinColumn(name = "stock_sale_income_item_id")
    private IncomeItem stockSaleIncomeItem;


}
