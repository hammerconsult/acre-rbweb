package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AberturaFechamentoExercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TransporteSaldoContaAuxiliarDetalhada;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Persistencia;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by mateus on 18/12/17.
 */
public class AssistenteAberturaFechamentoExercicio extends AssistenteBarraProgresso {

    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    private BarraProgressoItens barraProgressoItens;

    //Transporte Conta Auxiliar
    private List<HierarquiaOrganizacional> listaDeUnidades;
    private List<TransporteSaldoContaAuxiliarDetalhada> contasAuxiliaresTransporte;

    public AssistenteAberturaFechamentoExercicio() {
        this.barraProgressoItens = new BarraProgressoItens();
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public List<HierarquiaOrganizacional> getListaDeUnidades() {
        return listaDeUnidades;
    }

    public void setListaDeUnidades(List<HierarquiaOrganizacional> listaDeUnidades) {
        this.listaDeUnidades = listaDeUnidades;
    }

    public List<TransporteSaldoContaAuxiliarDetalhada> getContasAuxiliaresTransporte() {
        return contasAuxiliaresTransporte;
    }

    public void setContasAuxiliaresTransporte(List<TransporteSaldoContaAuxiliarDetalhada> contasAuxiliaresTransporte) {
        this.contasAuxiliaresTransporte = contasAuxiliaresTransporte;
    }

    public Integer getTotalDeItensDeTodasListas() {
        try {
            Integer retorno = 0;
            List<Field> atributos = Persistencia.getAtributos(this.aberturaFechamentoExercicio.getClass());
            for (Field atributo : atributos) {
                atributo.setAccessible(Boolean.TRUE);
                if (atributo.getType().equals(List.class)) {
                    List lista = (List) atributo.get(this.aberturaFechamentoExercicio);
                    retorno = retorno + lista.size();
                }
            }
            return retorno;
        } catch (Exception e) {
            return 0;
        }
    }
}
