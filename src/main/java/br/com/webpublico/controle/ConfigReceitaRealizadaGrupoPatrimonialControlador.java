package br.com.webpublico.controle;

import br.com.webpublico.entidades.BensMoveis;
import br.com.webpublico.entidades.ConfigReceitaRealizadaBensMoveis;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigReceitaRealizadaGrupoPatrimonialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-receita-realizada-grupo-patrimonial", pattern = "/configuracao-receita-realizada-grupo-patrimonial/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configreceitarealizadagrupopatrimonial/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-receita-realizada-grupo-patrimonial", pattern = "/configuracao-receita-realizada-grupo-patrimonial/editar/#{configReceitaRealizadaGrupoPatrimonialControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configreceitarealizadagrupopatrimonial/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-receita-realizada-grupo-patrimonial", pattern = "/configuracao-receita-realizada-grupo-patrimonial/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configreceitarealizadagrupopatrimonial/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-receita-realizada-grupo-patrimonial", pattern = "/configuracao-receita-realizada-grupo-patrimonial/ver/#{configReceitaRealizadaGrupoPatrimonialControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configreceitarealizadagrupopatrimonial/visualizar.xhtml")
})
public class ConfigReceitaRealizadaGrupoPatrimonialControlador extends ConfigEventoSuperControlador<ConfigReceitaRealizadaBensMoveis> implements Serializable, CRUD {

    @EJB
    private ConfigReceitaRealizadaGrupoPatrimonialFacade facade;

    public ConfigReceitaRealizadaGrupoPatrimonialControlador() {
        super(ConfigReceitaRealizadaBensMoveis.class);
    }

    @URLAction(mappingId = "novo-configuracao-receita-realizada-grupo-patrimonial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "ver-configuracao-receita-realizada-grupo-patrimonial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-configuracao-receita-realizada-grupo-patrimonial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-receita-realizada-grupo-patrimonial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTipoGrupos() {
        return Util.getListSelectItem(TipoGrupo.tipoGrupoPorClasseDeUtilizacao(BensMoveis.class));
    }

    public List<GrupoBem> buscarGrupoPatrimonial(String s) {
        switch (selecionado.getTipoGrupo()) {
            case BEM_MOVEL_PRINCIPAL:
            case BEM_MOVEL_ALIENAR:
            case BEM_MOVEL_INSERVIVEL:
            case BEM_MOVEL_INTEGRACAO:
                return facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(s, TipoBem.MOVEIS);
            case BEM_IMOVEL_ALIENAR:
            case BEM_IMOVEL_INSERVIVEL:
            case BEM_IMOVEL_INTEGRACAO:
            case BEM_IMOVEL_PRINCIPAL:
                return facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(s, TipoBem.IMOVEIS);
        }
        return null;
    }

    public void limparGrupo() {
        selecionado.setGrupoBem(null);
    }

    public List<Conta> buscarContasDeReceita(String filtro) {
        return facade.getContaFacade().listaFiltrandoReceita(filtro.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasConfiguracaoExistente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração vigente para a Conta de Receita: " + selecionado.getContaReceita());
        }
        ve.lancarException();
    }

    public void encerrarVigencia() {
        try {
            facade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
}
