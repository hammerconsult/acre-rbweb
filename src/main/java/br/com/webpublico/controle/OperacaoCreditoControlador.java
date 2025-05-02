/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.enums.TipoIndicador;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Romanini
 */
@ManagedBean(name = "operacaoCreditoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-operacao-credito", pattern = "/operacao-credito/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/operacaocredito/edita.xhtml"),
    @URLMapping(id = "editar-operacao-credito", pattern = "/operacao-credito/editar/#{operacaoCreditoControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/operacaocredito/edita.xhtml"),
    @URLMapping(id = "ver-operacao-credito", pattern = "/operacao-credito/ver/#{operacaoCreditoControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/operacaocredito/visualizar.xhtml"),
    @URLMapping(id = "listar-operacao-credito", pattern = "/operacao-credito/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/operacaocredito/lista.xhtml")})
public class OperacaoCreditoControlador extends DividaPublicaSuperControlador {


    @URLAction(mappingId = "novo-operacao-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-operacao-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-operacao-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/operacao-credito/";
    }

    @Override
    public void recuperarEditarVer() {
        super.recuperarEditarVer();
    }

    public void removerLiberacaoRecurso(LiberacaoRecurso liberacaoRecurso) {
        ((DividaPublica) selecionado).getLiberacaoRecursos().remove(liberacaoRecurso);
    }

    public void editarLiberacaoRecurso(LiberacaoRecurso liberacao) {
        liberacaoRecurso = liberacao;
    }

    public void adicionarLiberacaoRecurso() {
        if (validaAdicionarLiberacaoRecurso()) {
            liberacaoRecurso.setDividaPublica(selecionado);
            selecionado.setLiberacaoRecursos(Util.adicionarObjetoEmLista(selecionado.getLiberacaoRecursos(), liberacaoRecurso));
            liberacaoRecurso = new LiberacaoRecurso();
        }
    }

    public boolean validaAdicionarLiberacaoRecurso() {
        Boolean retorno = Boolean.TRUE;
        if (liberacaoRecurso.getValorLiberado().compareTo(BigDecimal.ZERO) <= 0
            && liberacaoRecurso.getValorLiberar().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Informe um valor maior que zero para um dos campos: 'Valor Liberado ou Valor a Liberar.'");
            return Boolean.FALSE;
        }
        return retorno;
    }

    @Override
    public List<Conta> completaContaReceita(String parte) {
        return dividaPublicaFacade.getContaFacade().listaFiltrandoReceitaOperacaoCredito(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    @Override
    public List<ClasseCredor> completaClasseCredor(String parte) {
        DividaPublica ad = ((DividaPublica) selecionado);
        return dividaPublicaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte, ad.getPessoa(), TipoClasseCredor.DIVIDA_PUBLICA);
    }

    public List<SubConta> completaContaFinanceira(String parte) {
        List<Long> listaUni = new ArrayList<Long>();
        for (UnidadeDividaPublica udp : selecionado.getUnidades()) {
            listaUni.add(udp.getUnidadeOrganizacional().getId());
        }
        if (!listaUni.isEmpty()) {
            return dividaPublicaFacade.getSubContaFacade().listaContaFinanceiraOperacaoDeCredito(parte.trim(), sistemaControlador.getExercicioCorrente(), listaUni);
        } else {
            FacesUtil.addAtencao("A Conta Financeira é filtrada por Unidade Organizacional, Tipo de Recurso 'Operação de Crédito' e Tipo de Conta(Movimento ou Pagamento).");
            return new ArrayList<>();
        }
    }

    public void limparFonte() {
        subContaDividaPublica.setFonteDeRecursos(null);
    }

    public List<IndicadorEconomico> completaIndicadorEconomicoMoeda(String parte) {
        return dividaPublicaFacade.getIndicadorEconomicoFacade().listaPorTipoIndicador(parte.trim(), TipoIndicador.MOEDA);
    }

    public List<IndicadorEconomico> completaIndicadorEconomicoPercentual(String parte) {
        return dividaPublicaFacade.getIndicadorEconomicoFacade().listaPorTipoIndicador(parte.trim(), TipoIndicador.INDICE_PERCENTUAL);
    }

    public LiberacaoRecurso getLiberacaoRecurso() {
        return liberacaoRecurso;
    }

    public void setLiberacaoRecurso(LiberacaoRecurso liberacaoRecurso) {
        this.liberacaoRecurso = liberacaoRecurso;
    }
}
