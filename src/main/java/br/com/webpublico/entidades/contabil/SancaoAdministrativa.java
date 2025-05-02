package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoSituacaoSancao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Audited
@Entity
public class SancaoAdministrativa extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Órgão/Entidade")
    private UnidadeGestora unidadeGestora;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Publicação")
    private VeiculoDePublicacao publicacao;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataPublicacao;
    @Obrigatorio
    @Etiqueta("Nº DOE")
    private String numeroDoe;
    @Obrigatorio
    @Etiqueta("Página")
    private String pagina;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Empresa Sancionada")
    private Pessoa pessoa;
    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;
    @Obrigatorio
    @Etiqueta("Objeto")
    private String objeto;
    @Etiqueta("Sanção")
    private String sancao;
    @Etiqueta("Prazo")
    private String prazo;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Emissão")
    private Date inicioPenalidade;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Conclusão")
    private Date terminoPenalidade;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Prorrogação")
    private Date prorrogacao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private TipoSituacaoSancao situacao;
    @Etiqueta("Informações Adicionais")
    private String informacoesAdicionais;
    @Monetario
    @Etiqueta("Multa/Valor Pago")
    private BigDecimal valor;

    public SancaoAdministrativa() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public VeiculoDePublicacao getPublicacao() {
        return publicacao;
    }

    public void setPublicacao(VeiculoDePublicacao publicacao) {
        this.publicacao = publicacao;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getNumeroDoe() {
        return numeroDoe;
    }

    public void setNumeroDoe(String numeroDoe) {
        this.numeroDoe = numeroDoe;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getSancao() {
        return sancao;
    }

    public void setSancao(String sancao) {
        this.sancao = sancao;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public Date getInicioPenalidade() {
        return inicioPenalidade;
    }

    public void setInicioPenalidade(Date inicioPenalidade) {
        this.inicioPenalidade = inicioPenalidade;
    }

    public Date getTerminoPenalidade() {
        return terminoPenalidade;
    }

    public void setTerminoPenalidade(Date terminoPenalidade) {
        this.terminoPenalidade = terminoPenalidade;
    }

    public Date getProrrogacao() {
        return prorrogacao;
    }

    public void setProrrogacao(Date prorrogacao) {
        this.prorrogacao = prorrogacao;
    }

    public TipoSituacaoSancao getSituacao() {
        return situacao;
    }

    public void setSituacao(TipoSituacaoSancao situacao) {
        this.situacao = situacao;
    }

    public String getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(String informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return sancao + " ,Publicada em " + DataUtil.getDataFormatada(dataPublicacao) + " (" + pessoa.toString() + ")";
    }
}
