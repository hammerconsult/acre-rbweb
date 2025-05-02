package br.com.webpublico.controle;

import br.com.webpublico.entidades.EmpresaIrregular;
import br.com.webpublico.entidades.Irregularidade;
import br.com.webpublico.entidades.MonitoramentoFiscal;
import br.com.webpublico.entidades.SituacaoEmpresaIrregular;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EmpresaIrregularFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-empresa-irregular", pattern = "/rol-empresas-irregulares/novo/", viewId = "/faces/tributario/rolempresasirregulares/edita.xhtml"),
    @URLMapping(id = "editar-empresa-irregular", pattern = "/rol-empresas-irregulares/editar/#{empresaIrregularControlador.id}/", viewId = "/faces/tributario/rolempresasirregulares/edita.xhtml"),
    @URLMapping(id = "ver-empresa-irregular", pattern = "/rol-empresas-irregulares/ver/#{empresaIrregularControlador.id}/", viewId = "/faces/tributario/rolempresasirregulares/visualizar.xhtml"),
    @URLMapping(id = "listar-empresa-irregular", pattern = "/rol-empresas-irregulares/listar/", viewId = "/faces/tributario/rolempresasirregulares/lista.xhtml")
})
public class EmpresaIrregularControlador extends PrettyControlador<EmpresaIrregular> implements Serializable, CRUD {

    @EJB
    private EmpresaIrregularFacade empresaIrregularFacade;

    public EmpresaIrregularControlador() {
        super(EmpresaIrregular.class);
    }

    public void inserirNovaSituacao(boolean salvar) {
        try {
            inserirSituacao(salvar);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void inserirSituacao(boolean salvar) throws ValidacaoException {
        try {
            EmpresaIrregular empresaIrregular = empresaIrregularFacade.buscarEmpresaIrregularParaCadastroEconomico(selecionado.getCadastroEconomico());
            validar(empresaIrregular);
            if (empresaIrregular == null) {
                if (selecionado.getUltimaSituacao() != null && SituacaoEmpresaIrregular.Situacao.INSERIDA.equals(selecionado.getUltimaSituacao().getSituacao())) {
                    selecionado.setSituacao(SituacaoEmpresaIrregular.Situacao.RETIRADA);
                } else {
                    selecionado.setSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA);
                }
                selecionado.setUsuarioSistema(Util.getSistemaControlador().getUsuarioCorrente());
                selecionado = empresaIrregularFacade.adicionarNovaSituacao(selecionado);
            } else {
                if (empresaIrregular.getUltimaSituacao() != null && SituacaoEmpresaIrregular.Situacao.INSERIDA.equals(empresaIrregular.getUltimaSituacao().getSituacao())) {
                    empresaIrregular.setSituacao(SituacaoEmpresaIrregular.Situacao.RETIRADA);
                } else {
                    empresaIrregular.setSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA);
                }
                empresaIrregular.setUsuarioSistema(Util.getSistemaControlador().getUsuarioCorrente());
                empresaIrregular.setMonitoramentoFiscal(selecionado.getMonitoramentoFiscal());
                empresaIrregular.setAnoProtocolo(selecionado.getAnoProtocolo());
                empresaIrregular.setNumeroProtocolo(selecionado.getNumeroProtocolo());
                empresaIrregular.setIrregularidade(selecionado.getIrregularidade());
                empresaIrregular.setMonitoramentoFiscal(selecionado.getMonitoramentoFiscal());
                empresaIrregular.setObservacao(selecionado.getObservacao());
                selecionado = empresaIrregularFacade.adicionarNovaSituacao(empresaIrregular);
            }

            if (salvar) {
                empresaIrregularFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("Situação registrada com sucesso");
                FacesUtil.executaJavaScript("dialogInserirRetirar.hide()");
                selecionado = empresaIrregularFacade.recuperar(selecionado.getId());
            }
        } catch (ValidacaoException ve) {
            throw ve;
        }
    }

    public SituacaoEmpresaIrregular getUltimaSituacao() {
        return selecionado.getUltimaSituacao();
    }

    private void validar(EmpresaIrregular empresaIrregular) {
        ValidacaoException ve = new ValidacaoException();
        if ((Strings.isNullOrEmpty(selecionado.getNumeroProtocolo()) || Strings.isNullOrEmpty(selecionado.getAnoProtocolo())) && selecionado.getMonitoramentoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Os campos Protoloco e/ou Monitoramento Fiscal devem ser informados!");
        }
        if ((empresaIrregular == null && selecionado.getIrregularidade() == null) ||
            (empresaIrregular != null && SituacaoEmpresaIrregular.Situacao.RETIRADA.equals(empresaIrregular.getUltimaSituacao().getSituacao()) && selecionado.getIrregularidade() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo irregularidade deve ser informado");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            inserirSituacao(false);
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    @URLAction(mappingId = "nova-empresa-irregular", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setExercicio(Util.getSistemaControlador().getExercicioCorrente());
        selecionado.setUsuarioSistema(Util.getSistemaControlador().getUsuarioCorrente());
        selecionado.setInseridaEm(Util.getSistemaControlador().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "ver-empresa-irregular", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-empresa-irregular", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public List<Irregularidade> completarIrregularidade(String parte) {
        return empresaIrregularFacade.getIrregularidadeFacade().buscarEmOrdemAlfabeticaParaTiposPorDescircao(parte.trim(), Irregularidade.Tipo.MONITORAMENTO_FISCAL);
    }

    @Override
    public AbstractFacade getFacede() {
        return empresaIrregularFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rol-empresas-irregulares/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<MonitoramentoFiscal> completarMonitoramentoDoCadastroEconomico(String parte) {
        return empresaIrregularFacade.getMonitoramentoFiscalFacade().buscarMonitoramentosDoCadastroEconomico(selecionado.getCadastroEconomico(), parte);
    }
}
