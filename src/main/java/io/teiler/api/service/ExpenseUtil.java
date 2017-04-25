package io.teiler.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.teiler.server.dto.Profiteer;
import io.teiler.server.persistence.repositories.ExpenseRepository;
import io.teiler.server.persistence.repositories.ProfiteerRepository;
import io.teiler.server.util.exceptions.ProfiteerNotFoundException;
import io.teiler.server.util.exceptions.SharesNotAddingUpException;
import io.teiler.server.util.exceptions.TransactionNotFoundException;

@Service
public class ExpenseUtil {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ProfiteerRepository profiteerRepository;

    public ExpenseUtil() { /* intentionally empty */ }

    /**
     * Checks whether an Expense exists within a Group.
     * 
     * @param groupId Id of the Group
     * @param expenseId Id of the Expense
     * @throws TransactionNotFoundException Expense does not exists within Group
     */
    public void checkExpenseBelongsToThisGroup(String groupId, int expenseId) throws TransactionNotFoundException {
        if (expenseRepository.getByGroupIdAndExpenseId(groupId, expenseId) == null) {
            throw new TransactionNotFoundException();
        }
    }

    /**
     * Checks whether an Expense exists.
     *
     * @param expenseId Id of the Expense
     * @throws TransactionNotFoundException Expense does not exist
     */
    public void checkExpenseExists(int expenseId) throws TransactionNotFoundException {
        if (expenseRepository.getById(expenseId) == null) {
            throw new TransactionNotFoundException();
        }
    }
    
    /**
     * Checks whether a Profiteer-Person exists within an Expense.
     * 
     * @param expenseId Id of the Expense
     * @param profiteerPersonId Id of the Profiteer-Person
     * @throws ProfiteerNotFoundException Profiteer does not exist
     */
    public void checkProfiteerExistsInThisExpense(int expenseId, int profiteerPersonId) throws ProfiteerNotFoundException {
        if (profiteerRepository.getByTransactionIdAndProfiteerPersonId(expenseId, profiteerPersonId) == null) {
            throw new ProfiteerNotFoundException();
        }
    }
    
    /**
     * Checks whether the given shares add up to the expected amount.
     * 
     * @param expectedTotalAmount Expected amount of the summed up shares
     * @param shares {@link List} of {@link Profiteer}
     * @throws SharesNotAddingUpException Shares do not add up
     */
    public void checkSharesAddUp(Integer expectedTotalAmount, List<Profiteer> shares) throws SharesNotAddingUpException {
        Integer total = shares.stream().map(Profiteer::getShare).mapToInt(Integer::intValue).sum();
        if (total.compareTo(expectedTotalAmount) != 0) {
            throw new SharesNotAddingUpException();
        }
    }
    
}
