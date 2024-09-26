package org.example.viewmodel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserViewModel {
    private String email;
    private String name;
}
