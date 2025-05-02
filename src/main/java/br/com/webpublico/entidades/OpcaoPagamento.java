/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity
@Etiqueta("Opção de Pagamento")
@Audited
public class OpcaoPagamento extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número de Parcelas")
    private Integer numeroParcelas;
    private Boolean permiteAtraso;
    @OneToMany(mappedBy = "opcaoPagamento", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Parcela> parcelas;
    @OneToMany(mappedBy = "opcaoPagamento", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DescontoOpcaoPagamento> descontos;
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor Mínimo")
    private BigDecimal valorMinimoParcela;
    @Enumerated(EnumType.STRING)
    private TipoParcela tipoParcela;
    private Boolean promocional;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data de Verificação de Débito")
    @Temporal(TemporalType.DATE)
    private Date dataVerificacaoDebito;

    public OpcaoPagamento() {
        descontos = Lists.newArrayList();
        parcelas = Lists.newArrayList();
        permiteAtraso = false;
        promocional = false;
    }

    public OpcaoPagamento(Long id){
        this.id = id;
    }

    public Boolean getPromocional() {
        if (promocional == null) {
            promocional = false;
        }
        return promocional;
    }

    public void setPromocional(Boolean promocional) {
        this.promocional = promocional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public List<Parcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<Parcela> parcelas) {
        this.parcelas = parcelas;
    }

    public Boolean getPermiteAtraso() {
        return permiteAtraso;
    }

    public void setPermiteAtraso(Boolean permiteAtraso) {
        this.permiteAtraso = permiteAtraso;
    }

    public List<DescontoOpcaoPagamento> getDescontos() {
        return descontos;
    }

    public void setDescontos(List<DescontoOpcaoPagamento> descontos) {
        this.descontos = descontos;
    }

    public BigDecimal getValorMinimoParcela() {
        return valorMinimoParcela;
    }

    public void setValorMinimoParcela(BigDecimal valorMinimoParcela) {
        this.valorMinimoParcela = valorMinimoParcela;
    }

    public TipoParcela getTipoParcela() {
        return tipoParcela;
    }

    public void setTipoParcela(TipoParcela tipoParcela) {
        this.tipoParcela = tipoParcela;
    }

    public Date getDataVerificacaoDebito() {
        return dataVerificacaoDebito;
    }

    public void setDataVerificacaoDebito(Date dataVerificacaoDebito) {
        this.dataVerificacaoDebito = dataVerificacaoDebito;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
