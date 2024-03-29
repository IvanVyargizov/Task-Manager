package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
@Table(name = "labels")
@NoArgsConstructor
@AllArgsConstructor
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 1, max = 100)
    @Column(unique = true)
    private String name;

    @CreationTimestamp
    private Date createdAt;

    public Label(final Long id) {
        this.id = id;
    }

}
