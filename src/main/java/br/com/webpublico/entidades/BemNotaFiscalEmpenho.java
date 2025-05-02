package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Wellington on 27/10/2017.
 */
@Entity
@Audited
public class BemNotaFiscalEmpenho extends SuperEntidade {
    private static Logger logger = LoggerFactory.getLogger(BemNotaFiscalEmpenho.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BemNotaFiscal bemNotaFiscal;
    @Obrigatorio
    @Etiqueta("Número do Empenho")
    private String numeroEmpenho;
    @Obrigatorio
    @Etiqueta("Data do Empenho")
    @Temporal(TemporalType.DATE)
    private Date dataEmpenho;
    @ManyToOne
    private Empenho empenho;

    public BemNotaFiscalEmpenho() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BemNotaFiscal getBemNotaFiscal() {
        return bemNotaFiscal;
    }

    public void setBemNotaFiscal(BemNotaFiscal bemNotaFiscal) {
        this.bemNotaFiscal = bemNotaFiscal;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }
}
