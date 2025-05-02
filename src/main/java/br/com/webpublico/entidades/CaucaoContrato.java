/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoCaucaoContrato;
import br.com.webpublico.enums.TipoMovimentacaoContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Caução de Contrato")
@Inheritance(strategy = InheritanceType.JOINED)
public class CaucaoContrato extends SuperEntidade implements ValidadorEntidade, Comparable<CaucaoContrato> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Movimentação")
    private TipoMovimentacaoContrato tipoMovimentacaoContrato;

    @Temporal(TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Data Caução")
    private Date dataCaucao;

    @Monetario
    @Obrigatorio
    @Tabelavel(ordemApresentacao = 3)
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Tipo da Caução")
    private TipoCaucaoContrato tipo;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável")
    private UnidadeOrganizacional orgao;

    @Transient
    private Operacoes operacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDeCriacao;
    @Transient
    private boolean ativo = false;

    public CaucaoContrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Date getDataCaucao() {
        return dataCaucao;
    }

    public void setDataCaucao(Date dataCaucao) {
        this.dataCaucao = dataCaucao;
    }

    public TipoCaucaoContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoCaucaoContrato tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }


    public TipoMovimentacaoContrato getTipoMovimentacaoContrato() {
        return tipoMovimentacaoContrato;
    }

    public void setTipoMovimentacaoContrato(TipoMovimentacaoContrato tipoMovimentacaoContrato) {
        this.tipoMovimentacaoContrato = tipoMovimentacaoContrato;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }


    public UnidadeOrganizacional getOrgao() {
        return orgao;
    }

    public void setOrgao(UnidadeOrganizacional orgao) {
        this.orgao = orgao;
    }

    @Override
    public String toString() {
        return id == null ? "Dados ainda não gravados" : "Caução de contrato código: " + id;
    }

    public Banco getBanco() {
        return null;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int compareTo(CaucaoContrato o) {
        try {
            return this.dataDeCriacao.compareTo(o.getDataDeCriacao());
        } catch (Exception e) {
            return -1;
        }
    }
}
