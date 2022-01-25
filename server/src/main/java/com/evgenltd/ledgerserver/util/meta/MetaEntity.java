package com.evgenltd.ledgerserver.util.meta;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaEntity {
    private String name;
    private String endpoint;
    private String localization;
    private List<MetaEntityField> fields;
}