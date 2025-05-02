package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;

import javax.persistence.*;
import java.util.Date;

@Entity
public class HistoricoInscricaoCadastral extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Cadastro cadastro;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String inscricaoCadastral;
    @Enumerated(EnumType.STRING)
    private SituacaoCadastralCadastroEconomico situacaoCadastral;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }
}
