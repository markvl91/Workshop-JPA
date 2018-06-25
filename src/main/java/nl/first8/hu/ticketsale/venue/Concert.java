package nl.first8.hu.ticketsale.venue;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concert implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

}
