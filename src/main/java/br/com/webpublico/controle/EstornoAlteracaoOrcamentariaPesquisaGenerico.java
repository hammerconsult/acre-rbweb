package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusAlteracaoORC;
import br.com.webpublico.enums.StatusPagamento;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 06/07/15
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class EstornoAlteracaoOrcamentariaPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    @Override
    public String getComplementoQuery() {
        return " where obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and obj.status = 'DEFERIDO'"
                + " and " + montaCondicao() + montaOrdenacao();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
