/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Publica")
@Etiqueta("Movimento Diário da Dívida Pública ")
@Table(name = "MOVIMENTODIARIODIVIDAPUB")
public class MovimentoDiarioDividaPublica extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private DividaPublica dividaPublica;
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    @Enumerated(EnumType.STRING)
    private OperacaoDiarioDividaPublica operacaoDiarioDividaPublica;
    @Enumerated(EnumType.STRING)
    private Intervalo intervalo;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    private BigDecimal valor;

    public MovimentoDiarioDividaPublica() {
        super();
    }

    public Long getId() {
        return id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public OperacaoDiarioDividaPublica getOperacaoDiarioDividaPublica() {
        return operacaoDiarioDividaPublica;
    }

    public void setOperacaoDiarioDividaPublica(OperacaoDiarioDividaPublica operacaoDiarioDividaPublica) {
        this.operacaoDiarioDividaPublica = operacaoDiarioDividaPublica;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.MovimentoDiarioDividaPublica[ id=" + id + " ]";
    }
}
