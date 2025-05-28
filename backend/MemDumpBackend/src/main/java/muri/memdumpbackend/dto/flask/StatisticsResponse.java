package muri.memdumpbackend.dto.flask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatisticsResponse {
    private List<List<String>> statistics;
}
