package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited

@Etiqueta("Execução Contrato Tipo")
public class ExecucaoContratoTipo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato")
    private ExecucaoContrato execucaoContrato;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Objeto de Compra")
    private TipoObjetoCompra tipoObjetoCompra;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @OneToMany(mappedBy = "execucaoContratoTipo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Fontes")
    private List<ExecucaoContratoTipoFonte> fontes;

    @ManyToOne
    @Etiqueta("Classe Credor")
    private ClasseCredor classeCredor;

    public ExecucaoContratoTipo() {
        super();
        fontes = Lists.newArrayList();
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public List<ExecucaoContratoTipoFonte> getFontes() {
        return fontes;
    }

    public void setFontes(List<ExecucaoContratoTipoFonte> fontes) {
        this.fontes = fontes;
    }

    public BigDecimal getValorTotalReservado() {
        BigDecimal total = BigDecimal.ZERO;
        if (getFontes() != null) {
            for (ExecucaoContratoTipoFonte exTipo : getFontes()) {
                total = total.add(exTipo.getValor());
            }
        }
        return total;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Boolean hasFontes() {
        return fontes != null && !fontes.isEmpty();
    }

    public Boolean isObjetoCompraConsumoOrPermanenteMovel() {
        return tipoObjetoCompra != null && (tipoObjetoCompra.isMaterialConsumo() || tipoObjetoCompra.isMaterialPermanente());
    }

    @Override
    public String toString() {
        try {
            return "Execução: " + execucaoContrato.getNumero() + " - " + " Tipo Objeto de Compra: " + tipoObjetoCompra.getDescricao() + " Valor R$: " + Util.formataValor(valor);
        } catch (NullPointerException e) {
            return "";
        }
    }
}
