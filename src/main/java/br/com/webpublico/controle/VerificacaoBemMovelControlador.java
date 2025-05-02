package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOBem;
import br.com.webpublico.entidadesauxiliares.VOVerificacaoBemMovel;
import br.com.webpublico.enums.SituacaoVerificacaoBemMovel;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VerificacaoBemMovelFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ManagedBean(name = "verificacaoBemMovelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaVerficacaoBemMovel", pattern = "/verificacao-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/verificacao-bem-movel/edita.xhtml"),
    @URLMapping(id = "editarVerficacaoBemMovel", pattern = "/verificacao-bem-movel/editar/#{verificacaoBemMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/verificacao-bem-movel/edita.xhtml"),
    @URLMapping(id = "listarVerficacaoBemMovel", pattern = "/verificacao-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/verificacao-bem-movel/lista.xhtml"),
    @URLMapping(id = "verVerficacaoBemMovel", pattern = "/verificacao-bem-movel/ver/#{verificacaoBemMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/verificacao-bem-movel/visualizar.xhtml")
})
public class VerificacaoBemMovelControlador extends PrettyControlador<VerificacaoBemMovel> implements Serializable, CRUD {

    @EJB
    private VerificacaoBemMovelFacade facade;
    private VOBem voBem;
    private Bem bem;

    public VerificacaoBemMovelControlador() {
        super(VerificacaoBemMovel.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/verificacao-bem-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaVerficacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVerificacao(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "editarVerficacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditar();
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasEspecificas();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e){
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    public void concluir() {
        try {
            Util.validarCampos(selecionado);
            validarBensSelecionados();
            validarRegrasEspecificas();
            selecionado = facade.concluirVerificacao(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Verificação concluída com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e){
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.verificarSeExisteVerificacaoBemParaMesmoMes(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma verificação de bem para a unidade e mês selecionado.");
        }
        ve.lancarException();
    }

    private void validarBensSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBensVerificacao().isEmpty() && selecionado.getBensVerificacao() != null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um bem para continuar a operação.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "verVerficacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<Bem> completarBem(String parte) {
        if (selecionado.getHierarquiaOrganizacional() == null) {
            return new ArrayList();
        }
        List<Bem> bens = facade.getBemFacade().buscarBensPorAdministrativaEstadoBemEstornadoAndBaixado(parte.trim(),
            selecionado.getHierarquiaOrganizacional().getSubordinada());
        if (bens.isEmpty()) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "Nenhum bem encontrado para a unidade administrativa " + selecionado.getHierarquiaOrganizacional().toString() + ".");
        }
        return bens;
    }

    private void recuperarEditar() {
        for (ItemVerificacaoBemMovel itemB : selecionado.getBensVerificacao()) {
            EstadoBem ultimoEstado = facade.getBemFacade().recuperarUltimoEstadoDoBem(itemB.getBem());
            itemB.getBem().setUltimoEstado(ultimoEstado);
            itemB.getBem().setAdministrativa(getCodigoDescricaoHierarquia(ultimoEstado.getDetentoraAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
            itemB.getBem().setOrcamentaria(getCodigoDescricaoHierarquia(ultimoEstado.getDetentoraOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
        }
    }

    public void recuperarBem() {
        if (bem != null) {
            EstadoBem ultimoEstado = facade.getBemFacade().recuperarUltimoEstadoDoBem(bem);
            bem.setUltimoEstado(ultimoEstado);
            bem.setAdministrativa(getCodigoDescricaoHierarquia(ultimoEstado.getDetentoraAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
            bem.setOrcamentaria(getCodigoDescricaoHierarquia(ultimoEstado.getDetentoraOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
        }
    }

    private String getCodigoDescricaoHierarquia(UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipoHo) {
        return facade.getHierarquiaOrganizacionalFacade().buscarCodigoDescricaoHierarquia(
            tipoHo.name(),
            unidade,
            selecionado.getInicioVerificacao());
    }

    public void adicionarBemMovelVerificado() {
        try {
            validarBensMoveis();
            ItemVerificacaoBemMovel item = new ItemVerificacaoBemMovel();
            item.setBem(bem);
            item.setVerificacaoBemMovel(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getBensVerificacao(), item);
            limparDadosBem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarBensMoveis() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemVerificacaoBemMovel itemBem : selecionado.getBensVerificacao()) {
            if (itemBem.getBem().equals(bem)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Bem Móvel: " + bem.getDescricaoParaAutoComplete() + " já está na lista de Bens Verificados.");
            }
        }
        ve.lancarException();
    }

    public void gerarRelatorioBensVerificados() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            String arquivoJasper = "RelatorioBensMoveisVerificados.jasper";
            abstractReport.setGeraNoDialog(true);
            HashMap parameters = getHashMap(abstractReport);
            parameters.put("NOMERELATORIO", "RELATÓRIO DE BENS MÓVEIS VERIFICADOS");
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(montarConsultaSqlVerificado());
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioBensNaoVerificados() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            String arquivoJasper = "RelatorioBensMoveisVerificados.jasper";
            abstractReport.setGeraNoDialog(true);
            HashMap parameters = getHashMap(abstractReport);
            parameters.put("NOMERELATORIO", "RELATÓRIO DE BENS MÓVEIS NÃO VERIFICADOS");
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(montarConsultaSqlNaoVerificado());
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private HashMap getHashMap(AbstractReport abstractReport) {
        HashMap parameters = new HashMap();
        parameters.put("MODULO", "Patrimônio");
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        parameters.put("BRASAO", abstractReport.getCaminhoImagem());
        parameters.put("SECRETARIA", selecionado.getHierarquiaOrganizacional().getDescricao().toUpperCase());
        parameters.put("UNIDADEORGANIZACIONAL", selecionado.getHierarquiaOrganizacional().getDescricaoHierarquia());
        parameters.put("DATACONCLUSAO", selecionado.getDataConclusao());
        parameters.put("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome());
        return parameters;
    }

    public boolean isEditarPesquisaGenerica(Object obj){
        return !SituacaoVerificacaoBemMovel.FINALIZADA.equals(((VerificacaoBemMovel) obj).getSituacao());
    }

    private List<VOVerificacaoBemMovel> montarConsultaSqlVerificado() {
        return facade.buscarDadosBensVerificados(selecionado);
    }

    private List<VOVerificacaoBemMovel> montarConsultaSqlNaoVerificado() {
        return facade.buscarDadosBensNaoVerificados(selecionado);
    }

    public boolean hasBensVerificados() {
        return selecionado.getBensVerificacao() != null && !selecionado.getBensVerificacao().isEmpty();
    }

    public void removerBem(ItemVerificacaoBemMovel itemVerificacaoBemMovel) {
        selecionado.getBensVerificacao().remove(itemVerificacaoBemMovel);
    }

    public void limparDadosBem() {
        setBem(null);
    }

    public boolean isEmElaboracao() {
        return SituacaoVerificacaoBemMovel.EM_ELABORACAO.equals(selecionado.getSituacao());
    }

    public boolean isFinalizada() {
        return SituacaoVerificacaoBemMovel.FINALIZADA.equals(selecionado.getSituacao());
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public VOBem getVoBem() {
        return voBem;
    }

    public void setVoBem(VOBem voBem) {
        this.voBem = voBem;
    }
}
