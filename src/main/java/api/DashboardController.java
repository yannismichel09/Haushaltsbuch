package api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dbaccess.DBAccessTransaction;
import dto.TransactionDtoOut;
import dto.TransactionFilterDtoIn;
import security.SecurityManager;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DBAccessTransaction dbAccessTransaction;
    private final SecurityManager securityManager;

    @Autowired
    public DashboardController(DBAccessTransaction dbAccessTransaction, SecurityManager securityManager) {
        this.dbAccessTransaction = dbAccessTransaction;
        this.securityManager = securityManager;
    }

    // Get-Mapping

    @RequestMapping("/transactions")
    public ResponseEntity<Collection<TransactionDtoOut>> getAllTransactions(@RequestHeader("Authorization") String token) {
        checkIsAccepted(token);
        Collection<TransactionDtoOut> transactions = dbAccessTransaction.getAllTransactions().stream().map(TransactionDtoOut::new).toList();

        return ResponseEntity.ok()
                             .body(transactions);
    }

    @GetMapping("/transactions/filter")
    public ResponseEntity<Collection<TransactionDtoOut>> getFilteredTransactions(@RequestHeader("Authorization") String token, @RequestBody TransactionFilterDtoIn filterDtoIn) {
        checkIsAccepted(token);
        Collection<TransactionDtoOut> transactions = dbAccessTransaction.getFilteredTransactions(
                filterDtoIn.transactionId(),
                filterDtoIn.userId(),
                filterDtoIn.categoryId(),
                filterDtoIn.amountMin(),
                filterDtoIn.amountMax(),
                filterDtoIn.transactionDateFrom(),
                filterDtoIn.transactionDateTo(),
                filterDtoIn.transactionType(),
                filterDtoIn.keyword(),
                filterDtoIn.transactionFrequency()).stream().map(TransactionDtoOut::new).toList();

        return ResponseEntity.ok()
                             .body(transactions);
    }

    @RequestMapping("/transactions/sumspendings")
    public ResponseEntity<Double> getSumTransactionsSpendings(@RequestHeader("Authorization") String token) {
        checkIsAccepted(token);
        Double sum = dbAccessTransaction.sumTransactionsSpendings();

        return ResponseEntity.ok()
                             .body(sum);
    }

    @RequestMapping("/transactions/sumincome")
    public ResponseEntity<Double> getSumTransactionsIncome(@RequestHeader("Authorization") String token) {
        checkIsAccepted(token);
        Double sum = dbAccessTransaction.sumTransactionsIncome();

        return ResponseEntity.ok()
                             .body(sum);
    }

    @RequestMapping("/category/{categoryId}/sumspendings")
    public ResponseEntity<Double> getSumCategorySpendings(@RequestHeader("Authorization") String token, @PathVariable int categoryId) {
        checkIsAccepted(token);
        Double sum = dbAccessTransaction.sumCategorySpendings(categoryId);

        return ResponseEntity.ok()
                             .body(sum);
    }

    @RequestMapping("/category/{categoryId}/sumincome")
    public ResponseEntity<Double> getSumCategoryIncome(@RequestHeader("Authorization") String token, @PathVariable int categoryId) {
        checkIsAccepted(token);
        Double sum = dbAccessTransaction.sumCategoryIncome(categoryId);

        return ResponseEntity.ok()
                             .body(sum);
    }

    // Token-Check

    private void checkIsAccepted(String token) {

        if (!securityManager.isValid(token)) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        }

    }
}
