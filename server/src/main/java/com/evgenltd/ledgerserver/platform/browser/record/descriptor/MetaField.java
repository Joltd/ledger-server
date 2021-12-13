package com.evgenltd.ledgerserver.platform.browser.record.descriptor;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MetaField {
    private String reference;
    private FieldType type;
    private List<MetaField> fields;
    private String api;
    private String localizationKey;
}
