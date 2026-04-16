package api;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbaccess.DBAccessTransaction;
import dto.TransactionCreateDtoIn;
import dto.TransactionDtoOut;
import dto.TransactionFilterDtoIn;
import dto.TransactionUpdateDtoIn;
import model.Transaction;
import util.ControllerTools;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final DBAccessTransaction dbAccessTransaction;
    private final ControllerTools controllerTools;

    // Initialisiert den Controller
    public TransactionController(DBAccessTransaction dbAccessTransaction, ControllerTools controllerTools) {
        this.dbAccessTransaction = dbAccessTransaction;
        this.controllerTools = controllerTools;
    }

    // Post-Mapping

    // Erstellt eine neue Transaktion.
    @PostMapping
    public ResponseEntity<TransactionDtoOut> createTransaction(@RequestHeader("Authorization") String token,
            @RequestBody TransactionCreateDtoIn transactionDtoIn) {
        controllerTools.checkIsAccepted(token);
        Transaction transaction = dbAccessTransaction.createTransaction(transactionDtoIn.userId(),
                transactionDtoIn.categoryId(), transactionDtoIn.transactionAmount(), transactionDtoIn.transactionDate(),
                transactionDtoIn.transactionType(), transactionDtoIn.transactionDescription(),
                transactionDtoIn.transactionFrequency());

        return ResponseEntity.ok()
                .body(new TransactionDtoOut(transaction));
    }

    // Liefert Transaktionen anhand von Filterkriterien.
    @PostMapping("/filter")
    public ResponseEntity<Collection<TransactionDtoOut>> getFilteredTransactions(
            @RequestHeader("Authorization") String token,
            @RequestBody TransactionFilterDtoIn transactionFilterDtoIn) {
        controllerTools.checkIsAccepted(token);
        Collection<TransactionDtoOut> transactions = dbAccessTransaction.getFilteredTransactions(
                transactionFilterDtoIn.transactionId(), transactionFilterDtoIn.userId(),
                transactionFilterDtoIn.categoryId(), transactionFilterDtoIn.amountMin(),
                transactionFilterDtoIn.amountMax(), transactionFilterDtoIn.transactionDateFrom(),
                transactionFilterDtoIn.transactionDateTo(), transactionFilterDtoIn.transactionType(),
                transactionFilterDtoIn.keyword(), transactionFilterDtoIn.transactionFrequency()).stream()
                .map(TransactionDtoOut::new).toList();

        return ResponseEntity.ok()
                .body(transactions);
    }

    // Get-Mapping

    // Liefert alle vorhandenen Transaktionen.
    @GetMapping
    public ResponseEntity<Collection<TransactionDtoOut>> getAllTransactions(
            @RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        Collection<TransactionDtoOut> transactions = dbAccessTransaction.getAllTransactions().stream()
                .map(TransactionDtoOut::new).toList();

        return ResponseEntity.ok()
                .body(transactions);
    }

    // Liefert eine Transaktion anhand ihrer ID.
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDtoOut> getTransactionById(@PathVariable int transactionId,
            @RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        Transaction transaction = dbAccessTransaction.getTransactionById(transactionId);

        return ResponseEntity.ok()
                .body(new TransactionDtoOut(transaction));
    }

    // Delete-Mapping

    // Loescht eine Transaktion anhand ihrer ID.
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Boolean> deleteTransaction(@PathVariable int transactionId,
            @RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        boolean result = dbAccessTransaction.deleteTransaction(transactionId);

        return ResponseEntity.ok()
                .body(result);
    }

    // Put-Mapping

    // Aktualisiert eine bestehende Transaktion.
    @PutMapping("/{transactionId}")
    public ResponseEntity<Boolean> updateTransaction(@PathVariable int transactionId,
            @RequestHeader("Authorization") String token,
            @RequestBody TransactionUpdateDtoIn transactionUpdateDtoIn) {
        controllerTools.checkIsAccepted(token);
        boolean result = dbAccessTransaction.updateTransaction(transactionId, transactionUpdateDtoIn.userId(),
                transactionUpdateDtoIn.categoryId(), transactionUpdateDtoIn.transactionAmount(),
                transactionUpdateDtoIn.transactionDate(), transactionUpdateDtoIn.transactionType(),
                transactionUpdateDtoIn.transactionDescription(), transactionUpdateDtoIn.transactionFrequency());

        return ResponseEntity.ok()
                .body(result);
    }
}
