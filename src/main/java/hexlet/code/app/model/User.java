package hexlet.code.app.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "users")
public final class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @NotBlank
    private String firstName;

//    @NotBlank
    private String lastName;

    @Email
//    @NotBlank
    private String email;

//    @NotBlank
    private String password;

    @CreationTimestamp
    private Date createdAt;

}
