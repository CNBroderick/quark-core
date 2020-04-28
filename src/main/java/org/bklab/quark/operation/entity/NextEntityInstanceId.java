package org.bklab.quark.operation.entity;

import dataq.core.jdbc.DBAccess;
import dataq.core.operation.JdbcQueryOperation;
import dataq.core.operation.OperationResult;

public class NextEntityInstanceId extends JdbcQueryOperation {

    private final static String update = "UPDATE tb_instance_id SET d_id = d_id + 1 WHERE d_name = 'entity';";
    private final static String select = "SELECT d_id FROM tb_instance_id WHERE d_name = 'entity';";

    @Override
    public OperationResult doExecute() throws Exception {
        DBAccess db = getDBAccess();
        db.execute(update);
        db.commit();
        return successResult(db.queryForLong(select));
    }

    @Override
    public String createSQLSelect() {
        return null;
    }
}
