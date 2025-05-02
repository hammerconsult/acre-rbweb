package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Buzatto on 19/07/2016.
 */
@Entity
@Audited
@Etiqueta("Prorrogação")
public class Prorrogacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Prorrogado Até")
    private Date prorrogadoAte;

    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Provimento FP")
    private ProvimentoFP provimentoFP;

    @Transient
    @Etiqueta("Matrícula/Contrato")
    @Tabelavel
    private ContratoFP contratoFP;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Etiqueta("Observação")
    private String observacao;

    public Prorrogacao() {
        super();
        provimentoFP = new ProvimentoFP();
    }

    public Prorrogacao(Prorrogacao prorrogacao, ContratoFP contratoFP) {
        this.id = prorrogacao.getId();
        this.prorrogadoAte = prorrogacao.getProrrogadoAte();
        this.provimentoFP = prorrogacao.getProvimentoFP();
        this.contratoFP = contratoFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getProrrogadoAte() {
        return prorrogadoAte;
    }

    public void setProrrogadoAte(Date prorrogadoAte) {
        this.prorrogadoAte = prorrogadoAte;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(prorrogadoAte);
    }
}
