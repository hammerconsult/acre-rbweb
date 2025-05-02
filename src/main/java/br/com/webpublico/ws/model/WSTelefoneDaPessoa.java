package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 28/03/14
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class WSTelefoneDaPessoa implements Serializable {
    private Long id;
    private String numero;
    private TipoTelefone tipo;
    private Boolean principal;

    public WSTelefoneDaPessoa() {
    }

    public WSTelefoneDaPessoa(Telefone telefone) {
        this.id = telefone.getId();
        this.numero = telefone.getTelefone();
        this.tipo = telefone.getTipoFone();
        this.principal = telefone.getPrincipal();
    }

    @JsonIgnore
    public static void copiarPropriedadesTelefone(Telefone telefone, WSTelefoneDaPessoa telPortal) {
        telefone.setId(telPortal.getId());
        String s = Util.substituiCaracterEspecial(telPortal.getNumero(), "");
        telefone.setTelefone(s);
        telefone.setTipoFone(telPortal.getTipo());
        telefone.setPrincipal(telPortal.getPrincipal());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoTelefone getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelefone tipo) {
        this.tipo = tipo;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }
}
