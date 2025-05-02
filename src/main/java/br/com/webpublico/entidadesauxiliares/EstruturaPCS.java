package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.EnquadramentoPCS;
import br.com.webpublico.entidades.ProgressaoPCS;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 17/09/14
 * Time: 16:25
 */
public class EstruturaPCS {
    private CategoriaPCS categoriaPCS;
    private ProgressaoPCS progressaoPCS;
    private Date inicioVigencia;
    private Date finalVigencia;
    private List<CategoriaPCS> categoriaPCSList;
    private List<ProgressaoPCS> progressaoPCSList;
    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valores;

    public EstruturaPCS() {
        categoriaPCSList = new LinkedList<>();
    }

    public EstruturaPCS(CategoriaPCS categoriaPCS, ProgressaoPCS progressaoPCS, Date inicioVigencia, Date finalVigencia) {
        this.categoriaPCS = categoriaPCS;
        this.progressaoPCS = progressaoPCS;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public List<CategoriaPCS> getCategoriaPCSList() {
        return categoriaPCSList;
    }

    public void setCategoriaPCSList(List<CategoriaPCS> categoriaPCSList) {
        this.categoriaPCSList = categoriaPCSList;
    }

    public List<ProgressaoPCS> getProgressaoPCSList() {
        return progressaoPCSList;
    }

    public void setProgressaoPCSList(List<ProgressaoPCS> progressaoPCSList) {
        this.progressaoPCSList = progressaoPCSList;
    }

    public Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> getValores() {
        return valores;
    }

    public void setValores(Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valores) {
        this.valores = valores;
    }
}
