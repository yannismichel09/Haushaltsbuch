package api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbaccess.DBAccessCSV;
import dto.CategoryFilterDtoIn;
import dto.TransactionFilterDtoIn;
import util.ControllerTools;

@RestController
@RequestMapping("/csv")
public class CSVController {
    private final DBAccessCSV dbAccessCSV;
    private final ControllerTools controllerTools;

        // Initialisiert den Controller mit CSV-Datenzugriff und Token-Pruefung.
    public CSVController(DBAccessCSV dbAccessCSV, ControllerTools controllerTools) {
        this.dbAccessCSV = dbAccessCSV;
        this.controllerTools = controllerTools;
    }

        // Exportiert gefilterte Kategorien als CSV-Datei.
    @PostMapping(value = "/categories", produces = "text/csv")
    public ResponseEntity<String> exportFilteredCategoriesToCsv(@RequestHeader("Authorization") String token,
            @RequestBody(required = false) CategoryFilterDtoIn categoryFilterDtoIn) {
        controllerTools.checkIsAccepted(token);
        CategoryFilterDtoIn filterDtoIn = categoryFilterDtoIn != null
                ? categoryFilterDtoIn
                : new CategoryFilterDtoIn(null, null, null, null, null);

        String csv = dbAccessCSV.exportFilteredCategoriesToCsv(filterDtoIn.categoryId(), filterDtoIn.keyword(),
                filterDtoIn.categoryColor(), filterDtoIn.amountMin(), filterDtoIn.amountMax());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categories.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

        // Exportiert gefilterte Transaktionen als CSV-Datei.
    @PostMapping(value = "/transactions", produces = "text/csv")
    public ResponseEntity<String> exportFilteredTransactionsToCsv(@RequestHeader("Authorization") String token,
            @RequestBody(required = false) TransactionFilterDtoIn transactionFilterDtoIn) {
        controllerTools.checkIsAccepted(token);
        TransactionFilterDtoIn filterDtoIn = transactionFilterDtoIn != null
                ? transactionFilterDtoIn
                : new TransactionFilterDtoIn(null, null, null, null, null, null, null, null, null, null);

        String csv = dbAccessCSV.exportFilteredTransactionsToCsv(filterDtoIn.transactionId(), filterDtoIn.userId(),
                filterDtoIn.categoryId(), filterDtoIn.amountMin(), filterDtoIn.amountMax(),
                filterDtoIn.transactionDateFrom(), filterDtoIn.transactionDateTo(), filterDtoIn.transactionType(),
                filterDtoIn.keyword(), filterDtoIn.transactionFrequency());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
