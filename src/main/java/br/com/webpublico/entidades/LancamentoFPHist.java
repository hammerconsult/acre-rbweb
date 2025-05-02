package br.com.webpublico.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Buzatto on 08/01/2016.
 */
@Entity
public class LancamentoFPHist extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date dataCadastro;
    private Date mesAnoInicial;
    private Date mesAnoFinal;

    public LancamentoFPHist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getMesAnoInicial() {
        return mesAnoInicial;
    }

    public void setMesAnoInicial(Date mesAnoInicial) {
        this.mesAnoInicial = mesAnoInicial;
    }

    public Date getMesAnoFinal() {
        return mesAnoFinal;
    }

    public void setMesAnoFinal(Date mesAnoFinal) {
        this.mesAnoFinal = mesAnoFinal;
    }

    public boolean temId() {
        return id != null;
    }
}
