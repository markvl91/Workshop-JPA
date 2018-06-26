package nl.first8.hu.ticketsale.sales;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.util.TestRepository;
import nl.first8.hu.ticketsale.venue.Concert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import static org.mockito.Matchers.isNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class TicketRepositoryIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void cleanDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "audit_trail", "sale", "ticket", "account", "concert", "artist");
    }

    @Test
    public void testInsertTicket() throws Exception {

        Account account = testRepository.createDefaultAccount("f.dejong@first8.nl");
        Concert concert = testRepository.createDefaultConcert("Parov Stellar", "Utrecht");


        mvc.perform(
                post("/sales/ticket").param("account_id", account.getId().toString()).param("concert_id", concert.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());


        final Ticket ticket = testRepository.findTicket(concert, account);
        assertThat(ticket.getConcert().getArtist().getName(), is(concert.getArtist().getName()));
        assertThat(ticket.getConcert().getLocation().getName(), is(concert.getLocation().getName()));
    }

    @Test
    public void testGetTickets() throws Exception {

        Account account = testRepository.createDefaultAccount("f.dejong@first8.nl");
        Ticket ticketGorillaz = testRepository.createDefaultTicket(account, "Gorillaz", "Utrecht");
        Ticket ticketThieveryCo = testRepository.createDefaultTicket(account, "Thievery Cooperation", "Apeldoorn");


        MvcResult result = mvc.perform(
                get("/sales/ticket").param("account_id", account.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        List<TicketDto> actualTickets = readTicketsResponse(result);
        assertEquals(2, actualTickets.size());
        assertEquals(ticketGorillaz.getConcert().getArtist().getName(), actualTickets.get(0).getArtist());
        assertEquals(ticketGorillaz.getConcert().getLocation().getName(), actualTickets.get(0).getLocation());
        assertEquals(ticketThieveryCo.getConcert().getArtist().getName(), actualTickets.get(1).getArtist());
        assertEquals(ticketGorillaz.getConcert().getLocation().getName(), actualTickets.get(0).getLocation());


    }

    @Test
    public void testInsertSale() throws Exception {

        Account account = testRepository.createDefaultAccount("t.poll@first8.nl");
        Concert concert = testRepository.createDefaultConcert("Disturbed", "Verdedig, Enschede");

        MvcResult result = mvc.perform(
                post("/sales/")
                        .param("account_id", account.getId().toString())
                        .param("concert_id", concert.getId().toString())
                        .param("price", Integer.toString(4500))
        ).andExpect(status().isOk()).andReturn();

        Long saleId = readSaleResult(result);


        Ticket createdTicket = entityManager.find(Ticket.class, new TicketId(concert, account));
        Sale createdSale = entityManager.find(Sale.class, saleId);

        assertThat(createdSale.getTicket().getAccount().getId(), is(account.getId()));
        assertThat(createdSale.getTicket().getConcert().getId(), is(createdTicket.getConcert().getId()));
    }

    @Test
    public void testInsertSaleWithoutPayment() throws Exception {

        Account account = testRepository.createDefaultAccount("t.poll@first8.nl");
        Concert concert = testRepository.createDefaultConcert("Disturbed", "Verdedig, Enschede");

        mvc.perform(
                post("/sales/")
                        .param("account_id", account.getId().toString())
                        .param("concert_id", concert.getId().toString())
                        .param("price", Integer.toString(0))
        ).andExpect(status().isConflict());


        Ticket createdTicket = entityManager.find(Ticket.class, new TicketId(concert, account));
        assertThat(createdTicket, is(isNull()));

    }

    private Long readSaleResult(MvcResult result) throws IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), Long.class);
    }

    private List<TicketDto> readTicketsResponse(MvcResult result) throws IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TicketDto>>() {
        });
    }


}
