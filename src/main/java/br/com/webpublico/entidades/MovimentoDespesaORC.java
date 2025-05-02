/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Movimento Despesa Orçamentária")
public class MovimentoDespesaORC extends SuperEntidade {

    protected static final Logger logger = LoggerFactory.getLogger(MovimentoDespesaORC.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data de Movimento")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataMovimento;

    @Tabelavel
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Tabelavel
    @Etiqueta("Operação Orçamentária")
    @Enumerated(value = EnumType.STRING)
    private OperacaoORC operacaoORC;

    @Tabelavel
    @Enumerated(value = EnumType.STRING)
    @Etiqueta("Tipo de Crédito")
    private TipoOperacaoORC tipoOperacaoORC;

    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    private FonteDespesaORC fonteDespesaORC;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    private String idOrigem;
    private String classeOrigem;
    private String numeroMovimento;
    private String historico;

    public MovimentoDespesaORC() {
    }

    public MovimentoDespesaORC(Date dataMovimento, BigDecimal valor, OperacaoORC operacaoORC, TipoOperacaoORC tipoOperacaoORC, FonteDespesaORC fonteDespesaORC, UnidadeOrganizacional unidadeOrganizacional, String uuid) {
        this.dataMovimento = dataMovimento;
        this.valor = valor;
        this.operacaoORC = operacaoORC;
        this.tipoOperacaoORC = tipoOperacaoORC;
        this.fonteDespesaORC = fonteDespesaORC;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public MovimentoDespesaORC(Date dataMovimento, BigDecimal valor, OperacaoORC operacaoORC, TipoOperacaoORC tipoOperacaoORC, FonteDespesaORC fonteDespesaORC, UnidadeOrganizacional unidadeOrganizacional) {
        this.dataMovimento = dataMovimento;
        this.valor = valor;
        this.operacaoORC = operacaoORC;
        this.tipoOperacaoORC = tipoOperacaoORC;
        this.fonteDespesaORC = fonteDespesaORC;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public OperacaoORC getOperacaoORC() {
        return operacaoORC;
    }

    public void setOperacaoORC(OperacaoORC operacaoORC) {
        this.operacaoORC = operacaoORC;
    }

    public TipoOperacaoORC getTipoOperacaoORC() {
        return tipoOperacaoORC;
    }

    public void setTipoOperacaoORC(TipoOperacaoORC tipoOperacaoORC) {
        this.tipoOperacaoORC = tipoOperacaoORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    @Override
    public String toString() {
        return operacaoORC.toString() + " - " + valor;
    }

    public Boolean isTipoNormal(){
        return TipoOperacaoORC.NORMAL.equals(this.getTipoOperacaoORC());
    }
}
