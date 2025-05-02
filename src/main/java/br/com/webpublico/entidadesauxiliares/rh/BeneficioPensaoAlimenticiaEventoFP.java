package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.BeneficioPensaoAlimenticia;
import br.com.webpublico.entidades.EventoFP;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author peixe on 25/11/2015  13:49.
 */
public class BeneficioPensaoAlimenticiaEventoFP {
    private BeneficioPensaoAlimenticia beneficiario;
    private List<EventoFP> eventos;

    public BeneficioPensaoAlimenticiaEventoFP() {
    }

    public BeneficioPensaoAlimenticiaEventoFP(BeneficioPensaoAlimenticia beneficiario, List<EventoFP> eventos) {
        this.beneficiario = beneficiario;
        this.eventos = eventos;
    }

    public void adicionarEvento(EventoFP e) {
        if (eventos == null) {
            eventos = Lists.newArrayList();
        }
        if (!eventos.contains(e)) {
            eventos.add(e);
        }

    }

    public BeneficioPensaoAlimenticia getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(BeneficioPensaoAlimenticia beneficiario) {
        this.beneficiario = beneficiario;
    }

    public List<EventoFP> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoFP> eventos) {
        this.eventos = eventos;
    }

    @Override
    public String toString() {
        return beneficiario + " - " + eventos;
    }
}
