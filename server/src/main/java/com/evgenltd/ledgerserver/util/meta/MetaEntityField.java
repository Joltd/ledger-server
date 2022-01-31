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
    private String typeName;
    private boolean sort;
    private String format;
}