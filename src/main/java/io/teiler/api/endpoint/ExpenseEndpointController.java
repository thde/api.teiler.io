package io.teiler.api.endpoint;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;

import io.teiler.api.service.ExpenseService;
import io.teiler.server.dto.Expense;
import io.teiler.server.dto.Person;
import io.teiler.server.util.Error;
import io.teiler.server.util.GsonUtil;
import io.teiler.server.util.Normalize;
import io.teiler.server.util.exceptions.PeopleNameConflictException;
import io.teiler.server.util.exceptions.PersonNotFoundException;

/**
 * Controller for Expense-related endpoints.
 * 
 * @author pbaechli
 */
@Controller
public class ExpenseEndpointController implements EndpointController {

    private Gson gson = GsonUtil.getHomebrewGson();

    @Autowired
    private ExpenseService expenseService;

    private static final int DEFAULT_QUERY_LIMIT = 20;
    private static final String EXPENSE_ID_PARAM = ":expenseid";
    private static final String LIMIT_PARAM = "limit";

    private static final String BASE_URL = GlobalEndpointController.URL_VERSION + "/groups/:groupid/expenses";
    private static final String URL_WITH_EXPENSE_ID = BASE_URL + "/" + EXPENSE_ID_PARAM;

    @Override
    public void register() {
        post(BASE_URL, (req, res) -> {
            String groupId = req.params(GroupEndpointController.GROUP_ID_PARAM);
            groupId = Normalize.normalizeGroupId(groupId);
            Expense requestExpense = gson.fromJson(req.body(), Expense.class);
            Expense newExpense = expenseService.createExpense(requestExpense);
            return gson.toJson(newExpense);
        });

        get(BASE_URL, (req, res) -> {
            String groupId = req.params(GroupEndpointController.GROUP_ID_PARAM);
            groupId = Normalize.normalizeGroupId(groupId);
            String limitString = req.queryParams(LIMIT_PARAM);
            long limit = DEFAULT_QUERY_LIMIT;
            if (limitString != null) {
                limit = Long.parseLong(limitString);
            }
            List<Expense> expenses = expenseService.getExpenses(groupId, limit);
            return gson.toJson(expenses);
        });
        
        get(URL_WITH_EXPENSE_ID, (req, res) -> {
            String groupId = req.params(GroupEndpointController.GROUP_ID_PARAM);
            groupId = Normalize.normalizeGroupId(groupId);
            int expenseId = Integer.parseInt(req.params(EXPENSE_ID_PARAM));
            Expense expense = expenseService.getExpense(groupId, expenseId);
            return gson.toJson(expense);
        });

        put(URL_WITH_EXPENSE_ID, (req, res) -> {
            String groupId = req.params(GroupEndpointController.GROUP_ID_PARAM);
            groupId = Normalize.normalizeGroupId(groupId);
            int expenseId = Integer.parseInt(req.params(EXPENSE_ID_PARAM));
            Expense changedExpense = gson.fromJson(req.body(), Expense.class);
            Expense expense = expenseService.editExpense(groupId, expenseId, changedExpense);
            return gson.toJson(expense);
        });

        delete(URL_WITH_EXPENSE_ID, (req, res) -> {
            String groupId = req.params(GroupEndpointController.GROUP_ID_PARAM);
            groupId = Normalize.normalizeGroupId(groupId);
            int expenseId = Integer.parseInt(req.params(EXPENSE_ID_PARAM));
            expenseService.deleteExpense(groupId, expenseId);
            return "";
        });

//        exception(PersonNotFoundException.class, (e, request, response) -> {
//            response.status(404);
//            Error error = new Error(e.getMessage());
//            response.body(gson.toJson(error));
//        });
//
//        exception(PeopleNameConflictException.class, (e, request, response) -> {
//            response.status(409);
//            Error error = new Error(e.getMessage());
//            response.body(gson.toJson(error));
//        });
    }

}
