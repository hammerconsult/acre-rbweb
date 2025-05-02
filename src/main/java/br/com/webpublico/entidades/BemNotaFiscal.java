package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington on 27/10/2017.
 */
@Entity
@Audited
public class BemNotaFiscal extends SuperEntidade implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(BemNotaFiscal.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Bem bem;
    @Obrigatorio
    @Etiqueta("Número da Nota Fiscal")
    private String numeroNotaFiscal;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data da Nota Fiscal")
    private Date dataNotaFiscal;
    @OneToMany(mappedBy = "bemNotaFiscal", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BemNotaFiscalEmpenho> empenhos;
    @ManyToOne
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;

    public BemNotaFiscal() {
        super();
        this.empenhos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public Date getDataNotaFiscal() {
        return dataNotaFiscal;
    }

    public void setDataNotaFiscal(Date dataNotaFiscal) {
        this.dataNotaFiscal = dataNotaFiscal;
    }

    public List<BemNotaFiscalEmpenho> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<BemNotaFiscalEmpenho> empenhos) {
        this.empenhos = empenhos;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }
}
