package br.com.webpublico.entidades;

import br.com.webpublico.enums.PartesCorpoHumano;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 01/09/15.
 */
@Entity
@Audited
@Etiqueta("Reconhecimento de Riscos PPRA")
public class ReconhecimentoRisco extends SuperEntidade implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private Risco risco;
    @Obrigatorio
    @ManyToOne
    private FatorDeRisco fatorDeRisco;
    @Obrigatorio
    @Etiqueta("Fonte Geradora")
    private String fonteGeradora;
    @Obrigatorio
    @Etiqueta("Parte do Corpo")
    @Enumerated(EnumType.STRING)
    private PartesCorpoHumano partesCorpoHumano;
    @Obrigatorio
    @Etiqueta("Meio de Propagação")
    private String meioDePropagacao;
    @Etiqueta("Danos a Saúde")
    private String danoSaude;
    @ManyToOne
    private PPRA ppra;

    public ReconhecimentoRisco() {
    }

    public String getDanoSaude() {
        return danoSaude;
    }

    public void setDanoSaude(String danoSaude) {
        this.danoSaude = danoSaude;
    }

    public FatorDeRisco getFatorDeRisco() {
        return fatorDeRisco;
    }

    public void setFatorDeRisco(FatorDeRisco fatorDeRisco) {
        this.fatorDeRisco = fatorDeRisco;
    }

    public String getFonteGeradora() {
        return fonteGeradora;
    }

    public void setFonteGeradora(String fonteGeradora) {
        this.fonteGeradora = fonteGeradora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeioDePropagacao() {
        return meioDePropagacao;
    }

    public void setMeioDePropagacao(String meioDePropagacao) {
        this.meioDePropagacao = meioDePropagacao;
    }

    public PartesCorpoHumano getPartesCorpoHumano() {
        return partesCorpoHumano;
    }

    public void setPartesCorpoHumano(PartesCorpoHumano partesCorpoHumano) {
        this.partesCorpoHumano = partesCorpoHumano;
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
        return partesCorpoHumano.toString();
    }
}
