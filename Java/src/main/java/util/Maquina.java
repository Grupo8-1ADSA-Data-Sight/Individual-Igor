package util;

import bancoDeDados.Conexao;
import bancoDeDados.ConexaoServer;
import com.github.britooo.looca.api.core.Looca;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.List;

public class Maquina {
    ConexaoServer conexaoServer = new ConexaoServer();
    JdbcTemplate conServer = conexaoServer.getConexaoDoBancoServer();
    Looca looca = new Looca();

    private String idMaquina;
    private String codPatrimonio;
    private String codRack;
    private String hostName;
    private Integer fkEmpresa;

    public Maquina() throws IOException {
        this.idMaquina = idMaquina;
        this.codPatrimonio = codPatrimonio;
        this.codRack = codRack;
        this.fkEmpresa = fkEmpresa;
        this.hostName = looca.getRede().getParametros().getHostName();
    }

    public Boolean isLoginMaquina(){
        List<Maquina> listaMaquina = conServer.query("Select * from maquina", new BeanPropertyRowMapper<>(Maquina.class));
        for (Maquina maquina : listaMaquina) {
            if (maquina.getHostName() != null && maquina.getHostName().equalsIgnoreCase(getHostName())) {
                setIdMaquina(maquina.getIdMaquina());
                return true;
            }
        }
        return false;
    }

    public String getIdMaquina() {
        return idMaquina;
    }

    public void setIdMaquina(String idMaquina) {
        this.idMaquina = idMaquina;
    }

    public String getCodPatrimonio() {
        return codPatrimonio;
    }

    public void setCodPatrimonio(String codPatrimonio) {
        this.codPatrimonio = codPatrimonio;
    }

    public String getCodRack() {
        return codRack;
    }

    public void setCodRack(String codRack) {
        this.codRack = codRack;
    }

    public Integer getFkEmpresa() {
        return fkEmpresa;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setFkEmpresa(Integer fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
    }
}