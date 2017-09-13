import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateDDL {

    public static final String JDBC_URL_PREFIX = "jdbc:oracle:thin:@//";
    public static final String DEFAULT_JDBC_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    public static final String SYS_USERNAME = "sys as sysdba";

    static class Config {
        @JsonProperty("connect-string")
        private String connectString;

        @JsonProperty("sys-password")
        private String sysPassword;

        @JsonProperty("username")
        private String username;

        @JsonProperty("statements")
        private List<String> statements;
        
        @JsonCreator
        public Config() {
        }

        public String getConnectString() {
            return connectString;
        }

        public void setConnectString(String connectString) {
            this.connectString = connectString;
        }

        public String getSysPassword() {
            return sysPassword;
        }

        public void setSysPassword(String sysPassword) {
            this.sysPassword = sysPassword;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getStatements() {
            return statements;
        }

        public void setStatements(List<String> statements) {
            this.statements = statements;
        }

    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must specify config file path");
        }

        Path filePath = Paths.get(args[0]);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Config file path " + filePath + " does not exist");
        } else {

            Config config = new ObjectMapper().readValue(Files.readAllBytes(filePath), Config.class);
            if ((config.getConnectString() != null || !"".equals(config.getConnectString()))
                    && (config.getSysPassword() != null || !"".equals(config.getSysPassword()))
                    && (config.getUsername() != null || !"".equals(config.getUsername()))) {
                String jdbcUrl = JDBC_URL_PREFIX+config.getConnectString();
                
                try (Connection con = DriverManager.getConnection(jdbcUrl, SYS_USERNAME, config.getSysPassword());) {
                    executeStatement(con,
                            "create user " + config.getUsername() + " identified by " + config.getSysPassword());
                    executeStatement(con, "grant all privileges to " + config.getUsername());
                }
                if (config.getStatements() != null && !config.getStatements().isEmpty()) {
                    try (Connection con = DriverManager.getConnection(jdbcUrl, config.getUsername(),
                            config.getSysPassword());) {
                        for (String statement : config.getStatements()) {
                            executeStatement(con, statement);
                        }
                    }

                }
            }

        }
    }

    private static void executeStatement(Connection con, String statement) throws SQLException {
        try (Statement stmt = con.createStatement();) {
            System.out.println("Executing " + statement);
            stmt.execute(statement);
        }
    }
}
