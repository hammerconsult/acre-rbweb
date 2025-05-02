package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;

import java.util.Date;

public class DeprecicaoNotificacao {

    private Long id;
    private Long numero;
    private Date dataLacamento;
    private UnidadeOrganizacional unidadeOrganizacional;
    private UsuarioSistema usuarioSistema;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLacamento() {
        return dataLacamento;
    }

    public void setDataLacamento(Date dataLacamento) {
        this.dataLacamento = dataLacamento;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
