package api;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbaccess.DBAccessWarning;
import dto.CategoryDtoOut;
import util.ControllerTools;

@RestController
@RequestMapping("/warnings")
public class WarningController {
    private final DBAccessWarning dbAccessWarning;
    private final ControllerTools controllerTools;

    public WarningController(DBAccessWarning dbAccessWarning, ControllerTools controllerTools) {
        this.dbAccessWarning = dbAccessWarning;
        this.controllerTools = controllerTools;
    }

    private static final double BUDGET_WARNING_THRESHOLD = 0.9;

    // Get-Mapping

    @GetMapping("/budgetlimit")
    public ResponseEntity<Collection<CategoryDtoOut>> checkBudgetLimit(@RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        Collection<CategoryDtoOut> categories = dbAccessWarning.checkBudgetLimit(BUDGET_WARNING_THRESHOLD).stream().map(CategoryDtoOut::new).toList();

        return ResponseEntity.ok()
                             .body(categories);
    }

    @GetMapping("/netbalance")
    public ResponseEntity<Boolean> checkNetBalanceNegative(@RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        boolean result = dbAccessWarning.checkNetBalanceNegative();

        return ResponseEntity.ok()
                             .body(result);
    }

}
