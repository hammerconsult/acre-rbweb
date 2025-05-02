package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class DiariaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private PropostaConcessaoDiaria propostaConcessaoDiaria;

    public DiariaPortal() {
    }

    public DiariaPortal(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
        super.setTipo(TipoObjetoPortalTransparencia.DIARIA);
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    @Override
    public String toString() {
        return propostaConcessaoDiaria.toString();
    }
}
