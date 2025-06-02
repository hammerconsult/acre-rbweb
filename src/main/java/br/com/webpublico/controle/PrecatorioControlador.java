/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.UnidadeDividaPublica;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * @author Romanini
 */
@ManagedBean(name = "precatorioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-precatorios", pattern = "/precatorios/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/precatorios/edita.xhtml"),
    @URLMapping(id = "editar-precatorios", pattern = "/precatorios/editar/#{precatorioControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/precatorios/edita.xhtml"),
    @URLMapping(id = "ver-precatorios", pattern = "/precatorios/ver/#{precatorioControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/precatorios/visualizar.xhtml"),
    @URLMapping(id = "listar-precatorios", pattern = "/precatorios/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/precatorios/lista.xhtml")})
public class PrecatorioControlador extends DividaPublicaSuperControlador {

    @URLAction(mappingId = "novo-precatorios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-precatorios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-precatorios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/precatorios/";
    }

    @Override
    public void recuperarEditarVer() {
        super.recuperarEditarVer();
    }

    @Override
    public List<ClasseCredor> completaClasseCredor(String parte) {
        DividaPublica ad = ((DividaPublica) selecionado);
        return dividaPublicaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte, ad.getPessoa(), TipoClasseCredor.PRECATORIOS);
    }

    @Override
    public List<SubConta> completarContasFinanceiras(String parte) {
        List<Long> unidades = Lists.newArrayList();
        for (UnidadeDividaPublica udp : selecionado.getUnidades()) {
            unidades.add(udp.getUnidadeOrganizacional().getId());
        }
        if (!unidades.isEmpty()) {
            return dividaPublicaFacade.getSubContaFacade().buscarContasFinanceirasPrecatorios(parte.trim(), sistemaControlador.getExercicioCorrente(), unidades);
        } else {
            FacesUtil.addAtencao("A Conta Financeira é filtrada por Unidade Organizacional e Tipo de Conta(Movimento e Pagamento).");
            return Lists.newArrayList();
        }
    }
}
