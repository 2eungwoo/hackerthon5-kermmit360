package hackathon.kermmit360.graph.commitstat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CommitStatDto {
    private LocalDate date;
    private int count;
}
