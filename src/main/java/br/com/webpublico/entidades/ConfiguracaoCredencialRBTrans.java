package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zaca on 06/01/17.
 */
@GrupoDiagrama(nome = "RBTrans")
@Entity
@Audited
@Table(name = "CONFIGCREDENCIALRBTRANS")
@Etiqueta("Configuração de Assinatura")
public class ConfiguracaoCredencialRBTrans extends SuperEntidade  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data Inicial")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date inicioEm;

    @Etiqueta("Data Final")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date finalEm;

    @Etiqueta("Chancela RBTrans")
    @OneToOne(cascade = CascadeType.ALL)
    private ChancelaRBTrans chancelaRBTrans;


    public ConfiguracaoCredencialRBTrans() {
        super();
        this.chancelaRBTrans = new ChancelaRBTrans();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioEm() {
        return inicioEm;
    }

    public void setInicioEm(Date inicioEm) {
        this.inicioEm = inicioEm;
    }

    public Date getFinalEm() {
        return finalEm;
    }

    public void setFinalEm(Date finalEm) {
        this.finalEm = finalEm;
    }

    public ChancelaRBTrans getChancelaRBTrans() {
        return chancelaRBTrans;
    }

    public void setChancelaRBTrans(ChancelaRBTrans chancelaRBTrans) {
        this.chancelaRBTrans = chancelaRBTrans;
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
        return "ConfiguracaoCredencialRBTrans{" +
                "id=" + id +
                ", inicioEm=" + inicioEm +
                ", finalEm=" + finalEm +
                ", chancelaRBTrans=" + chancelaRBTrans +
                '}';
    }
}
