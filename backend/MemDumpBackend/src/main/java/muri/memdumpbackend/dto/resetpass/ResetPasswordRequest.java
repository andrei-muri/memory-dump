package muri.memdumpbackend.dto.resetpass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResetPasswordRequest {
    private String token;
    private String email;
    private String newPassword;
    private String confirmNewPassword;
}
