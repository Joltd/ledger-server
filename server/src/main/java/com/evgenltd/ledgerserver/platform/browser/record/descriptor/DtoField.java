package com.evgenltd.ledgerserver.platform.browser.record.descriptor;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class DtoField {
    private String reference;
    private boolean sort;
    private FieldType type;
    private String format;
    private String localizationKey;
}
