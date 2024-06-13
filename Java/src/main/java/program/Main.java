package program;

import bancoDeDados.Conexao;
import bancoDeDados.ConexaoServer;
import org.springframework.jdbc.core.JdbcTemplate;
import util.ApresentarDados;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Conexao conexao = new Conexao();
        Boolean isConectado = false;
        String mensagemConexao = "Aguardando...";
        System.out.println(mensagemConexao);
        while(!isConectado){
            try {
                conexao.selectBanco();
                isConectado = true;
                mensagemConexao = "Conectado com banco!";
            } catch (Exception e) {
            }
        }
        System.out.println(mensagemConexao);



        ApresentarDados apresentarDados = new ApresentarDados();
        apresentarDados.iniciarDadosPrograma();
    }
}