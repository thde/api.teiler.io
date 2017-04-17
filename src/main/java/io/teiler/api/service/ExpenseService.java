package io.teiler.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.teiler.server.dto.Expense;
import io.teiler.server.dto.Share;
import io.teiler.server.persistence.entities.ExpenseEntity;
import io.teiler.server.persistence.repositories.ExpenseRepository;
import io.teiler.server.persistence.repositories.ProfiteerRepository;

/**
 * Provides service-methods for Expenses.
 * 
 * @author pbaechli
 */
@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ProfiteerRepository profiteerRepository;
    
    @Autowired
    private GroupUtil groupUtil;
    
    @Autowired
    private ExpenseUtil expenseUtil;
    
    /**
     * Creates a new Expense.
     * @param expense {@link List} of {@link Share} related to the Expense
     * 
     * @return Information about the Expense
     */
    public Expense createExpense(Expense expense) {
        ExpenseEntity expenseEntity = expenseRepository.create(expense);
        
        for(Share share : expense.getShares()) {
            share.setExpenseId(expenseEntity.getId());
            profiteerRepository.create(share);
        }
        
        return expenseRepository.getById(expenseEntity.getId()).toExpense();
    }

    /**
     * Returns an {@link Expense} with the given Id and Group-Id.
     * 
     * @param groupId Id of the Group
     * @param expenseId Id of the Expense
     * @return {@link List} of {@link Expense}
     */
    public Expense getExpense(String groupId, int expenseId) {
        groupUtil.checkIdExists(groupId);
        expenseUtil.checkExpenseExists(expenseId);
        
        ExpenseEntity expense = expenseRepository.getByGroupIdAndExpenseId(groupId, expenseId);
        return expense.toExpense();
    }

    /**
     * Returns a {@link List} of {@link Expense} in the Group with the given Id.
     * <i>Note:</i> The Group of the Payer of the Expense has to to match the given Group.
     * 
     * @param groupId Id of the Group
     * @param limit Maximum amount of Expenses to fetch
     * @return {@link List} of {@link Expense}
     */
    public List<Expense> getExpenses(String groupId, long limit) {
        groupUtil.checkIdExists(groupId);
        
        List<ExpenseEntity> expenses = expenseRepository.getExpensesByGroupId(groupId, limit);
        return expenses.stream().map(e -> e.toExpense()).collect(Collectors.toList());
    }

    /**
     * Updates and already created Expense with the given values.
     * <i>Note:</i> The Expense has to exist within the given Group.
     * 
     * @param groupId Id of the Group
     * @param expenseId Id of the Expense
     * @param changedExpense {@link Expense} containing the new values
     */
    public Expense editExpense(String groupId, int expenseId, Expense changedExpense) {
        groupUtil.checkIdExists(groupId);
        expenseUtil.checkExpenseExists(expenseId);
        expenseUtil.checkExpenseBelongsToThisGroup(groupId, expenseId);
        
        ExpenseEntity expenseEntity = expenseRepository.editExpense(expenseId, changedExpense);

        // TODO Add support for editing profiteers/shares as well
//        for(Share changedShare : changedExpense.getShares()) {
//            changedShare.setExpenseId(expenseEntity.getId());
//            profiteerRepository.editProfiteer(expenseEntity.getId(), changedShare);
//        }
        
        return expenseRepository.getById(expenseEntity.getId()).toExpense();
    }

    /**
     * Deletes the Expense with the given Id and Group-Id.<br>
     * <i>Note:</i> The Expense has to exist within the given Group.
     * 
     * @param groupId Id of the Group
     * @param expenseId Id of the Expense
     */
    public void deleteExpense(String groupId, int expenseId) {
        groupUtil.checkIdExists(groupId);
        expenseUtil.checkExpenseExists(expenseId);
        expenseUtil.checkExpenseBelongsToThisGroup(groupId, expenseId);

        expenseRepository.deleteExpense(expenseId);
    }
    
}
