package util;

import bancoDeDados.InserirDadosNaTabela;
import com.github.britooo.looca.api.core.Looca;
import com.sun.jna.platform.mac.IOReturnException;

import java.io.IOException;

public class ApresentarDados {
    Looca looca = new Looca();
    Maquina maquina = new Maquina();

    public ApresentarDados() throws IOException {
    }

    public void iniciarDadosPrograma() throws IOException {
        Componentes componentes = new Componentes();
        InserirDadosNaTabela inserirDadosNaTabela  = new InserirDadosNaTabela();

        int i = 1;
        System.out.println("""
                  _____        __     _______     __        _____    _____    _____    _    _   _______
                 |  __ \\      /  \\   |__   __|   /  \\      / ____|  |_   _|  / ____|  | |  | | |__   __|
                 | |  | |    / /\\ \\     | |     / /\\ \\    | |         | |   | |  __   | |__| |    | |
                 | |  | |   / /__\\ \\    | |    / /__\\ \\    \\____ \\    | |   | | |_ |  |  __  |    | |
                 | |__| |  /  ____  \\   | |   /  ____  \\   _____) |  _| |_  | |__| |  | |  | |    | |
                 |_____/  /_/      \\_\\  |_|  /_/      \\_\\ |______/  |_____|  \\_____|  |_|  |_|    |_|                
                """);

        if (maquina.isLoginMaquina()) {
            System.out.println("Login do usuário efetuado com sucesso");
            inserirDadosNaTabela.inserirDadosFixos();
            System.out.println(componentes.exibirComponentesEstaticos());
            try{
                while(true){
                    inserirDadosNaTabela.inserindoDadosDinamicos();
                    System.out.println(componentes.exibirLeituraComponentes());
                    Thread.sleep(5000);
                }
            } catch (IOReturnException e){
                System.out.println(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else{
            String menssagem = "Maquina não encontrada, realize o cadastro no nosso site";
            System.out.println(menssagem);
        }
    }
}
