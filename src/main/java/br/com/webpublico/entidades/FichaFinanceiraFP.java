/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.ColunaAuditoriaRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.OrderBy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
public class FichaFinanceiraFP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @ManyToOne
    @ColunaAuditoriaRH
    @Etiqueta("Vinculo FP")
    private VinculoFP vinculoFP;
    @ManyToOne
    private RecursoFP recursoFP;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(mappedBy = "fichaFinanceiraFP")
    @OrderBy(clause = "eventoFP DESC")
    private List<ItemFichaFinanceiraFP> itemFichaFinanceiraFP;
    @Etiqueta("Crédito Salário Pago: ")
    private Boolean creditoSalarioPago;
    private String autenticidade;
    private String identificador;

    private BigDecimal diasTrabalhados;

    private BigDecimal valorIsento;

    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCriacao;

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public FichaFinanceiraFP() {
        this.identificador = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public List<ItemFichaFinanceiraFP> getItemFichaFinanceiraFP() {
        return itemFichaFinanceiraFP;
    }

    public void setItemFichaFinanceiraFP(List<ItemFichaFinanceiraFP> itemFichaFinanceiraFP) {
        this.itemFichaFinanceiraFP = itemFichaFinanceiraFP;
    }

    public Boolean getCreditoSalarioPago() {
        return creditoSalarioPago != null ? creditoSalarioPago : false;
    }

    public void setCreditoSalarioPago(Boolean creditoSalarioPago) {
        this.creditoSalarioPago = creditoSalarioPago;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValorIsento() {
        return valorIsento;
    }

    public void setValorIsento(BigDecimal valorIsento) {
        this.valorIsento = valorIsento;
    }

    public BigDecimal getDiasTrabalhados() {
        return diasTrabalhados;
    }

    public void setDiasTrabalhados(BigDecimal diasTrabalhados) {
        this.diasTrabalhados = diasTrabalhados;
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}
