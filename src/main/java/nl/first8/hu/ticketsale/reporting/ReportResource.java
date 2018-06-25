package nl.first8.hu.ticketsale.reporting;

import nl.first8.hu.ticketsale.venue.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/report")
@Transactional
public class ReportResource {

    private final ReportRepository repository;

    @Autowired
    public ReportResource(ReportRepository repository) {
        this.repository = repository;
    }


    @GetMapping(path = "/location")
    public ResponseEntity<List<LocationReport>> getById(@RequestParam("genre") final Genre genre) {
        try {
            List<LocationReport> reports = repository.findGenreLocations(genre);
            return ResponseEntity.ok(reports);
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }


}
