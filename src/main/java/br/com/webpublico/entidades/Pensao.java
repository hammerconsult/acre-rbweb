/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.PensaoAtoLegal;
import br.com.webpublico.enums.ModoRateio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@Etiqueta("Pensão Previdenciária")
@GrupoDiagrama(nome = "RecursosHumanos")
public class Pensao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Instituidor")
    @ManyToOne
    private ContratoFP contratoFP;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Número de Cotas")
    private Integer numeroCotas;
    @Tabelavel
    @Etiqueta("Pensionista(s)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pensao")
    private List<Pensionista> listaDePensionistas = new ArrayList<>();
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Remuneração do Instituidor")
    private BigDecimal remuneracaoInstituidor;
    @Etiqueta("Modo de Rateio")
    @Enumerated(EnumType.STRING)
    private ModoRateio modoRateio;
    @Transient
    @Invisivel
    private String tipoPensionista;
    @Transient
    private Integer activeIndex = 0;
    @OneToMany(mappedBy = "pensao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PensaoAtoLegal> pensaoAtoLegal;

    public Pensao() {
        this.pensaoAtoLegal = Lists.newArrayList();
    }

    public Integer getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(Integer activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<PensaoAtoLegal> getPensaoAtoLegal() {
        return pensaoAtoLegal;
    }

    public void setPensaoAtoLegal(List<PensaoAtoLegal> pensaoAtoLegal) {
        this.pensaoAtoLegal = pensaoAtoLegal;
    }

    public String getTipoPensionista() {
        return tipoPensionista;
    }

    public void setTipoPensionista(String tipoPensionista) {
        this.tipoPensionista = tipoPensionista;
    }

    public List<Pensionista> getListaDePensionistas() {
        return listaDePensionistas;
    }

    public void setListaDePensionistas(List<Pensionista> listaDePensionistas) {
        this.listaDePensionistas = listaDePensionistas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Integer getNumeroCotas() {
        return numeroCotas;
    }

    public void setNumeroCotas(Integer numeroCotas) {
        this.numeroCotas = numeroCotas;
    }

    public BigDecimal getRemuneracaoInstituidor() {
        return remuneracaoInstituidor;
    }

    public void setRemuneracaoInstituidor(BigDecimal remuneracaoInstituidor) {
        this.remuneracaoInstituidor = remuneracaoInstituidor;
    }

    public ModoRateio getModoRateio() {
        return modoRateio;
    }

    public void setModoRateio(ModoRateio modoRateio) {
        this.modoRateio = modoRateio;
    }

    @Override
    public String toString() {
        return "Instituidor : " + contratoFP.getMatriculaFP().getPessoa().getNome();
    }

    public boolean isModoRateioManual() {
        return ModoRateio.MANUAL.equals(modoRateio);
    }

    public boolean isModoRateioAutomatico() {
        return ModoRateio.AUTOMATICO.equals(modoRateio);
    }
}
