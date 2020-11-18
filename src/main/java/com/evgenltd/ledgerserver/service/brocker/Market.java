package com.evgenltd.ledgerserver.service.brocker;

import java.math.BigDecimal;

public interface Market {

    BigDecimal price(String figi);

}
