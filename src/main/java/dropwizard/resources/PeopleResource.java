package dropwizard.resources;


import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import dropwizard.core.Person;
import dropwizard.db.PersonDAO;
import io.dropwizard.hibernate.UnitOfWork;


@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {

    private final PersonDAO peopleDAO;

    public PeopleResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @POST
    @UnitOfWork
    public Person createPerson(@Valid Person person) {
        return peopleDAO.create(person);
    }

    @GET
    @UnitOfWork
    public List<Person> listPeople() {
        return peopleDAO.findAll();
    }

}
