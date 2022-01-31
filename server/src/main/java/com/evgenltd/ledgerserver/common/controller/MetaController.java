package com.evgenltd.ledgerserver.common.controller;

import com.evgenltd.ledgerserver.util.ApplicationException;
import com.evgenltd.ledgerserver.util.meta.MetaEntity;
import com.evgenltd.ledgerserver.util.meta.MetaEntityField;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/meta")
public class MetaController {

    private final ObjectMapper mapper;

    public MetaController(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public List<MetaEntity> meta() {
        try (final InputStream stream = getClass().getResourceAsStream("meta.json")) {
            return mapper.readValue(stream, new TypeReference<ArrayList<MetaEntity>>() {});
        } catch (final IOException e) {
            throw new ApplicationException(e, "Unable to read meta info");
        }
    }
//        return Arrays.asList(
//                new MetaEntity("BuyCurrency", "/entities/document/buy-currency/buy-currency", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.name"),
//                        new MetaEntityField("amount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.amount"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.account"),
//                        new MetaEntityField("currency", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.currency"),
//                        new MetaEntityField("currencyRate", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.currencyRate"),
//                        new MetaEntityField("currencyAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.currencyAmount"),
//                        new MetaEntityField("commission", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.commission"),
//                        new MetaEntityField("commissionAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrency.BuyCurrency.commissionAmount")
//                )),
//                new MetaEntity("BuyCurrencyStock", "/entities/document/buy-currency-stock/buy-currency-stock", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.name"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.account"),
//                        new MetaEntityField("ticker", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.ticker"),
//                        new MetaEntityField("price", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.price"),
//                        new MetaEntityField("count", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.count"),
//                        new MetaEntityField("currency", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.currency"),
//                        new MetaEntityField("currencyAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buycurrencystock.BuyCurrencyStock.currencyAmount")
//                )),
//                new MetaEntity("BuyStock", "/entities/document/buy-stock/buy-stock", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.name"),
//                        new MetaEntityField("amount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.amount"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.account"),
//                        new MetaEntityField("ticker", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.ticker"),
//                        new MetaEntityField("price", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.price"),
//                        new MetaEntityField("count", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.count"),
//                        new MetaEntityField("commission", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.commission"),
//                        new MetaEntityField("commissionAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.buystock.BuyStock.commissionAmount")
//                )),
//                new MetaEntity("FounderContribution", "/entities/document/founder-contribution/founder-contribution", "com.evgenltd.ledgerserver.platform.entities.document.foundercontribution.FounderContribution", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.foundercontribution.FounderContribution.name"),
//                        new MetaEntityField("amount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.foundercontribution.FounderContribution.amount"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.foundercontribution.FounderContribution.account")
//                )),
//                new MetaEntity("SellCurrency", "/entities/document/sell-currency/sell-currency", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.name"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.account"),
//                        new MetaEntityField("currency", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.currency"),
//                        new MetaEntityField("currencyRate", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.currencyRate"),
//                        new MetaEntityField("currencyAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.currencyAmount"),
//                        new MetaEntityField("commission", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.commission"),
//                        new MetaEntityField("commissionAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.commissionAmount"),
//                        new MetaEntityField("currencySaleIncome", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.currencySaleIncome"),
//                        new MetaEntityField("currencySaleExpense", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrency.SellCurrency.currencySaleExpense")
//                )),
//                new MetaEntity("SellCurrencyStock", "/entities/document/sell-currency-stock/sell-currency-stock", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.name"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.account"),
//                        new MetaEntityField("ticker", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.ticker"),
//                        new MetaEntityField("price", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.price"),
//                        new MetaEntityField("count", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.count"),
//                        new MetaEntityField("currencyRate", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.currencyRate"),
//                        new MetaEntityField("currency", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.currency"),
//                        new MetaEntityField("commission", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.commission"),
//                        new MetaEntityField("commissionAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.commissionAmount"),
//                        new MetaEntityField("stockSaleIncome", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.stockSaleIncome"),
//                        new MetaEntityField("stockSaleExpense", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.stockSaleExpense"),
//                        new MetaEntityField("directSelling", FieldType.BOOLEAN, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellcurrencystock.SellCurrencyStock.directSelling")
//                )),
//                new MetaEntity("SellStock", "/entities/document/sell-stock/sell-stock", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.name"),
//                        new MetaEntityField("account", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.account"),
//                        new MetaEntityField("ticker", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.ticker"),
//                        new MetaEntityField("price", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.price"),
//                        new MetaEntityField("count", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.count"),
//                        new MetaEntityField("commission", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.commission"),
//                        new MetaEntityField("commissionAmount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.commissionAmount"),
//                        new MetaEntityField("stockSaleIncome", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.stockSaleIncome"),
//                        new MetaEntityField("stockSaleExpense", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.sellstock.SellStock.stockSaleExpense")
//                )),
//                new MetaEntity("Transfer", "/entities/document/transfer/transfer", "com.evgenltd.ledgerserver.platform.entities.document.transfer.Transfer", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.document.transfer.Transfer.name"),
//                        new MetaEntityField("amount", FieldType.NUMBER, false, "", "com.evgenltd.ledgerserver.platform.entities.document.transfer.Transfer.amount"),
//                        new MetaEntityField("from", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.transfer.Transfer.from"),
//                        new MetaEntityField("to", FieldType.OBJECT, false, "", "com.evgenltd.ledgerserver.platform.entities.document.transfer.Transfer.to")
//                )),
//                new MetaEntity("Account", "/entities/reference/account/account", "com.evgenltd.ledgerserver.platform.entities.reference.account.Account", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.reference.account.Account.name")
//                )),
//                new MetaEntity("Currency", "/entities/reference/currency/currency", "com.evgenltd.ledgerserver.platform.entities.reference.currency.Currency", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.reference.currency.Currency.name")
//                )),
//                new MetaEntity("ExpenseItem", "/entities/reference/expense-item/expense-item", "com.evgenltd.ledgerserver.platform.entities.reference.expenseitem.ExpenseItem", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.reference.expenseitem.ExpenseItem.name")
//                )),
//                new MetaEntity("IncomeItem", "/entities/reference/income-item/income-item", "com.evgenltd.ledgerserver.platform.entities.reference.incomeitem.IncomeItem", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.reference.incomeitem.IncomeItem.name")
//                )),
//                new MetaEntity("Person", "/entities/reference/person/person", "com.evgenltd.ledgerserver.platform.entities.reference.person.Person", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.reference.person.Person.name")
//                )),
//                new MetaEntity("TickerSymbol", "/entities/reference/ticker-symbol/ticker-symbol", "com.evgenltd.ledgerserver.platform.entities.reference.tickersymbol.TickerSymbol", Arrays.asList(
//                        new MetaEntityField("name", FieldType.STRING, true, "", "com.evgenltd.ledgerserver.platform.entities.reference.tickersymbol.TickerSymbol.name"),
//                        new MetaEntityField("figi", FieldType.STRING, false, "", "com.evgenltd.ledgerserver.platform.entities.reference.tickersymbol.TickerSymbol.figi"),
//                        new MetaEntityField("withoutCommission", FieldType.BOOLEAN, false, "", "com.evgenltd.ledgerserver.platform.entities.reference.tickersymbol.TickerSymbol.withoutCommission")
//                ))
//        );
//    }

}