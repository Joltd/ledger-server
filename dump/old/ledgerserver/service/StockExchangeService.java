package old.ledgerserver.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StockExchangeService {

    BigDecimal rate(String ticker);

    BigDecimal rate(LocalDate date, String ticker);

}
