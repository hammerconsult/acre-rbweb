/*
 * Codigo gerado automaticamente em Mon Oct 17 08:48:31 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.AutorizacaoUsuarioRH;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "cedenciaContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCedenciaContratoFP", pattern = "/cedencia/novo/", viewId = "/faces/rh/administracaodepagamento/cedenciacontratofp/edita.xhtml"),
    @URLMapping(id = "editarCedenciaContratoFP", pattern = "/cedencia/editar/#{cedenciaContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cedenciacontratofp/edita.xhtml"),
    @URLMapping(id = "listarCedenciaContratoFP", pattern = "/cedencia/listar/", viewId = "/faces/rh/administracaodepagamento/cedenciacontratofp/lista.xhtml"),
    @URLMapping(id = "verCedenciaContratoFP", pattern = "/cedencia/ver/#{cedenciaContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cedenciacontratofp/visualizar.xhtml")
})
public class CedenciaContratoFPControlador extends PrettyControlador<CedenciaContratoFP> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CedenciaContratoFPControlador.class);

    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private PessoaJuridicaFacade cedenteFacade;
    private ConverterAutoComplete converterCedente;
    @EJB
    private PessoaJuridicaFacade cessionarioFacade;
    private ConverterAutoComplete converterCessionario;
    @EJB
    private EntidadeFacade entidadeFacade;
    //    private List<ContratoFP> listaContratoFP;
    private Boolean contratoFPCadastrado;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "contratoFPControlador", value = "#{contratoFPControlador}")
    private ContratoFPControlador contratoFPControlador;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private ArquivoFacade arquivoFacade;
    private StreamedContent arquivo;
    @EJB
    private CargoFacade cargoFacade;
    private ConverterAutoComplete cargoConverter;
    private String label = "Cedência";
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public CedenciaContratoFPControlador() {
        super(CedenciaContratoFP.class);
        //selecionado.setCedido(new Integer(0));
    }

    public StreamedContent getArquivo() {
        if (selecionado.getAnexo() != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : selecionado.getAnexo().getPartes()) {
                try {
                    buffer.write(a.getDados());
                } catch (IOException ex) {
                    logger.error("Erro: ", ex);
                }
            }
            InputStream is = new ByteArrayInputStream(buffer.toByteArray());
            arquivo = new DefaultStreamedContent(is, selecionado.getAnexo().getMimeType(), selecionado.getAnexo().getNome());
        }
        return arquivo;
    }

    public void setArquivo(StreamedContent arquivo) {
        this.arquivo = arquivo;
    }

    public ConverterAutoComplete getCargoConverter() {
        if (cargoConverter == null) {
            cargoConverter = new ConverterAutoComplete(Cargo.class, cargoFacade);
        }
        return cargoConverter;
    }

    public void setCargoConverter(ConverterAutoComplete cargoConverter) {
        this.cargoConverter = cargoConverter;
    }

    public CedenciaContratoFPFacade getFacade() {
        return cedenciaContratoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return cedenciaContratoFPFacade;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), "ADMINISTRATIVA", new Date());
    }

    public ConverterAutoComplete getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<SelectItem> getCedente() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (PessoaJuridica object : cedenteFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterCedente() {
        if (converterCedente == null) {
            converterCedente = new ConverterAutoComplete(UnidadeExterna.class, cedenteFacade);
        }
        return converterCedente;
    }

    public List<SelectItem> getCessionario() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (PessoaJuridica object : cessionarioFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterCessionario() {
        if (converterCessionario == null) {
            converterCessionario = new ConverterAutoComplete(UnidadeExterna.class, cessionarioFacade);
        }
        return converterCessionario;
    }

    public CedenciaContratoFP esteSelecionado() {
        return (CedenciaContratoFP) selecionado;
    }

    @URLAction(mappingId = "novoCedenciaContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (isSessao()) {
            return;
        }
        selecionado.setCedido(new Integer(0));
        contratoFPCadastrado = false;
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        hierarquiaOrganizacional.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setDataCadastro(new Date());
        selecionado.setDataCessao(new Date());
        selecionado.setFinalidadeCedenciaContratoFP(FinalidadeCedenciaContratoFP.INTERNA);
        label = "Cedência";
    }

    @URLAction(mappingId = "verCedenciaContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCedenciaContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
    }

    public List<UnidadeExterna> completaCessionario(String parte) {
        return unidadeExternaFacade.listaFiltrandoPessoaJuridicaAtivaEsferaGoverno(parte.trim());
    }

    public List<Cargo> completaCargos(String parte) {
        return cargoFacade.listaFiltrando(parte.trim(), "descricao", "codigoDoCargo");
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatriculaSql(parte.trim());
    }

    public List<ContratoFP> completaContratoFPCedente(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public List<UnidadeExterna> completaCedente(String parte) {
        return unidadeExternaFacade.listaFiltrandoPessoaJuridicaEsferaGoverno(parte.trim());
    }

    public List<SelectItem> getTipoContratoCedenciaFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCedenciaContratoFP object : TipoCedenciaContratoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getFinalidadesCedenciaContratoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (FinalidadeCedenciaContratoFP object : FinalidadeCedenciaContratoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                CedenciaContratoFP ccfp = selecionado;
                validarCedencia(ccfp);
                salvarCessao(ccfp);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public void checarFichaFinanceiraEfetivada() {
        if (selecionado.getContratoFP() != null && selecionado.getDataCessao() != null) {
            boolean fichaComFolhaEfetivada = fichaFinanceiraFPFacade.existeFichaComFolhaEfetivadaPorContratoFP(selecionado.getContratoFP(), selecionado.getDataCessao());
            if (fichaComFolhaEfetivada) {
                FacesUtil.addWarn("ATENÇÃO!", "Existe ficha financeira lançada para o servidor no período de cedência selecionado.");
            }
        }
    }

    private void salvarCessao(CedenciaContratoFP ccfp) {
        try {
            if (isOperacaoNovo()) {
                if (cedenciaContratoFPFacade.podeSalvarCedencia(ccfp)) {
                    cedenciaContratoFPFacade.salvarNovoCedencia(ccfp);
                    FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    redireciona();
                } else {
                    FacesUtil.addWarn("Atenção", "Já existe uma Cedência salva com a mesma vigência!");
                }
            } else {
                if (selecionado.getAnexo() != null) {
                    Arquivo arquivo = arquivoFacade.recuperaDependencias(selecionado.getAnexo().getId());
                    if (arquivoFoiAlterado(ccfp.getAnexo(), arquivo)) {
                        cedenciaContratoFPFacade.salvar(ccfp);
                    } else {
                        cedenciaContratoFPFacade.salvarApenasDados(ccfp);
                    }
                } else {
                    cedenciaContratoFPFacade.salvarApenasDados(ccfp);
                }
                FacesUtil.addInfo("Sucesso", "Salvo com Sucesso");
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addError("Atenção", "Houve um erro ao salvar. Erro:" + e);
        }
    }

    private boolean arquivoFoiAlterado(Arquivo novoArquivo, Arquivo arquivoExistente) {
        if (novoArquivo == null && arquivoExistente == null) {
            return false;
        }
        if (novoArquivo == null || arquivoExistente == null) {
            return true;
        }

        if (!novoArquivo.getNome().equals(arquivoExistente.getNome())) {
            return true;
        }
        if (novoArquivo.getPartes().size() != arquivoExistente.getPartes().size()) {
            return true;
        }
        return false;
    }

    private void validarCedencia(CedenciaContratoFP ccfp) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCedido() == 1) {
            if (FinalidadeCedenciaContratoFP.INTERNA.equals(selecionado.getFinalidadeCedenciaContratoFP())) {
                if (ccfp.getUnidadeOrganizacional() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Por favor, informe a Unidade Organizacional!");
                }
            }

            if (FinalidadeCedenciaContratoFP.EXTERNA.equals(selecionado.getFinalidadeCedenciaContratoFP())) {
                if (ccfp.getCessionario() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Por favor, informe o Órgão Receptor!");
                } else if (ccfp.getCessionario().getPessoaJuridica().getCnpj() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe o Órgão Receptor vinculado a uma pessoa jurídica com CNPJ!");
                }
            }
            if (selecionado.getDataRetorno() != null && selecionado.getDataRetorno().compareTo(selecionado.getDataCessao()) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Retorno deve ser posterior a Data de Cedência!");
            }
            ve.lancarException();

        } else if (selecionado.getCedido() == 2) {
            if (ccfp.getCedente() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione um Cedente!");
            }
            if (selecionado.getDataRetorno() != null && selecionado.getDataRetorno().compareTo(selecionado.getDataCadastro()) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Retorno deve ser posterior a data de hoje!");
            }

            contratoFPCadastrado = true;
        }

        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosNoPeriodoPorTipos(selecionado.getContratoFP(), selecionado.getDataCessao(), selecionado.getDataRetorno(), 14, 15);
        if (afastamentos != null && !afastamentos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado registro de Afastamento códigos 14 ou 15 no mesmo período do atual lançamento.");
        }

        ve.lancarException();
    }

    public void selecionar() {
        CedenciaContratoFP ccfp = ((CedenciaContratoFP) selecionado);
        contratoFPCadastrado = true;

        if (ccfp.getUnidadeOrganizacional() != null) {
            selecionado.setHo(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUnidadeOrganizacional(), UtilRH.getDataOperacao()));
        }

        if (ccfp.getCedente() != null) {
            selecionado.setCedido(2);
            if (ccfp.getCedente().getId() != null) {
                selecionado.setCedente(unidadeExternaFacade.recuperar(ccfp.getCedente().getId()));
            }
        } else {
            selecionado.setCedido(1);
            if (ccfp.getCessionario() != null && ccfp.getCessionario().getId() != null) {
                selecionado.setCessionario(unidadeExternaFacade.recuperar(ccfp.getCessionario().getId()));
            }
        }

        if (selecionado.getAnexo() != null) {
            selecionado.setAnexo(arquivoFacade.recuperaDependencias(selecionado.getAnexo().getId()));
        }
        recuperaLotacaoFuncional();
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        List<AtoLegal> lista = atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
        return lista;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public void setAtoLegalFacade(AtoLegalFacade atoLegalFacade) {
        this.atoLegalFacade = atoLegalFacade;
    }

    public CedenciaContratoFPFacade getCedenciaContratoFPFacade() {
        return cedenciaContratoFPFacade;
    }

    public void setCedenciaContratoFPFacade(CedenciaContratoFPFacade cedenciaContratoFPFacade) {
        this.cedenciaContratoFPFacade = cedenciaContratoFPFacade;
    }

    public PessoaJuridicaFacade getCedenteFacade() {
        return cedenteFacade;
    }

    public void setCedenteFacade(PessoaJuridicaFacade cedenteFacade) {
        this.cedenteFacade = cedenteFacade;
    }

    public PessoaJuridicaFacade getCessionarioFacade() {
        return cessionarioFacade;
    }

    public void setCessionarioFacade(PessoaJuridicaFacade cessionarioFacade) {
        this.cessionarioFacade = cessionarioFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    @Override
    public void cancelar() {
        if (operacao == Operacoes.NOVO) {
            if (selecionado.getCedido() == 2) {
                ContratoFP cfp = ((CedenciaContratoFP) selecionado).getContratoFP();
                if (cfp != null) {
                    contratoFPFacade.remover(cfp);
                    contratoFPCadastrado = false;
                }
            }
        } else {
            contratoFPCadastrado = true;
        }

        super.cancelar();
    }

    @Override
    public void excluir() {
        CedenciaContratoFP ccfp = ((CedenciaContratoFP) selecionado);

        try {
            validarForeinKeysComRegistro(selecionado);

        if (temFichaPorVigenciaCedencia() && !usuarioTemPermissaoParaExcluirComFichaVinculada()) {
            FacesUtil.addError("Impossível continuar!", "Não é possível excluir, a Cedência do Contrato FP possui uma Ficha Financeira vinculada!");
        } else {
            ContratoFP contratoFP = ccfp.getContratoFP();
            if (contratoFP != null) {
                SituacaoContratoFP situacaoContratoFP = cedenciaContratoFPFacade.getSituacaoContratoFPFacade().ultimaSituacaoContratoFP(contratoFP);
                if (situacaoContratoFP != null) {
                    if (selecionado.getCedido() == 1 && situacaoContratoFP.getSituacaoFuncional().getCodigo() == SituacaoFuncional.A_DISPOSICAO || (usuarioTemPermissaoParaExcluirComFichaVinculada())) {
                        cedenciaContratoFPFacade.excluirCedenciaUnidadeExterna(selecionado, situacaoContratoFP);
                    } else if (selecionado.getCedido() == 2 && situacaoContratoFP.getSituacaoFuncional().getCodigo() == SituacaoFuncional.A_NOSSA_DISPOSICAO  || (usuarioTemPermissaoParaExcluirComFichaVinculada())) {
//                        cedenciaContratoFPFacade.excluirCedenciaPrefeitura(selecionado);
                        selecionado.setContratoFP(null);
                        cedenciaContratoFPFacade.salvar(selecionado);
                        contratoFPFacade.remover(contratoFP);
                        cedenciaContratoFPFacade.remover(selecionado);
                    } else {
                        FacesUtil.addWarn("Atenção!", "O registro não pode ser excluído, a situação do contrato encontra-se " + situacaoContratoFP.getSituacaoFuncional().getDescricao());
                        return;
                    }
                }
                redireciona();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
            }
        }

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    private boolean temFichaPorVigenciaCedencia() {
        boolean temFicha = false;
        if (!cedenciaContratoFPFacade.buscaFichaFinaceiraFPPorVigenciaCedenciaContratoFP((CedenciaContratoFP) selecionado).isEmpty()) {
            temFicha = true;
        }
        return temFicha;
    }

    public boolean usuarioTemPermissaoParaExcluirComFichaVinculada() {

        UsuarioSistema usuario = usuarioSistemaFacade.recuperarAutorizacaoUsuarioRH(sistemaFacade.getUsuarioCorrente().getId());

        for (AutorizacaoUsuarioRH autorizacaoUsuarioRH : usuario.getAutorizacaoUsuarioRH()) {
            if (TipoAutorizacaoRH.PERMITIR_EXCLUIR_CEDENCIA_FICHA_FINANCEIRA_VINCULADA.equals(autorizacaoUsuarioRH.getTipoAutorizacaoRH())) {
                return true;
            }
        }
        return false;
    }

    public Boolean getContratoFPCadastrado() {
        return contratoFPCadastrado;
    }

    public void setContratoFPCadastrado(Boolean contratoFPCadastrado) {
        this.contratoFPCadastrado = contratoFPCadastrado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ContratoFPControlador getContratoFPControlador() {
        return contratoFPControlador;
    }

    public void setContratoFPControlador(ContratoFPControlador contratoFPControlador) {
        this.contratoFPControlador = contratoFPControlador;
    }

    public void novoContratoFP() {
        contratoFPControlador.novo();
        contratoFPControlador.getSelecionado().setModalidadeContratoFP(modalidadeContratoFPFacade.recuperaModalidadePorCodigo(ModalidadeContratoFP.CARGO_COMISSAO));
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, Exception {
        UploadedFile f = event.getFile();
        Arquivo a = new Arquivo();
        a.setDescricao(f.getFileName());
        a.setMimeType(f.getContentType());
        a.setTamanho(f.getSize());
        a.setNome(f.getFileName());
        a = arquivoFacade.novoArquivoMemoria(a, f.getInputstream());
        selecionado.setAnexo(a);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void verificaFichaFinanceira() {
        if (selecionado.getContratoFP() != null && selecionado.getContratoFP().getId() != null) {
            boolean temFichaFinanceira = folhaDePagamentoFacade.existeFolhaPorContratoData(selecionado.getContratoFP(), SistemaFacade.getDataCorrente());
            if (temFichaFinanceira) {
                FacesUtil.addWarn("Atenção!", "Existe ficha financeira lançada para o servidor no período de cedência selecionado.");
            }
        }
    }

    public void recuperaLotacaoFuncional() {
        if (this.selecionado.getContratoFP() != null && this.selecionado.getContratoFP().getId() != null) {
            this.selecionado.setLocalTrabalhoAtual(lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(this.selecionado.getContratoFP(), lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao()));
        } else {
            this.selecionado.setLocalTrabalhoAtual(null);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cedencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
