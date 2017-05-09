package io.teiler.server.endpoints;

import static spark.Spark.get;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;

import io.teiler.server.dto.Debt;
import io.teiler.server.endpoints.util.EndpointUtil;
import io.teiler.server.services.DebtService;
import io.teiler.server.util.GsonUtil;

/**
 * Controller for Compensation-related endpoints.
 *
 * @author dthoma
 */
@Controller
public class DebtEndpointController implements EndpointController {

    private static final String BASE_URL = GlobalEndpointController.URL_VERSION + "/groups/" + EndpointUtil.GROUP_ID_PARAM + "/debts";
    
    private Gson gson = GsonUtil.getHomebrewGson();

    @Autowired
    private DebtService debtService;

    @Override
    public void register() {
        get(BASE_URL, (req, res) -> {
            String groupId = EndpointUtil.readGroupId(req);
            List<Debt> debts = debtService.getDebts(groupId);
            return gson.toJson(debts);
        });
    }

}
