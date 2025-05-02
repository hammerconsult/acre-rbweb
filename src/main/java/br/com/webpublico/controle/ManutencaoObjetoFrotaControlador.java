/*
 * Codigo gerado automaticamente em Thu Oct 27 13:45:25 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ManutencaoObjetoFrotaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "manutencaoObjetoFrotaNovo", pattern = "/frota/manutencao/novo/", viewId = "/faces/administrativo/frota/manutencao/edita.xhtml"),
    @URLMapping(id = "manutencaoObjetoFrotaListar", pattern = "/frota/manutencao/listar/", viewId = "/faces/administrativo/frota/manutencao/lista.xhtml"),
    @URLMapping(id = "manutencaoObjetoFrotaEditar", pattern = "/frota/manutencao/editar/#{manutencaoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/manutencao/edita.xhtml"),
    @URLMapping(id = "manutencaoObjetoFrotaVer", pattern = "/frota/manutencao/ver/#{manutencaoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/manutencao/visualizar.xhtml"),
})
public class ManutencaoObjetoFrotaControlador extends PrettyControlador<ManutencaoObjetoFrota> implements Serializable, CRUD {

    @EJB
    private ManutencaoObjetoFrotaFacade manutencaoObjetoFrotaFacade;
    private ConverterAutoComplete converterPeca;
    private PecaInstalada pecaInstalada;
    private Boolean houveLubrificacao = Boolean.FALSE;
    private ConverterAutoComplete converterLubrificante;

    public ManutencaoObjetoFrotaControlador() {
        super(ManutencaoObjetoFrota.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return manutencaoObjetoFrotaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/manutencao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "manutencaoObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
    }

    public void inicializar() {
        incializarAtributosDaTela();
    }

    public void incializarAtributosDaTela() {
        criaNovaPecaInstalada();
        houveLubrificacao = selecionado.getManutencaoObjLubrificacao() != null;
    }

    @URLAction(mappingId = "manutencaoObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        incializarAtributosDaTela();
    }

    @URLAction(mappingId = "manutencaoObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public ManutencaoObjetoFrotaFacade getFacade() {
        return manutencaoObjetoFrotaFacade;
    }

    public PecaInstalada getPecaInstalada() {
        return pecaInstalada;
    }

    public void setPecaInstalada(PecaInstalada pecaInstalada) {
        this.pecaInstalada = pecaInstalada;
    }

    public ConverterAutoComplete getConverterPeca() {
        if (converterPeca == null) {
            converterPeca = new ConverterAutoComplete(PecaObjetoFrota.class, manutencaoObjetoFrotaFacade.getPecaObjetoFrotaFacade());
        }
        return converterPeca;
    }

    public ConverterAutoComplete getConverterLubrificante() {
        if (converterLubrificante == null) {
            converterLubrificante = new ConverterAutoComplete(Material.class, manutencaoObjetoFrotaFacade.getMaterialFacade());
        }
        return converterLubrificante;
    }

    public List<PecaObjetoFrota> completaPeca(String parte) {
        return manutencaoObjetoFrotaFacade.getPecaObjetoFrotaFacade().buscarPecas(parte.trim());
    }

    public void novoItemPecaInstalada() {
        if (pecaInstalada.getPecaObjetoFrota() == null &&
            (pecaInstalada.getDescricao() == null || pecaInstalada.getDescricao().trim().isEmpty())) {
            FacesUtil.addCampoObrigatorio("O campo Peça/Descrição deve ser informado.");
            return;
        } else {
            if (pecaInstalada.getQuantidade() <= 0) {
                FacesUtil.addCampoObrigatorio("A quantidade deve ser informada com valor maior que 0(zero).");
                return;
            }
        }
        adicionaItemOuQuantidade();
        criaNovaPecaInstalada();
    }

    public void removeItemPecaInstalada(PecaInstalada pecaInstalada) {
        selecionado.getPecaInstalada().remove(pecaInstalada);
    }

    public void editarItemPeca(PecaInstalada peca) {
        pecaInstalada = peca;
        selecionado.getPecaInstalada().remove(peca);
    }

    private void criaNovaPecaInstalada() {
        this.pecaInstalada = new PecaInstalada();
        this.pecaInstalada.setFoiSubstituicao(Boolean.FALSE);
        this.pecaInstalada.setQuantidade(0);
    }

    public Boolean getHouveLubrificacao() {
        return houveLubrificacao;
    }

    public void setHouveLubrificacao(Boolean houveLubrificacao) {
        this.houveLubrificacao = houveLubrificacao;
    }

    private void adicionaItemOuQuantidade() {
        pecaInstalada.setManutencaoObjetoFrota(selecionado);
        int index = -1;
        if (selecionado.getPecaInstalada() == null) {
            selecionado.setPecaInstalada(new ArrayList());
            selecionado.getPecaInstalada().add(pecaInstalada);
            return;
        }
        if (pecaInstalada.getFoiSubstituicao()) {
            for (PecaInstalada pecaLaco : selecionado.getPecaInstalada()) {
                if (pecaLaco.getFoiSubstituicao() && pecaLaco.getPecaObjetoFrota().getId().equals(pecaInstalada.getPecaObjetoFrota().getId())) {
                    index = selecionado.getPecaInstalada().indexOf(pecaLaco);
                    int quantidade = selecionado.getPecaInstalada().get(index).getQuantidade();
                    quantidade += pecaInstalada.getQuantidade();
                    selecionado.getPecaInstalada().get(index).setQuantidade(quantidade);
                    break;
                }
            }
        }
        if (index == -1) {
            selecionado.getPecaInstalada().add(pecaInstalada);
        }
    }

    public void alteraStatusHouveLubrificacao() {
        if (this.houveLubrificacao) {
            selecionado.setManutencaoObjLubrificacao(new ManutencaoObjLubrificacao());
            selecionado.getManutencaoObjLubrificacao().setManutencaoObjetoFrota(selecionado);
            selecionado.getManutencaoObjLubrificacao().setQuantidade(BigDecimal.ZERO);
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabViewGeral:autocomplete-lubrificante_input')");
        } else {
            selecionado.setManutencaoObjLubrificacao(null);
        }
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        // Valida a revisão do veículo
        if (selecionado != null) {
            RevisaoObjetoFrota revisao = manutencaoObjetoFrotaFacade.manutencaoRelacionadaEmRevisao((ManutencaoObjetoFrota) selecionado);
            if (revisao != null) {
                FacesUtil.addOperacaoNaoPermitida("Esta manutenção está relacionada a revisão <b>" + revisao.getDescricao() + "</b> realizada em " + new SimpleDateFormat("dd/MM/yyyy").format(revisao.getRealizadoEm()) + " e não poderá ser excluída.");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (this.houveLubrificacao) {
            if (selecionado.getManutencaoObjLubrificacao().getLubrificante() == null ||
                selecionado.getManutencaoObjLubrificacao().getLubrificante().trim().isEmpty()) {
                FacesUtil.addCampoObrigatorio("O campo Lubrificante deve ser informado.");
                retorno = false;
            }
            if (selecionado.getManutencaoObjLubrificacao().getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addCampoObrigatorio("A quantidade de lubrificante utilizado deve ser informada.");
                retorno = false;
            }
        }
        if (selecionado.getRealizadaEm().compareTo(selecionado.getObjetoFrota().getDataAquisicao()) < 0) {
            FacesUtil.addCampoObrigatorio("A Data da Manutenção não pode ser inferior a Data de Aquisição do Veículo ou Equipamento/Máquina.");
            retorno = false;
        }
        return retorno;
    }

    public void novaPeca() {
        Web.navegacao(getCaminhoOrigem(), "/frota/peca/novo/", selecionado);
    }

    public void processaMudancaTipoObjetoFrota() {
        selecionado.setObjetoFrota(null);
    }

    public void processaMudancaFoiSubstituicaoPeca() {
        pecaInstalada.setPecaObjetoFrota(null);
        pecaInstalada.setDescricao("");
    }

    public List<Contrato> completarContratoUnidadeLogada(String filtro) {
        return manutencaoObjetoFrotaFacade.getContratoFacade().listaFiltrandoContrato(filtro, Util.getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
    }
}
