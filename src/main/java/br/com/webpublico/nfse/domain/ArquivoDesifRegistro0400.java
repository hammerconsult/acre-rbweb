package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ArquivoDesifRegistro0400 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    private String codigoDependencia;
    @Enumerated(EnumType.STRING)
    private IdentificacaoDependenciaArquivoDesif identificacaoDependencia;
    private String cnpjProprio;
    @ManyToOne
    private TipoDependenciaDesif tipoDependencia;
    private String enderecoDependencia;
    private String cnpjResponsavel;
    @ManyToOne
    private Cidade cidadeResponsavel;
    private Boolean contabilidadePropria;
    @Temporal(TemporalType.DATE)
    private Date dataInicioParalizacao;
    @Temporal(TemporalType.DATE)
    private Date dataFimParalizacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    public String getCodigoDependencia() {
        return codigoDependencia;
    }

    public void setCodigoDependencia(String codigoDependencia) {
        this.codigoDependencia = codigoDependencia;
    }

    public IdentificacaoDependenciaArquivoDesif getIdentificacaoDependencia() {
        return identificacaoDependencia;
    }

    public void setIdentificacaoDependencia(IdentificacaoDependenciaArquivoDesif identificacaoDependencia) {
        this.identificacaoDependencia = identificacaoDependencia;
    }

    public String getCnpjProprio() {
        return cnpjProprio;
    }

    public void setCnpjProprio(String cnpjProprio) {
        this.cnpjProprio = cnpjProprio;
    }

    public TipoDependenciaDesif getTipoDependencia() {
        return tipoDependencia;
    }

    public void setTipoDependencia(TipoDependenciaDesif tipoDependencia) {
        this.tipoDependencia = tipoDependencia;
    }

    public String getEnderecoDependencia() {
        return enderecoDependencia;
    }

    public void setEnderecoDependencia(String enderecoDependencia) {
        this.enderecoDependencia = enderecoDependencia;
    }

    public String getCnpjResponsavel() {
        return cnpjResponsavel;
    }

    public void setCnpjResponsavel(String cnpjResponsavel) {
        this.cnpjResponsavel = cnpjResponsavel;
    }

    public Cidade getCidadeResponsavel() {
        return cidadeResponsavel;
    }

    public void setCidadeResponsavel(Cidade cidadeResponsavel) {
        this.cidadeResponsavel = cidadeResponsavel;
    }

    public Boolean getContabilidadePropria() {
        return contabilidadePropria;
    }

    public void setContabilidadePropria(Boolean contabilidadePropria) {
        this.contabilidadePropria = contabilidadePropria;
    }

    public Date getDataInicioParalizacao() {
        return dataInicioParalizacao;
    }

    public void setDataInicioParalizacao(Date dataInicioParalizacao) {
        this.dataInicioParalizacao = dataInicioParalizacao;
    }

    public Date getDataFimParalizacao() {
        return dataFimParalizacao;
    }

    public void setDataFimParalizacao(Date dataFimParalizacao) {
        this.dataFimParalizacao = dataFimParalizacao;
    }
}
