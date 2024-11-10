package com.foodics.task.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
    private String from;
    private String[] to;
    private String subject;
    private String text;

    public void setTo(String to) {
        this.to = new String[]{to};
    }

    public void setTo(String... to) {
        this.to = to;
    }
}
