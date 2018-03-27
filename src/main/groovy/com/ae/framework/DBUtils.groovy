package com.ae.framework

import com.eviware.soapui.support.GroovyUtils
import groovy.sql.Sql

class DBUtils {

    /**
     *
     * @param project
     * @return
     */
    static def getOracleDBObject(project) {
        def db = [url:project.getContext().appProperties.oracle_db_url,
                  user:project.getContext().appProperties.oracle_db_user,
                  password:project.getContext().appProperties.oracle_db_password,
                  driver:project.getContext().appProperties.oracle_db_driver ]
        if(project.getContext().oracleObj==null) {
            project.getContext().oracleObj =  createDBConnection(db)
        }
    }

    /**
     *
     * @param db
     * @return
     */
    static createDBConnection(db){
        GroovyUtils.registerJdbcDriver( db.driver);
        return Sql.newInstance(db.url, db.user, db.password, db.driver)
    }

    /**
     *
     * @param sql
     * @return
     */
    static def closeDBConnection(sql) {
        if(sql)
            sql.close()
    }

}
