package com.evgenltd.ledgerserver;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/meta")
public class MetaController {

    @GetMapping
    public List<MetaEntity> meta() {
        return Arrays.asList(
                new MetaEntity("", "", "", Arrays.asList(
                        new MetaEntityField("", null, false, "", ""),
                        new MetaEntityField(),
                ))
        );
    }

}

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class MetaEntity {
    private String name;
    private String fullName;
    private String localization;
    private List<MetaEntityField> fields;
}

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class MetaEntityField {
    private String name;
    private Object type;
    private boolean sort;
    private String format;
    private String localization;
}
