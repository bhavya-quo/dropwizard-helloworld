package dropwizard.resources;


import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dropwizard.core.Person;
import dropwizard.db.PersonDAO;
import dropwizard.views.PersonView;
import io.dropwizard.hibernate.UnitOfWork;


@Path("/people/{personId}")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    private final PersonDAO peopleDAO;

    public PersonResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @GET
    @UnitOfWork
    public Person getPerson(@PathParam("personId") Long personId) {
        return findSafely(personId);
    }

    @GET
    @Path("/view_freemarker")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewFreemarker(@PathParam("personId") Long personId) {
        return new PersonView(PersonView.Template.FREEMARKER, findSafely(personId));
    }

    @GET
    @Path("/view_mustache")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewMustache(@PathParam("personId") Long personId) {
        return new PersonView(PersonView.Template.MUSTACHE, findSafely(personId));
    }

    @PUT
    @UnitOfWork
    public Person updatePerson(@PathParam("personId") Long personId,@Valid Person person) {
        return update(personId,person);
    }

    private Person findSafely(long personId) {
        return peopleDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
    }

    private Person update(Long personId, Person person) {
        Person existingPerson = findSafely(personId);
        existingPerson.setFullName(person.getFullName());
        existingPerson.setJobTitle(person.getJobTitle());
        existingPerson.setYearBorn(person.getYearBorn());
        return peopleDAO.update(existingPerson).orElseThrow(() -> new NotFoundException("No such person"));
    }
}
