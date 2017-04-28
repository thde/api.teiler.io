package io.teiler.api.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.teiler.server.dto.Person;
import io.teiler.server.persistence.entities.PersonEntity;
import io.teiler.server.persistence.repositories.PersonRepository;

/**
 * Provides service-methods for Groups.
 *
 * @author lroellin
 * @author pbaechli
 */
@Service
public class PersonService {

    /* Spring Components (Services/Controller) */
    @Autowired
    private GroupUtil groupUtil;
    
    @Autowired
    private PersonUtil personUtil;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Creates a new Person.
     *
     * @param groupId Group ID this person belongs to
     * @param name Name of the new Person
     * @return Information about the Person
     */
    public Person createPerson(String groupId, String name) {
        groupUtil.checkIdExists(groupId);
        personUtil.checkNamesAreUnique(groupId, name);

        Person newPerson = new Person(null, name);
        PersonEntity personEntity = personRepository.create(groupId, newPerson);

        return personEntity.toPerson();
    }

    public List<Person> getPeople(String groupId, long limit) {
        groupUtil.checkIdExists(groupId);

        List<Person> people = new LinkedList<>();
        for (PersonEntity personEntity : personRepository.getPeople(groupId, limit)) {
            people.add(personEntity.toPerson());
        }
        return people;
    }

    public Person getPerson(String groupId, int personId) {
        groupUtil.checkIdExists(groupId);
        personUtil.checkPersonBelongsToThisGroup(groupId, personId);
        return personRepository.getById(personId).toPerson();
    }

    public Person editPerson(String groupId, int personId, Person changedPerson) {
        groupUtil.checkIdExists(groupId);
        personUtil.checkPersonExists(personId);
        personUtil.checkPersonBelongsToThisGroup(groupId, personId);
        personUtil.checkNamesAreUnique(groupId, changedPerson.getName());
        
        /* TODO
         *   - If personId = changedPerson.id -> return person with personId
         *   - If active-flag changed -> throw exception (active flag has to
         *      be set through the corresponding service) 
         */

        return personRepository.editPerson(personId, changedPerson).toPerson();
    }

    public void deactivatePerson(String groupId, int personId) {
        groupUtil.checkIdExists(groupId);
        personUtil.checkPersonExists(personId);
        personUtil.checkPersonBelongsToThisGroup(groupId, personId);
        
        PersonEntity person = personRepository.getById(personId);
        person.setActive(false);
        
        personRepository.editPerson(personId, person.toPerson());
    }
    
}
