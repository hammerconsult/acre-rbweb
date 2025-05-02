package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/01/14
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class ObjetoResultado implements Serializable {
    private VinculoFP vinculoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<ItensResultado> itensResultados;
    private String comentario;
    private boolean fichaWebNaoEncontrada;

    public ObjetoResultado() {
        itensResultados = new LinkedList<>();
        fichaWebNaoEncontrada = false;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public boolean isFichaWebNaoEncontrada() {
        return fichaWebNaoEncontrada;
    }

    public void setFichaWebNaoEncontrada(boolean fichaWebNaoEncontrada) {
        this.fichaWebNaoEncontrada = fichaWebNaoEncontrada;
    }

    public List<ItensResultado> getItensResultados() {
        return itensResultados;
    }

    public void setItensResultados(List<ItensResultado> itensResultados) {
        this.itensResultados = itensResultados;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
