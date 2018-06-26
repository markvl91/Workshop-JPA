package nl.first8.hu.ticketsale.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public List<Concert> getConcertByEverything(String artistName, Genre genre, String locationName) {
        return venueRepository.findConcertByEverything(artistName, genre, locationName);
    }

}
