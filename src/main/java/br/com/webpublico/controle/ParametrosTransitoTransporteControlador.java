/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametrosTransitoTransporteFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "parametrosTransitoTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "criarParametroTransitoTransporte",
        pattern = "/rbtrans/parametro-de-transito-transporte/",
        viewId = "/faces/tributario/rbtrans/parametros/transitotransporte/edita.xhtml")
})
public class ParametrosTransitoTransporteControlador extends PrettyControlador<ParametrosTransitoTransporte> implements Serializable, CRUD {

    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;
    private Map<TipoPermissaoRBTrans, ParametrosTransitoTransporte> mapa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterTributo;
    private int maximoTranferencias;

    public ParametrosTransitoTransporteControlador() {
        super(ParametrosTransitoRBTrans.class);
    }

    @URLAction(mappingId = "criarParametroTransitoTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        definirSessao();
        if (isSessao()) {
            try {
                Web.pegaTodosFieldsNaSessao(this);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        } else {
            mapa = Maps.newHashMap();
            for (TipoPermissaoRBTrans tipo : getTiposPermissao()) {
                ParametrosTransitoTransporte param = parametrosTransitoTransporteFacade.recuperarParametroVigentePeloTipo(tipo);
                if (param == null) {
                    param = new ParametrosTransitoTransporte(tipo);
                } else {
                    param.carregarListaTaxas(tipo);
                    if (param.getParametrosTermos().isEmpty()) {
                        param.carregarListaTermos(tipo);
                    }
                    if (param.getVencimentosLicenciamento().isEmpty()) {
                        param.carregarVencimentosLicenciamento();
                    }
                    if (param.getVencimentosCredencial().isEmpty()) {
                        param.carregarVencimentosCredencial();
                    }
                }
                mapa.put(tipo, param);
            }
            maximoTranferencias = buscaMaximoTransferencias();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rbtrans/parametro-de-transito-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            parametrosTransitoTransporteFacade.salvar(mapa);
            FacesUtil.addInfo("Salvo com sucesso!", "");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
        }
    }

    public boolean validaCampos() {
        boolean retorno = true;

        for (TipoPermissaoRBTrans tipo : getTiposPermissao()) {
            if (mapa.get(tipo).getLimitePermissoes() == null || mapa.get(tipo).getLimitePermissoes() <= 0) {
                FacesUtil.addCampoObrigatorio("Informe o Limite de Permissões do Tipo de Permissão de " + tipo.getDescricao());
                retorno = false;
            }
            if (mapa.get(tipo).getLimiteIdade() == null || mapa.get(tipo).getLimiteIdade() <= 0) {
                FacesUtil.addCampoObrigatorio("Informe o Limite de Idade do Veículo do Tipo de Permissão de " + tipo.getDescricao());
                retorno = false;
            }
            if (mapa.get(tipo).getNaturezaJuridica() == null) {
                FacesUtil.addCampoObrigatorio("Informe a Natureza Jurídica do Tipo de Permissão de " + tipo.getDescricao());
                retorno = false;
            }
            if (mapa.get(tipo).getTipoAutonomo() == null) {
                FacesUtil.addCampoObrigatorio("Informe o Tipo de Autônomo do Tipo de Permissão de " + tipo.getDescricao());
                retorno = false;
            }
            for (TaxaTransito tt : mapa.get(tipo).getTaxasTransito()) {
                if (tt.getTributo() == null) {
                    FacesUtil.addCampoObrigatorio("Informe o Tributo de " + tt.getTipoCalculoRBTRans().getDescricao() + " do Tipo de Permissão de " + tipo.getDescricao());
                    retorno = false;
                }
                if (tt.getDivida() == null) {
                    FacesUtil.addCampoObrigatorio("Informe a Dívida de " + tt.getTipoCalculoRBTRans().getDescricao() + " do Tipo de Permissão de " + tipo.getDescricao());
                    retorno = false;
                }
            }
            for (ParametrosTransitoTermos tt : mapa.get(tipo).getParametrosTermos()) {
                if (tt.getTipoDoctoOficial() == null) {
                    FacesUtil.addCampoObrigatorio("Informe o Tipo de Documento Oficial de " + tt.getTipoTermoRBTrans().getDescricao() + " do Tipo de Permissão de " + tipo.getDescricao());
                    retorno = false;
                }
            }
            if (tipo.equals(TipoPermissaoRBTrans.MOTO_TAXI)) {
                if (mapa.get(tipo).getParametrosValoresTransferencias().isEmpty()) {
                    FacesUtil.addCampoObrigatorio("Informe os parâmetros de transferência de " + tipo.getDescricao());
                    retorno = false;
                } else {
                    for (ParametrosValoresTransferencia trans : mapa.get(tipo).getParametrosValoresTransferencias()) {
                        if (trans.getDivida() == null) {
                            FacesUtil.addCampoObrigatorio("Informe a Dívida da " + trans.getQuantidade() + "ª transferência de " + tipo.getDescricao());
                            retorno = false;
                        }
                        if (trans.getTributo() == null) {
                            FacesUtil.addCampoObrigatorio("Informe o Tributo da " + trans.getQuantidade() + "ª transferência de " + tipo.getDescricao());
                            retorno = false;
                        }
                        if (trans.getValor() == null || trans.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                            FacesUtil.addCampoObrigatorio("Informe o Valor da " + trans.getQuantidade() + "ª transferência de " + tipo.getDescricao());
                            retorno = false;
                        }
                    }
                }
            }

            if (!mapa.get(tipo).getVencimentos().isEmpty()) {
                for (DigitoVencimento obj : mapa.get(tipo).getVencimentos()) {
                    if (obj.getDia() == null) {
                        retorno = false;
                        FacesUtil.addCampoObrigatorio("Informe o dia do vencimento referente ao dígito final " + obj.getDigito() + " do parâmetro de " + tipo.getDescricao());
                    } else if (obj.getMes() == null) {
                        retorno = false;
                        FacesUtil.addCampoObrigatorio("Informe o mês do vencimento referente ao dígito final " + obj.getDigito() + " do parâmetro de " + tipo.getDescricao());
                    } else {
                        if (DataUtil.validaMes(obj.getMes())) {
                            if (!DataUtil.validaDiaMes(obj.getDia(), obj.getMes())) {
                                FacesUtil.addOperacaoNaoPermitida("Insira um dia válido para o vencimento da permissão com o dígito final " + obj.getDigito() + " do parâmetro de " + tipo.getDescricao());
                                retorno = false;
                            }
                        } else {
                            FacesUtil.addOperacaoNaoPermitida("Insira um mês válido para o vencimento da permissão com o dígito final " + obj.getDigito() + " do parâmetro de " + tipo.getDescricao());
                            retorno = false;
                        }
                    }
                }
            }
        }
        return retorno;
    }

    @Override
    public AbstractFacade getFacede() {
        return parametrosTransitoTransporteFacade;
    }

    public Map<TipoPermissaoRBTrans, ParametrosTransitoTransporte> getMapa() {
        return mapa;
    }

    public void setMapa(Map<TipoPermissaoRBTrans, ParametrosTransitoTransporte> mapa) {
        this.mapa = mapa;
    }

    public List<TipoPermissaoRBTrans> getTiposPermissao() {
        return Lists.newArrayList(TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, parametrosTransitoTransporteFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, parametrosTransitoTransporteFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<Tributo> completaTributos(String parte) {
        return parametrosTransitoTransporteFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<TipoDoctoOficial> completaDocumentosOficiais(String parte) {
        return parametrosTransitoTransporteFacade.getTipoDoctoOficialFacade().recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial.RBTRANS, parte.trim());
    }

    public List<Divida> completaDividas(String parte) {
        return parametrosTransitoTransporteFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public void novoTributo() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/tributo/novo/");
    }

    public void novaDivida() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/divida/novo/");
    }

    public void novoTipoDocumentoOficial() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/tipo-de-documento-oficial/novo/");
    }

    public String stringDoTipo(TipoPermissaoRBTrans tipoPermissao) {
        return tipoPermissao.name();
    }

    public void setMaximoTranferencias(int maximoTranferencias) {
        this.maximoTranferencias = maximoTranferencias;
    }

    public int getMaximoTranferencias() {
        return maximoTranferencias;
    }

    public void adicionaParamTransferencias(TipoPermissaoRBTrans tipoPermissao) {
        if (maximoTranferencias < buscaMaximoTransferencias()) {
            while (maximoTranferencias < buscaMaximoTransferencias()) {
                mapa.get(tipoPermissao).getParametrosValoresTransferencias().remove(mapa.get(tipoPermissao).getParametrosValoresTransferencias().size() - 1);
            }
        } else {
            for (int i = 1; i <= maximoTranferencias; i++) {
                if (!exiteNumeroTransferencia(mapa.get(tipoPermissao).getParametrosValoresTransferencias(), i)) {
                    ParametrosValoresTransferencia transferencia = new ParametrosValoresTransferencia();
                    transferencia.setQuantidade(i);
                    transferencia.setParametrosTransitoConfiguracao(mapa.get(tipoPermissao));
                    transferencia.setValor(BigDecimal.ZERO);
                    mapa.get(tipoPermissao).getParametrosValoresTransferencias().add(transferencia);
                }
            }
        }
    }

    public boolean exiteNumeroTransferencia(List<ParametrosValoresTransferencia> lista, int numero) {
        for (ParametrosValoresTransferencia transferencia : lista) {
            if (transferencia.getQuantidade() == numero) {
                return true;
            }
        }
        return false;
    }

    public int buscaMaximoTransferencias() {
        int maior = 0;
        if (mapa != null) {
            for (ParametrosValoresTransferencia trans : mapa.get(TipoPermissaoRBTrans.MOTO_TAXI).getParametrosValoresTransferencias()) {
                if (maior < trans.getQuantidade()) {
                    maior = trans.getQuantidade();
                }
            }
        }
        return maior;
    }
}
