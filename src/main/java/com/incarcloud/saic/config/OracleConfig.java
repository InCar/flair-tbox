package com.incarcloud.saic.config;

import com.incarcloud.auxiliary.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConfig {
    private static final Logger s_logger = LoggerFactory.getLogger(OracleConfig.class);

    private String driver;
    private String url;
    private String username;
    private String password;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static Connection conn = null;

    public Connection createClient(){
        try {
            Object obj = Class.forName(driver);
            s_logger.info("oracle driver : {}", obj);
            conn = DriverManager.getConnection(url, username, password);
            s_logger.info("oracle conn: {}", conn);
        }catch (Exception ex) {
            s_logger.error("create oracle client failed: {}", Helper.printStackTrace(ex));
            throw new RuntimeException("Connect to database failed!");
        }
        return conn;
    }
}
