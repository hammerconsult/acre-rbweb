package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AberturaFechamentoExercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TransporteSaldoContaAuxiliarDetalhada;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Persistencia;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Pedro.
 */
public class AssistenteMalaDiretaRBTrans {

    private Long idMala;
    private List<Long> idsItens;
    private String usuario;

    public Long getIdMala() {
        return idMala;
    }

    public void setIdMala(Long idMala) {
        this.idMala = idMala;
    }

    public List<Long> getIdsItens() {
        return idsItens;
    }

    public void setIdsItens(List<Long> idsItens) {
        this.idsItens = idsItens;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
