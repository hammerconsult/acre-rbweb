package br.com.webpublico.controle.rh.esocial;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Aposentadoria;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.service.S1207Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroS1207Facade;
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
import java.util.List;

@ManagedBean(name = "registroS1207Controlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-registro-s1207-esocial", pattern = "/rh/e-social/registro-s1207-esocial/novo/", viewId = "/faces/rh/esocial/registro-s1207-esocial/edita.xhtml"),
    @URLMapping(id = "listar-registro-s1207-esocial", pattern = "/rh/e-social/registro-s1207-esocial/listar/", viewId = "/faces/rh/esocial/registro-s1207-esocial/lista.xhtml"),
    @URLMapping(id = "ver-registro-s1207-esocial", pattern = "/rh/e-social/registro-s1207-esocial/ver/#{registroS1207Controlador.id}/", viewId = "/faces/rh/esocial/registro-s1207-esocial/visualizar.xhtml")
})
public class RegistroS1207Controlador extends PrettyControlador<RegistroEventoEsocial> implements Serializable, CRUD {

    @EJB
    private RegistroS1207Facade registroS1207Facade;

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private VinculoFP vinculoFP;
    private S1207Service s1207Service;


    public RegistroS1207Controlador() {
        super(RegistroEventoEsocial.class);
        s1207Service = (S1207Service) Util.getSpringBeanPeloNome("s1207Service");
    }

    @Override
    public AbstractFacade getFacede() {
        return registroS1207Facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/e-social/registro-s1207-esocial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-registro-s1207-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novo-registro-s1207-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoClasseESocial(TipoClasseESocial.S1207);
    }

    public List<SelectItem> getMes() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso(), false);
    }

    public List<SelectItem> getEntidade() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial configuracao : registroS1207Facade.getConfiguracaoEmpregadorESocialFacade().lista()) {
            toReturn.add(new SelectItem(configuracao.getEntidade(), configuracao.getEntidade().getPessoaJuridica().getCnpj() + " - " + configuracao.getEntidade().getNome()));
        }
        return toReturn;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public List<SelectItem> buscarMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> buscarTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso(), false);
    }

    public void pesquisarVinculoComFolhaEfetivadaNaCompetencia() {
        try {
            validarPesquisaEnvioEvento();
            selecionado.setItemVinculoFP(getVinculoComFolhaEfetivadaNaCompetencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private List<VinculoFPEventoEsocial> getVinculoComFolhaEfetivadaNaCompetencia() {
        ValidacaoException ve = new ValidacaoException();
        ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = registroS1207Facade.getConfiguracaoEmpregadorESocialFacade().recuperarPorEntidade(selecionado.getEntidade());
        if (configuracaoEmpregadorESocial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração para a Entidade " + selecionado.getEntidade());
            ve.lancarException();
        } else {
            configuracaoEmpregadorESocial = registroS1207Facade.getConfiguracaoEmpregadorESocialFacade().recuperar(configuracaoEmpregadorESocial.getId());
            List<Long> idsUnidade = contratoFPFacade.montarIdOrgaoEmpregador(configuracaoEmpregadorESocial);
            List<VinculoFPEventoEsocial> vinculoFPS = folhaDePagamentoFacade.buscarAposentadosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(selecionado, idsUnidade, vinculoFP);
            if (vinculoFPS != null && !vinculoFPS.isEmpty()) {
                return vinculoFPS;
            }
        }
        FacesUtil.addOperacaoNaoRealizada("Nenhum Servidor Encontrado!");
        return null;
    }

    private void validarPesquisaEnvioEvento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (selecionado.getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Folha.");
        }
        if (selecionado.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNPJ/Entidade.");
        }
        ve.lancarException();
    }

    private void validarEnvioEvento() {
        ValidacaoException ve = new ValidacaoException();
        validarPesquisaEnvioEvento();
        if (selecionado.getItemVinculoFP().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum servidor para envio do evento.");
        }
        if(vinculoFP != null && !(vinculoFP instanceof Aposentadoria)){
            ve.adicionarMensagemDeOperacaoNaoPermitida("Servidor selecionado não é aposentado.");
        }
        ve.lancarException();
    }

    public void enviarEventoS1207() {
        try {
            validarEnvioEvento();
            for (VinculoFPEventoEsocial vinculoEvento : selecionado.getItemVinculoFP()) {
                VinculoFP vinculo = vinculoFPFacade.recuperarSimples(vinculoEvento.getIdVinculo());
                vinculoEvento.setVinculoFP(vinculo);
                s1207Service.enviarS1207(selecionado, vinculo, vinculoEvento.getIdFichaFinanceira());
            }

            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

}
