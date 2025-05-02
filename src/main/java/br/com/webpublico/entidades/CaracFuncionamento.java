/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalculoCaracteristicaFuncionamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.enumeration.TipoCaracteristicaFuncionamentoDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.UFM;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Heinz
 */
@Etiqueta(value = "Características de Funcionamento")
@GrupoDiagrama(nome = "CadastroEconomico")
@Audited
@Entity

public class CaracFuncionamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("ID")
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    private Long codigo;
    @Tabelavel
    @Etiqueta("Descrição Curta")
    @Pesquisavel
    private String descricaoCurta;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Tabelavel
    @Etiqueta("Observação")
    @Pesquisavel
    private String observacao;
    @Tabelavel
    @UFM
    @Etiqueta("Valor Dia")
    @Pesquisavel
    private BigDecimal valorDia;
    @Tabelavel
    @UFM
    @Etiqueta("Valor Mês")
    @Pesquisavel
    private BigDecimal valorMes;
    @Tabelavel
    @UFM
    @Etiqueta("Valor Ano")
    @Pesquisavel
    private BigDecimal valorAno;
    @Tabelavel
    @Etiqueta("Tipo de Característica")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoCaracteristicaFuncionamentoDTO tipo;
    @Tabelavel
    @Etiqueta("Tipo de Cálculo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoCalculoCaracteristicaFuncionamento tipoCalculo;
    @Tabelavel
    @Etiqueta("Tributo")
    @Pesquisavel
    @ManyToOne
    private Tributo tributo;
    private Boolean semPublicidade;
    private Boolean calculaAlvara;
    @Transient
    private Long criadoEm;

    public CaracFuncionamento() {
        calculaAlvara = Boolean.FALSE;
        this.criadoEm = System.nanoTime();
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public void setDescricaoCurta(String descricaoCurta) {
        this.descricaoCurta = descricaoCurta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorAno() {
        return valorAno != null ? valorAno : BigDecimal.ZERO;
    }

    public void setValorAno(BigDecimal valorAno) {
        this.valorAno = valorAno;
    }

    public BigDecimal getValorDia() {
        return valorDia != null ? valorDia : BigDecimal.ZERO;
    }

    public void setValorDia(BigDecimal valorDia) {
        this.valorDia = valorDia;
    }

    public BigDecimal getValorMes() {
        return valorMes != null ? valorMes : BigDecimal.ZERO;
    }

    public void setValorMes(BigDecimal valorMes) {
        this.valorMes = valorMes;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoCaracteristicaFuncionamentoDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoCaracteristicaFuncionamentoDTO tipo) {
        this.tipo = tipo;
    }

    public TipoCalculoCaracteristicaFuncionamento getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculoCaracteristicaFuncionamento tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getSemPublicidade() {
        return semPublicidade != null ? semPublicidade : false;
    }

    public void setSemPublicidade(Boolean semPublicidade) {
        this.semPublicidade = semPublicidade;
    }

    public Boolean getCalculaAlvara() {
        return calculaAlvara;
    }

    public void setCalculaAlvara(Boolean calculaAlvara) {
        this.calculaAlvara = calculaAlvara;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return this.descricaoCurta;
    }

    public boolean temValor() {
        return getValorAno().compareTo(BigDecimal.ZERO) > 0 || getValorMes().compareTo(BigDecimal.ZERO) > 0 || getValorDia().compareTo(BigDecimal.ZERO) > 0;
    }
}
