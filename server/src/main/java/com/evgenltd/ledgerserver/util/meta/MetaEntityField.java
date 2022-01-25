package com.evgenltd.ledgerserver.util.meta;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaEntityField {
    private String name;
    private FieldType type;
    private boolean sort;
    private String format;
    private String localization;
}