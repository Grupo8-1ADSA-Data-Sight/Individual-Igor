package bancoDeDados;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.util.Conversor;
import com.github.britooo.looca.api.group.processos.Processo;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import util.Componentes;
import util.Maquina;
import java.io.IOException;

public class InserirDadosNaTabela {
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();
    ConexaoServer conexaoServer = new ConexaoServer();
    JdbcTemplate conServer = conexaoServer.getConexaoDoBancoServer();
    Looca looca = new Looca();
    Maquina maquina = new Maquina();
    Componentes componentes = new Componentes();

    public InserirDadosNaTabela() throws IOException {
    }

    private boolean dadosExistemSql(String sql, Object... params) throws IOException {
        Integer count = conServer.queryForObject(sql, params, Integer.class);
        return count == null || count <= 0;
    }

    private Boolean dadosExistemMysql(String sql, Object... params) throws IOException {
        Integer count = con.queryForObject(sql, params, Integer.class);
        return count == null || count <= 0;
    }

    public void inserirDadosFixos() throws IOException {
        maquina.isLoginMaquina();

        // Inserindo no banco de dados da CPU, puxando os dados pela API - looca
        if (dadosExistemSql("SELECT COUNT(*) FROM cpu join Maquina on fkMaquina = idMaquina WHERE hostName = ?", looca.getRede().getParametros().getHostName())) {
            conServer.update("INSERT INTO CPU (fabricante, nome, identificador, frequenciaGHz, fkMaquina) values (?, ?, ?, ?, ?)", looca.getProcessador().getFabricante(), looca.getProcessador().getNome(), looca.getProcessador().getIdentificador(), looca.getProcessador().getFrequencia(), maquina.getIdMaquina());
        }

        // Inserindo no banco de dados da HD, puxando os dados pela API - looca
        for (Volume volume : looca.getGrupoDeDiscos().getVolumes()) {
            if (dadosExistemSql("SELECT COUNT(*) FROM HD WHERE nome = ? AND fkMaquina = ?", volume.getNome(), maquina.getIdMaquina())) {
                conServer.update("INSERT INTO HD (nome, tamanho, fkMaquina) values (?, ? , ?)", volume.getNome(), (volume.getTotal() / 1e+9), maquina.getIdMaquina());
            }
        }

        // Inserindo no banco de dados da RAM, puxando os dados pela API - looca
        if (dadosExistemSql("SELECT COUNT(*) FROM RAM WHERE fkMaquina = ?", maquina.getIdMaquina())) {
            conServer.update("INSERT INTO RAM (armazenamentoTotal, fkMaquina) values (?, ?)", looca.getMemoria().getTotal(), maquina.getIdMaquina());
        }

        if(dadosExistemMysql("SELECT COUNT(*) FROM CPU WHERE idCPU = 1")){
            con.update("INSERT INTO CPU (fabricante, nome, identificador, frequenciaGHz, fkMaquina) values (?, ?, ?, ?, ?)", looca.getProcessador().getFabricante(), looca.getProcessador().getNome(), looca.getProcessador().getIdentificador(), looca.getProcessador().getFrequencia(), maquina.getIdMaquina());
            for (Volume volume : looca.getGrupoDeDiscos().getVolumes()) {
                con.update("INSERT INTO HD (nome, tamanho, fkMaquina) values (?, ? , ?)", volume.getNome(), (volume.getTotal() / 1e+9), maquina.getIdMaquina());
            }
            con.update("INSERT INTO RAM (armazenamentoTotal, fkMaquina) values (?, ?)", (looca.getMemoria().getTotal() / 1e+9), maquina.getIdMaquina());
        }
    }

    public void inserindoDadosDinamicos() throws IOException {
        final StringBuilder sb = new StringBuilder();
        // Inserindo no banco de dados da CPULeitura, puxando os dados pela API - looca
        con.update("INSERT INTO CPULeitura (uso, tempoAtividade, dataHoraLeitura, fkCPU) values (?, ?, now(), (select max(idcpu) from CPU))", looca.getProcessador().getUso(), (sb.append("")
                .append(Conversor.formatarSegundosDecorridos(looca.getSistema().getTempoDeAtividade()))));
        conServer.update("INSERT INTO CPULeitura (uso, tempoAtividade, dataHoraLeitura, fkCPU) values (?, ?, GETDATE(), (select max(idcpu) from CPU))", looca.getProcessador().getUso(), (sb.append("")
                .append(Conversor.formatarSegundosDecorridos(looca.getSistema().getTempoDeAtividade()))));

        // Inserindo no banco de dados da HDLeitura, puxando os dados pela API - looca
        for (Volume volume : looca.getGrupoDeDiscos().getVolumes()) {
            con.update("INSERT INTO HDLeitura (uso, disponivel, dataHoraLeitura, fkHD) values (?, ?, now(), (select max(idHD) from HD))", componentes.emUsoHD(), componentes.disponivelHd());
            conServer.update("INSERT INTO HDLeitura (uso, disponivel, dataHoraLeitura, fkHD) values (?, ?, GETDATE(), (select max(idHD) from HD))", componentes.emUsoHD(), componentes.disponivelHd());
        }

        // Inserindo no banco de dados da RAMLeitura, puxando os dados pela API - looca
        con.update("INSERT INTO RAMLeitura (emUso, disponivel, dataHoraLeitura, fkRam) values (?, ?, now(), (select max(idRAM) from RAM))", (looca.getMemoria().getEmUso() / 1e+9), (looca.getMemoria().getDisponivel() / 1e+9));
        conServer.update("INSERT INTO RAMLeitura (emUso, disponivel, dataHoraLeitura, fkRam) values (?, ?, GETDATE(), (select max(idRAM) from RAM))", (looca.getMemoria().getEmUso() / 1e+9), (looca.getMemoria().getDisponivel() / 1e+9));

        // Inserindo no banco de dados dos Processos, puxando dados pela API - looca
        List<Processo> processos = looca.getGrupoDeProcessos().getProcessos();
        processos = processos.stream()
                .filter(p -> !p.getNome().equalsIgnoreCase("idle"))
                .collect(Collectors.toList());

        // Sort the list by CPU usage in descending order
        processos.sort((p1, p2) -> Double.compare(p2.getUsoCpu(), p1.getUsoCpu()));

        // Select the top 5 processes
        List<Processo> top5Processes = processos.subList(0, Math.min(processos.size(), 5));

        // Insert each process in the top 5 list into the Processo table
        for (Processo processo : top5Processes) {
            con.update("INSERT INTO Processo (nome, uso, dataHoraLeitura, fkMaquina) values (?, ?, now(), (select max(idMaquina) from Maquina))",
                    processo.getNome(), processo.getUsoCpu());
            conServer.update("INSERT INTO Processo (nome, uso, dataHoraLeitura, fkMaquina) values (?, ?, GETDATE(), (select max(idMaquina) from Maquina))",
                    processo.getNome(), processo.getUsoCpu());
        }
    }
}