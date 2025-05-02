package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil - Retencao")
@Table(name = "RETENCAOINTEGRACAORHCONT")
public class RetencaoIntegracaoRHContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private IntegracaoRHContabil integracaoRHContabil;
    @ManyToOne
    private ItemIntegracaoRHContabil itemIntegracaoRHContabil;
    @Obrigatorio
    @Etiqueta("Conta Extraorçamentária")
    @ManyToOne
    private Conta contaExtraorcamentaria;
    @Tabelavel
    @Etiqueta("Conta Financeira")
    @ManyToOne
    @Obrigatorio
    private SubConta subConta;
    @Enumerated(EnumType.STRING)
    private TipoContabilizacao tipoContabilizacao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    @Obrigatorio
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "retencao")
    private List<ServidorRetencaoRHContabil> servidores;
    @Transient
    private String recursoRH;

    public RetencaoIntegracaoRHContabil() {
        this.valor = BigDecimal.ZERO;
        this.servidores = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntegracaoRHContabil getIntegracaoRHContabil() {
        return integracaoRHContabil;
    }

    public void setIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil) {
        this.integracaoRHContabil = integracaoRHContabil;
    }

    public Conta getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(Conta contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public TipoContabilizacao getTipoContabilizacao() {
        return tipoContabilizacao;
    }

    public void setTipoContabilizacao(TipoContabilizacao tipoContabilizacao) {
        this.tipoContabilizacao = tipoContabilizacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ServidorRetencaoRHContabil> getServidores() {
        return servidores;
    }

    public void setServidores(List<ServidorRetencaoRHContabil> servidores) {
        this.servidores = servidores;
    }

    public ItemIntegracaoRHContabil getItemIntegracaoRHContabil() {
        return itemIntegracaoRHContabil;
    }

    public void setItemIntegracaoRHContabil(ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        this.itemIntegracaoRHContabil = itemIntegracaoRHContabil;
    }

    public String getRecursoRH() {
        return recursoRH;
    }

    public void setRecursoRH(String recursoRH) {
        this.recursoRH = recursoRH;
    }
}
