package io.teiler.server.services;

import io.teiler.server.dto.Debt;
import io.teiler.server.persistence.entities.DebtEntity;
import io.teiler.server.persistence.repositories.DebtRepository;
import io.teiler.server.persistence.repositories.PersonRepository;
import io.teiler.server.services.util.GroupUtil;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides service-methods for debts.
 *
 * @author dthoma
 */
@Service
public class DebtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebtService.class);

    @Autowired
    private GroupUtil groupUtil;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DebtRepository debtRepository;

    public List<Debt> getDebts(String groupID) {
        groupUtil.checkIdExists(groupID);

        List<Debt> debts = new LinkedList<>();
        for (DebtEntity debtEntity : debtRepository.get(groupID)) {
            // TODO: Find a better solution than converting it here
            Debt debt = new Debt(personRepository.getById(debtEntity.getPersonId()).toPerson(),
                debtEntity.getBalance());
            debts.add(debt);
        }
        LOGGER.debug("View debts: {}", debts);
        return debts;
    }
}
