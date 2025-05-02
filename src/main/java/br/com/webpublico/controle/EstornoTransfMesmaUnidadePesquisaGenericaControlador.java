package br.com.webpublico.controle;

import br.com.webpublico.enums.StatusPagamento;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 12/03/14
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class EstornoTransfMesmaUnidadePesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    @Override
    public String getComplementoQuery() {
        return " where obj.saldo > 0 "
                + " and obj.unidadeOrganizacional.id in (" + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + ")"
                + " and obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and (obj.statusPagamento = '" + StatusPagamento.DEFERIDO
                +  "' or obj.statusPagamento = '" + StatusPagamento.INDEFERIDO + "')"
                +  " and " + montaCondicao() + montaOrdenacao();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
