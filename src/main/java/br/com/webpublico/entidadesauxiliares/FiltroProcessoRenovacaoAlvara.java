package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.entidades.TipoAutonomo;
import br.com.webpublico.enums.TipoAlvara;
import com.google.common.collect.Lists;

import java.util.List;

public class FiltroProcessoRenovacaoAlvara {
    private Exercicio exercicio;
    private String cmcInicial;
    private String cmcFinal;
    private TipoAlvara tipoAlvara;
    private List<NaturezaJuridica> naturezasJuridicas;
    private List<TipoAutonomo> tiposAutonomos;
    private List<VOCnae> cnaes;
    private boolean grauDeRiscoBaixoA;
    private boolean grauDeRiscoBaixoB;
    private boolean mei;

    public FiltroProcessoRenovacaoAlvara() {
        naturezasJuridicas = Lists.newArrayList();
        tiposAutonomos = Lists.newArrayList();
        cnaes = Lists.newArrayList();
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public List<NaturezaJuridica> getNaturezasJuridicas() {
        return naturezasJuridicas;
    }

    public void setNaturezasJuridicas(List<NaturezaJuridica> naturezasJuridicas) {
        this.naturezasJuridicas = naturezasJuridicas;
    }

    public List<TipoAutonomo> getTiposAutonomos() {
        return tiposAutonomos;
    }

    public void setTiposAutonomos(List<TipoAutonomo> tiposAutonomos) {
        this.tiposAutonomos = tiposAutonomos;
    }

    public List<VOCnae> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<VOCnae> cnaes) {
        this.cnaes = cnaes;
    }

    public boolean isGrauDeRiscoBaixoA() {
        return grauDeRiscoBaixoA;
    }

    public void setGrauDeRiscoBaixoA(boolean grauDeRiscoBaixoA) {
        this.grauDeRiscoBaixoA = grauDeRiscoBaixoA;
    }

    public boolean isGrauDeRiscoBaixoB() {
        return grauDeRiscoBaixoB;
    }

    public void setGrauDeRiscoBaixoB(boolean grauDeRiscoBaixoB) {
        this.grauDeRiscoBaixoB = grauDeRiscoBaixoB;
    }

    public boolean isMei() {
        return mei;
    }

    public void setMei(boolean mei) {
        this.mei = mei;
    }
}
