package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import java.io.Serializable;
import java.util.Date;

public class RelatorioAcompanhamentoAtualizacaoCadastralServidores implements Serializable {
    private String nome;
    private String matricula;
    private Date dataAtualizacao;
    private String orgao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}
