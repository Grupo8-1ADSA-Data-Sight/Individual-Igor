package bancoDeDados;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

public class ConexaoServer {
    private JdbcTemplate conexaoDoBancoDockerServer;
    public ConexaoServer() throws IOException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://3.225.31.33:1433;databaseName=datasight;encrypt=true;trustServerCertificate=true");
        dataSource.setUsername("sa");
        dataSource.setPassword("datasightgrupo8");

        conexaoDoBancoDockerServer = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConexaoDoBancoServer() throws IOException {
        return conexaoDoBancoDockerServer;
    }
}
