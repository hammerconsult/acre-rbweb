/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Liquidacao;
import br.com.webpublico.entidades.LiquidacaoEstorno;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adriano Jandre
 */
@ManagedBean
@ViewScoped
public class LiquidacaoEmpenhoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "liquidacaoControlador", value = "#{liquidacaoControlador}")
    private LiquidacaoControlador  liquidacaoControlador;

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

        if (liquidacaoControlador.getCategoriaOrcamentoriaLiquidacao()) {
            return " where obj.saldo > 0 "
                    + " and to_char(obj.dataEmpenho, 'yyyy') = " + "'" + sistemaControlador.getExercicioCorrente() + "'"
                    + " and obj.categoriaOrcamentaria = 'NORMAL'"
                    + " and obj.unidadeOrganizacional.id in (" + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + ") "
                    + " and " + montaCondicao() + montaOrdenacao();
        } else {
            return " where obj.saldo > 0 "
                    + " and to_char(obj.dataEmpenho, 'yyyy') = " + "'" + sistemaControlador.getExercicioCorrente() + "'"
                    + " and obj.categoriaOrcamentaria = 'RESTO'"
                    + " and obj.unidadeOrganizacional.id in (" + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + ") "
                    + " and " + montaCondicao() + montaOrdenacao();
        }
    }

    private String removeUltimoCaracter(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public LiquidacaoControlador getLiquidacaoControlador() {
        return liquidacaoControlador;
    }

    public void setLiquidacaoControlador(LiquidacaoControlador liquidacaoControlador) {
        this.liquidacaoControlador = liquidacaoControlador;
    }
}
