package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author venom
 */
@Entity
@Audited
public class ObraServico extends SuperEntidade implements ValidadorEntidade, Comparable<ObraServico>, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta(value = "Serviço")
    @ManyToOne
    private ServicoObra servicoObra;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Código")
    @Obrigatorio
    private String codigo;

    @Obrigatorio
    @Etiqueta(value = "Quantidade")
    private BigDecimal quantidade;

    @Obrigatorio
    @Etiqueta(value = "Valor Unitário")
    private BigDecimal valorUnitario;

    @ManyToOne
    @Etiqueta(value = "Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Obra")
    private Obra obra;

    @Invisivel
    @ManyToOne
    @Etiqueta(value = "Superior")
    private ObraServico superior;

    @OneToMany(mappedBy = "superior", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraServico> filhos;

    public ObraServico() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        filhos = new ArrayList<>();
    }

    public ObraServico(ObraServico original) {
        this.servicoObra = original.servicoObra;
        this.superior = original.superior;
        this.codigo = original.codigo;
        this.quantidade = original.quantidade;
        this.valorUnitario = original.valorUnitario;
        this.unidadeMedida = original.unidadeMedida;
        this.obra = original.obra;
        this.filhos = new ArrayList<>(original.filhos);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServicoObra getServicoObra() {
        return servicoObra;
    }

    public void setServicoObra(ServicoObra servicoObra) {
        this.servicoObra = servicoObra;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida UnidadeMedida) {
        this.unidadeMedida = UnidadeMedida;
    }

    public BigDecimal getTotalServico() {
        if (this.getFilhos().isEmpty()) {
            return getValorServico();
        }
        BigDecimal valor = BigDecimal.ZERO;
        for (ObraServico os : this.getFilhos()) {
            valor = valor.add(os.getValorServico());
        }
        return valor;
    }

    private BigDecimal getValorServico() {
        return this.quantidade.multiply(this.valorUnitario);
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public ObraServico getSuperior() {
        return superior;
    }

    public void setSuperior(ObraServico superior) {
        this.superior = superior;
    }


    public List<ObraServico> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<ObraServico> filhos) {
        this.filhos = filhos;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + servicoObra.getNome();
        } catch (Exception ex) {
            return codigo;
        }
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {

    }

    @Override
    public int compareTo(ObraServico obraServico) {
        if (this.codigo != null && obraServico != null) {
            return this.codigo.compareTo(obraServico.getCodigo());
        }
        return 0;
    }
}
