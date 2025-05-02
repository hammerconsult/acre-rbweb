package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/04/14
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Etiqueta("Ata Registro de Preço")
public class AtaRegistroPreco extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Pesquisavel
    @Tabelavel
    private Long numero;

    @Etiqueta("Data de Início")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date dataInicio;

    @Etiqueta("Data de Vencimento")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date dataVencimento;

    @Etiqueta("Data de Vencimento Atual")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    private Date dataVencimentoAtual;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Licitação")
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    private Licitacao licitacao;

    private Boolean gerenciadora;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ataDeRegistroDePreco", orphanRemoval = true)
    private List<Adesao> adesoes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Etiqueta("Data de Assinatura")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date dataAssinatura;

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio da Ata")
    @Tabelavel(campoSelecionado = false)
    private String sequencialIdPncp;

    @Etiqueta("Id Criado pelo PNCP ao Realizar o Envio da Ata")
    @Tabelavel(campoSelecionado = false)
    private String idPncp;

    public AtaRegistroPreco() {
        gerenciadora = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Date getDataVencimentoAtual() {
        return dataVencimentoAtual;
    }

    public void setDataVencimentoAtual(Date dataVencimentoAtual) {
        this.dataVencimentoAtual = dataVencimentoAtual;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }


    public List<Adesao> getAdesoes() {
        return adesoes;
    }

    public void setAdesoes(List<Adesao> adesoes) {
        this.adesoes = adesoes;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Boolean getGerenciadora() {
        return gerenciadora;
    }

    public void setGerenciadora(Boolean ataGerenciadora) {
        this.gerenciadora = ataGerenciadora;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.ATA_REGISTRO_PRECO;
    }

    public Integer getAnoPcnp(){
        return DataUtil.getAno(dataInicio);
    }

    @Override
    public String toString() {
        String retorno = "Nº Ata: " + this.getNumero() + " Nº Licitação: " + this.getLicitacao().getNumeroLicitacao() + " Nº Modalidade: " + this.getLicitacao().getNumero() + " - " + licitacao.getResumoDoObjeto();
        if (retorno.length() > 100) {
            retorno = retorno.substring(0, 100) + "...";
        }
        return retorno;
    }
}
