package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ObraFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author venom
 */
@ManagedBean(name = "obraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-obra", pattern = "/obra/novo/", viewId = "/faces/administrativo/obras/obra/edita.xhtml"),
    @URLMapping(id = "editar-obra", pattern = "/obra/editar/#{obraControlador.id}/", viewId = "/faces/administrativo/obras/obra/edita.xhtml"),
    @URLMapping(id = "ver-obra", pattern = "/obra/ver/#{obraControlador.id}/", viewId = "/faces/administrativo/obras/obra/visualizar.xhtml"),
    @URLMapping(id = "listar-obra", pattern = "/obra/listar/", viewId = "/faces/administrativo/obras/obra/lista.xhtml")
})
public class ObraControlador extends PrettyControlador<Obra> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ObraControlador.class);
    @EJB
    private ObraFacade obraFacade;
    private OrdemDeServicoContrato ordemServico;
    private ObraServico obraServico;
    private ObraServico obraServicoMedicao;
    private ObraMedicao obraMedicao;
    private ObraAnotacao obraAnotacao;
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    private ResponsavelTecnicoFiscal responsavelTecnicoFiscalTermo;
    private ObraTermo obraTermoJaImpresso;
    private Empenho empenho;
    private ObraTecnicoFiscal obraTecnicoFiscal;
    private ConverterGenerico converterUnidadeMedida;
    private ConverterGenerico converterServicosDeObra;
    private ConverterAutoComplete converterResponsavel;
    private ConverterAutoComplete converterPessoaFisica;
    private ConverterAutoComplete converterTipoDocumentoAnexo;
    private List<OrdemDeServicoContrato> ordensDeServico;
    private List<AlteracaoContratual> aditivos;
    private List<ObraTecnicoFiscal> responsaveisTecnicos;
    private List<ObraTecnicoFiscal> responsaveisFiscais;
    private boolean editarServico;
    private boolean editarMedicao;
    private String enderecoCI;
    private boolean isEditandoTecnico;
    private Boolean tipoImpressaoTermo = Boolean.FALSE;
    private ObraSituacao obraSituacao;

    public ObraControlador() {
        super(Obra.class);
    }

    private static void atualizaComponentesDoDialog() {
        FacesUtil.atualizarComponente("FormularioDocumentoOficial");
        FacesUtil.executaJavaScript("mostraDocumentoOficial()");
        FacesUtil.executaJavaScript("ajustaImpressaoDocumentoOficial()");
    }

    @Override
    public AbstractFacade getFacede() {
        return obraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/obra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        this.ordemServico = null;
        this.obraServico = null;
        this.obraServicoMedicao = null;
        this.obraMedicao = null;
        this.obraAnotacao = null;
        this.ordensDeServico = new ArrayList<>();
        this.editarServico = false;
        this.editarMedicao = false;
        this.enderecoCI = "";
        this.obraTecnicoFiscal = null;
        this.selecionado.setPrestadoConta(Boolean.FALSE);
        this.responsaveisTecnicos = Lists.newArrayList();
        responsaveisFiscais = Lists.newArrayList();
        this.responsavelTecnicoFiscalTermo = null;
        this.obraTermoJaImpresso = recupearObraTermoAoAcessarATelaDeObras();
        isEditandoTecnico = false;
        obraSituacao = null;
    }

    @Override
    public void salvar() {
        try {
            validarSelecionado();
            if (isOperacaoNovo()) {
                obraFacade.salvarNovoComOrdensServico(selecionado, this.ordensDeServico);
            } else {
                obraFacade.salvarComOrdensServico(selecionado, this.ordensDeServico);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Ocorreu um erro ao salvar a obra: " + ex);
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarSelecionado() {
        selecionado.realizarValidacoes();
        validarObraLOA();
        validarMinimoResponsavelTecnico();
        atribuirResponsaveisTecnicosToSelecionado();
    }

    public void selecionarMedicao(ObraMedicao medicao) {
        this.obraMedicao = medicao;
    }

    private void atribuirResponsaveisTecnicosToSelecionado() {
        for (ObraTecnicoFiscal tecnico : responsaveisTecnicos) {
            Util.adicionarObjetoEmLista(selecionado.getTecnicoFiscais(), tecnico);
        }
        for (ObraTecnicoFiscal fiscal : responsaveisFiscais) {
            Util.adicionarObjetoEmLista(selecionado.getTecnicoFiscais(), fiscal);
        }
    }

    private void validarMinimoResponsavelTecnico() {
        ValidacaoException ve = new ValidacaoException();
        if (CollectionUtils.isEmpty(getResponsaveisTecnicos())) {
            ve.adicionarMensagemDeCampoObrigatorio(" Por favor, informe ao menos um responsável técnico para a obra.");
        }
        ve.lancarException();
    }

    private void validarObraLOA() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLoa() != null && selecionado.getAcaoPPA() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Projeto Atividade deve ser informado.");
        }
        ve.lancarException();
    }

    @Override
    @URLAction(mappingId = "ver-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        ordenarSituacoes();
    }

    @Override
    @URLAction(mappingId = "editar-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        this.obraMedicao = null;
        this.ordensDeServico = new ArrayList<>(selecionado.getContrato().getOrdensDeServico());
        setAditivos(selecionado.getContrato().getAditivos());
        this.editarServico = false;
        this.editarMedicao = false;
        this.obraServicoMedicao = null;
        if (selecionado.getCadastroImobiliario() != null) {
            this.enderecoCI = selecionado.getCadastroImobiliario().getLote().getEndereco();
        }
        if (selecionado.getCadastroImobiliario() != null) {
            this.enderecoCI = selecionado.getCadastroImobiliario().getLote().getEndereco();
        }
        this.obraTecnicoFiscal = null;
        this.ordemServico = null;
        this.obraServico = null;
        this.obraServicoMedicao = null;
        this.obraAnotacao = null;
        this.responsaveisTecnicos = selecionado.getTecnicos();
        this.responsaveisFiscais = selecionado.getFiscais();
        this.responsavelTecnicoFiscalTermo = null;
        this.obraTermoJaImpresso = recupearObraTermoAoAcessarATelaDeObras();
        isEditandoTecnico = false;
        recuperaCadastroImobiliario();
        verInscricaoMapa();
        buscarDotacoesDoContrato();
        ordenarSituacoes();
    }

    public List<SelectItem> getBuscarTodasLOAs() {
        List<LOA> loas = obraFacade.getLoaFacade().buscarTodasLOA();
        List<SelectItem> items = Lists.newArrayList();
        items.add(new SelectItem(null, ""));
        for (LOA loa : loas) {
            items.add(new SelectItem(loa, loa.getLdo().getExercicio().toString()));
        }
        Util.ordenaSelectItem(items);
        return items;
    }

    public List<AcaoPPA> buscarAcaoPPA(String filter) {
        try {
            validarAcaoPPA();
            Exercicio exercicio = getExercicioLoa();
            return obraFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filter, exercicio);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
        return Lists.newArrayList();
    }

    private Exercicio getExercicioLoa() {
        return this.selecionado.getLoa().getLdo().getExercicio();
    }

    private void validarAcaoPPA() {
        ValidacaoException ve = new ValidacaoException();

        if (this.selecionado.getLoa() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada(" Por favor informe a loa para informar o projeto atividade");
        }

        ve.lancarException();
    }

    public String getModalidadeLicitacao() {
        if (selecionado.getContrato().isDeDispensaDeLicitacao()) {
            return selecionado.getContrato().getDispensaDeLicitacao().getModalidade().getDescricao();
        }
        if (selecionado.getContrato().isDeLicitacao()) {
            return selecionado.getContrato().getLicitacao().getModalidadeLicitacao().getDescricao();
        }
        return " ";
    }

    public String getNumeroLicitacao() {
        if (selecionado.getContrato().isDeDispensaDeLicitacao()) {
            return selecionado.getContrato().getDispensaDeLicitacao().getNumeroDaDispensa().toString();
        }
        if (selecionado.getContrato().isDeLicitacao()) {
            return selecionado.getContrato().getLicitacao().getNumeroLicitacao().toString();
        }
        return " ";
    }

    public BigDecimal getTotalValorContrato() {
        try {
            return selecionado.getContrato().getValorTotal();
        } catch (NullPointerException npe) {
            return BigDecimal.ZERO;
        }
    }

    public List<Contrato> completarContrato(String parte) {
        List<Contrato> contratos = obraFacade.getContratoFacade().buscarContratoDisponivelNovaObra(parte.trim());
        if (!contratos.isEmpty()) {
            return contratos;
        }
        FacesUtil.addAtencao("Nenhum contrato encontrado! Este pode ter sido vinculado a outra obra.");
        return contratos;
    }

    public List<PessoaFisica> buscarPessoasAtivas(String filter) {
        return obraFacade.getPessoaFisicaFacade().listaPessoaFisica(filter);
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return obraFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        return new ConverterAutoComplete(CadastroImobiliario.class, obraFacade.getCadastroImobiliarioFacade());
    }

    public void buscarInformacaoContrato() {
        try {
            validarContrato();
            definirContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (this.selecionado.getContrato() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível buscar as informações do contrato, por favor tente novamente");
        }
        ve.lancarException();
    }

    private void definirContrato() {
        selecionado.setContrato(obraFacade.getContratoFacade().recuperar(selecionado.getContrato().getId()));
        setAditivos(selecionado.getContrato().getAditivos());
        definirSubtipoObjetoCompra();
        buscarDotacoesDoContrato();
    }

    private void definirSubtipoObjetoCompra() {
        if (selecionado.getContrato().getObjetoAdequado().getProcessoDeCompra() != null) {
            SolicitacaoMaterial sol = selecionado.getContrato().getObjetoAdequado().getProcessoDeCompra().getSolicitacaoMaterial();
            if (TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA.equals(sol.getTipoSolicitacao()) && sol.getSubTipoObjetoCompra() != null) {
                selecionado.setSubTipoObjetoCompra(sol.getSubTipoObjetoCompra());
            }
        }
    }

    private void buscarDotacoesDoContrato() {
        if (selecionado.getContrato() != null) {
            selecionado.setDotacoes(obraFacade.getContratoFacade().buscarFonteDespesaORCOfContrato(selecionado.getContrato()));
        }
    }

    public void novaOrdemServico() {
        if (selecionado.getContrato() != null) {
            this.ordemServico = new OrdemDeServicoContrato();
            this.ordemServico.setContrato(selecionado.getContrato());
            this.ordemServico.setNumero(getProximoNumero(ordensDeServico));
            this.ordemServico.setOrigem(OrigemOrdemServico.OBRA);
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Nenhum contrato foi selecionado.");
        }
    }

    private Integer getProximoNumero(List<? extends Object> lista) {
        try {
            return lista.size() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public List<SelectItem> getTiposOrdemDeServico() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOrdemDeServicoContrato object : TipoOrdemDeServicoContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public void confirmarOrdemDeServico() {
        if (!validarConfirmacao(this.ordemServico)) {
            return;
        }
        Util.adicionarObjetoEmLista(ordensDeServico, ordemServico);
        cancelarOrdemDeServico();
    }

    public void cancelarOrdemDeServico() {
        this.ordemServico = null;
    }

    private boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public void selecionarOrdemServico(OrdemDeServicoContrato os) {
        this.ordemServico = os;
    }

    public void removerOrdemServico(OrdemDeServicoContrato os) {
        ordensDeServico.remove(os);
    }

    public boolean liberaOperacaoOrdemServico(OrdemDeServicoContrato os) {
        return (os.getOrigem().equals(OrigemOrdemServico.OBRA) && os.getNumero().equals(ordensDeServico.size()));
    }

    public void novoResponsavelTecnico() {
        try {
            validarNovoResponsavelTecnicoAndFiscal();
            this.obraTecnicoFiscal = new ObraTecnicoFiscal();
            this.obraTecnicoFiscal.setTecnicoFiscal(new ResponsavelTecnicoFiscal());
            this.obraTecnicoFiscal.getTecnicoFiscal().setAtribuicao(ObraAtribuicao.RESPONSAVEL_TECNICO);
            this.obraTecnicoFiscal.getTecnicoFiscal().setTipoResponsavel(TipoResponsavelFiscal.TECNICO);
            this.obraTecnicoFiscal.getTecnicoFiscal().setTipoResponsavelTecnico(TipoResponsavelTecnico.CONTRATADO);
            this.obraTecnicoFiscal.getTecnicoFiscal().setTipoContratante(null);
            this.obraTecnicoFiscal.getTecnicoFiscal().setContrato(selecionado.getContrato());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarNovoResponsavelTecnicoAndFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");
        }
        ve.lancarException();
    }

    public void novoResponsavelFiscal() {
        try {
            validarNovoResponsavelTecnicoAndFiscal();
            this.obraTecnicoFiscal = new ObraTecnicoFiscal();
            isEditandoTecnico = false;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public BigDecimal getPercentualTotalObra() {
        try {
            BigDecimal valorMedicoes = selecionado.getValorTotalMedicoes().setScale(4);
            BigDecimal valorObra = getTotalValorContrato().setScale(4);
            BigDecimal divisao = valorMedicoes.divide(valorObra, RoundingMode.HALF_DOWN);
            BigDecimal percentual = divisao.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_DOWN);
            return percentual;
        } catch (Exception ex) {
            return new BigDecimal("00.00");
        }
    }

    public void confirmarResponsavelTecnico() {
        try {
            validarResponsavelTecnico();
            obraTecnicoFiscal.setObra(selecionado);
            setResponsaveisTecnicos(Util.adicionarObjetoEmLista(getResponsaveisTecnicos(), this.obraTecnicoFiscal));
            cancelarResponsavelTecnicoAndFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void confirmarResponsavelFiscal() {
        try {
            validarResponsavelFiscal();
            obraTecnicoFiscal.setObra(selecionado);
            this.setResponsaveisFiscais(Util.adicionarObjetoEmLista(responsaveisFiscais, obraTecnicoFiscal));
            cancelarResponsavelTecnicoAndFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarResponsavelTecnico() {
        ValidacaoException ve = new ValidacaoException();
        ResponsavelTecnicoFiscal responsavelTecnico = obraTecnicoFiscal.getTecnicoFiscal();

        if (TipoResponsavelTecnico.CONTRATADO.equals(responsavelTecnico.getTipoResponsavelTecnico())
            && responsavelTecnico.getResponsavelTecnico().getPessoaFisica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo contratado deve ser informado.");
        }

        if (TipoResponsavelTecnico.CONTRATANTE.equals(responsavelTecnico.getTipoResponsavelTecnico())
            && responsavelTecnico.getContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo servidor deve ser informado.");
        }
        if (StringUtils.isEmpty(responsavelTecnico.getCreaCau())) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo CREA/CAU deve ser informado.");
        }
        validarAtributoComunsResponsavelTecnicoAndFiscal(ve);
        ve.lancarException();
        for (ObraTecnicoFiscal tecnico : getResponsaveisTecnicos()) {
            if (!obraTecnicoFiscal.equals(tecnico)
                && responsavelTecnico.getTipoResponsavelTecnico().equals(tecnico.getTecnicoFiscal().getTipoResponsavelTecnico())) {
                String mensagem = "Responsável Técnico: " + tecnico.getTecnicoFiscal().getResponsavelTecnicoPorTipoResponsavel() + ", está vigente no período informado.";
                if (obraTecnicoFiscal.getTecnicoFiscal().getTipoContratante() == null) {
                    validarVigenciaResponsavelTecnicoAndFiscal(tecnico, mensagem);
                } else {
                    if (obraTecnicoFiscal.getTecnicoFiscal().getTipoContratante().equals(tecnico.getTecnicoFiscal().getTipoContratante())) {
                        validarVigenciaResponsavelTecnicoAndFiscal(tecnico, mensagem);
                    }
                }
            }
        }
    }

    private void validarVigenciaResponsavelTecnicoAndFiscal(ObraTecnicoFiscal tecnicoFiscal, String mensagem) {
        ValidacaoException ve = new ValidacaoException();
        if (tecnicoFiscal.getFimVigencia() == null ||
            (getObraTecnicoFiscal().getInicioVigencia().compareTo(tecnicoFiscal.getFimVigencia()) <= 0)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        ve.lancarException();
    }

    private void validarAtributoComunsResponsavelTecnicoAndFiscal(ValidacaoException ve) {
        if (obraTecnicoFiscal.getTipoArt() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo tipo de ART/RRT deve ser informado.");
        }
        if (StringUtils.isEmpty(obraTecnicoFiscal.getArtRrt())) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo ART/RRT deve ser informado.");
        }
        if (obraTecnicoFiscal.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo início da vigência de ser informado. ");
        }
        if (obraTecnicoFiscal.getInicioVigencia() != null && obraTecnicoFiscal.getFimVigencia() != null) {
            if (obraTecnicoFiscal.getInicioVigencia().after(obraTecnicoFiscal.getFimVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data final de vigência deve ser posterior a data inicial de vigência.");
            }
        }
    }

    private void validarResponsavelFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (obraTecnicoFiscal.getTecnicoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo fiscal deve ser informado.");
        }
        validarAtributoComunsResponsavelTecnicoAndFiscal(ve);
        ve.lancarException();
        for (ObraTecnicoFiscal tecnico : getResponsaveisFiscais()) {
            if (!obraTecnicoFiscal.equals(tecnico)
                && obraTecnicoFiscal.getTecnicoFiscal().equals(tecnico.getTecnicoFiscal())) {
                String mensagem = "Fiscal: " + tecnico.getTecnicoFiscal().getResponsavelTecnicoPorTipoResponsavel() + ", está vigente no período informado.";
                validarVigenciaResponsavelTecnicoAndFiscal(tecnico, mensagem);
            }
        }
    }

    public void cancelarResponsavelTecnicoAndFiscal() {
        this.obraTecnicoFiscal = null;
    }

    public void editarResponsavelTecnico(ObraTecnicoFiscal obraTecnicoFiscal) {
        this.obraTecnicoFiscal = (ObraTecnicoFiscal) Util.clonarObjeto(obraTecnicoFiscal);
    }

    public void editarResponsavelFiscal(ObraTecnicoFiscal obraTecnicoFiscal) {
        this.obraTecnicoFiscal = (ObraTecnicoFiscal) Util.clonarObjeto(obraTecnicoFiscal);
        isEditandoTecnico = true;
    }

    public void removerResponsavelTecnico(ObraTecnicoFiscal tecnicoFiscal) {
        this.getResponsaveisTecnicos().remove(tecnicoFiscal);
    }

    public List<SelectItem> getTiposResponsavelTecnico() {
        return Util.getListSelectItemSemCampoVazio(TipoResponsavelTecnico.values(), false);
    }

    public void definirTipoContratante() {
        if (getObraTecnicoFiscal().getTecnicoFiscal().getTipoResponsavelTecnico() != null &&
            TipoResponsavelTecnico.CONTRATADO.equals(getObraTecnicoFiscal().getTecnicoFiscal().getTipoResponsavelTecnico())) {
            getObraTecnicoFiscal().getTecnicoFiscal().setTipoContratante(null);
            getObraTecnicoFiscal().getTecnicoFiscal().getResponsavelTecnico().setPessoaFisica(null);

        }
        if (getObraTecnicoFiscal().getTecnicoFiscal().getTipoResponsavelTecnico() != null &&
            TipoResponsavelTecnico.CONTRATANTE.equals(getObraTecnicoFiscal().getTecnicoFiscal().getTipoResponsavelTecnico())) {
            getObraTecnicoFiscal().getTecnicoFiscal().setContratoFP(null);
        }
    }

    public List<SelectItem> getTiposContratantes() {
        return Util.getListSelectItemSemCampoVazio(TipoContratante.values(), false);
    }

    public List<SelectItem> getTipoArtRtt() {
        return Util.getListSelectItemSemCampoVazio(TipoArt.values(), false);
    }

    public ConverterAutoComplete getConverterContratoFP() {
        return new ConverterAutoComplete(ContratoFP.class, obraFacade.getContratoFPFacade());
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return obraFacade.getContratoFPFacade().recuperarFiltrandoContratosVigentesEm(parte.trim(), UtilRH.getDataOperacao());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return obraFacade.getAtoLegalFacade().listaFiltrandoAtoLegal(parte.trim());
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        return new ConverterAutoComplete(AtoLegal.class, obraFacade.getAtoLegalFacade());
    }

    public ConverterAutoComplete getConverterContrato() {
        return new ConverterAutoComplete(Contrato.class, obraFacade.getContratoFacade());
    }

    public List<Contrato> completarContratoFirmado(String parte) {
        return obraFacade.getContratoFacade().buscarContratoPorNumeroOrExercicio(parte.trim());
    }

    public List<ProfissionalConfea> completaProfissionalConfea(String parte) {
        return obraFacade.getProfissionalConfeaFacade().listaFiltrando(parte.trim(), "codigo", "tituloMasculino", "tituloFeminino", "tituloAbreviado");
    }

    public ConverterAutoComplete getConverterProfissionalConfea() {
        return new ConverterAutoComplete(ProfissionalConfea.class, obraFacade.getProfissionalConfeaFacade());
    }

    //Serviços e Medições

    public List<SelectItem> getTiposART() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, " "));
        for (TipoArt t : TipoArt.values()) {
            itens.add(new SelectItem(t, t.getDescricao()));
        }
        return itens;
    }

    public void novoObraServico() {
        this.obraServico = new ObraServico();
        this.obraServico.setObra(selecionado);
    }

    public void confirmarObraServico() {
        if (this.obraServico.getServicoObra().getSuperior() == null) {
            setaCodigoServicoAgrupador();
            geraObraServicosAgrupadas();
        }
        if (!validarConfirmacao(this.obraServico)) {
            return;
        }
        selecionado.setServicos(Util.adicionarObjetoEmLista(selecionado.getServicos(), this.obraServico));
        seAdicionaNaListaDoSuperior(this.obraServico);
        for (ObraServico os : this.obraServico.getFilhos()) {
            selecionado.setServicos(Util.adicionarObjetoEmLista(selecionado.getServicos(), os));
        }
        this.obraServico = null;
    }

    public void editarServico() {
        this.editarServico = true;
    }

    public void naoEditarServico() {
        this.editarServico = false;
    }

    public void efetivarServicos() {
        selecionado.setEfetivada(true);
    }

    public boolean liberaBotoesEdicaoServico() {
        if (selecionado.getEfetivada()) {
            return false;
        }
        if (this.editarServico) {
            return false;
        }
        return true;
    }

    public boolean liberaBotaoConfirmaValoresServicos() {
        if (selecionado.getEfetivada()) {
            return false;
        }
        if (this.editarServico) {
            return true;
        }
        return false;
    }

    public boolean liberaEditarServico(ObraServico os) {
        if (this.editarServico && os.getSuperior() != null) {
            return true;
        }
        return false;
    }

    private void seAdicionaNaListaDoSuperior(ObraServico os) {
        ObraServico superior = os.getSuperior();
        if (superior != null) {
            superior.setFilhos(Util.adicionarObjetoEmLista(superior.getFilhos(), os));
        }
    }

    public void setaCodigoServicoAgrupador() {
        Integer inicial = 1;
        for (ObraServico os : selecionado.getServicos()) {
            if (os.getSuperior() == null) {
                inicial++;
            }
        }
        if (inicial < 10) {
            this.obraServico.setCodigo("0" + inicial);
        } else {
            this.obraServico.setCodigo(inicial.toString());
        }
    }

    private void geraObraServicosAgrupadas() {
        this.obraServico.setServicoObra(obraFacade.getServicoObraFacade().recuperar(this.obraServico.getServicoObra().getId()));
        for (ServicoObra so : this.obraServico.getServicoObra().getFilhos()) {
            ObraServico os = new ObraServico();
            os.setServicoObra(so);
            os.setObra(selecionado);
            os.setSuperior(this.obraServico);
            this.obraServico.setFilhos(Util.adicionarObjetoEmLista(this.obraServico.getFilhos(), os));
        }
        setaCodigoServicoAgrupado(this.obraServico);
    }

    private void setaCodigoServicoAgrupado(ObraServico superior) {
        Integer inicio = 0;
        String numero = "";
        for (ObraServico os : superior.getFilhos()) {
            inicio++;
            if (inicio < 10) {
                numero = "0" + inicio;
            } else {
                numero = inicio.toString();
            }
            os.setCodigo(superior.getCodigo() + numero);
        }
    }

    public void cancelarObraServico() {
        this.obraServico = null;
    }

    public List<SelectItem> getServicosDeObra() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ServicoObra object : obraFacade.getServicoObraFacade().listaRecuperados()) {
            if (object.getSuperior() == null) {
                toReturn.add(new SelectItem(object, object.getNome()));
            }
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public ConverterGenerico getConverterServicosDeObra() {
        if (converterServicosDeObra == null) {
            converterServicosDeObra = new ConverterGenerico(ServicoObra.class, obraFacade.getServicoObraFacade());
        }
        return converterServicosDeObra;
    }

    public boolean liberaSelectServicosSuperiores() {
        if (this.obraServico.getServicoObra() == null) {
            return true;
        }
        if (this.obraServico.getServicoObra().getSuperior() == null) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getUnidadeMedida() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeMedida object : obraFacade.getUnidadeMedidaFacade().lista()) {
            String texto = object.getDescricao();
            if (object.getSigla() != "" && object.getSigla() != null) {
                texto = texto + " - " + object.getSigla();
            }
            toReturn.add(new SelectItem(object, texto));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public ConverterGenerico getConverterUnidadeMedida() {
        if (converterUnidadeMedida == null) {
            converterUnidadeMedida = new ConverterGenerico(UnidadeMedida.class, obraFacade.getUnidadeMedidaFacade());
        }
        return converterUnidadeMedida;
    }

    public void removerServico(ObraServico os) {
        if (os.getSuperior() != null) {
            os.getSuperior().getFilhos().remove(os);
        }
        selecionado.getServicos().remove(os);
    }

    public boolean liberaOperacaoServico(ObraServico os) {
        if (os.getSuperior() == null) {
            if (os.getFilhos().isEmpty()) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public void novoObraAnotacao() {
        if (selecionado.getContrato() != null) {
            this.obraAnotacao = new ObraAnotacao();
            this.obraAnotacao.setObra(selecionado);
        } else {
            FacesUtil.addOperacaoNaoPermitida("Nenhum contrato foi selecionado.");
        }
    }

    public void confirmarObraAnotacao() {
        try {
            Util.validarCampos(obraAnotacao);
            selecionado.setAnotacoes(Util.adicionarObjetoEmLista(selecionado.getAnotacoes(), this.obraAnotacao));
            cancelarObraAnotacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }


    public void cancelarObraAnotacao() {
        this.obraAnotacao = null;
    }

    public void selecionarAnotacao(ObraAnotacao oa) {
        this.obraAnotacao = oa;
    }

    public void removerAnotacao(ObraAnotacao oa) {
        selecionado.getAnotacoes().remove(oa);
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            Arquivo a = new Arquivo();
            a.setMimeType(event.getFile().getContentType());
            a.setNome(event.getFile().getFileName());
            a.setDescricao(event.getFile().getFileName());
            a.setTamanho(event.getFile().getSize());
            a.setInputStream(event.getFile().getInputstream());
            a = obraFacade.getArquivoFacade().novoArquivoMemoria(a);
            ObraAnexo anexo = new ObraAnexo();
            anexo.setArquivo(a);
            anexo.setObra(selecionado);
            anexo.setTipoDocumentoAnexo(this.getTipoDocumentoAnexo());
            selecionado.setAnexos(Util.adicionarObjetoEmLista(selecionado.getAnexos(), anexo));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public StreamedContent recuperarArquivoParaDownload(Arquivo arq) {
        try {
            return obraFacade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(arq);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return null;
        }
    }

    public List<SelectItem> getTiposDocumentos() {
        List<TipoDocumentoAnexo> anexos = obraFacade.getTipoDocumentoAnexoFacade().buscarTodosTipoDocumentosAnexo();
        return Util.getListSelectItem(anexos, true);
    }

    public void removerArquivoAnexo(ObraAnexo anexo) {
        selecionado.getAnexos().remove(anexo);
    }

    public void emitirRelatorioMedicoes() throws IOException, JRException {
        AbstractReport ar = AbstractReport.getAbstractReport();
        HashMap parameters = new HashMap();
        String nomeArquivo = "RelatorioObras.jasper";
        parameters.put("IMAGEM", ar.getCaminhoImagem());
        parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
        parameters.put("USER", obraFacade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
        parameters.put("OBRA", selecionado.getId());
        ar.gerarRelatorio(nomeArquivo, parameters);
    }

    public OrdemDeServicoContrato getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemDeServicoContrato ordemServico) {
        this.ordemServico = ordemServico;
    }

    public ObraServico getObraServico() {
        return obraServico;
    }

    public void setObraServico(ObraServico obraServico) {
        this.obraServico = obraServico;
    }

    public ObraMedicao getObraMedicao() {
        return obraMedicao;
    }

    public void setObraMedicao(ObraMedicao obraMedicao) {
        this.obraMedicao = obraMedicao;
    }

    public ObraAnotacao getObraAnotacao() {
        return obraAnotacao;
    }

    public void setObraAnotacao(ObraAnotacao obraAnotacao) {
        this.obraAnotacao = obraAnotacao;
    }

    public ObraServico getObraServicoMedicao() {
        return obraServicoMedicao;
    }

    public void setObraServicoMedicao(ObraServico obraServicoMedicao) {
        this.obraServicoMedicao = obraServicoMedicao;
    }

    public boolean isEditarMedicao() {
        return editarMedicao;
    }

    public void setEditarMedicao(boolean editarMedicao) {
        this.editarMedicao = editarMedicao;
    }

    public String getEnderecoCI() {
        return enderecoCI;
    }

    public void setEnderecoCI(String enderecoCI) {
        this.enderecoCI = enderecoCI;
    }

    public ObraTecnicoFiscal getObraTecnicoFiscal() {
        return obraTecnicoFiscal;
    }

    public void setObraTecnicoFiscal(ObraTecnicoFiscal obraTecnicoFiscal) {
        this.obraTecnicoFiscal = obraTecnicoFiscal;
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, obraFacade.getPessoaFisicaFacade());
        }
        return converterPessoaFisica;
    }

    public void setConverterPessoaFisica(ConverterAutoComplete converterPessoaFisica) {
        this.converterPessoaFisica = converterPessoaFisica;
    }

    public List<Obra> completaObras(String parte) {
        return obraFacade.buscarObrasPorContratoExercicioContratado(parte);
    }

    public Converter getConverterResponsavelTecnicoFiscal() {
        if (converterResponsavel == null) {
            converterResponsavel = new ConverterAutoComplete(ResponsavelTecnicoFiscal.class, obraFacade.getResponsavelTecnicoFiscalFacade());
        }
        return converterResponsavel;
    }

    public void atribuirTipoImpressaoTermo(boolean definitivo) {
        obraTermoJaImpresso = obraFacade.recuperarTermoDaObra(selecionado, definitivo);
        tipoImpressaoTermo = definitivo;
        if (definitivo && !isTermoDefinitivoEmitido()) {
            FacesUtil.executaJavaScript("termoDefinitivo.show();");
            return;
        }
        if (!definitivo && !isTermoProvisorioEmitido()) {
            imprimirDocumentoOficial();
            return;
        }
        FacesUtil.executaJavaScript("impressaoTermo.show();");
    }

    public void imprimirDocumentoOficial() {
        try {
            if (obraTermoJaImpresso == null) {
                gerarNovoTermo(criarObraTermo(tipoImpressaoTermo));
            } else {
                gerarTermo(obraTermoJaImpresso);
            }
            FacesUtil.executaJavaScript("impressaoTermo.hide();");
            FacesUtil.executaJavaScript("termoDefinitivo.hide();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private ObraTermo criarObraTermo(boolean definitivo) {
        ObraTermo obraTermo = new ObraTermo();
        obraTermo.setObra(selecionado);
        obraTermo.setDataEmissao(new Date());
        obraTermo.setDefinitivo(definitivo);
        return obraTermo;
    }

    private void gerarNovoTermo(ObraTermo obraTermo) {
        ModeloDocto modelo;
        if (obraTermo.getDefinitivo()) {
            modelo = obraFacade.getModeloDoctoFacade().recuperarModeloDocumentoPorTipo(TipoModeloDocto.TipoModeloDocumento.TAGSRECEBIMENTODEFINITIVOOBRA);
        } else {
            modelo = obraFacade.getModeloDoctoFacade().recuperarModeloDocumentoPorTipo(TipoModeloDocto.TipoModeloDocumento.TAGSRECEBIMENTOPROVISORIOOBRA);
        }
        validarImpressaoTermo(modelo);
        obraTermo.setConteudo(obraFacade.mesclarTagsModelo(modelo, selecionado, responsavelTecnicoFiscalTermo));
        obraTermo.setNome(modelo.getNome());
        selecionado.getItemObraTermo().add(obraTermo);
        gerarTermo(obraTermo);
    }

    private void validarImpressaoTermo(ModeloDocto modelo) {
        if (modelo == null) {
            ValidacaoException validacaoException = new ValidacaoException();
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Nenhum modelo Cadastrado para o Termo.");
            validacaoException.lancarException();
        }
    }

    public String gerarConteudoTermo(ObraTermo obraTermo) {
        String conteudo = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <title>WebPúblico</title>"
            + " <style type=\"text/css\">"
            + " @media print {\n"
            + " html, body {\n"
            + "     height: 99%;"
            + "  }\n"
            + "    *{-webkit-print-color-adjust:exact}\n"
            + "    .naoImprime {\n"
            + "        display:none;\n"
            + "    }\n"
            + "}"
            + "@page{\n"
            + "    \n"
            + "}\n"
            + "}\n"
            + " .watermark {\n"
            + "    -webkit-transform: rotate(-45deg);\n"
            + "    -moz-transform: rotate(-45deg);\n"
            + "    -ms-transform: rotate(-45deg);\n"
            + "    -o-transform: rotate(-90deg);\n"
            + "    font-size: 92pt;\n"
            + " font-weight: bold;\n"
            + " position: absolute;\n"
            + "    opacity: 0.25;\n"
            + "    width: 100%;\n"
            + "text-align: center;\n"
            + "    z-index: 1000;\n"
            + " top: 40%;\n"
            + "}\n"
            + "#footer { \n"
            + "     display: block;"
            + "     position: running(footer); \n"
            + "} "
            + " </style>"
            + " <style type=\"text/css\">"
            + " .break { page-break-before: always; }"
            + " </style>"
            + " </head>"
            + " <body>";
        conteudo += "<div id=\"documentoOficial" + obraTermo.getId() + "\">";
        conteudo += FacesUtil.alteraURLImagens(obraTermo.getConteudo()) + "</div>";
        conteudo += " </body>"
            + " </html>";
        return conteudo;
    }

    private void emiteTermo(String conteudo) {
        Web.getSessionMap().put("documentoOficial", conteudo);
        atualizaComponentesDoDialog();
    }

    public void gerarTermo(ObraTermo obraTermo) {
        try {
            String conteudo = gerarConteudoTermo(obraTermo);
            emiteTermo(conteudo);
            obraFacade.salvarObra(selecionado);
            obraTermoJaImpresso = obraTermo;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean isTermoDefinitivoEmitido() {
        for (ObraTermo obraTermo : selecionado.getItemObraTermo()) {
            if (obraTermo.getDefinitivo())
                return true;
        }
        return false;
    }

    public boolean isTermoProvisorioEmitido() {
        for (ObraTermo obraTermo : selecionado.getItemObraTermo()) {
            if (!obraTermo.getDefinitivo())
                return true;
        }
        return false;
    }

    public ObraTermo recupearObraTermoAoAcessarATelaDeObras() {
        for (ObraTermo obraTermo : selecionado.getItemObraTermo()) {
            if (obraTermo.getDefinitivo()) {
                return obraTermo;
            }
        }
        for (ObraTermo obraTermo : selecionado.getItemObraTermo()) {
            if (!obraTermo.getDefinitivo()) {
                return obraTermo;
            }
        }
        return null;
    }

    public List<SelectItem> getFiscaisObra() {
        List<SelectItem> retorno = new ArrayList<>();
        for (ResponsavelTecnicoFiscal responsavelTecnicoFiscal : obraFacade.recuperarFiscaisObra(selecionado)) {
            if (responsavelTecnicoFiscal.getId() != null) {
                retorno.add(new SelectItem(responsavelTecnicoFiscal, responsavelTecnicoFiscal.toString()));
            }
        }
        return retorno;
    }

    public String getDataEmissaoTermoDefinitivo() {
        for (ObraTermo obraTermo : selecionado.getItemObraTermo()) {
            if (obraTermo.getDefinitivo())
                return DataUtil.getDataFormatadaDiaHora(obraTermo.getDataEmissao());
        }
        return "";
    }

    public String getValorTotalReservadoSolicitado(boolean formatado) {
        BigDecimal total = BigDecimal.ZERO;
        for (ObraMedicaoSolicitacaoEmpenho solicitado : obraMedicao.getObraMedicaoSolicitacaoEmpenhos()) {
            total = total.add(solicitado.getValor());
        }
        if (formatado) {
            return Util.formataValor(total);
        }
        return String.valueOf(total);
    }

    public List<Empenho> buscarEmpenhosObraMedicao() {
        return obraFacade.buscarEmpenhosObraMedicao(obraMedicao);
    }

    public ConverterAutoComplete getConverterTipoDocumentoAnexo() {
        if (converterTipoDocumentoAnexo == null) {
            converterTipoDocumentoAnexo = new ConverterAutoComplete(TipoDocumentoAnexo.class, obraFacade.getTipoDocumentoAnexoFacade());
        }
        return converterTipoDocumentoAnexo;
    }

    public void setConverterTipoDocumentoAnexo(ConverterAutoComplete converterTipoDocumentoAnexo) {
        this.converterTipoDocumentoAnexo = converterTipoDocumentoAnexo;
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public List<Obra> buscarTodasObrasPorNome(String filter) {
        return obraFacade.buscarTodasObrasPorNome(filter);
    }

    public List<ResponsavelTecnicoFiscal> buscarResponsaveisFiscaisDaObraAprovados() {
        if (!isOperacaoNovo()) {
            return obraFacade.buscarResponsaveisFiscaisDaObraPorSituacao(selecionado, SituacaoSolicitacaoFiscal.APROVADA);
        }
        return Lists.newArrayList();
    }


    public List<ResponsavelTecnicoFiscal> completarResponsaveisFiscaisAprovados(String filtro) {
        return obraFacade.buscarResponsaveisFiscaisDaObraPorSituacao(selecionado, SituacaoSolicitacaoFiscal.APROVADA, filtro);
    }

    public String buscarDescricaoPrincipalSubstituto(ResponsavelTecnicoFiscal responsavelTecnicoFiscal) {
        try {
            return obraFacade.buscarSolicitacaoTecnicoFiscal(selecionado, SituacaoSolicitacaoFiscal.APROVADA, responsavelTecnicoFiscal).getPrincipalSubstituto().getDescricao();
        } catch (NullPointerException ex) {
            return "Não Informado";
        }
    }

    public ResponsavelTecnicoFiscal getResponsavelTecnicoFiscalTermo() {
        return responsavelTecnicoFiscalTermo;
    }

    public void setResponsavelTecnicoFiscalTermo(ResponsavelTecnicoFiscal responsavelTecnicoFiscalTermo) {
        this.responsavelTecnicoFiscalTermo = responsavelTecnicoFiscalTermo;
    }

    public ObraTermo getObraTermoJaImpresso() {
        return obraTermoJaImpresso;
    }

    public void setObraTermoJaImpresso(ObraTermo obraTermoJaImpresso) {
        this.obraTermoJaImpresso = obraTermoJaImpresso;
    }

    public Boolean getTipoImpressaoTermo() {
        return tipoImpressaoTermo;
    }

    public void setTipoImpressaoTermo(Boolean tipoImpressaoTermo) {
        this.tipoImpressaoTermo = tipoImpressaoTermo;
    }

    public void recuperaCadastroImobiliario() {
        if (selecionado.getCadastroImobiliario() != null) {
            selecionado.setCadastroImobiliario(obraFacade.getCadastroImobiliarioFacade().recuperar(selecionado.getCadastroImobiliario().getId()));
            this.enderecoCI = selecionado.getCadastroImobiliario().getLote().getEndereco();
        }
        verInscricaoMapa();
    }

    public void verInscricaoMapa() {
        if (selecionado.getCadastroImobiliario() == null) {
            return;
        }
        int setor = Integer.parseInt(selecionado.getCadastroImobiliario().getLote().getSetor().getCodigo());
        int quadra = selecionado.getCadastroImobiliario().getLote().getQuadra().getCodigoToInteger();
        int lote = Integer.parseInt(selecionado.getCadastroImobiliario().getLote().getCodigoLote());
        String inscricaoMapa = "1-" + setor + "-" + quadra + "-" + lote;
        FacesUtil.executaJavaScript("verIncricao('" + inscricaoMapa + "')");
        FacesUtil.atualizarComponente("Formulario:panel-map");
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public List<ObraTecnicoFiscal> getResponsaveisFiscais() {
        return responsaveisFiscais;
    }

    public void setResponsaveisFiscais(List<ObraTecnicoFiscal> responsaveisFiscais) {
        this.responsaveisFiscais = responsaveisFiscais;
    }

    public boolean isEditandoTecnico() {
        return isEditandoTecnico;
    }

    public void setEditandoTecnico(boolean editandoTecnico) {
        isEditandoTecnico = editandoTecnico;
    }

    public List<ObraTecnicoFiscal> getResponsaveisTecnicos() {
        return responsaveisTecnicos;
    }

    public void setResponsaveisTecnicos(List<ObraTecnicoFiscal> responsaveisTecnicos) {
        this.responsaveisTecnicos = responsaveisTecnicos;
    }

    public List<OrdemDeServicoContrato> getOrdensDeServico() {
        return ordensDeServico;
    }

    public void setOrdensDeServico(List<OrdemDeServicoContrato> ordensDeServico) {
        this.ordensDeServico = ordensDeServico;
    }

    public List<AlteracaoContratual> getAditivos() {
        return aditivos;
    }

    public void setAditivos(List<AlteracaoContratual> aditivos) {
        this.aditivos = aditivos;
    }

    public ObraSituacao getObraSituacao() {
        return obraSituacao;
    }

    public void setObraSituacao(ObraSituacao obraSituacao) {
        this.obraSituacao = obraSituacao;
    }

    public void limparObraSituacao() {
        obraSituacao = null;
    }

    public void removerObraSituacao(ObraSituacao obraSituacao) {
        selecionado.getSituacoes().remove(obraSituacao);
    }

    public void editarObraSituacao(ObraSituacao obraSituacao) {
        this.obraSituacao = (ObraSituacao) Util.clonarObjeto(obraSituacao);
    }

    public void novoObraSituacao() {
        obraSituacao = new ObraSituacao();
    }

    public void adicionarObraSituacao() {
        try {
            Util.validarCampos(obraSituacao);
            obraSituacao.setObra(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getSituacoes(), obraSituacao);
            limparObraSituacao();
            ordenarSituacoes();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public List<SelectItem> getTiposSituacoes() {
        return Util.getListSelectItem(TipoSituacaoObra.values(), false);
    }

    private void ordenarSituacoes() {
        if (selecionado.getSituacoes() != null && !selecionado.getSituacoes().isEmpty()) {
            Collections.sort(selecionado.getSituacoes(), new Comparator<ObraSituacao>() {
                @Override
                public int compare(ObraSituacao o1, ObraSituacao o2) {
                    return o2.getDataSituacao().compareTo(o1.getDataSituacao());
                }
            });
        }
    }
}

