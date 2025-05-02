package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 26/05/14
 * Time: 09:31
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class RequisicaoDeCompraEmpenhoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public String getComplementoQuery() {
        RequisicaoDeCompraControlador requisicaoDeCompraControlador = (RequisicaoDeCompraControlador) Util.getControladorPeloNome("requisicaoDeCompraControlador");

        List<HierarquiaOrganizacionalDTO> unidadesOrcamentariasDoUsuario = super.getSistemaControlador().getUnidadesOrcamentariasDoUsuario();
        List<Long> listaUnidades = new ArrayList<>();
        String idDasUnidades = " ";
        for (HierarquiaOrganizacionalDTO hierarquiaOrganizacional : unidadesOrcamentariasDoUsuario) {
            listaUnidades.add(hierarquiaOrganizacional.getSubordinadaId());
        }
        for (int i = 0; i < listaUnidades.size(); i++) {
            idDasUnidades += String.valueOf(listaUnidades.get(i)) + ",";
        }

        if (requisicaoDeCompraControlador.getSelecionado().getContrato() != null) {
            return " where obj.saldo > 0 "
                    + " and obj.contrato.id = " + requisicaoDeCompraControlador.getSelecionado().getContrato().getId()
                    + " and to_char(obj.dataEmpenho, 'yyyy') = " + "'" + super.getSistemaControlador().getExercicioCorrente() + "'"
                    + " and obj.categoriaOrcamentaria = 'NORMAL'"
                    + " and obj.unidadeOrganizacional.id in (" + super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ") "
                    + " and " + montaCondicao() + montaOrdenacao();
        } else {
            return "where obj.id = 0 and " + montaCondicao() + montaOrdenacao();
        }
    }
}
