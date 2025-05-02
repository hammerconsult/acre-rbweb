package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoInvestimento;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.InvestimentoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 18/10/17.
 */

@ManagedBean(name = "investimentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-investimento", pattern = "/investimento/novo/", viewId = "/faces/financeiro/investimento/edita.xhtml"),
    @URLMapping(id = "editar-investimento", pattern = "/investimento/editar/#{investimentoControlador.id}/", viewId = "/faces/financeiro/investimento/edita.xhtml"),
    @URLMapping(id = "listar-investimento", pattern = "/investimento/listar/", viewId = "/faces/financeiro/investimento/lista.xhtml"),
    @URLMapping(id = "ver-investimento", pattern = "/investimento/ver/#{investimentoControlador.id}/", viewId = "/faces/financeiro/investimento/visualizar.xhtml")
})
public class InvestimentoControlador extends PrettyControlador<Investimento> implements Serializable, CRUD {

    @EJB
    private InvestimentoFacade investimentoFacade;

    public InvestimentoControlador() {
        super(Investimento.class);
    }

    @URLAction(mappingId = "novo-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(investimentoFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidadeOrganizacional(investimentoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setExercicio(investimentoFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<SelectItem> getTiposDeLancamento() {
        return Util.getListSelectItemSemCampoVazio(TipoLancamento.values(), false);
    }

    public List<SelectItem> getTiposDeOperacao() {
        return Util.getListSelectItem(OperacaoInvestimento.values());
    }

    @URLAction(mappingId = "ver-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/investimento/";
    }

    public List<Pessoa> completarPessoa(String filtro) {
        return investimentoFacade.getPessoaFacade().listaTodasPessoasAtivas(filtro.trim());
    }

    public void limparClasse() {
        selecionado.setClasseCredor(null);
    }

    public List<ClasseCredor> completarClasseCredor(String filtro) {
        if (selecionado.getPessoa() != null) {
            return investimentoFacade.getClasseCredorFacade().buscarClassesPorPessoa(filtro.trim(), selecionado.getPessoa());
        }
        return Lists.newArrayList();
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que 0(zero)!");
        }
        ve.lancarException();
    }

    public void definirEventoContabil() {
        try {
            if (selecionado.getTipoLancamento() != null && selecionado.getOperacaoInvestimento() != null) {
                EventoContabil eventoContabil = investimentoFacade.buscarEventoContabil(selecionado);
                if (eventoContabil != null) {
                    selecionado.setEventoContabil(eventoContabil);
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            selecionado.setEventoContabil(null);
        }
    }

    public ParametroEvento getParametroEvento() {
        try {
            ParametroEvento parametroEvento = investimentoFacade.criarParametroEvento(selecionado);
            if (parametroEvento != null) {
                return parametroEvento;
            }
            throw new ExcecaoNegocioGenerica("Parametro evento não encontrado para visualizar o evento contábil.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (RuntimeException ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        return null;
    }


    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                investimentoFacade.salvarNovo(selecionado);
            } else {
                investimentoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return investimentoFacade;
    }
}
