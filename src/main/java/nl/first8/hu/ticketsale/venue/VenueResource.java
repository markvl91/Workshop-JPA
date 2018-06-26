package nl.first8.hu.ticketsale.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/venue")
@Transactional
public class VenueResource {

    private final VenueService service;

    @Autowired
    public VenueResource(VenueService service) {
        this.service = service;
    }

    @GetMapping(path = "/concert")
    public ResponseEntity<List<ConcertDto>> getConcertByEverything(@RequestParam(value = "artist", required = false) final String artistName, @RequestParam(value = "genre", required = false) final Genre genre, @RequestParam(value = "location", required = false) final String locationName) {
        try {
            List<Concert> concerts = service.getConcertByEverything(artistName, genre, locationName);
            List<ConcertDto> responseConcerts = concerts.stream()
                    .map(concert -> new ConcertDto(concert.getArtist().getName(), concert.getArtist().getGenre().toString(), concert.getLocation().getName()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseConcerts);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
