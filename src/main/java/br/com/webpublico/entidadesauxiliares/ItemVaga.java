/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Cargo;

import java.io.Serializable;

/**
 * @author andre
 */
public class ItemVaga implements Serializable {

    private String hierarquiaOrganizacional;
    private Long vagas;
    private Cargo cargo;

    public ItemVaga() {
    }

    public ItemVaga(String hierarquiaOrganizacional, Long vagas) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        this.vagas = vagas;
    }

    public String getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(String hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Long getVagas() {
        return vagas;
    }

    public void setVagas(Long vagas) {
        this.vagas = vagas;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}
