package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.util.DetailProcessAsync;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AssistenteImpressaoMalaDiretaRBTrans implements DetailProcessAsync {

    private Integer gerados;
    private Integer total;
    private List jaspers;
    private UsuarioSistema usuarioSistema;
    private String descricao;
    private ImprimeDAM imprimeDAM;
    private Long idMala;
    private Integer numFuture;

    public AssistenteImpressaoMalaDiretaRBTrans() {
        total = 0;
        gerados = 0;
        numFuture = 0;
        jaspers = Lists.newArrayList();
    }

    public ImprimeDAM getImprimeDAM() {
        return imprimeDAM;
    }

    public void setImprimeDAM(ImprimeDAM imprimeDAM) {
        this.imprimeDAM = imprimeDAM;
    }

    public Long getIdMala() {
        return idMala;
    }

    public void setIdMala(Long idMala) {
        this.idMala = idMala;
    }

    public Integer getNumFuture() {
        return numFuture;
    }

    public void setNumFuture(Integer numFuture) {
        this.numFuture = numFuture;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getUsuario() {
        return usuarioSistema != null ? usuarioSistema.getLogin() : null;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getTotal() {
        return total;
    }

    @Override
    public Double getPorcentagemExecucao() {
        return 0.0;
    }

    public void setGerados(Integer gerados) {
        this.gerados = gerados;
    }

    public List getJaspers() {
        return jaspers;
    }

    public void setJaspers(List jaspers) {
        this.jaspers = jaspers;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void contar() {
        gerados++;
    }

    public void contar(int quantidade) {
        gerados = gerados + quantidade;
    }

    public int getPorcentagemGerados() {
        if (gerados == null || total == null) {
            return 0;
        }
        return new BigDecimal(((gerados.doubleValue() / total.doubleValue()) * 100)).setScale(0, RoundingMode.UP).intValue();
    }
}
