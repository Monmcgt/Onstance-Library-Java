package me.monmcgt.code.onstance.library.java.object;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OnstanceResponse {
    private boolean isAlive;
    private String message;
}
