package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Cacheable
@Audited
@Etiqueta("Evento de Integração com a Rede Sim")
public class EventoRedeSim extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    private Date dataOperacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Identificador")
    private String identificador;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    private TipoEvento tipoEvento;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private Situacao situacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("CNPJ")
    private String cnpj;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    private String conteudo;
    @Tabelavel
    private String situacaoEmpresa;
    @Tabelavel
    @Etiqueta("Versão")
    private String versao;
    private String inscricaoCadastral;

    public EventoRedeSim() {
        dataOperacao = new Date();
        situacao = Situacao.AGUARDANDO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getSituacaoEmpresa() {
        return situacaoEmpresa;
    }

    public void setSituacaoEmpresa(String situacaoEmpresa) {
        this.situacaoEmpresa = situacaoEmpresa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificados) {
        this.identificador = identificados;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public static enum TipoEvento implements EnumComDescricao {
        INSERCAO("Inscrição"), ATUALIZACAO("Atualização");

        private String descricao;

        TipoEvento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public static enum Situacao implements EnumComDescricao {
        PROCESSADO("Processado"), AGUARDANDO("Aguardando");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }


}
