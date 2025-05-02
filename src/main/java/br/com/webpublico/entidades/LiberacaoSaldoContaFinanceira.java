/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Contabil")

@Entity
@Table(name = "LiberacaoSaldoContFinanc")
@Audited
@Etiqueta("Solicitação de Saldo de Conta Financeira")
public class LiberacaoSaldoContaFinanceira implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Número")
    private String numero;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataLiberacao;
    @Obrigatorio
    private TipoLiberacaoFinanceira tipoLiberacaoFinanceira;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Conta Financeira")
    private SubConta contaFinanceira;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Fonte de Recursos")
    private FonteDeRecursos fonteDeRecursos;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Solicitação")
    private SolicitacaoCotaFinanceira solicitacaoCotaFinanceira;
    @Obrigatorio
    private String historico;
    @Obrigatorio
    @Etiqueta("Valor Liberado")
    private BigDecimal valorLiberado;
    @Obrigatorio
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    public LiberacaoSaldoContaFinanceira() {
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public TipoLiberacaoFinanceira getTipoLiberacaoFinanceira() {
        return tipoLiberacaoFinanceira;
    }

    public void setTipoLiberacaoFinanceira(TipoLiberacaoFinanceira tipoLiberacaoFinanceira) {
        this.tipoLiberacaoFinanceira = tipoLiberacaoFinanceira;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public BigDecimal getValorLiberado() {
        return valorLiberado;
    }

    public void setValorLiberado(BigDecimal valorLiberado) {
        this.valorLiberado = valorLiberado;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public SolicitacaoCotaFinanceira getSolicitacaoCotaFinanceira() {
        return solicitacaoCotaFinanceira;
    }

    public void setSolicitacaoCotaFinanceira(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        this.solicitacaoCotaFinanceira = solicitacaoCotaFinanceira;
    }
}
