package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 11/08/15.
 */
@Entity
@Audited
@Etiqueta("Identificação Risco PPRA")
public class IdentificacaoRisco extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data da vistoria")
    @Temporal(TemporalType.DATE)
    private Date dataVistoria;
    @Obrigatorio
    @ManyToOne
    private Risco risco;
    @Obrigatorio
    @ManyToOne
    private FatorDeRisco fatorDeRisco;
    @Obrigatorio
    @Etiqueta("Local Vistoriado")
    private String localVistoriado;
    @ManyToOne
    private PPRA ppra;

    public IdentificacaoRisco() {

    }

    public Date getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(Date dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public FatorDeRisco getFatorDeRisco() {
        return fatorDeRisco;
    }

    public void setFatorDeRisco(FatorDeRisco fatorDeRisco) {
        this.fatorDeRisco = fatorDeRisco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalVistoriado() {
        return localVistoriado;
    }

    public void setLocalVistoriado(String localVistoriado) {
        this.localVistoriado = localVistoriado;
    }

    public PPRA getPpra() {
        return ppra;
    }

    public void setPpra(PPRA ppra) {
        this.ppra = ppra;
    }

    public Risco getRisco() {
        return risco;
    }

    public void setRisco(Risco risco) {
        this.risco = risco;
    }

    @Override
    public String toString() {
        return localVistoriado;
    }
}
