package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.StatusPagamento;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 26/11/13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PagamentoEstornoPagamentoPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "pagamentoEstornoControlador", value = "#{pagamentoEstornoControlador}")
    private PagamentoEstornoControlador pagamentoEstornoControlador;

    @Override
    public String getComplementoQuery() {

        List<HierarquiaOrganizacionalDTO> unidadesOrcamentariasDoUsuario = sistemaControlador.getUnidadesOrcamentariasDoUsuario();
        List<Long> listaUnidades = new ArrayList<>();
        String idDasUnidades = " ";
        for (HierarquiaOrganizacionalDTO hierarquiaOrganizacional : unidadesOrcamentariasDoUsuario) {
            listaUnidades.add(hierarquiaOrganizacional.getSubordinadaId());
        }
        for (int i = 0; i < listaUnidades.size(); i++) {
            idDasUnidades += String.valueOf(listaUnidades.get(i)) + ",";
        }

        if (pagamentoEstornoControlador.getCategoriaOrcamentariaPagamento()) {
            return " where obj.saldo > 0 "
                    + " and (obj.status = '" + StatusPagamento.DEFERIDO + "' or obj.status = '" + StatusPagamento.INDEFERIDO + "')"
                    + " and obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId()
                    + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.NORMAL + "'"
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId()
                    + " and " + montaCondicao() + montaOrdenacao();
        } else {
            return " where obj.saldo > 0 "
                    + " and (obj.status = '" + StatusPagamento.DEFERIDO + "' or obj.status = '" + StatusPagamento.INDEFERIDO + "')"
                    + " and obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId()
                    + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO+ "'"
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId()
                    + " and " + montaCondicao() + montaOrdenacao();
        }
    }

    private String removeVirgulaFinal(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public PagamentoEstornoControlador getPagamentoEstornoControlador() {
        return pagamentoEstornoControlador;
    }

    public void setPagamentoEstornoControlador(PagamentoEstornoControlador pagamentoEstornoControlador) {
        this.pagamentoEstornoControlador = pagamentoEstornoControlador;
    }
}
