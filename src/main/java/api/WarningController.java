package api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dbaccess.DBAccessWarning;
import dto.CategoryDtoOut;
import security.SecurityManager;

@RestController
@RequestMapping("/warnings")
public class WarningController {
    private final DBAccessWarning dbAccessWarning;
    private final SecurityManager securityManager;

    @Autowired
    public WarningController(DBAccessWarning dbAccessWarning, SecurityManager securityManager) {
        this.dbAccessWarning = dbAccessWarning;
        this.securityManager = securityManager;
    }

    private static final double BUDGET_WARNING_THRESHOLD = 0.9;

    // Get-Mapping

    @GetMapping("/budgetlimit")
    public ResponseEntity<Collection<CategoryDtoOut>> checkBudgetLimit(@RequestHeader("Authorization") String token) {
        checkIsAccepted(token);
        Collection<CategoryDtoOut> categories = dbAccessWarning.checkBudgetLimit(BUDGET_WARNING_THRESHOLD).stream().map(CategoryDtoOut::new).toList();

        return ResponseEntity.ok()
                             .body(categories);
    }

    @GetMapping("/netbalance")
    public ResponseEntity<Boolean> checkNetBalanceNegative(@RequestHeader("Authorization") String token) {
        checkIsAccepted(token);
        boolean result = dbAccessWarning.checkNetBalanceNegative();

        return ResponseEntity.ok()
                             .body(result);
    }

    // Token-Check

    private void checkIsAccepted(String token) {

        if (!securityManager.isValid(token)) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        }

    }
}
