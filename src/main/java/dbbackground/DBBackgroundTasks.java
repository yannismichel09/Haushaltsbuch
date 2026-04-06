package dbbackground;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbaccess.DBAccessWarning;
import jakarta.transaction.Transactional;
import model.Category;

@Service
@Transactional
public class DBBackgroundTasks {
    private final DBAccessWarning dbAccess;
	
	@Autowired
	public DBBackgroundTasks(DBAccessWarning dbAccess) {
		
		this.dbAccess=dbAccess;
		
	}

    public List<Category> backgroundCheckBudgetLimit() {
        List<Category> categories = dbAccess.checkBudgetLimit(90.0);
        return categories;
    }

    public boolean backgroundCheckNetBalance() {
        boolean result = dbAccess.checkNetBalanceNegative();
        return result;
    }
}
