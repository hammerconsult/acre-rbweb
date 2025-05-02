package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoExperienciaExtraCurricular {

    private Integer item;
    private Date inicioVigencia;
    private Date fimVigencia;
    private String tipoCadastro;
    private String comissaoCurso;


    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getComissaoCurso() {
        return comissaoCurso;
    }

    public void setComissaoCurso(String comissaoCurso) {
        this.comissaoCurso = comissaoCurso;
    }
}
