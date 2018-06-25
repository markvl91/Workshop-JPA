package nl.first8.hu.ticketsale.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
@Transactional
public class SalesResource {

    private final SalesService service;

    @Autowired
    public SalesResource(SalesService service) {
        this.service = service;
    }

    @PostMapping(path = "/ticket")
    public ResponseEntity postTicket(@RequestParam("account_id") final Long accountId, @RequestParam("concert_id") final Long concertId) {
        try {
            service.insertTicket(accountId, concertId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping(path = "/")
    public ResponseEntity postSale(@RequestParam("account_id") final Long accountId, @RequestParam("concert_id") final Long concertId, @RequestParam("price") Integer price) {
        try {
            service.insertSale(accountId, concertId, price);

            Optional<Sale> sale = service.getSale(accountId, concertId);

            return sale.map(s -> ResponseEntity.ok(s.getId()))
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(path = "/ticket")
    public ResponseEntity<List<TicketDto>> getById(@RequestParam("account_id") final Long accountId) {
        try {
            List<Ticket> tickets = service.getById(accountId);
            List<TicketDto> responseTickets = tickets.stream()
                    .map(t -> new TicketDto(t.getConcert().getArtist().getName(), t.getConcert().getArtist().getGenre().toString(), t.getConcert().getLocation().getName()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseTickets);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
