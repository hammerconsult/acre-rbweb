package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtributosCalculoAmbiental;
import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.ParametrosCalcAmbiental;
import br.com.webpublico.enums.TipoLicencaAmbiental;
import br.com.webpublico.enums.TipoLocalizacao;
import br.com.webpublico.enums.TipoMateriaPrima;
import br.com.webpublico.enums.TipoSimNao;
import br.com.webpublico.enums.tributario.AtributoParamAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametrosCalcAmbientalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroCalcAmbiental", pattern = "/parametro-calc-ambiental/novo/", viewId = "/faces/tributario/alvara/parametros/calculoambiental/edita.xhtml"),
    @URLMapping(id = "editaParametroCalcAmbiental", pattern = "/parametro-calc-ambiental/editar/#{parametrosCalcAmbientalControlador.id}/", viewId = "/faces/tributario/alvara/parametros/calculoambiental/edita.xhtml"),
    @URLMapping(id = "verParametroCalcAmbiental", pattern = "/parametro-calc-ambiental/ver/#{parametrosCalcAmbientalControlador.id}/", viewId = "/faces/tributario/alvara/parametros/calculoambiental/visualizar.xhtml"),
    @URLMapping(id = "listarParametroCalcAmbiental", pattern = "/parametro-calc-ambiental/listar/", viewId = "/faces/tributario/alvara/parametros/calculoambiental/lista.xhtml")
})
public class ParametrosCalcAmbientalControlador extends PrettyControlador<ParametrosCalcAmbiental> implements CRUD {
    @EJB
    private ParametrosCalcAmbientalFacade facade;
    private AtributosCalculoAmbiental atributosCalculoAmbiental;

    public ParametrosCalcAmbientalControlador() {
        super(ParametrosCalcAmbiental.class);
        atributosCalculoAmbiental = new AtributosCalculoAmbiental();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-calc-ambiental/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AtributosCalculoAmbiental getAtributosCalculoAmbiental() {
        return atributosCalculoAmbiental;
    }

    public void setAtributosCalculoAmbiental(AtributosCalculoAmbiental atributosCalculoAmbiental) {
        this.atributosCalculoAmbiental = atributosCalculoAmbiental;
    }

    @Override
    @URLAction(mappingId = "novoParametroCalcAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editaParametroCalcAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        ordenarParametros();
    }

    @Override
    @URLAction(mappingId = "verParametroCalcAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        ordenarParametros();
    }

    @Override
    public void salvar() {
        try {
            validarParametro();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar Parâmetro.", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Parâmetro. Detalhes: " + e.getMessage());
        }
    }

    private void validarParametro() {
        ValidacaoException ve = new ValidacaoException();
        validar(ve, selecionado.getCnae() == null, "O campo CNAE deve ser informado.");
        validar(ve, selecionado.getLicencaAmbiental() == null, "O campo Licença Ambiental " +
            "deve ser informado.");
        ve.lancarException();
    }

    public List<CNAE> buscarCnaes(String parte) {
        return facade.buscarCnaesCalcAmbiental(parte);
    }

    public void validarCnaeAndLicenca() {
        if(selecionado.getCnae() == null) {
            selecionado.setLicencaAmbiental(null);
            FacesUtil.addOperacaoNaoRealizada("Informe o CNAE primeiro.");
        } else {
            if(selecionado.getLicencaAmbiental() != null) {
                ParametrosCalcAmbiental parametro = facade.buscarParametroPorCnaeAndTipoLicenca(selecionado.getCnae().getId(), selecionado.getLicencaAmbiental());

                if (selecionado != null && parametro != null && ((selecionado.getId() != null && parametro.getId() != null && !selecionado.getId().equals(
                    parametro.getId())) || (isOperacaoNovo() && selecionado.getCnae() != null && parametro.getCnae() != null && selecionado.getLicencaAmbiental() != null
                    && parametro.getLicencaAmbiental() != null && selecionado.getCnae().getId().equals(parametro.getCnae().getId()) && selecionado.
                    getLicencaAmbiental().equals(parametro.getLicencaAmbiental())))) {

                    FacesUtil.addOperacaoNaoRealizada("O CNAE : " + selecionado.getCnae().getCodigoCnae() + " - " + selecionado.getCnae().
                        getDescricaoReduzida() + ", com Licença Ambiental: " + selecionado.getLicencaAmbiental().getDescricao() + " " +
                        " já possuí parâmetro configurado.");

                    selecionado.setLicencaAmbiental(isOperacaoNovo() ? null : parametro.getLicencaAmbiental());
                }
            }
        }
    }

    public List<SelectItem> montarAtributos() {
        return Util.getListSelectItem(AtributoParamAmbiental.values(), false);
    }

    public List<SelectItem> montarTiposMateriaPrima() {
        return Util.getListSelectItem(TipoMateriaPrima.values(), false);
    }

    public List<SelectItem> montarTipoLocalizacao() {
        return Util.getListSelectItem(TipoLocalizacao.values(), false);
    }

    public List<SelectItem> montarTiposLicencaAmbiental() {
        return Util.getListSelectItem(TipoLicencaAmbiental.values(), false);
    }

    public List<SelectItem> montarTiposSimNao() {
        return Util.getListSelectItem(TipoSimNao.values(), false);
    }

    public void adicionarAtributo() {
        try {
            validarAtributo();
            atributosCalculoAmbiental.setParametroCalcAmbiental(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getAtributosCalculo(), atributosCalculoAmbiental);

            ordenarParametros();

            atributosCalculoAmbiental = new AtributosCalculoAmbiental();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void ordenarParametros() {
        Collections.sort(selecionado.getAtributosCalculo(), new Comparator<AtributosCalculoAmbiental>() {
            @Override
            public int compare(AtributosCalculoAmbiental o1, AtributosCalculoAmbiental o2) {
                return ComparisonChain.start().compare(o1.getAtributo().getOrdenacao(), o2.getAtributo().getOrdenacao()).result();
            }
        });
    }

    private void validarAtributo() {
        ValidacaoException ve = new ValidacaoException();
        validarAtributosBasicos(ve);
        validarValoresAtributos(ve);
        ve.lancarException();
    }

    private void validarAtributosBasicos(ValidacaoException ve) {
        validar(ve, atributosCalculoAmbiental.getAtributo() == null, "O campo Atributo deve ser informado.");

        if (atributosCalculoAmbiental.getAtributo() != null) {
            if (isCampoValor(atributosCalculoAmbiental.getAtributo())) {
                validar(ve, atributosCalculoAmbiental.getValorInicial() == null, "O campo Valor Inicial deve ser Informado.");
                validar(ve, atributosCalculoAmbiental.getValorFinal() == null, "O campo Valor Final deve ser Informado.");
                validar(ve, (atributosCalculoAmbiental.getValorInicial() != null && atributosCalculoAmbiental.getValorFinal() != null) &&
                    atributosCalculoAmbiental.getValorInicial().compareTo(atributosCalculoAmbiental.getValorFinal()) > 0, "O Valor Final deve ser " +
                    "maior ou igual ao Valor Inicial.");
            }
            validar(ve, AtributoParamAmbiental.MATERIA_PRIMA.equals(atributosCalculoAmbiental.getAtributo()) && atributosCalculoAmbiental.
                getTipoMateriaPrima() == null, "O campo Tipo de Matéria Prima deve ser informado.");

            validar(ve, AtributoParamAmbiental.LOCALIZACAO.equals(atributosCalculoAmbiental.getAtributo()) && atributosCalculoAmbiental.
                getTipoLocalizacao() == null, "O campo Tipo de Localização deve ser informado.");

            validar(ve, isCampoSimNao(atributosCalculoAmbiental.getAtributo()) && atributosCalculoAmbiental.getTipoSimNao() == null,
                "O campo \"" + atributosCalculoAmbiental.getAtributo().getDescricao() + "\" deve ser informado.");

            validar(ve, ((isCampoSimNao(atributosCalculoAmbiental.getAtributo()) && atributosCalculoAmbiental.getTipoSimNao() != null) ||
                    isCampoValor(atributosCalculoAmbiental.getAtributo()) || AtributoParamAmbiental.MATERIA_PRIMA.equals(atributosCalculoAmbiental.getAtributo()) ||
                    AtributoParamAmbiental.LOCALIZACAO.equals(atributosCalculoAmbiental.getAtributo())) && atributosCalculoAmbiental.getValoracao() == null,
                "O campo Valoração deve ser Informado.");
        }
    }

    private void validar(ValidacaoException ve, boolean condicao, String mensagem) {
        if (condicao) {
            ve.adicionarMensagemDeCampoObrigatorio(mensagem);
        }
    }

    private void validarValoresAtributos(ValidacaoException ve) {
        if (ve.getMensagens().isEmpty()) {
            for (AtributosCalculoAmbiental atributoCalculo : selecionado.getAtributosCalculo()) {

                if (atributoCalculo.getAtributo().equals(atributosCalculoAmbiental.getAtributo()) && !selecionado.getAtributosCalculo().contains(atributosCalculoAmbiental)) {

                    validar(ve, (isCampoValor(atributoCalculo.getAtributo())) && (atributoCalculo.getValorInicial() != null && atributoCalculo.getValorFinal() != null &&
                            atributosCalculoAmbiental.getValorInicial() != null && atributosCalculoAmbiental.getValorFinal() != null)
                            && ((atributosCalculoAmbiental.getValorInicial().compareTo(atributoCalculo.getValorInicial()) >= 0 && atributosCalculoAmbiental.getValorInicial().compareTo(atributoCalculo.getValorFinal()) <= 0)
                            || (atributosCalculoAmbiental.getValorFinal().compareTo(atributoCalculo.getValorInicial()) >= 0 && atributosCalculoAmbiental.getValorFinal().compareTo(atributoCalculo.getValorFinal()) <= 0)),

                        "O intervalo de valores informado para \"" + atributoCalculo.getAtributo().getDescricao() +
                            "\" já está entre outro intervalo.");

                    validar(ve, atributosCalculoAmbiental.getTipoMateriaPrima() != null && AtributoParamAmbiental.MATERIA_PRIMA.equals(atributoCalculo.getAtributo()) && atributosCalculoAmbiental.
                        getTipoMateriaPrima().equals(atributoCalculo.getTipoMateriaPrima()), "O tipo Matéria Prima: " + (atributosCalculoAmbiental.getTipoMateriaPrima() != null ?
                        atributosCalculoAmbiental.getTipoMateriaPrima().getDescricao() : "") + " já foi adicionado.");

                    validar(ve, atributosCalculoAmbiental.getTipoLocalizacao() != null && AtributoParamAmbiental.LOCALIZACAO.equals(atributoCalculo.getAtributo()) && atributosCalculoAmbiental.
                        getTipoLocalizacao().equals(atributoCalculo.getTipoLocalizacao()), "O tipo Localização: " + (atributosCalculoAmbiental.getTipoLocalizacao() != null
                        ? atributosCalculoAmbiental.getTipoLocalizacao().getDescricao() : "") + " já foi adicionado.");

                    validar(ve, isCampoSimNao(atributoCalculo.getAtributo()), "O atributo " + atributoCalculo.getAtributo().getDescricao() + " já foi adicionado para esse CNAE.");

                    if (!ve.getMensagens().isEmpty())
                        break;
                }
            }
        }
    }

    public String montarValor(AtributosCalculoAmbiental atributoCalculo) {
        if (isCampoValor(atributoCalculo.getAtributo())) {
            return Util.reaisToString(atributoCalculo.getValorInicial()) + " - " +
                Util.reaisToString(atributoCalculo.getValorFinal());
        } else if (isCampoSimNao(atributoCalculo.getAtributo())) {
            return atributoCalculo.getTipoSimNao().getDescricao();
        } else if (AtributoParamAmbiental.MATERIA_PRIMA.equals(atributoCalculo.getAtributo())) {
            return atributoCalculo.getTipoMateriaPrima().getDescricao();
        }
        return atributoCalculo.getTipoLocalizacao().getDescricao();
    }

    public void editarAtributo(AtributosCalculoAmbiental atributoCalculo) {
        atributosCalculoAmbiental = (AtributosCalculoAmbiental) Util.clonarObjeto(atributoCalculo);
    }

    public void removerAtributo(AtributosCalculoAmbiental atributoCalculo) {
        selecionado.getAtributosCalculo().remove(atributoCalculo);
    }

    public boolean isCampoSimNao(AtributoParamAmbiental atributo) {
        return AtributoParamAmbiental.buscarCamposSimNao().contains(atributo);
    }

    public boolean isCampoValor(AtributoParamAmbiental atributo) {
        return AtributoParamAmbiental.buscarAtributosValor().contains(atributo);
    }
}
