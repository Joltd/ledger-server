package com.evgenltd.ledgerserver.service.bot.activity.doimport;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Document;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.repository.AccountRepository;
import com.evgenltd.ledgerserver.repository.DocumentRepository;
import com.evgenltd.ledgerserver.repository.TickerSymbolRepository;
import com.evgenltd.ledgerserver.service.bot.BotActivity;
import com.evgenltd.ledgerserver.service.bot.BotState;
import com.evgenltd.ledgerserver.service.bot.activity.document.*;
import com.evgenltd.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TinkoffStonksImportActivity extends BotActivity {

    private static final Logger log = LoggerFactory.getLogger(TinkoffStonksImportActivity.class);

    @Value("${downloads.directory}")
    private String downloads;

    private final BeanFactory beanFactory;
    private final AccountRepository accountRepository;
    private final TickerSymbolRepository tickerSymbolRepository;
    private final DocumentRepository documentRepository;

    private Account account;

    public TinkoffStonksImportActivity(
            final BeanFactory beanFactory,
            final AccountRepository accountRepository,
            final TickerSymbolRepository tickerSymbolRepository,
            final DocumentRepository documentRepository
    ) {
        this.beanFactory = beanFactory;
        this.accountRepository = accountRepository;
        this.tickerSymbolRepository = tickerSymbolRepository;
        this.documentRepository = documentRepository;

        command(this::doImport, "import");
        command(this::account, "account");
        command(this::files, "files", "file");
    }

    private void files(final Tokenizer tokenizer) {

        final String action = tokenizer.next();

        if (Utils.isSimilar(action, "list", "l")) {

            final File directory = downloadDirectory();
            final String[] files = directory.list();
            if (files == null || files.length == 0) {
                BotState.sendMessage("No files");
            } else {
                BotState.sendMessage(String.join("\n", files));
            }

        } else if (Utils.isSimilar(action, "remove", "rm")) {

            final String fileName = tokenizer.next();
            final File file = new File(downloadDirectory(), fileName);
            BotState.sendMessage(deleteFile(file));

        } else if (Utils.isSimilar(action, "removeAll", "rma")) {

            final File directory = downloadDirectory();
            final File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                BotState.sendMessage("No files for deletion");
            } else {
                final StringBuilder sb = new StringBuilder();
                for (File file : files) {
                    sb.append(deleteFile(file)).append("\n");
                }
                BotState.sendMessage(sb.toString());
            }

        } else {

            BotState.sendMessage("list, remove <name>, removeAll");

        }

    }

    private File downloadDirectory() {
        return new File(downloads, BotState.chatId());
    }

    private String deleteFile(final File file) {
        if (!file.exists()) {
            return String.format("File [%s] does not exists", file.getName());
        } else if (file.delete()) {
            return String.format("File [%s] deleted", file.getName());
        } else {
            return String.format("Unable to delete file [%s]", file.getName());
        }
    }

    private void account(final Tokenizer tokenizer) {
        final String accountId = tokenizer.next();
        try {
            final long id = Long.parseLong(accountId);
            accountRepository.findById(id)
                    .ifPresentOrElse(
                            account -> {
                                this.account = account;
                                BotState.sendMessage("Account found [%s]", this.account);
                            },
                            () -> BotState.sendMessage("Account [%s] not found", id)
                    );
        } catch (NumberFormatException e) {
            BotState.sendMessage("Unable to parse account id");
        }
    }

    private void doImport(final Tokenizer tokenizer) {
        doImportImpl();
    }

    private void doImportImpl() {
        if (account == null) {
            BotState.sendMessage("Specify target account by command \"account <id>\"");
            return;
        }

        final File directory = downloadDirectory();
        final File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            BotState.sendMessage("Upload xlsx broker report");
            return;
        }

        for (File file : files) {
            doImportFile(file);
            file.delete();
        }
        account = null;

    }

    private void doImportFile(final File file) {
        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(file);
        } catch (final IOException e) {
            BotState.sendMessage("Unable to read broker report [%s]", file.getName());
            throw new RuntimeException(e);
        }

        final Report report = read(workbook);

        final Stream<String> ordersResult = report.orders
                .stream()
                .filter(order -> order.externalId != null)
                .filter(order -> Objects.equals(order.currency, "RUB"))
                .map(this::importOrder);

        final Stream<String> founderContributionResult = report.operations
                .stream()
                .filter(operation -> Objects.equals(operation.name, "Пополнение счета"))
                .map(this::importFounderContribution);

        final Stream<String> brokerCommissionResult = report.operations
                .stream()
                .filter(operation -> Objects.equals(operation.name, "Комиссия по тарифу"))
                .map(this::importBrokerCommission);

        String result = Stream.concat(
                        ordersResult,
                        Stream.concat(founderContributionResult, brokerCommissionResult)
                )
                .collect(Collectors.joining("\n"));

        if (result.isEmpty()) {
            result = "No records in report";
        }

        BotState.sendMessage(file.getName() + "\n" + result);
    }

    private String importOrder(final Order order) {
        final Document existedDocument = documentRepository.findByExternalId(order.externalId);
        if (existedDocument != null) {
            return String.format("Document [%s] already existed", order.externalId);
        }

        final TickerSymbol tickerSymbol = tickerSymbolRepository.findByName(order.ticker);
        if (tickerSymbol == null) {
            return String.format("Document [%s] skipped due to unknown ticker [%s]", order.externalId, order.ticker);
        }

        final Document document = new Document();
        document.setDate(order.date);
        document.setExternalId(order.externalId);

        DocumentActivity documentActivity;
        if (order.type.equals(Type.BUY)) {
            document.setType(Document.Type.BUY_STOCK);
            documentActivity = beanFactory.getBean(BuyStockActivity.class);
        } else {
            document.setType(Document.Type.SELLS_STOCK);
            documentActivity = beanFactory.getBean(SellStockActivity.class);
        }

        documentActivity.setup(document);

        documentActivity.document().set(BuyStockActivity.ACCOUNT, account);
        documentActivity.document().set(BuyStockActivity.TICKER, tickerSymbol);
        documentActivity.document().set(BuyStockActivity.PRICE, order.price);
        documentActivity.document().set(BuyStockActivity.COUNT, order.count);
        documentActivity.document().set(BuyStockActivity.COMMISSION_AMOUNT, order.commission);

        return saveDocument(documentActivity, document);
    }

    private String importFounderContribution(final Operation operation) {
        final Document document = new Document();
        document.setDate(operation.date.atStartOfDay());
        document.setExternalId(operation.date.toString() + "_" + operation.dt.toString());
        document.setType(Document.Type.FOUNDER_CONTRIBUTION);

        final DocumentActivity documentActivity = beanFactory.getBean(FounderContributionActivity.class);
        documentActivity.setup(document);

        documentActivity.document().set(FounderContributionActivity.ACCOUNT, account);
        documentActivity.document().set(FounderContributionActivity.AMOUNT, operation.dt);

        return saveDocument(documentActivity, document);
    }

    private String importBrokerCommission(final Operation operation) {
        final Document document = new Document();
        document.setDate(operation.date.atStartOfDay());
        document.setExternalId(operation.date.toString() + "_" + operation.ct.toString());
        document.setType(Document.Type.BROKER_COMMISSION);

        final DocumentActivity documentActivity = beanFactory.getBean(BrokerCommission.class);
        documentActivity.setup(document);

        documentActivity.document().set(BrokerCommission.ACCOUNT, account);
        documentActivity.document().set(BrokerCommission.AMOUNT, operation.ct);

        return saveDocument(documentActivity, document);
    }

    private String saveDocument(final DocumentActivity documentActivity, final Document document) {
        try {
            documentActivity.apply();
            return String.format("Document [%s] imported", document.getExternalId());
        } catch (Exception e) {
            log.error("", e);
            return String.format("Document [%s] saving failed due to error - %s", document.getExternalId(), e.getMessage());
        }
    }

    // ##################################################
    // #                                                #
    // #  Model                                         #
    // #                                                #
    // ##################################################

    private Report read(Workbook workbook) {
        final Sheet sheet = workbook.getSheetAt(0);
        final ReportReader reportReader = new ReportReader();
        final Report report = new Report();

        for (final Row row : sheet) {
            final boolean skip = reportReader.setupContext(row);
            if (skip) {
                continue;
            }

            if (reportReader.getContext() == Context.ORDER) {
                report.orders.add(mapToOrder(reportReader));
            } else if (reportReader.getContext() == Context.OPERATION_RUB) {
                report.operations.add(mapToOperation(reportReader));
            }
        }

        return report;
    }

    private Order mapToOrder(final ReportReader row) {

        final Order order = new Order();

        order.externalId = row.asString("Номер сделки");
        final String date = row.asString("Дата заключения");
        final String time = row.asString("Время");
        if (date != null && time != null) {
            order.date = LocalDateTime.parse(date + " " + time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        }
        order.type = Objects.equals(row.asString("Вид сделки"), "Покупка") ? Type.BUY : Type.SELL;
        order.ticker = row.asString("Код актива");
        order.price = row.asMoney("Цена за единицу");
        order.count = row.asLong("Количество");
        order.commission = row.asMoney("Комиссия брокера");
        order.currency = row.asString("Валюта цены");

        if (Objects.equals(order.ticker, "FXUS") && order.date.isBefore(LocalDate.of(2021, 10, 7).atStartOfDay())) {
            order.price = order.price.setScale(6, RoundingMode.HALF_DOWN).divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
            order.count = order.count * 100;
        }
        if (Objects.equals(order.ticker, "FXTB") && order.date.isBefore(LocalDate.of(2022, 2, 3).atStartOfDay())) {
            order.price = order.price.setScale(6, RoundingMode.HALF_DOWN).divide(new BigDecimal(10), RoundingMode.HALF_DOWN);
            order.count = order.count * 10;
        }
        if (Objects.equals(order.ticker, "FXDE") && order.date.isBefore(LocalDate.of(2021, 9, 9).atStartOfDay())) {
            order.price = order.price.setScale(6, RoundingMode.HALF_DOWN).divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
            order.count = order.count * 100;
        }

        return order;

    }

    private Operation mapToOperation(final ReportReader reportReader) {

        final Operation operation = new Operation();

        operation.name = reportReader.asString("Операция");
        final String date = reportReader.asString("Дата исполнения");
        if (date != null) {
            operation.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        operation.dt = reportReader.asMoney("Сумма зачисления");
        operation.ct = reportReader.asMoney("Сумма списания");

        return operation;

    }

    private static class Report {
        private final List<Order> orders = new ArrayList<>();
        private final List<Operation> operations = new ArrayList<>();
    }

    private static class Order {
        public String externalId;
        public LocalDateTime date;
        public Type type;
        public String ticker;
        public BigDecimal price;
        public Long count;
        public BigDecimal commission;
        public String currency;
    }

    private static class Operation {
        public LocalDate date;
        public String name;
        public BigDecimal dt;
        public BigDecimal ct;
    }

    public enum Type {
        BUY,
        SELL
    }

    // ##################################################
    // #                                                #
    // #  Util                                          #
    // #                                                #
    // ##################################################

    private static class ReportReader {
        private Row row;
        private Context context;
        private Map<String,Integer> index = new HashMap<>();

        public Context getContext() {
            return context;
        }

        public boolean setupContext(final Row row) {
            this.row = row;

            final String rowValue = rowAsString(row);

            if (rowValue.contains("1.1 Информация")) {
                context = Context.ORDER;
                return true;
            }

            if (context == Context.ORDER && rowValue.contains("Номер сделки,Номер поручения")) {
                index = headerIndex();
                return true;
            }

            if (rowValue.contains("2. Операции")) {
                context = Context.OPERATION;
                return true;
            }

            if (context == Context.OPERATION && rowValue.contains("Дата,Время совершения")) {
                context = Context.OPERATION_RUB;
                index = headerIndex();
                return true;
            }

            final boolean toNone = rowValue.contains("1.2 Информация")
                    || rowValue.contains("3.1 Движение")
                    || context == Context.OPERATION_RUB && rowValue.contains("Дата,Время совершения");
            if (toNone) {
                context = Context.NONE;
                return true;
            }

            return false;
        }

        private String rowAsString(final Row row) {
            return StreamSupport.stream(row.spliterator(), false)
                    .map(this::cellAsString)
                    .filter(Objects::nonNull)
                    .map(value -> value.replaceAll("\n", ""))
                    .collect(Collectors.joining(","));
        }

        private String cellAsString(final Cell cell) {
            return !Objects.equals(cell.getCellType(), CellType.STRING)
                    ? null
                    : cell.getStringCellValue();
        }

        public String asString(final String name) {
            final Integer i = index.get(name);
            if (i == null) {
                return null;
            }

            final Cell cell = row.getCell(i);
            if (!Objects.equals(cell.getCellType(), CellType.STRING)) {
                return null;
            }

            return cell.getStringCellValue();
        }

        public BigDecimal asMoney(final String name) {
            final String value = asString(name);
            return value != null
                    ? new BigDecimal(value.replace(",", "."))
                    : null;
        }

        public Long asLong(final String name) {
            final String value = asString(name);
            return value != null
                    ? Long.parseLong(value)
                    : null;
        }

        private Map<String,Integer> headerIndex() {
            for (final Cell cell : row) {
                final String value = cellAsString(cell);
                if (value == null) {
                    continue;
                }

                index.put(value.replaceAll("\n", ""), cell.getColumnIndex());
            }
            return index;
        }
    }

    private enum Context {
        NONE, ORDER, OPERATION, OPERATION_RUB
    }
}
