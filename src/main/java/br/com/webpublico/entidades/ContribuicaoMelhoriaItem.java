package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

;

/**
 * Created by William on 30/06/2016.
 */
@Audited
@Entity
public class ContribuicaoMelhoriaItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ContribuicaoMelhoriaLancamento contribuicaoMelhoriaLanc;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    private BigDecimal areaAtingida;
    private String observacao;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContribuicaoMelhoriaLancamento getContribuicaoMelhoriaLanc() {
        return contribuicaoMelhoriaLanc;
    }

    public void setContribuicaoMelhoriaLanc(ContribuicaoMelhoriaLancamento contribuicaoMelhoriaLanc) {
        this.contribuicaoMelhoriaLanc = contribuicaoMelhoriaLanc;
    }

    public BigDecimal getAreaAtingida() {
        return areaAtingida;
    }

    public void setAreaAtingida(BigDecimal areaAtingida) {
        this.areaAtingida = areaAtingida;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
