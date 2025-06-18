package restDTO;

import lombok.Data;

@Data
public class UpdateUserDataRequest {
    private String location;
    private String biography;
}