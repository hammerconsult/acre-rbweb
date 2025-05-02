package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.CategoriasAssuntoLicenciamentoAmbiental;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class ProcessoLicenciamentoAmbientalUnidadeMedida extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedidaAmbiental unidadeMedidaAmbiental;
    @Obrigatorio
    @ManyToOne
    private ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental;
    @Obrigatorio
    @Etiqueta("Valor")
    private BigDecimal valorUnidadeMedidaAmbiental;
    @Obrigatorio
    @Etiqueta("Categoria")
    @Enumerated(EnumType.STRING)
    private CategoriasAssuntoLicenciamentoAmbiental categoria;
    @Etiqueta("Valor UFM da categoria")
    private BigDecimal valorUfmCategoria;

    public ProcessoLicenciamentoAmbientalUnidadeMedida() {

    }

    public ProcessoLicenciamentoAmbientalUnidadeMedida(UnidadeMedidaAmbiental unidadeMedidaAmbiental, ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental, BigDecimal valorUnidadeMedidaAmbiental) {
        this.unidadeMedidaAmbiental = unidadeMedidaAmbiental;
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
        this.valorUnidadeMedidaAmbiental = valorUnidadeMedidaAmbiental;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeMedidaAmbiental getUnidadeMedidaAmbiental() {
        return unidadeMedidaAmbiental;
    }

    public void setUnidadeMedidaAmbiental(UnidadeMedidaAmbiental unidadeMedidaAmbiental) {
        this.unidadeMedidaAmbiental = unidadeMedidaAmbiental;
    }

    public ProcessoLicenciamentoAmbiental getProcessoLicenciamentoAmbiental() {
        return processoLicenciamentoAmbiental;
    }

    public void setProcessoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) {
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
    }

    public BigDecimal getValorUnidadeMedidaAmbiental() {
        return valorUnidadeMedidaAmbiental;
    }

    public void setValorUnidadeMedidaAmbiental(BigDecimal valorUnidadeMedidaAmbiental) {
        this.valorUnidadeMedidaAmbiental = valorUnidadeMedidaAmbiental;
    }

    public CategoriasAssuntoLicenciamentoAmbiental getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriasAssuntoLicenciamentoAmbiental categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValorUfmCategoria() {
        return valorUfmCategoria;
    }

    public void setValorUfmCategoria(BigDecimal valorUfmCategoria) {
        this.valorUfmCategoria = valorUfmCategoria;
    }
}
