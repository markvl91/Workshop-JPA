package nl.first8.hu.ticketsale.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VenueRepository {

    private final EntityManager entityManager;

    @Autowired
    public VenueRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Concert> findConcertById(Long concertId) {
        return Optional.ofNullable(entityManager.find(Concert.class, concertId));
    }

    public List<Concert> findConcertByEverything(String artistName, Genre genre, String locationName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Concert> query = criteriaBuilder.createQuery(Concert.class);
        Root<Concert> concert = query.from(Concert.class);

        Path<String> artistNamePath = concert.get("artist").get("name");
        Path<String> artistGenrePath = concert.get("artist").get("genre");
        Path<String> locationNamePath = concert.get("location").get("name");

        Predicate predicates = null;

        if (artistName != null) {
            predicates = criteriaBuilder.like(artistNamePath, artistName);
        }
        if (genre != null) {
            Predicate genrePredicate = artistGenrePath.in(genre);

            if(predicates != null) {
                predicates = criteriaBuilder.and(genrePredicate, predicates);
            } else {
                predicates = genrePredicate;
            }
        }
        if (locationName != null) {
            Predicate locationPredicate = criteriaBuilder.like(locationNamePath, locationName);

            if(predicates != null) {
                predicates = criteriaBuilder.and(locationPredicate, predicates);
            } else {
                predicates = locationPredicate;
            }
        }

        if(predicates != null) {
            query.where(predicates);
        }

        TypedQuery<Concert> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}
