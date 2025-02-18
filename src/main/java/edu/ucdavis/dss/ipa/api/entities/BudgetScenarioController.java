package edu.ucdavis.dss.ipa.api.entities;

import edu.ucdavis.dss.ipa.entities.BudgetScenario;
import edu.ucdavis.dss.ipa.entities.Workgroup;
import edu.ucdavis.dss.ipa.security.Authorizer;
import edu.ucdavis.dss.ipa.services.BudgetScenarioService;
import edu.ucdavis.dss.ipa.services.WorkgroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class BudgetScenarioController {
  @Inject BudgetScenarioService budgetScenarioService;
  @Inject WorkgroupService workgroupService;

  @Inject Authorizer authorizer;

  @RequestMapping(value = "/api/workgroups/{workgroupId}/years/{year}/budgetScenarios", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  public List<BudgetScenario> getBudgetScenarios(@PathVariable long workgroupId,
                                                 @PathVariable long year,
                                                 HttpServletResponse httpResponse) {
    Workgroup workgroup = workgroupService.findOneById(workgroupId);

    if (workgroup == null) {
      httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
      return null;
    }

    authorizer.hasWorkgroupRoles(workgroup.getId(), "academicPlanner", "reviewer");
    List<BudgetScenario> budgetScenarios = budgetScenarioService.findbyWorkgroupIdAndYear(workgroupId, year);

    return budgetScenarios;
  }
}
