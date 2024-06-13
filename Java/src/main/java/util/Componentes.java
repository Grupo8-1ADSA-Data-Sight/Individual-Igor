package util;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.util.Conversor;
import com.github.britooo.looca.api.group.processos.Processo;
import org.json.JSONObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Componentes extends Looca {
    Maquina maquina = new Maquina();

    private Integer fkMaquina;

    public Componentes() throws IOException {
    }

    public Boolean getPermissao() {
        return super.getSistema().getPermissao();
    }

    // METODOS:

    // Metodos de listas:
    public String listaArmazenamento() {
        String informacoesArmazenamento = null;

        for (int i = 0; i < getGrupoDeDiscos().getVolumes().size(); i++) {
            informacoesArmazenamento = String.format("""
                            Disco (%d)
                            %s
                            Espaço disponivel do disco = %.1f GiB
                            Espaço total do disco = %.1f GiB
                            """,
                    (i + 1),
                    getGrupoDeDiscos().getVolumes().get(i).getNome(),
                    (getGrupoDeDiscos().getVolumes().get(i).getDisponivel() / 1e+9),
                    (getGrupoDeDiscos().getVolumes().get(i).getTotal() / 1e+9)
            );

        }
        return informacoesArmazenamento;
    }

    public Double totalArmazenamentoDisponivel() {
        Double totalArmazenamentoDisponivel = 0.0;

        for (int i = 0; i < getGrupoDeDiscos().getVolumes().size(); i++) {
            totalArmazenamentoDisponivel += getGrupoDeDiscos().getVolumes().get(i).getDisponivel();
        }
        return totalArmazenamentoDisponivel / 1e+9;
    }

    public Double totalArmazenamento() {
        Double totalArmazenamento = 0.0;

        for (int i = 0; i < getGrupoDeDiscos().getVolumes().size(); i++) {
            totalArmazenamento += getGrupoDeDiscos().getVolumes().get(i).getTotal();
        }
        return totalArmazenamento / 1e+9;
    }

    public Double disponivelHd(){
        Double disponivelHd = 0.0;
        for (Volume volume : getGrupoDeDiscos().getVolumes()) {
            disponivelHd += volume.getDisponivel();
        }
        return disponivelHd / 1e+9;
    }

    public Double emUsoHD(){
        return (totalArmazenamento() - totalArmazenamentoDisponivel());
    }


    public String getProcessInfo() {
        StringBuilder processSb = new StringBuilder();
        List<Processo> processos = new ArrayList<>(getGrupoDeProcessos().getProcessos());

        // Ordenar processos de acordo com o uso de CPU em ordem decrescente
        processos.sort((p1, p2) -> Double.compare(p2.getUsoCpu(), p1.getUsoCpu()));

        // Listar os 5 processos mais intensivos em CPU
        List<Processo> topProcesses = processos.subList(0, Math.min(processos.size(), 5));

        for (Processo processo : topProcesses) {
            processSb.append(String.format("""
        Processo: %s
        ID: %d
        Consumo de CPU: %.2f%%
        Consumo de memória: %.2f%%
        --------------------------------
        """,
                    processo.getNome(),
                    processo.getPid(),
                    processo.getUsoCpu(),
                    processo.getUsoMemoria()
            ));
        }
        return processSb.toString();
    }

    // Metodos para apresentar
    public String exibirComponentesEstaticos() {
        StringBuilder sb = new StringBuilder();
        maquina.isLoginMaquina();
        String processInfo = getProcessInfo();
        StringBuilder processSb = new StringBuilder();



        return String.format("""
                                                                                
                                                                                
                                                                      Sistema
                        ------------------------------------------------------------------------------------------------                               
                        Sistema Operacional = %s
                        Fabricante = %s
                        Arquitetura = x%s
                        Permissões = %s
                        fkMaquina = %s                   
                        ------------------------------------------------------------------------------------------------
                                                                                
                                                                       CPU
                        ------------------------------------------------------------------------------------------------                              
                        idCpuMaquina = %s
                        Fabricante = %s
                        Nome = %s
                        Identificador = %s
                        FrequenciaGhz = %d
                        Nucleos Fisicos = %d
                        Nucleos Logicos = %d
                        fkMaquina = %s                        
                        ------------------------------------------------------------------------------------------------
                               
                                                                  Armazenamento
                        ------------------------------------------------------------------------------------------------
                                                
                        %s
                        Espaço Disponivel geral = %.1f GiB
                        Espaço total geral = %.1f GiB
                        fkMaquina = %s 
                        ------------------------------------------------------------------------------------------------
                                   
                                                                       RAM
                        ------------------------------------------------------------------------------------------------
                                                
                        Tamanho= %s
                        fkMaquina = %s 
                        ------------------------------------------------------------------------------------------------
                                                
                                                                       Rede
                        ------------------------------------------------------------------------------------------------
                        hostName= %s 
                        modelo= %s
                        ipv4= %s
                        fkMaquina= %s
                        ------------------------------------------------------------------------------------------------
                        """,
                // Dados sistema
                super.getSistema().getSistemaOperacional(),
                super.getSistema().getFabricante(),
                super.getSistema().getArquitetura(),
                sb.append(("Executando como ")).append((this.getPermissao() ? "root" : "usuário padrão")),
                maquina.getIdMaquina(),

                // Dados processador (CPU)
                super.getProcessador().getId(),
                super.getProcessador().getFabricante(),
                super.getProcessador().getNome(),
                super.getProcessador().getIdentificador(),
                super.getProcessador().getFrequencia(),
                super.getProcessador().getNumeroCpusFisicas(),
                super.getProcessador().getNumeroCpusLogicas(),
                maquina.getIdMaquina(),

                // Dados armazenamento (HD/SD)
                listaArmazenamento(),
                totalArmazenamentoDisponivel(),
                totalArmazenamento() ,
                maquina.getIdMaquina(),


                // Dados memória RAM
                Conversor.formatarBytes(getMemoria().getTotal()),
                maquina.getIdMaquina(),

                // Dados Rede
                super.getRede().getParametros().getHostName(),
                super.getRede().getGrupoDeInterfaces().getInterfaces().get(1).getNomeExibicao(),
                super.getRede().getGrupoDeInterfaces().getInterfaces().get(1).getEnderecoIpv4(),
                maquina.getIdMaquina()
        );
    }

    public String exibirLeituraComponentes() throws IOException, InterruptedException {
        Double porcentagemDeUsoRAM = (double) super.getMemoria().getEmUso() / super.getMemoria().getTotal() * 100;
        Double porcentagemDeUsoHD = (double) emUsoHD() / (totalArmazenamento()) * 100;
        // convetendo disponivel da memória RAM
        Double disponivelEmGibRAM = super.getMemoria().getDisponivel() / 1e+9;
        JSONObject json = new JSONObject();
        String topProcessesInfo = getProcessInfo();

        if (getProcessador().getUso() > 1.0) {
            json.put("text", (String.format("""
                    Uso da CPU acima de %.2f%%, manter alerta!!""", getProcessador().getUso())));
            Slack.sendMessage(json);

        }

        if (porcentagemDeUsoHD > 70.0) {
            json.put("text", (String.format("""
                    Uso do disco acima de %.2f%%, manter alerta!!""", porcentagemDeUsoHD)));
            Slack.sendMessage(json);
        }

        if (porcentagemDeUsoRAM > 80.0) {
            json.put("text", (String.format("""
                    Uso da memória ram acima de %.2f%%, manter alerta!!""", porcentagemDeUsoRAM)));
            Slack.sendMessage(json);
        }

        return String.format("""
                           |--------------------------|
                           |           CPU            |
                           |--------------------------|
                           |    Uso   |   Frequência  |
                           |                          |
                           |   %.1f%%  |    %.1fGhz      |
                           |__________________________|

                           |--------------------------|
                           |      Armazenamento       |
                           |--------------------------|
                           |     Uso   |   Disponível |
                           |                          |
                           |  %.1f%%   |   %.1f GiB  |
                           |__________________________|

                           |--------------------------|
                           |      Memória Ram         |
                           |--------------------------|
                           |      USO   |  Disponível |
                           |                          |
                           |    %.1f%%   |  %.1f Gib    |
                           |__________________________|

                           |--------------------------|
                           |      Top 5 Processes     |
                           |--------------------------|
                           %s
                        %n""",
                super.getProcessador().getUso(),
                super.getProcessador().getFrequencia() / 1e+9,
                porcentagemDeUsoHD,
                totalArmazenamentoDisponivel(),
                porcentagemDeUsoRAM,
                disponivelEmGibRAM,
                topProcessesInfo // Include the top 5 processes info here
        );
    }
}


