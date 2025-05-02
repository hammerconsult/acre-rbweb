package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CalculoIPTU;
import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.entidades.ParecerProcIsencaoIPTU;
import br.com.webpublico.entidades.ProcessoIsencaoIPTU;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "VOPARECERISENCAOIPTU")
@Entity
public class VOParecerIsencaoIptu implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cadastro Imobiliário")
    private String inscricaoCadastral;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data do Parecer")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataParecer;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário do Parecer")
    private String usuarioParecer;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação da Solicitação")
    @Enumerated(EnumType.STRING)
    private ProcessoIsencaoIPTU.Situacao situacaoSolicitacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Existe Cálculo")
    private Boolean existeCalculo;

    public VOParecerIsencaoIptu() {
    }

    public VOParecerIsencaoIptu(IsencaoCadastroImobiliario isencao, ParecerProcIsencaoIPTU parecer, CalculoIPTU calculoIPTU) {
        this.id = isencao.getId();
        this.inscricaoCadastral = isencao.getCadastroImobiliario().getInscricaoCadastral();
        this.dataParecer = parecer.getDataParecer();
        this.usuarioParecer = parecer.getUsuarioParecer().getPessoaFisica().getNomeCpfCnpj();
        this.situacaoSolicitacao = parecer.getSolicitacaoIsencao().getSituacao();
        this.existeCalculo = calculoIPTU != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public String getUsuarioParecer() {
        return usuarioParecer;
    }

    public void setUsuarioParecer(String usuarioParecer) {
        this.usuarioParecer = usuarioParecer;
    }

    public ProcessoIsencaoIPTU.Situacao getSituacaoSolicitacao() {
        return situacaoSolicitacao;
    }

    public void setSituacaoSolicitacao(ProcessoIsencaoIPTU.Situacao situacaoSolicitacao) {
        this.situacaoSolicitacao = situacaoSolicitacao;
    }

    public Boolean getExisteCalculo() {
        return existeCalculo;
    }

    public void setExisteCalculo(Boolean existeCalculo) {
        this.existeCalculo = existeCalculo;
    }
}
