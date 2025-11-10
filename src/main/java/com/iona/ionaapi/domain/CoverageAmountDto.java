package com.iona.ionaapi.domain;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CoverageAmountDto {
    private String guaranteeType;
    private String amount;
}
