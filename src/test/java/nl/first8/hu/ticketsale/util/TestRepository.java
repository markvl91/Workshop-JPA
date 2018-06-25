package nl.first8.hu.ticketsale.util;

import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.registration.AccountInfo;
import nl.first8.hu.ticketsale.sales.Ticket;
import nl.first8.hu.ticketsale.sales.TicketId;
import nl.first8.hu.ticketsale.venue.Artist;
import nl.first8.hu.ticketsale.venue.Concert;
import nl.first8.hu.ticketsale.venue.Genre;
import nl.first8.hu.ticketsale.venue.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
public class TestRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Account createDefaultAccount(String emailAdress) {
        AccountInfo info = new AccountInfo("TestStraat", "0612345678", "Utrecht");
        Account account = new Account(emailAdress);
        account.setInfo(info);
        entityManager.persist(info);
        entityManager.persist(account);
        return account;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Account createAccount(String emailAdress, String city) {
        AccountInfo info = new AccountInfo("TestStraat", "0612345678", city);
        Account account = new Account(emailAdress);
        account.setInfo(info);
        entityManager.persist(info);
        entityManager.persist(account);
        return account;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Ticket createDefaultTicket(Account acc, String artistName, String location) {
        Account account = entityManager.find(Account.class, acc.getId());
        Concert concert = createDefaultConcert(artistName, location);
        Ticket ticket = new Ticket(concert, account);
        entityManager.persist(ticket);
        return ticket;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTicket(Concert c, Account a) {
        Concert concert = entityManager.find(Concert.class, c.getId());
        Account account  = entityManager.find(Account.class, a.getId());
        Ticket ticket = new Ticket(concert, account);
        entityManager.persist(ticket);
    }

    public Ticket findTicket(Concert concert, Account account) {
        TicketId key = new TicketId(concert, account);
        return entityManager.find(Ticket.class, key);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Concert createDefaultConcert(String artistName, String locationName) {
        Location location = createLocation(locationName);
        Artist artist = createDefaultArtist(artistName);
        Concert concert = new Concert();
        concert.setArtist(artist);
        concert.setLocation(location);
        entityManager.persist(concert);
        return concert;

    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Concert createConcert(Artist artist, String locationName) {
        Location location = createLocation(locationName);
        Concert concert = new Concert();
        concert.setArtist(artist);
        concert.setLocation(location);
        entityManager.persist(concert);
        return concert;

    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Location createLocation(String locationName) {
        Location location = new Location();
        location.setName(locationName);
        entityManager.persist(location);
        return location;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Artist createDefaultArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setGenre(Genre.GRINDCORE);
        entityManager.persist(artist);
        return artist;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Artist createArtist(String name, Genre genre) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setGenre(genre);
        entityManager.persist(artist);
        return artist;
    }



}
