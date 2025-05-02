package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.ProtocoloImpressao;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltrosPesquisaProtocolo;
import br.com.webpublico.entidadesauxiliares.VoProcesso;
import br.com.webpublico.entidadesauxiliares.VoTramite;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroListaProtocolo;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.LocalDate;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProtocolo", pattern = "/protocolo/novo/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/edita.xhtml"),
    @URLMapping(id = "editarProtocolo", pattern = "/protocolo/editar/#{protocoloSimplesControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/edita.xhtml"),
    @URLMapping(id = "listarProtocolo", pattern = "/protocolo/listar/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/lista.xhtml"),
    @URLMapping(id = "consultaProtocolo", pattern = "/protocolo/consultar/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/consulta.xhtml"),
    @URLMapping(id = "verProtocolo", pattern = "/protocolo/ver/#{protocoloSimplesControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/visualizar.xhtml"),
    @URLMapping(id = "verConsultaProtocolo", pattern = "/protocolo/verconsulta/#{protocoloSimplesControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/visualizar.xhtml"),
    @URLMapping(id = "verNumeroAnoProtocolo", pattern = "/protocolo/ver/#{protocoloSimplesControlador.numeroUrl}/#{protocoloSimplesControlador.anoUrl}/", viewId = "/faces/tributario/cadastromunicipal/protocolosimples/visualizar.xhtml")
})
public class ProtocoloSimplesControlador extends PrettyControlador<Processo> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ProtocoloSimplesControlador.class);

    @EJB
    private ProcessoFacade facade;
    private ConverterAutoComplete converterDoc;
    private ConverterAutoComplete converterHierarquiaOrganizacional;

    private UnidadeOrganizacional unidadeSelecionada;
    private DocumentoProcesso documento;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaProtocolo;
    private Boolean encaminhamentoMultiplo;
    private Arquivo arquivos;
    private TreeNode rootOrc;
    private TreeNode selectedNode;
    private String destinoExterno;
    private List<UnidadeOrganizacional> unidadesEncaminhamentosMultiplos;
    private List<UnidadeOrganizacional> unidadeSemUsuarioCadastrado;
    private Boolean consulta;
    private String numeroUrl;
    private String anoUrl;
    private String novoCpfCnpj;
    private Processo processo;
    private ProcessoEnglobado processoEnglobado;

    private FiltrosPesquisaProtocolo filtrosGerados;
    private FiltrosPesquisaProtocolo filtrosRecebimentoMultiplo;
    private List<VoProcesso> protocolosSelecionadosGuiaRecebimento;
    private FiltrosPesquisaProtocolo filtrosTramitesPendentes;
    private FiltrosPesquisaProtocolo filtrosTramitesAceitos;
    private FiltrosPesquisaProtocolo filtrosEncaminhados;
    private FiltrosPesquisaProtocolo filtrosArquivados;
    private FiltrosPesquisaProtocolo filtrosFinalizados;
    private FiltrosPesquisaProtocolo filtrosCancelados;
    private Tramite novoTramite;


    public ProtocoloSimplesControlador() {
        super(Processo.class);
    }

    @URLAction(mappingId = "verNumeroAnoProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verUrl() {
        if (getNumeroUrl() != null && getAnoUrl() != null) {
            selecionado = facade.buscarProcessoPorNumeroEAno(Integer.parseInt(getNumeroUrl()), Integer.parseInt(getAnoUrl())).get(0);
            selecionado = facade.recuperar(selecionado.getId());
        } else {
            FacesUtil.addError("Erro ao localizar o Protocolo!", "Protocolo não encontrado!");
            redireciona();
        }
        consulta = false;
    }

    public Boolean getConsulta() {
        return consulta;
    }

    public void setConsulta(Boolean consulta) {
        this.consulta = consulta;
    }

    public List<UnidadeOrganizacional> getUnidadesEncaminhamentosMultiplos() {
        return unidadesEncaminhamentosMultiplos;
    }

    public void setUnidadesEncaminhamentosMultiplos(List<UnidadeOrganizacional> unidadesEncaminhamentosMultiplos) {
        this.unidadesEncaminhamentosMultiplos = unidadesEncaminhamentosMultiplos;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRootOrc() {
        return rootOrc;
    }

    public void setRootOrc(TreeNode rootOrc) {
        this.rootOrc = rootOrc;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Arquivo getArquivo() {
        return arquivos;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivos = arquivo;
    }

    public List<VoProcesso> getProtocolosGerados() {
        if (filtrosGerados.getProcessos() == null || filtrosGerados.getProcessos().isEmpty()) {
            montarPaginasGerados();
        }
        return filtrosGerados.getProcessos();
    }

    public void setProtocolosGerados(List<VoProcesso> protocolosGerados) {
        filtrosGerados.setProcessos(protocolosGerados);
    }

    public List<VoProcesso> getProcessosRecebimento() {
        if (filtrosRecebimentoMultiplo.getProcessos() == null || filtrosRecebimentoMultiplo.getProcessos().isEmpty()) {
            montarPaginasRecebimentoMultiplo();
        }
        return filtrosRecebimentoMultiplo.getProcessos();
    }

    public void setProcessosRecebimento(List<VoProcesso> processosRecebimento) {
        filtrosTramitesPendentes.setProcessos(processosRecebimento);
    }

    public List<VoProcesso> getProcessosEncaminhados() {
        if (filtrosEncaminhados.getProcessos() == null || filtrosEncaminhados.getProcessos().isEmpty()) {
            montarPaginasEncaminhados();
        }
        return filtrosEncaminhados.getProcessos();
    }

    public void setProcessosEncaminhados(List<VoProcesso> processosEncaminhados) {
        filtrosEncaminhados.setProcessos(processosEncaminhados);
    }

    public List<VoProcesso> getProcessosCancelados() {
        if (filtrosCancelados.getProcessos() == null || filtrosCancelados.getProcessos().isEmpty()) {
            montarPaginasCancelados();
        }
        return filtrosCancelados.getProcessos();
    }

    public void setProcessosCancelados(List<VoProcesso> processosCancelados) {
        filtrosCancelados.setProcessos(processosCancelados);
    }

    public List<VoProcesso> getProcessosArquivados() {
        if (filtrosArquivados.getProcessos() == null || filtrosArquivados.getProcessos().isEmpty()) {
            montarPaginasArquivados();
        }
        return filtrosArquivados.getProcessos();
    }

    public void setProcessosArquivados(List<VoProcesso> processosArquivados) {
        filtrosArquivados.setProcessos(processosArquivados);
    }

    public List<VoProcesso> getProcessosFinalizados() {
        if (filtrosFinalizados.getProcessos() == null || filtrosFinalizados.getProcessos().isEmpty()) {
            montarPaginasFinalizados();
        }
        return filtrosFinalizados.getProcessos();
    }

    public void setProcessosFinalizados(List<VoProcesso> processosFinalizados) {
        filtrosFinalizados.setProcessos(processosFinalizados);
    }

    public List<VoTramite> getTramitesPendentes() {
        if (filtrosTramitesPendentes.getTramites() == null || filtrosTramitesPendentes.getTramites().isEmpty()) {
            montarPaginasTramitesPendentes();
        }
        return filtrosTramitesPendentes.getTramites();
    }

    public void setTramitesPendentes(List<VoTramite> tramitesPendentes) {
        filtrosTramitesPendentes.setTramites(tramitesPendentes);
    }

    public List<VoTramite> getTramitesAceitos() {
        if (filtrosTramitesAceitos.getTramites() == null || filtrosTramitesAceitos.getTramites().isEmpty()) {
            montarPaginasTramitesAceitos();
        }
        return filtrosTramitesAceitos.getTramites();
    }

    public void setTramitesAceitos(List<VoTramite> tramitesAceitos) {
        filtrosTramitesAceitos.setTramites(tramitesAceitos);
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, facade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public List<SelectItem> getListaSituacao() {
        List<SelectItem> listaSituacao = new ArrayList<>();
        listaSituacao.add(new SelectItem(null, ""));
        for (SituacaoProcesso sp : SituacaoProcesso.values()) {
            listaSituacao.add(new SelectItem(sp, sp.getDescricao()));
        }
        return listaSituacao;
    }

    public void limparListaUnidadeEncaminhamentoMultiplos() {
        unidadesEncaminhamentosMultiplos = Lists.newArrayList();
    }

    public void adicionarUnidadeOrganizacional() {
        try {
            validarUnidade();
            Util.adicionarObjetoEmLista(unidadesEncaminhamentosMultiplos, hierarquiaOrganizacional.getSubordinada());
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        }
    }

    private void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade de Destino deve ser informado.");
        }
        ve.lancarException();
        for (UnidadeOrganizacional unidadeOrganizacional : unidadesEncaminhamentosMultiplos) {
            if (unidadeOrganizacional.equals(hierarquiaOrganizacional.getSubordinada())) {
                ve.adicionarMensagemDeCampoObrigatorio("A Unidade de Destino selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public void removeUnidadeOrganizacional(UnidadeOrganizacional uni) {
        if (uni != null) {
            unidadesEncaminhamentosMultiplos.remove(uni);
        }
    }

    private void validarCamposProtocolo() {
        ValidacaoException ve = new ValidacaoException();
        validarCamposObrigatorios(ve);
        validarNaoEncaminhamentosMultiplos(ve);
        validarEncaminhamentosMultiplos(ve);
        if (selecionado.getDocumentoProcesso().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Por Favor informe algum documento!");
        }
        ve.lancarException();
    }

    private void validarEncaminhamentosMultiplos(ValidacaoException ve) {
        if (encaminhamentoMultiplo && selecionado.getTramites().isEmpty()) {
            if (unidadesEncaminhamentosMultiplos.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos uma Unidade de Destino!");
            }
            if (!unidadesEncaminhamentosMultiplos.isEmpty()) {
                for (UnidadeOrganizacional uni : unidadesEncaminhamentosMultiplos) {
                    if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(uni).isEmpty()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível encaminhar um protocolo para uma unidade organizacional (" + uni.getDescricao() + ") que não possua usuários cadastrados!");
                    }
                }
            }
        }
    }

    private void validarNaoEncaminhamentosMultiplos(ValidacaoException ve) {
        if (!encaminhamentoMultiplo && selecionado.getTramites().isEmpty()) {
            if (selecionado.getTipoProcessoTramite().equals(TipoProcessoTramite.INTERNO)) {
                if (isHierarquiaPreenchida()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe uma Unidade de Destino!");
                } else {
                    if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(hierarquiaOrganizacional.getSubordinada()).isEmpty()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível encaminhar um protocolo para uma unidade organizacional que não possua usuários cadastrados!");
                    }
                }
            } else {
                if (Strings.isNullOrEmpty(destinoExterno)) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe o Destino Externo!");
                }
            }
        }
    }

    private void validarCamposObrigatorios(ValidacaoException ve) {
        if (selecionado.getUoCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Unidade Organizacional que o protocolo será cadastrado!");
        }
        if (selecionado.getTipoProcessoTramite() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Tipo do Processo!");
        }
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Autor/Requerente!");
        }
        if (selecionado.getAssunto() == null || "".equals(selecionado.getAssunto())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Assunto do protocolo!");
        }
    }

    private boolean isHierarquiaPreenchida() {
        return hierarquiaOrganizacional == null || hierarquiaOrganizacional.getSubordinada() == null;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoa() != null) {
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                if (!valida_CpfCnpj(((PessoaFisica) selecionado.getPessoa()).getCpf())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa selecionada não possui um CPF válido!");
                } else if (facade.getPessoaFacade().hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado.getPessoa(), true)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF da pessoa selecionada pertence a mais de uma pessoa física!");
                }
            } else {
                if (!valida_CpfCnpj(((PessoaJuridica) selecionado.getPessoa()).getCnpj())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa selecionada não possui um CNPJ válido!");
                } else if (facade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado.getPessoa(), true)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ da pessoa selecionada pertence a mais de uma pessoa jurídica!");
                }
            }
        }
        ve.lancarException();
    }

    private void gerarHistoricoProcesso(Processo processo, String descricao, UnidadeOrganizacional unidadeOrganizacional) {
        ProcessoHistorico processoHistorico;
        if (processo.isProcessoInterno()) {
            processoHistorico = processo.criarHistoricoProcesso(descricao, unidadeOrganizacional, getUsuarioCorrente());
        } else {

            if (isOperacaoEditar() || SituacaoProcesso.CANCELADO.equals(processo.getSituacao())) {
                destinoExterno = processo.getTramites().get(0).getDestinoExterno();
            }
            processoHistorico = processo.criarHistoricoProcessoExterno(descricao, destinoExterno, getUsuarioCorrente());
        }
        processo.getHistoricos().add(processoHistorico);
    }

    private void gerarHistoricoProcessoExterno(Processo processo, String descricao, String unidadeExterna) {
        ProcessoHistorico processoHistorico = processo.criarHistoricoProcessoExterno(descricao, unidadeExterna, getUsuarioCorrente());
        processo.getHistoricos().add(processoHistorico);
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCampos();
            validarCamposProtocolo();
            definirSenhaAndSituacao();
            criarTramite();
            gerarHistoricoAlteracaoProtocolo();
            movimentarProcessosSubordinados();
            selecionado = facade.salvarProcessoProtocolo(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            descobrirETratarExceptionProcesso(e);
        }
    }

    private void movimentarProcessosSubordinados() {
        if (Operacoes.EDITAR.equals(operacao)) {
            if (!selecionado.getProcessosSubordinados().isEmpty()) {
                copiaDadosDoOrigem(selecionado);
            }
        }
    }

    private void gerarHistoricoAlteracaoProtocolo() {
        if (Operacoes.EDITAR.equals(operacao)) {
            UnidadeOrganizacional unidadeOrganizacional = selecionado.isProcessoInterno() ? selecionado.getTramites().get(0).getUnidadeOrganizacional() : null;
            gerarHistoricoProcesso(selecionado, Processo.PROTOCOLO_ALTERADO, unidadeOrganizacional);
        }
    }

    private void definirSenhaAndSituacao() {
        if (selecionado.getSenha() == null) {
            selecionado.setSenha(facade.geraSenha());
        }
        if (selecionado.getSituacao() == null) {
            selecionado.setSituacao(SituacaoProcesso.GERADO);
        }
    }

    private void criarTramite() {
        if (Operacoes.NOVO.equals(operacao)) {
            selecionado.setProtocolo(Boolean.TRUE);
            selecionado.setDataRegistro(new Date());
            selecionado.setEncaminhamentoMultiplo(encaminhamentoMultiplo);

            if (getExterno() && destinoExterno != null && !destinoExterno.trim().isEmpty()) {
                criarTramiteProtocoloExterno();

            } else if (!encaminhamentoMultiplo) {
                if (hierarquiaOrganizacional == null) {
                    Tramite tramite = new Tramite();
                    tramite.setDataRegistro(new Date());
                    tramite.setProcesso(selecionado);
                    if (unidadesEncaminhamentosMultiplos.isEmpty()) {
                        tramite.setUnidadeOrganizacional(selecionado.getUoCadastro());
                    } else {
                        tramite.setUnidadeOrganizacional(unidadesEncaminhamentosMultiplos.get(0));
                    }
                    tramite.setOrigem(selecionado.getUoCadastro());
                    tramite.setSituacaoTramite(null);
                    int tamanho = 0;
                    if (selecionado.getTramites() != null) {
                        tamanho = selecionado.getTramites().size();
                    }
                    tramite.setIndice(tamanho);
                    tramite.setStatus(true);
                    tramite.setAceito(false);
                    tramite.setUsuarioTramite(getUsuarioCorrente());

                    gerarHistoricoProcesso(tramite.getProcesso(), Processo.PROTOCOLO_CRIADO, tramite.getUnidadeOrganizacional());
                    selecionado.getTramites().add(tramite);
                } else {
                    if ((hierarquiaOrganizacional != null) && (hierarquiaOrganizacional.getSubordinada() != null)) {
                        boolean jaTemUO = false;
                        for (Tramite traAux : selecionado.getTramites()) {
                            if (traAux.getUnidadeOrganizacional().equals(hierarquiaOrganizacional.getSubordinada())) {
                                jaTemUO = true;
                            }
                        }
                        if (!jaTemUO) {
                            Tramite tramite = new Tramite();
                            tramite.setDataRegistro(new Date());
                            tramite.setProcesso(selecionado);
                            tramite.setOrigem(selecionado.getUoCadastro());
                            tramite.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
                            tramite.setSituacaoTramite(null);
                            int tamanho = 0;
                            if (selecionado.getTramites() != null) {
                                tamanho = selecionado.getTramites().size();
                            }
                            tramite.setIndice(tamanho);
                            tramite.setStatus(true);
                            tramite.setAceito(false);
                            tramite.setUsuarioTramite(getUsuarioCorrente());
                            gerarHistoricoProcesso(tramite.getProcesso(), Processo.PROTOCOLO_CRIADO, tramite.getUnidadeOrganizacional());
                            selecionado.getTramites().add(tramite);
                        }
                    }
                }
            } else {
                multiplicarProtocolo(selecionado);
            }
        }
    }

    private void criarTramiteProtocoloExterno() {
        Tramite tramite = new Tramite();
        tramite.setDataRegistro(new Date());
        tramite.setProcesso(selecionado);
        tramite.setDestinoExterno(destinoExterno);
        tramite.setOrigem(selecionado.getUoCadastro());
        tramite.setSituacaoTramite(null);
        int tamanho = 0;
        if (selecionado.getTramites() != null) {
            tamanho = selecionado.getTramites().size();
        }
        tramite.setIndice(tamanho);
        tramite.setStatus(true);
        tramite.setAceito(false);
        tramite.setUsuarioTramite(getUsuarioCorrente());
        gerarHistoricoProcessoExterno(tramite.getProcesso(), Processo.PROTOCOLO_CRIADO, tramite.getDestinoExterno());
        selecionado.getTramites().add(tramite);
    }

    private void multiplicarProtocolo(Processo pr) {
        int qtde = 1;
        for (UnidadeOrganizacional uni : unidadesEncaminhamentosMultiplos) {
            Tramite tra = new Tramite();
            tra.setIndice(0);
            tra.setDataRegistro(new Date());
            tra.setUnidadeOrganizacional(uni);
            tra.setUsuarioTramite(getUsuarioCorrente());
            tra.setOrigem(pr.getUoCadastro());
            tra.setStatus(true);
            tra.setAceito(false);
            tra.setSituacaoTramite(null);

            if (qtde == 1) {
                tra.setEncaminhamentoMultiplo(true);
                tra.setProcesso(pr);
                gerarHistoricoProcesso(pr, Processo.PROTOCOLO_CRIADO, uni);
                pr.getTramites().add(tra);
            } else {
                Processo p = new Processo();
                p.setProcessoSuperior(pr);
                p.setAssunto(pr.getAssunto());
                p.setConfidencial(pr.getConfidencial());
                p.setDataRegistro(pr.getDataRegistro());
                p.setEncaminhamentoMultiplo(pr.getEncaminhamentoMultiplo());
                p.setExercicio(facade.getExercicioFacade().getExercicioPorAno(LocalDate.now().getYear()));
                p.setAno(LocalDate.now().getYear());
                p.setMotivo(pr.getMotivo());
                p.setNumeroProcessoAntigo(pr.getNumeroProcessoAntigo());
                p.setNumeroProcessoSAJ(pr.getNumeroProcessoSAJ());
                p.setObjetivo(pr.getObjetivo());
                p.setObservacoes(pr.getObservacoes());
                p.setPessoa(pr.getPessoa());
                p.setProtocolo(pr.getProtocolo());
                p.setResponsavelCadastro(pr.getResponsavelCadastro());
                p.setSenha(facade.geraSenha());
                p.setSituacao(pr.getSituacao());
                p.setTipoProcessoTramite(pr.getTipoProcessoTramite());
                p.setUoCadastro(pr.getUoCadastro());

                gerarHistoricoProcesso(p, Processo.PROTOCOLO_CRIADO, uni);
                for (ProcessoEnglobado englobado : pr.getProcessosEnglobados()) {
                    ProcessoEnglobado novoEnglobado = new ProcessoEnglobado();
                    novoEnglobado.setEnglobado(englobado.getEnglobado());
                    novoEnglobado.setProcesso(p);
                    p.getProcessosEnglobados().add(novoEnglobado);
                }
                for (DocumentoProcesso doc : pr.getDocumentoProcesso()) {
                    DocumentoProcesso novoDoc = new DocumentoProcesso();
                    novoDoc.setDataRegistro(doc.getDataRegistro());
                    novoDoc.setDocumento(doc.getDocumento());
                    novoDoc.setNumeroDocumento(doc.getNumeroDocumento());
                    novoDoc.setProcesso(p);
                    p.getDocumentoProcesso().add(novoDoc);
                }
                for (ProcessoArquivo arq : pr.getArquivos()) {
                    ProcessoArquivo novoArq = new ProcessoArquivo();
                    novoArq.setArquivo(arq.getArquivo());
                    novoArq.setProcesso(p);
                    p.getArquivos().add(novoArq);
                }
                tra.setProcesso(p);
                p.getTramites().add(tra);
                pr.getProcessosSubordinados().add(p);
            }
            qtde++;
        }
    }

    private void copiaDadosDoOrigem(Processo pr) {
        try {
            List<Processo> listaProtocolos = new ArrayList<>();

            for (Processo pm : pr.getProcessosSubordinados()) {
                Processo multiplo = facade.recuperar(pm.getId());
                multiplo.setAssunto(pr.getAssunto());
                multiplo.setConfidencial(pr.getConfidencial());
                multiplo.setEncaminhamentoMultiplo(pr.getEncaminhamentoMultiplo());
                if (multiplo.getExercicio() == null) {
                    multiplo.setExercicio(pr.getExercicio());
                    multiplo.setAno(pr.getAno());
                }
                multiplo.setMotivo(pr.getMotivo());
                multiplo.setNumeroProcessoAntigo(pr.getNumeroProcessoAntigo());
                multiplo.setNumeroProcessoSAJ(pr.getNumeroProcessoSAJ());
                multiplo.setObjetivo(pr.getObjetivo());
                multiplo.setObservacoes(pr.getObservacoes());
                multiplo.setPessoa(pr.getPessoa());
                multiplo.setTipoProcessoTramite(pr.getTipoProcessoTramite());
                multiplo.setUoCadastro(pr.getUoCadastro());
                multiplo.getDocumentoProcesso().clear();
                for (ProcessoEnglobado englobado : pr.getProcessosEnglobados()) {
                    ProcessoEnglobado novoEnglobado = new ProcessoEnglobado();
                    novoEnglobado.setEnglobado(englobado.getEnglobado());
                    novoEnglobado.setProcesso(multiplo);
                    multiplo.getProcessosEnglobados().add(novoEnglobado);
                }
                for (DocumentoProcesso doc : pr.getDocumentoProcesso()) {
                    DocumentoProcesso novoDoc = new DocumentoProcesso();
                    novoDoc.setDataRegistro(doc.getDataRegistro());
                    novoDoc.setDocumento(doc.getDocumento());
                    novoDoc.setNumeroDocumento(doc.getNumeroDocumento());
                    novoDoc.setProcesso(multiplo);
                    multiplo.getDocumentoProcesso().add(novoDoc);
                }
                multiplo.getArquivos().clear();
                for (ProcessoArquivo arq : pr.getArquivos()) {
                    ProcessoArquivo novoArq = new ProcessoArquivo();
                    novoArq.setArquivo(arq.getArquivo());
                    novoArq.setProcesso(multiplo);
                    multiplo.getArquivos().add(novoArq);
                }
                listaProtocolos.add(multiplo);
            }
            pr.getProcessosSubordinados().clear();
            for (Processo p : listaProtocolos) {
                pr.getProcessosSubordinados().add(p);
            }
        } catch (Exception e) {
            logger.error("ERRO: Erro ao alterar os protocolos multiplos. ", e);
        }
    }

    public List<Arquivo> getListaDocumentos(ActionEvent evt) {
        Processo pr = (Processo) evt.getComponent().getAttributes().get("arquivo");
        return facade.listaArquivosUpload(pr.getId());
    }

    public void removeFileUpload(ActionEvent event) {
        ProcessoArquivo arq = (ProcessoArquivo) event.getComponent().getAttributes().get("remove");
        selecionado.getArquivos().remove(arq);
    }

    public ProcessoFacade getFacade() {
        return facade;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        operacao = Operacoes.NOVO;
        facade.novoProcesso(selecionado, getSistemaControlador());
        documento = (DocumentoProcesso) Web.pegaDaSessao(DocumentoProcesso.class);
        if (documento == null) {
            documento = new DocumentoProcesso();
        }
        destinoExterno = (String) Web.pegaDaSessao(String.class);
        if (destinoExterno == null) {
            destinoExterno = "";
        }
        encaminhamentoMultiplo = (Boolean) Web.pegaDaSessao(Boolean.class);
        if (encaminhamentoMultiplo == null) {
            encaminhamentoMultiplo = false;
        }
        unidadesEncaminhamentosMultiplos = (List) Web.pegaDaSessao(List.class);
        if (unidadesEncaminhamentosMultiplos == null) {
            unidadesEncaminhamentosMultiplos = new ArrayList<>();
        }
        rootOrc = (TreeNode) Web.pegaDaSessao(TreeNode.class);
        if (rootOrc == null) {
            recuperaArvoreHierarquiaOrganizacionalOrc();
        }
        PessoaFisica pessoaPf = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        if (pessoaPf != null) {
            if (selecionado.getPessoa() == null) {
                selecionado.setPessoa(pessoaPf);
            }
        } else {
            PessoaJuridica pessoaPj = (PessoaJuridica) Web.pegaDaSessao(PessoaJuridica.class);
            if (pessoaPj != null) {
                if (selecionado.getPessoa() == null) {
                    selecionado.setPessoa(pessoaPj);
                }
            }
        }
        hierarquiaOrganizacional = (HierarquiaOrganizacional) Web.pegaDaSessao(HierarquiaOrganizacional.class);
        if (hierarquiaOrganizacional == null) {
            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        }
        unidadeSelecionada = new UnidadeOrganizacional();
        processoEnglobado = null;
    }

    public List<Tramite> getTramites() {
        List<Tramite> trm = selecionado.getTramites();
        if (trm != null) {
            Collections.sort(trm);
        }
        return trm;
    }

    private SistemaControlador getSistemaControlador() {
        return Util.getSistemaControlador();
    }

    public List<ProcessoHistorico> getProcessosHistorico() {
        List<ProcessoHistorico> proc = selecionado.getHistoricos();
        if (proc != null) {
            Collections.sort(proc);
        }
        return proc;
    }

    public List<SelectItem> getListaUOUsuarioLogado() {
        List<SelectItem> listaUOS = new ArrayList<>();
        listaUOS.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional uo : facade.getTramiteFacade().getUnidadeOrganizacionalFacade().buscarUnidadesUsuarioCorrenteProtocolo(getUsuarioCorrente())) {
            listaUOS.add(new SelectItem(uo.getSubordinada(), uo.toString()));
        }
        return listaUOS;
    }

    public void removeDocumento(ActionEvent evt) {
        DocumentoProcesso dec = (DocumentoProcesso) evt.getComponent().getAttributes().get("removeDocumento");
        selecionado.getDocumentoProcesso().remove(dec);
    }

    public List<Documento> completaDocumentos(String parte) {
        return facade.getDocumentoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterDocumento() {
        if (converterDoc == null) {
            converterDoc = new ConverterAutoComplete(Documento.class, facade.getDocumentoFacade());
        }
        return converterDoc;
    }

    public List<SelectItem> getTipoProcessoTramites() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoProcessoTramite object : TipoProcessoTramite.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return facade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            atribuirDocumento(documento);
            documento = new DocumentoProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (documento.getDocumento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Documento deve ser informado!");
        }
        if (documento.getNumeroDocumento().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Documento deve ser informado!");
        }
        ve.lancarException();
    }

    private void atribuirDocumento(DocumentoProcesso documentoProcesso) {
        documentoProcesso.setProcesso(selecionado);
        selecionado.getDocumentoProcesso().add(documentoProcesso);
    }

    @URLAction(mappingId = "editarProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        try {
            operacao = Operacoes.EDITAR;
            documento = new DocumentoProcesso();
            encaminhamentoMultiplo = selecionado.getEncaminhamentoMultiplo();
            unidadesEncaminhamentosMultiplos = new ArrayList<>();
            recuperaArvoreHierarquiaOrganizacionalOrc();
            atribuirHierarquiaCadastroProtocolo();
            atribuirHierarquiaDestino();
        } catch (Exception e) {
            logger.error("Erro ao editar o protocolo {}", e);
        }
    }

    private void atribuirHierarquiaDestino() {
        try {
            Tramite tramite = selecionado.getTramites().get(0);
            HierarquiaOrganizacional hoTramite = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), tramite.getUnidadeOrganizacional(), selecionado.getDataRegistro());
            setHierarquiaOrganizacional(hoTramite);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.debug("Não foi possível atribuir a unidade do primeiro tramite do protocolo {}", e);
        }
    }

    private void atribuirHierarquiaCadastroProtocolo() {
        HierarquiaOrganizacional hoCadastro = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUoCadastro(), selecionado.getDataRegistro());
        setHierarquiaProtocolo(hoCadastro);
    }

    @URLAction(mappingId = "verProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        consulta = false;
        recuperarProcessoAndAtualizarTela();
    }

    public boolean isUsuarioGestorUnidadeDestinoAndProcessoIncorporado(VoTramite tramite) {
        ProcessoEnglobado processoDoTramiteEnglobado = facade.buscarProcessoEnglobado(tramite.getProcesso().getId());
        if (tramite.getUnidadeOrganizacional() != null && processoDoTramiteEnglobado != null) {
            return facade.isUsuarioGestorUnidadeDestino(
                getUsuarioCorrente(),
                tramite.getUnidadeOrganizacional())
                && StatusProcessoEnglobado.INCORPORADO.equals(processoDoTramiteEnglobado.getStatus());
        }
        return false;
    }

    private void recuperarProcessoAndAtualizarTela() {
        processo = facade.buscarProcessoPeloEnglobado(selecionado);
        if (processo != null) {
            FacesUtil.atualizarComponente("FormVisualizar");
        }
    }

    public void desmembrarProcesso(VoTramite tramite) {
        processoEnglobado = facade.buscarProcessoEnglobado(tramite.getProcesso().getId());
        if (processoEnglobado != null) {
            processoEnglobado = facade.recuperarProcessoHistorico(processoEnglobado.getId());
            processoEnglobado.setDataDesmembramento(facade.getSistemaFacade().getDataOperacao());
            FacesUtil.atualizarComponente("formDesmembrar");
            FacesUtil.executaJavaScript("desmembrarProcesso.show()");
        }
    }

    public void confirmarDesmembramento() {
        try {
            validarDesmembramento();
            Tramite ultimoTramiteEnglobado = ultimoTramiteProcesso(processoEnglobado.getProcesso());
            facade.salvarProcessoEnglobadoProtocoloDesmembrado(processoEnglobado, ultimoTramiteEnglobado);
            FacesUtil.atualizarComponente("Formulario:tabsProtocolo");
            FacesUtil.addOperacaoRealizada("O " + processoEnglobado.getEnglobado() + " foi desmembrado com sucesso!");
            cancelarDesmembramento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarDesmembramento() {
        processoEnglobado = null;
        FacesUtil.executaJavaScript("desmembrarProcesso.hide()");
    }

    private void validarDesmembramento() {
        ValidacaoException ve = new ValidacaoException();
        if ("".equals(processoEnglobado.getMotivoDesmembramento()) || processoEnglobado.getMotivoDesmembramento().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo do Desmembramento é obrigatório!");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "verConsultaProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verConsulta() {
        super.ver();
        consulta = true;
        recuperarProcessoAndAtualizarTela();
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return facade.getUnidadeOrganizacionalFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public DocumentoProcesso getDocumento() {
        return documento;
    }

    public void setDocumento(DocumentoProcesso documento) {
        this.documento = documento;
    }

    public UnidadeOrganizacional getUnidadeSelecionada() {
        return unidadeSelecionada;
    }

    public void setUnidadeSelecionada(UnidadeOrganizacional unidadeSelecionada) {
        this.unidadeSelecionada = unidadeSelecionada;
    }

    public Boolean getEncaminhamentoMultiplo() {
        return encaminhamentoMultiplo;
    }

    public void setEncaminhamentoMultiplo(Boolean encaminhamentoMultiplo) {
        this.encaminhamentoMultiplo = encaminhamentoMultiplo;
    }

    public void imprimeCapaProtocolo() {
        try {
            facade.geraDocumento(TipoDocumentoProtocolo.CAPA_PROTOCOLO, selecionado, null, selecionado.getPessoa());
        } catch (Exception ex) {
            logger.error("imprimeCapaProtocolo ", ex);
        }
    }

    public void imprimeCapaProtocolo(Processo p) {
        try {
            Processo protocolo = facade.recuperar(p.getId());
            facade.geraDocumento(TipoDocumentoProtocolo.CAPA_PROTOCOLO, protocolo, null, protocolo.getPessoa());
        } catch (Exception ex) {
            logger.error("imprimeCapaProtocolo ", ex);
        }
    }

    public void recuperaArvoreHierarquiaOrganizacionalOrc() {
        unidadeSemUsuarioCadastrado = facade.getUsuarioSistemaFacade().listaUnidadeSemUsuarioCadastrado();
        rootOrc = facade.getSingletonHO().getArvoreAdministrativa(facade.getSistemaFacade().getDataOperacao());
    }

    public void selecionarHierarquiaDaArvore() {
        if (selectedNode != null) {
            hierarquiaOrganizacional = (HierarquiaOrganizacional) selectedNode.getData();
            if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(hierarquiaOrganizacional.getSubordinada()).isEmpty()) {
                FacesUtil.addError("Atenção", "Não é possível encaminhar um protocolo para uma unidade organizacional que não possua usuários cadastrados!");
                hierarquiaOrganizacional = null;
            }
        }
    }

    public void validaUnidadeSelecionada() {
        if (hierarquiaOrganizacional != null) {
            if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(hierarquiaOrganizacional.getSubordinada()).isEmpty()) {
                FacesUtil.addError("Atenção!", "Não é possível encaminhar um protocolo para uma unidade organizacional que não possua usuários cadastrados!");
                hierarquiaOrganizacional = null;
            }
        }
    }

    public void removeArquivo(ActionEvent evento) {
        ProcessoArquivo arq = (ProcessoArquivo) evento.getComponent().getAttributes().get("objeto");
        String descricao = getDescricaoAnexoRemovido(arq);
        ProcessoHistorico historico = gerarHistoricoAnexo(descricao);
        selecionado.getHistoricos().add(historico);
        selecionado.getArquivos().remove(arq);
        selecionado = facade.salvarArquivosProcesso(selecionado);
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(facade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            ProcessoArquivo proArq = new ProcessoArquivo();
            proArq.setArquivo(facade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setProcesso(selecionado);

            String descricao = getDescricaoAnexoAdicionado(proArq);
            ProcessoHistorico historico = gerarHistoricoAnexo(descricao);
            selecionado.getHistoricos().add(historico);
            selecionado.getArquivos().add(proArq);
            selecionado = facade.salvarArquivosProcesso(selecionado);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    private String getDescricaoAnexoAdicionado(ProcessoArquivo processoArquivo) {
        return Processo.ANEXO_ADICIONADO + " - Nome: " + processoArquivo.getArquivo().getNome() + " - Tamanho: " + processoArquivo.getArquivo().getTamanho() + ".";
    }

    private String getDescricaoAnexoRemovido(ProcessoArquivo processoArquivo) {
        return Processo.ANEXO_REMOVIDO + " - Nome: " + processoArquivo.getArquivo().getNome() + " - Tamanho: " + processoArquivo.getArquivo().getTamanho() + ".";
    }

    private ProcessoHistorico gerarHistoricoAnexo(String descricao) {
        return selecionado.criarHistoricoProcesso(descricao, selecionado.getUoCadastro(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public boolean temProtocolosMultiplos() {
        return selecionado.getProcessosSubordinados() != null && !selecionado.getProcessosSubordinados().isEmpty();
    }

    public List<ProcessoEnglobado> getTramitesIncorporados() {
        List<ProcessoEnglobado> tramitesIncorporados = Lists.newArrayList();
        for (ProcessoEnglobado processoEnglobadoSelecionado : selecionado.getProcessosEnglobados()) {
            Util.adicionarObjetoEmLista(tramitesIncorporados, processoEnglobadoSelecionado);
        }
        return tramitesIncorporados;
    }

    public void visualizarProcessoPeloEnglobado(ProcessoEnglobado processoEnglobado) {
        FacesUtil.redirecionamentoInterno("/protocolo/ver/" + processoEnglobado.getEnglobado().getId());
    }

    public void visualizarProcesso(Processo processo) {
        FacesUtil.redirecionamentoInterno("/protocolo/ver/" + processo.getId());
    }

    public Boolean habilitarMensagemIncorporado() {
        return processo != null;
    }

    public boolean protocoloMultiplo() {
        if (selecionado != null) {
            return selecionado.getProcessoSuperior() != null;
        }
        return false;
    }

    private boolean algumProtocoloMultiploJaAceito() {
        boolean retorno = false;
        for (Processo p : selecionado.getProcessosSubordinados()) {
            if (!p.getSituacao().equals(SituacaoProcesso.GERADO)) {
                retorno = true;
            }
        }
        return retorno;
    }

    public boolean podeEditarProtocolo() {
        return !algumProtocoloMultiploJaAceito() && podeEditar();
    }

    public boolean podeEditar() {
        boolean retorno = false;
        if (facade.getUsuarioSistemaFacade().isGestorProtocolo(facade.getSistemaFacade().getUsuarioCorrente(), selecionado.getUoCadastro(), selecionado.getDataRegistro())) {
            retorno = true;
        }
        if (!SituacaoProcesso.GERADO.equals(selecionado.getSituacao())) {
            retorno = false;
        }
        return retorno;
    }

    public boolean podeFazerDownload() {
        for (Tramite tra : selecionado.getTramites()) {
            UnidadeOrganizacional unidade = tra.getProcesso().isProcessoInterno() ? tra.getUnidadeOrganizacional() : tra.getProcesso().getUoCadastro();
            if (unidade != null && facade.getUsuarioSistemaFacade().isGestorProtocolo(facade.getSistemaFacade().getUsuarioCorrente(), unidade, selecionado.getDataRegistro())) {
                return true;
            }
        }
        return facade.getUsuarioSistemaFacade().isGestorProtocolo(facade.getSistemaFacade().getUsuarioCorrente(), selecionado.getUoCadastro(), selecionado.getDataRegistro());
    }

    public boolean getInterno() {
        if (selecionado != null) {
            if (selecionado.getTipoProcessoTramite() != null) {
                return selecionado.getTipoProcessoTramite().equals(TipoProcessoTramite.INTERNO);
            }
        }
        return true;

    }

    public boolean getExterno() {
        if (selecionado != null) {
            if (selecionado.getTipoProcessoTramite() != null) {
                return selecionado.getTipoProcessoTramite().equals(TipoProcessoTramite.EXTERNO);
            }
        }
        return false;
    }

    public String getDestinoExterno() {
        return destinoExterno;
    }

    public void setDestinoExterno(String destinoExterno) {
        this.destinoExterno = destinoExterno;
    }

    public void pesquisarProtocolosGerados() {
        filtrosGerados.limparPaginas();
        buscarProtocolosGerados();
        montarPaginasGerados();
    }

    public void pesquisarTramitesPendentes() {
        filtrosTramitesPendentes.limparPaginas();
        buscarTramitesPendentes();
        montarPaginasTramitesPendentes();
        pesquisarTramitesAceitos();
    }

    public void pesquisarTramitesAceitos() {
        filtrosTramitesAceitos.limparPaginas();
        buscarTramitesAceitos();
        montarPaginasTramitesAceitos();
    }

    public void pesquisarProtocolosEncaminhados() {
        filtrosEncaminhados.limparPaginas();
        buscarProtocolosEncaminhados();
        montarPaginasEncaminhados();
    }

    public void pesquisarProtocolosCancelados() {
        filtrosCancelados.limparPaginas();
        buscarProtocolosCancelados();
        montarPaginasCancelados();
    }

    private void montarPaginasCancelados() {
        filtrosCancelados.getFiltro().setGestor(true);
        filtrosCancelados.getFiltro().setProtocolo(true);
        filtrosCancelados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosCancelados.montarPaginas(facade.quantidadeDeProcessosCancelados(filtrosCancelados.getFiltro()));
    }

    private void buscarProtocolosCancelados() {
        filtrosCancelados.getFiltro().setGestor(true);
        filtrosCancelados.getFiltro().setConfidencial(true);
        filtrosCancelados.getFiltro().setProtocolo(true);
        filtrosCancelados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosCancelados.getFiltro().setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        filtrosCancelados.setProcessos(facade.buscarProcessosCancelados(
            filtrosCancelados.getFiltro(),
            filtrosCancelados.getInicioConsulta(),
            filtrosCancelados.getQuantidadeRegistros().getQuantidade()));
    }

    private void buscarProtocolosGerados() {
        filtrosGerados.getFiltro().setGestor(true);
        filtrosGerados.getFiltro().setConfidencial(true);
        filtrosGerados.getFiltro().setProtocolo(true);
        filtrosGerados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosGerados.getFiltro().setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        filtrosGerados.setProcessos(facade.buscarProcessosGerados(
            filtrosGerados.getFiltro(),
            filtrosGerados.getInicioConsulta(),
            filtrosGerados.getQuantidadeRegistros().getQuantidade()));
    }

    public void pesquisarProcessosFinalizados() {
        filtrosFinalizados.limparPaginas();
        buscarProcessosFinalizados();
        montarPaginasFinalizados();
    }

    private void buscarProcessosFinalizados() {
        filtrosFinalizados.getFiltro().setProtocolo(true);
        filtrosFinalizados.getFiltro().setGestor(true);
        filtrosFinalizados.getFiltro().setUsuarioSistema(getUsuarioCorrente());

        filtrosFinalizados.setProcessos(facade.buscarProcessosFinalizados(
            filtrosFinalizados.getFiltro(),
            filtrosFinalizados.getInicioConsulta(),
            filtrosFinalizados.getQuantidadeRegistros().getQuantidade()));
    }

    public void pesquisarProtocolosGuiaRecebimento() {
        try {
            validarHierarquiaOrganizacional();
            if (hierarquiaOrganizacional != null) {
                filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            }
            filtrosRecebimentoMultiplo.validarPesquisaGuiaRecebimento();
            protocolosSelecionadosGuiaRecebimento = Lists.newArrayList();
            filtrosRecebimentoMultiplo.limparPaginas();
            buscarProcessosEncaminhadosParaEncaminhamentoMultiplo();
            montarPaginasProcessosEncaminhadosParaEncaminhamentoMultiplo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void montarPaginasProcessosEncaminhadosParaEncaminhamentoMultiplo() {
        FiltroListaProtocolo filtro = atribuirFiltroProtocoloEncaminhadoParaEncaminhamentoMultiplo();
        Integer total = facade.quantidadeProtocolosEncaminhadosParaGuiRecebimentoMultiplo(filtro, filtrosRecebimentoMultiplo.getInicioConsulta(), filtrosRecebimentoMultiplo.getQuantidadeRegistros().getQuantidade());
        filtrosRecebimentoMultiplo.montarPaginas(total);
    }

    private FiltroListaProtocolo atribuirFiltroProtocoloEncaminhadoParaEncaminhamentoMultiplo() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo("", "", "", getUsuarioCorrente());
        filtro.setGestor(true);
        filtro.setProtocolo(true);
        filtro.setUnidadeOrganizacional(filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().getUnidadeOrganizacional());
        filtro.setDestinoExterno(filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().getDestinoExterno());
        return filtro;
    }

    private void buscarProcessosEncaminhadosParaEncaminhamentoMultiplo() {
        FiltroListaProtocolo filtro = atribuirFiltroProtocoloEncaminhadoParaEncaminhamentoMultiplo();
        filtrosRecebimentoMultiplo.setProcessos(facade.buscarProtocolosEncaminhadosParaGuiRecebimentoMultiplo(
            filtro,
            filtrosRecebimentoMultiplo.getInicioConsulta(),
            filtrosRecebimentoMultiplo.getQuantidadeRegistros().getQuantidade()));
        filtrosRecebimentoMultiplo.setProcessos(facade.atualizarUnidadeAtualProcessos(filtrosRecebimentoMultiplo.getProcessos()));
    }

    private void validarHierarquiaOrganizacional() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade de Destino deve ser informado.");
        }
        ve.lancarException();
    }

    public void pesquisarProtocolosArquivados() {
        filtrosArquivados.limparPaginas();
        buscarProtocolosArquivados();
        montarPaginasArquivados();
    }

    private void buscarProtocolosArquivados() {
        filtrosArquivados.getFiltro().setProtocolo(true);
        filtrosArquivados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosArquivados.getFiltro().setGestor(true);
        filtrosArquivados.getFiltro().setStatus(true);

        filtrosArquivados.setProcessos(facade.buscarProcessosArquivados(
            filtrosArquivados.getFiltro(),
            filtrosArquivados.getInicioConsulta(),
            filtrosArquivados.getQuantidadeRegistros().getQuantidade()));
    }

    public void reabrirProtocoloFinalizado(VoProcesso voProcesso) {
        try {
            Tramite ultimoTramiteProcesso = facade.getTramiteFacade().buscarUltimoTramite(voProcesso.getId());
            novoTramite = (Tramite) Util.clonarObjeto(ultimoTramiteProcesso);

            if (novoTramite != null) {
                novoTramite.setId(null);
                novoTramite.setDataRegistro(new Date());
                novoTramite.setStatus(true);
                novoTramite.setAceito(false);
                novoTramite.setDataAceite(null);
                novoTramite.setDataConclusao(null);
                novoTramite.setUsuarioTramite(getUsuarioCorrente());
                novoTramite.setMotivo(null);
                novoTramite.setIndice(novoTramite.getIndice() + 1);

                processo = novoTramite.getProcesso();
                processo.setTramiteFinalizador(null);
                processo.setSituacao(SituacaoProcesso.EMTRAMITE);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarReaberturaProtocolo() {
        try {
            validarCampoObrigatorioReabertura();
            facade.salvarReaberturaProtocolo(processo, novoTramite);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.executaJavaScript("dlgReabrirProtocolo.hide()");
            pesquisarProcessosFinalizados();
            FacesUtil.atualizarComponente("Formulario:tabsProtocolo:tabelaFinalizados");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao tentar reabrir protocolo. {}", e.getMessage());
            logger.debug("Detalhes do erro ao tentar reabrir protocolo.", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível reabrir o protocolo. Detalhes: " + e.getMessage());
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        }
    }

    public void cancelarReaberturaProtocolo() {
        novoTramite = null;
    }

    public void validarCampoObrigatorioReabertura() {
        ValidacaoException ve = new ValidacaoException();
        if (novoTramite.getMotivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo precisa ser informado.");
        }
        ve.lancarException();
    }

    public void abrirGuiaRecebimentoMultiplo() {
        carregarDadosGuiaRecebimentoMultiplo();
        FacesUtil.executaJavaScript("guiaRecebimentoMultiplo.show()");
    }

    public void fecharGuiaRecebimentoMultiplo() {
        filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().setTipoProcesso(TipoProcessoTramite.INTERNO);
        FacesUtil.executaJavaScript("guiaRecebimentoMultiplo.hide()");
    }

    public void carregarDadosGuiaRecebimentoMultiplo() {
        if (filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento() == null) {
            filtrosRecebimentoMultiplo.setFiltroGuiaRecebimento(new FiltroGuiaRecebimento());
        }
        filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().setDestinoExterno(null);
        filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().setUnidadeOrganizacional(null);
        hierarquiaOrganizacional = null;
        filtrosRecebimentoMultiplo.setProcessos(new ArrayList<VoProcesso>());
        protocolosSelecionadosGuiaRecebimento = Lists.newArrayList();
    }

    private void buscarProtocolosEncaminhados() {
        filtrosEncaminhados.getFiltro().setProtocolo(true);
        filtrosEncaminhados.getFiltro().setGestor(true);
        filtrosEncaminhados.getFiltro().setUnidadeOrganizacional(null);
        filtrosEncaminhados.getFiltro().setDestinoExterno(null);

        filtrosEncaminhados.setProcessos(facade.buscarProcessosProtocoloEncaminhados(
            filtrosEncaminhados.getFiltro(),
            filtrosEncaminhados.getInicioConsulta(),
            filtrosEncaminhados.getQuantidadeRegistros().getQuantidade()));
    }

    private void buscarTramitesPendentes() {
        atribuirFiltrosTramitePendentes();
        filtrosTramitesPendentes.setTramites(facade.getTramiteFacade().buscarTramitesPendentesUnidadeProcessoProtocolo(
            filtrosTramitesPendentes.getFiltro(),
            filtrosTramitesPendentes.getInicioConsulta(),
            filtrosTramitesPendentes.getQuantidadeRegistros().getQuantidade()));
    }

    private void atribuirFiltrosTramitePendentes() {
        filtrosTramitesPendentes.getFiltro().setAceito(false);
        filtrosTramitesPendentes.getFiltro().setStatus(true);
        filtrosTramitesPendentes.getFiltro().setGestor(true);
        filtrosTramitesPendentes.getFiltro().setProtocolo(true);
    }

    private void buscarTramitesAceitos() {
        atribuirFiltrosBuscaTramitesAceitos();
        filtrosTramitesAceitos.setTramites(facade.getTramiteFacade().buscarProtocoloAceitoRecebidoPorUsuario(
            filtrosTramitesAceitos.getFiltro(),
            filtrosTramitesAceitos.getInicioConsulta(),
            filtrosTramitesAceitos.getQuantidadeRegistros().getQuantidade()));
    }

    private void atribuirFiltrosBuscaTramitesAceitos() {
        filtrosTramitesAceitos.setFiltro(new FiltroListaProtocolo(
            filtrosTramitesPendentes.getFiltro().getNumero(),
            filtrosTramitesPendentes.getFiltro().getAno(),
            filtrosTramitesPendentes.getFiltro().getSolicitante(),
            getUsuarioCorrente()
        ));
        filtrosTramitesAceitos.getFiltro().setAceito(true);
        filtrosTramitesAceitos.getFiltro().setProtocolo(true);
        filtrosTramitesAceitos.getFiltro().setStatus(true);
        filtrosTramitesAceitos.getFiltro().setGestor(true);
    }

    public void alterarTabs(TabChangeEvent evt) {
        int tab = ((TabView) evt.getComponent()).getActiveIndex();
        switch (tab) {
            case 0: {
                pesquisarProtocolosGerados();
                break;
            }
            case 1: {
                pesquisarTramitesPendentes();
                break;
            }
            case 2: {
                pesquisarProtocolosEncaminhados();
                break;
            }
            case 3: {
                pesquisarProtocolosCancelados();
                break;
            }
            case 4: {
                pesquisarProtocolosArquivados();
                break;
            }
            case 5: {
                pesquisarProcessosFinalizados();
                break;
            }
        }
    }

    @URLAction(mappingId = "listarProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparPesquisa() {
        filtrosGerados = new FiltrosPesquisaProtocolo();
        filtrosTramitesPendentes = new FiltrosPesquisaProtocolo();
        filtrosTramitesAceitos = new FiltrosPesquisaProtocolo();
        filtrosRecebimentoMultiplo = new FiltrosPesquisaProtocolo();
        filtrosEncaminhados = new FiltrosPesquisaProtocolo();
        filtrosArquivados = new FiltrosPesquisaProtocolo();
        filtrosFinalizados = new FiltrosPesquisaProtocolo();
        filtrosCancelados = new FiltrosPesquisaProtocolo();
        inicializarFiltros();
        pesquisarProtocolosGerados();
    }

    private void inicializarFiltros() {
        Calendar cal = Calendar.getInstance();
        filtrosGerados.setFiltro(new FiltroListaProtocolo("", cal.get(Calendar.YEAR) + "", "", getUsuarioCorrente()));
        filtrosEncaminhados.setFiltro(new FiltroListaProtocolo("", cal.get(Calendar.YEAR) + "", "", getUsuarioCorrente()));
        filtrosCancelados.setFiltro(new FiltroListaProtocolo("", cal.get(Calendar.YEAR) + "", "", getUsuarioCorrente()));
        filtrosTramitesPendentes.setFiltro(new FiltroListaProtocolo("", cal.get(Calendar.YEAR) + "", "", getUsuarioCorrente()));
        filtrosTramitesPendentes.setFiltroGuiaRecebimento(new FiltroGuiaRecebimento());
        filtrosRecebimentoMultiplo.setFiltroGuiaRecebimento(new FiltroGuiaRecebimento());
        filtrosArquivados.setFiltro(new FiltroListaProtocolo("", cal.get(Calendar.YEAR) + "", "", getUsuarioCorrente()));
        filtrosFinalizados.setFiltro(new FiltroListaProtocolo("", cal.get(Calendar.YEAR) + "", "", getUsuarioCorrente()));
    }

    private UsuarioSistema getUsuarioCorrente() {
        return facade.getSistemaFacade().getUsuarioCorrente();
    }

    public void validaCPFCNPJPessoa() {
        Pessoa pes = selecionado.getPessoa();
        if (pes instanceof PessoaFisica) {
            if (!valida_CpfCnpj(((PessoaFisica) pes).getCpf())) {
                FacesUtil.executaJavaScript("confirmEditarPessoaFisica.show()");
            } else if (facade.getPessoaFacade().hasOutraPessoaComMesmoCpf((PessoaFisica) pes, true)) {
                FacesUtil.addWarn("Atenção!", "O CPF da pessoa selecionada pertence a mais de uma pessoa física!");
                selecionado.setPessoa(null);
            }
        } else {
            if (!valida_CpfCnpj(((PessoaJuridica) pes).getCnpj())) {
                FacesUtil.executaJavaScript("confirmEditarPessoaJuridica.show()");
            } else if (facade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) pes, true)) {
                FacesUtil.addWarn("Atenção!", "O CNPJ da pessoa selecionada pertence a mais de uma pessoa jurídica!");
                selecionado.setPessoa(null);
            }
        }
    }

    public boolean valida_CpfCnpj(String s_aux) {
        if (s_aux == null) {
            return false;
        }
        s_aux = s_aux.replace(".", "");
        s_aux = s_aux.replace("-", "");
        s_aux = s_aux.replace("/", "");
//------- Rotina para CPF
        if (s_aux.length() == 11) {
            if (Sets.newHashSet("00000000000", "11111111111", "999999999").contains(s_aux)) {
                return false;
            }
            int d1, d2;
            int digito1, digito2, resto;
            int digitoCPF;
            String nDigResult;
            d1 = d2 = 0;
            digito1 = digito2 = resto = 0;
            for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count));
                //--------- Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                d1 = d1 + (11 - n_Count) * digitoCPF;
                //--------- Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                d2 = d2 + (12 - n_Count) * digitoCPF;
            }
            //--------- Primeiro resto da divisão por 11.
            resto = (d1 % 11);
            //--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
            if (resto < 2) {
                digito1 = 0;
            } else {
                digito1 = 11 - resto;
            }
            d2 += 2 * digito1;
            //--------- Segundo resto da divisão por 11.
            resto = (d2 % 11);
            //--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
            if (resto < 2) {
                digito2 = 0;
            } else {
                digito2 = 11 - resto;
            }
            //--------- Digito verificador do CPF que está sendo validado.
            String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
            //--------- Concatenando o primeiro resto com o segundo.
            nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
            //--------- Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
            return nDigVerific.equals(nDigResult);
        } //-------- Rotina para CNPJ
        else if (s_aux.length() == 14) {
            if (Sets.newHashSet("00000000000000", "11111111111111", "999999999999").contains(s_aux)) {
                return false;
            }
            int soma = 0, dig;
            String cnpj_calc = s_aux.substring(0, 12);
            char[] chr_cnpj = s_aux.toCharArray();
            //--------- Primeira parte
            for (int i = 0; i < 4; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
            //--------- Segunda parte
            soma = 0;
            for (int i = 0; i < 5; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
            return s_aux.equals(cnpj_calc);
        } else {
            return false;
        }
    }

    public void geraGuiaRecebimentoVisualizar() {
        ProtocoloImpressao guia = new ProtocoloImpressao();
        try {
            guia.geraGuiaRecebimentoProtocolo(selecionado);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void gerarGuiaRecebimento(Tramite tra) {
        ProtocoloImpressao guia = new ProtocoloImpressao();
        try {
            Boolean encMult = false;
            if (tra.getEncaminhamentoMultiplo() != null) {
                encMult = tra.getEncaminhamentoMultiplo();
            }
            guia.gerarGuiaRecebimentoTramite(tra, encMult);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarEmissaoGuiaRecebimento() {
        ValidacaoException ve = new ValidacaoException();
        if (filtrosTramitesPendentes.getFiltroGuiaRecebimento().getUnidadeOrganizacional() == null && "".equals(filtrosTramitesPendentes.getFiltroGuiaRecebimento().getDestinoExterno())) {
            ve.adicionarMensagemDeCampoObrigatorio("A unidade de destino deve ser informada!");
        }
        if (protocolosSelecionadosGuiaRecebimento.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ao menos um protocolo deve ser selecionado!");
        }
        ve.lancarException();
    }

    private void ordenarProcessos(List<VoProcesso> processos) {
        Comparator<VoProcesso> comparator = new Comparator<VoProcesso>() {
            @Override
            public int compare(VoProcesso um, VoProcesso dois) {
                int i = dois.getAno().compareTo(um.getAno());
                if (i != 0) {
                    return i;
                }
                i = dois.getNumero().compareTo(um.getNumero());
                if (i != 0) {
                    return i;
                }
                return 0;
            }
        };
        Collections.sort(processos, comparator);
    }

    public void gerarGuiaRecebimentoMultiplo() {
        ProtocoloImpressao guia = new ProtocoloImpressao();
        try {
            validarEmissaoGuiaRecebimento();
            ordenarProcessos(protocolosSelecionadosGuiaRecebimento);
            List<Tramite> tramites = Lists.newArrayList();
            for (VoProcesso processoSelecionado : protocolosSelecionadosGuiaRecebimento) {
                List<Tramite> tramitesDoProtocolo = facade.buscarTramitesDoProcesso(processoSelecionado.getId());
                for (Tramite tramite : tramitesDoProtocolo) {
                    if (!tramite.getAceito()) {
                        tramites.add(tramite);
                    }
                }
            }
            if (!tramites.isEmpty()) {
                protocolosSelecionadosGuiaRecebimento = facade.atualizarUnidadeAtualProcessos(protocolosSelecionadosGuiaRecebimento);
                guia.gerarGuiaRecebimentoMultiplo(tramites, protocolosSelecionadosGuiaRecebimento.get(0).getUnidadeAtual());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro: {}", ex);
        }
    }

    public boolean getEditandoProtocolo() {
        return operacao != null && operacao.equals(Operacoes.EDITAR);
    }

    public String descricaoPrimeiroTramite(Processo processo) {
        Processo p = facade.recuperar(processo.getId());
        if (!p.getTramites().isEmpty()) {
            Tramite t = p.getTramites().get(0);
            return t.getDestinoTramite();
        }
        return "";
    }

    public void gerarGuiaRecebimentoUltimoTramite(Processo processo) {
        Tramite ultimo = ultimoTramiteProcesso(processo);
        if (ultimo != null) {
            ProtocoloImpressao guia = new ProtocoloImpressao();
            try {
                Boolean encMult = false;
                if (ultimo.getEncaminhamentoMultiplo() != null) {
                    encMult = ultimo.getEncaminhamentoMultiplo();
                }
                guia.gerarGuiaRecebimentoTramite(ultimo, encMult);
            } catch (WebReportRelatorioExistenteException e) {
                ReportService.getInstance().abrirDialogConfirmar(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
                FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            }
        } else {
            FacesUtil.addError("Não é possível imprimir a Guia!", "Esse protocolo não possuí trâmites!");
        }
    }

    public Tramite recuperarUltimoTramiteProcesso(VoProcesso voProcesso) {
        Processo processoRecurado = facade.recuperar(voProcesso.getId());
        if (processoRecurado.getTramites() != null && processoRecurado.getTramites().size() > 0) {
            return processoRecurado.getTramites().get(processoRecurado.getTramites().size() - 1);
        }
        return null;
    }

    public Tramite ultimoTramiteProcesso(Processo processo) {
        Processo p = facade.recuperar(processo.getId());
        if (p.getTramites() != null && p.getTramites().size() > 0) {
            return p.getTramites().get(p.getTramites().size() - 1);
        }
        return null;
    }

    public String caminhoTramitar() {
        return "/tributario/cadastromunicipal/protocolo/edita.xhtml";
    }

    public boolean processoFinalizado() {
        return selecionado.getTramiteFinalizador() != null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/protocolo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void navegaColocandoNaSessa(String origem, String destino) {
        Web.navegacao(origem, destino,
            selecionado,
            documento,
            destinoExterno,
            encaminhamentoMultiplo,
            unidadesEncaminhamentosMultiplos,
            rootOrc,
            hierarquiaOrganizacional);
    }

    public void novoTipoDocumento() {
        String origem = getCaminhoPadrao() + "novo/";
        if (operacao.equals(Operacoes.EDITAR)) {
            origem = getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        navegaColocandoNaSessa(origem, "/documento/novo/");
    }

    public void movimentarArquivado(VoProcesso voProcesso) {
        Tramite ultimoTramite = recuperarUltimoTramiteProcesso(voProcesso);
        Web.navegacao(getCaminhoPadrao() + "listar/", "/tramite/movimentar/" + ultimoTramite.getId() + "/");
    }

    public void cancelarConsulta() {
        Web.navegacao(getCaminhoPadrao(), getCaminhoPadrao() + "consultar/");
    }

    public void novaPessoaFisica() {
        navegaColocandoNaSessa(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/novapessoafisica/");
    }

    public void novaPessoaJuridica() {
        navegaColocandoNaSessa(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/novapessoajuridica/");
    }

    public void alteraPessoaFisica() {
        navegaColocandoNaSessa(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/editarpessoafisica/" + selecionado.getPessoa().getId() + "/");
    }

    public void alteraPessoaJuridica() {
        navegaColocandoNaSessa(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/editarpessoajuridica/" + selecionado.getPessoa().getId() + "/");
    }

    public String styleNaoTemUsuario(UnidadeOrganizacional unidade) {
        if (unidadeSemUsuarioCadastrado.contains(unidade)) {
            return "color: blue";
        }
        return "color: #000000";
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativaComUsuarios(String parte) {
        return facade.getUsuarioSistemaFacade().filtrandoHierarquiaOrganizacionalAdministrativaComUsuarios(parte.trim(), facade.getSistemaFacade().getDataOperacao());
    }

    public List<UsuarioSistema> buscarUsuariosDaUnidadeOrganizacionalProtocolo() {
        if (hierarquiaOrganizacional != null && hierarquiaOrganizacional.getSubordinada() != null) {
            return facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(hierarquiaOrganizacional.getSubordinada());
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacionalGestorProtocolo(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiasUsuarioCorrenteProtocolo(getUsuarioCorrente(), parte.trim());
    }

    private void montarPaginasGerados() {
        filtrosGerados.getFiltro().setGestor(true);
        filtrosGerados.getFiltro().setProtocolo(true);
        filtrosGerados.getFiltro().setConfidencial(true);
        filtrosGerados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosGerados.getFiltro().setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        filtrosGerados.montarPaginas(facade.quantidadeTotalDeProcessosGerados(filtrosGerados.getFiltro()));
    }

    private void montarPaginasRecebimentoMultiplo() {
        filtrosRecebimentoMultiplo.montarPaginas(facade.quantidadeDeEncaminhamentoMultiplo(
            getUsuarioCorrente(),
            null, null, null,
            true,
            filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().getUnidadeOrganizacional(),
            filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().getDestinoExterno()));
    }

    private void montarPaginasEncaminhados() {
        filtrosEncaminhados.getFiltro().setProtocolo(true);
        filtrosEncaminhados.getFiltro().setGestor(true);
        filtrosEncaminhados.montarPaginas(facade.quantidadeDeProcessosEncaminhados(filtrosEncaminhados.getFiltro()));
    }

    private void montarPaginasArquivados() {
        filtrosArquivados.getFiltro().setGestor(true);
        filtrosArquivados.getFiltro().setProtocolo(true);
        filtrosArquivados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosArquivados.montarPaginas(facade.quantidadeDeProcessosArquivados(filtrosArquivados.getFiltro()));
    }

    private void montarPaginasFinalizados() {
        filtrosFinalizados.getFiltro().setGestor(true);
        filtrosFinalizados.getFiltro().setProtocolo(true);
        filtrosFinalizados.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosFinalizados.montarPaginas(facade.quantidadeDeProcessosFinalizados(filtrosFinalizados.getFiltro()));
    }

    private void montarPaginasTramitesPendentes() {
        atribuirFiltrosTramitePendentes();
        filtrosTramitesPendentes.montarPaginas(facade.getTramiteFacade().quantidadeTotalDeTramitesPendentes(filtrosTramitesPendentes.getFiltro()));
    }

    private void montarPaginasTramitesAceitos() {
        atribuirFiltrosBuscaTramitesAceitos();
        filtrosTramitesAceitos.montarPaginas(facade.getTramiteFacade().quantidadeTotalDeTramitesAceitos(filtrosTramitesAceitos.getFiltro()));
    }

    public void alterouPaginaGerados() {
        try {
            filtrosGerados.alterarPagina();
            buscarProtocolosGerados();
        } catch (ValidacaoException ve) {
            filtrosGerados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosGerados.resetarPaginas();
        }
    }

    public void alterouPaginaRecebimentoMultiplo() {
        try {
            filtrosRecebimentoMultiplo.validarPesquisaGuiaRecebimento();
            filtrosRecebimentoMultiplo.alterarPagina();
            buscarProcessosEncaminhadosParaEncaminhamentoMultiplo();
        } catch (ValidacaoException ve) {
            filtrosRecebimentoMultiplo.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosRecebimentoMultiplo.resetarPaginas();
        }
    }

    public void alterouPaginaTramitesPendentes() {
        try {
            filtrosTramitesPendentes.alterarPagina();
            buscarTramitesPendentes();
        } catch (ValidacaoException ve) {
            filtrosTramitesPendentes.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesPendentes.resetarPaginas();
        }
    }

    public void alterouPaginaTramitesAceitos() {
        try {
            filtrosTramitesAceitos.alterarPagina();
            buscarTramitesAceitos();
        } catch (ValidacaoException ve) {
            filtrosTramitesAceitos.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesAceitos.resetarPaginas();
        }
    }

    public void alterouPaginaEncaminhados() {
        try {
            filtrosEncaminhados.alterarPagina();
            buscarProtocolosEncaminhados();
        } catch (ValidacaoException ve) {
            filtrosEncaminhados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosEncaminhados.resetarPaginas();
        }
    }

    public void alterouPaginaCancelados() {
        try {
            filtrosCancelados.alterarPagina();
            buscarProtocolosCancelados();
        } catch (ValidacaoException ve) {
            filtrosCancelados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosCancelados.resetarPaginas();
        }
    }

    public void alterouPaginaArquivados() {
        try {
            filtrosArquivados.alterarPagina();
            buscarProtocolosArquivados();
        } catch (ValidacaoException ve) {
            filtrosArquivados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosArquivados.resetarPaginas();
        }
    }

    public void alterouPaginaFinalizados() {
        try {
            filtrosFinalizados.alterarPagina();
            buscarProcessosFinalizados();
        } catch (ValidacaoException ve) {
            filtrosFinalizados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosFinalizados.resetarPaginas();
        }
    }

    public void proximosGerados() {
        filtrosGerados.proximos();
        buscarProtocolosGerados();
    }

    public void ultimosGerados() {
        filtrosGerados.irParaUltimo();
        buscarProtocolosGerados();
    }

    public void ultimosGuiaRecebimento() {
        filtrosRecebimentoMultiplo.irParaUltimo();
        buscarProcessosEncaminhadosParaEncaminhamentoMultiplo();
    }

    public void proximosFinalizado() {
        filtrosFinalizados.proximos();
        buscarProcessosFinalizados();
    }

    public void proximosGuiaRecebimento() {
        filtrosRecebimentoMultiplo.proximos();
        buscarProcessosEncaminhadosParaEncaminhamentoMultiplo();
    }

    public void proximosTramitePendente() {
        filtrosTramitesPendentes.proximos();
        buscarTramitesPendentes();
    }

    public void proximosTramiteAceitos() {
        filtrosTramitesAceitos.proximos();
        buscarTramitesAceitos();
    }

    public void proximosEncaminhados() {
        filtrosEncaminhados.proximos();
        buscarProtocolosEncaminhados();
    }

    public void proximosCancelados() {
        filtrosCancelados.proximos();
        buscarProtocolosCancelados();
    }

    public void proximosArquivados() {
        filtrosArquivados.proximos();
        buscarProtocolosArquivados();
    }

    public void buscarPrimeirosTramitePendente() {
        filtrosTramitesPendentes.irParaPrimeiro();
        buscarTramitesPendentes();
    }

    public void buscarPrimeirosTramiteAceitos() {
        filtrosTramitesAceitos.irParaPrimeiro();
        buscarTramitesAceitos();
    }

    public void buscarPrimeirosEncaminhados() {
        filtrosEncaminhados.irParaPrimeiro();
        buscarProtocolosEncaminhados();
    }

    public void buscarPrimeirosCancelados() {
        filtrosCancelados.irParaPrimeiro();
        buscarProtocolosCancelados();
    }

    public void ultimosTramitePendente() {
        filtrosTramitesPendentes.irParaUltimo();
        buscarTramitesPendentes();
    }

    public void ultimosTramiteAceitos() {
        filtrosTramitesAceitos.irParaUltimo();
        buscarTramitesAceitos();
    }

    public void ultimosEncaminhados() {
        filtrosEncaminhados.irParaUltimo();
        buscarProtocolosEncaminhados();
    }

    public void ultimosCancelados() {
        filtrosCancelados.irParaUltimo();
        buscarProtocolosCancelados();
    }

    public void ultimosArquivados() {
        filtrosArquivados.irParaUltimo();
        buscarProtocolosArquivados();
    }

    public void ultimosFinalizados() {
        filtrosFinalizados.irParaUltimo();
        buscarProcessosFinalizados();
    }

    public void anterioresGerados() {
        filtrosGerados.anteriores();
        buscarProtocolosGerados();
    }

    public void anterioresEncaminhados() {
        filtrosEncaminhados.anteriores();
        buscarProtocolosEncaminhados();
    }

    public void anterioresCancelados() {
        filtrosCancelados.anteriores();
        buscarProtocolosCancelados();
    }

    public void buscarPrimeirosGerados() {
        filtrosGerados.irParaPrimeiro();
        buscarProtocolosGerados();
    }

    public void buscarPrimeirosArquivados() {
        filtrosArquivados.irParaPrimeiro();
        buscarProtocolosArquivados();
    }

    public void buscarPrimeirosFinalizados() {
        filtrosFinalizados.irParaPrimeiro();
        buscarProcessosFinalizados();
    }

    public void buscarPrimeirosGuiaRecebimento() {
        filtrosRecebimentoMultiplo.irParaPrimeiro();
        buscarProcessosEncaminhadosParaEncaminhamentoMultiplo();
    }

    public void anterioresArquivado() {
        filtrosArquivados.anteriores();
        buscarProtocolosArquivados();
    }

    public void anterioresFinalizado() {
        filtrosFinalizados.anteriores();
        buscarProcessosFinalizados();
    }

    public void anterioresGuiaRecebimento() {
        filtrosRecebimentoMultiplo.anteriores();
        buscarProcessosEncaminhadosParaEncaminhamentoMultiplo();
    }

    public void anterioresTramitePendente() {
        filtrosTramitesPendentes.anteriores();
        buscarTramitesPendentes();
    }

    public void anterioresTramiteAceitos() {
        filtrosTramitesAceitos.anteriores();
        buscarTramitesAceitos();
    }

    public void descobrirETratarExceptionProcesso(Exception e) {
        try {
            Util.getRootCauseDataBaseException(e);
        } catch (SQLIntegrityConstraintViolationException sql) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Ocorreu um problema ao salvar o processo, tente cadastrar-lo novamente!");
        } catch (Exception throwable) {
            throwable.printStackTrace();
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public void poeNaSessao() throws IllegalAccessException {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO, SituacaoCadastralPessoa.INATIVO, SituacaoCadastralPessoa.BAIXADO);
    }

    public String getAnoUrl() {
        return anoUrl;
    }

    public void setAnoUrl(String anoUrl) {
        this.anoUrl = anoUrl;
    }

    public String getNumeroUrl() {
        return numeroUrl;
    }

    public void setNumeroUrl(String numeroUrl) {
        this.numeroUrl = numeroUrl;
    }

    public void atualizarCpfCnpj() {
        try {
            validarCpfCnpj();
            Pessoa p = selecionado.getPessoa();
            if (p instanceof PessoaFisica) {
                PessoaFisica pf = facade.getPessoaFisicaFacade().recuperar(p.getId());
                pf.setCpf(novoCpfCnpj);
                facade.getPessoaFisicaFacade().salvar(pf);
                selecionado.setPessoa(pf);
                FacesUtil.executaJavaScript("confirmEditarPessoaFisica.hide()");
            }

            if (p instanceof PessoaJuridica) {
                PessoaJuridica pj = facade.getPessoaJuridicaFacade().recuperar(p.getId());
                pj.setCnpj(novoCpfCnpj);
                facade.getPessoaJuridicaFacade().salvar(pj);
                selecionado.setPessoa(pj);
                FacesUtil.executaJavaScript("confirmEditarPessoaJuridica.hide()");
            }
            novoCpfCnpj = "";
        } catch (ValidacaoException ve) {
            novoCpfCnpj = "";
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCpfCnpj() {
        ValidacaoException ve = new ValidacaoException();
        Pessoa p = selecionado.getPessoa();

        if (!Util.valida_CpfCnpj(novoCpfCnpj)) {
            if (p instanceof PessoaFisica) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado é inválido");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ digitado é inválido");
            }
        }

        if (p instanceof PessoaFisica) {
            if (Util.valida_CpfCnpj(novoCpfCnpj) &&
                !facade.getPessoaFacade().buscarPessoasPorCPFCNPJ(novoCpfCnpj, true).isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado já pertence a outra Pessoa Física!");
            }
        }

        if (p instanceof PessoaJuridica) {
            if (Util.valida_CpfCnpj(novoCpfCnpj) &&
                !facade.getPessoaFacade().buscarPessoasPorCPFCNPJ(novoCpfCnpj, true).isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ digitado já pertence a outra Pessoa Jurídica!");
            }
        }

        ve.lancarException();
    }

    public String getNovoCpfCnpj() {
        return novoCpfCnpj;
    }

    public void setNovoCpfCnpj(String novoCpfCnpj) {
        this.novoCpfCnpj = novoCpfCnpj;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public ProcessoEnglobado getProcessoEnglobado() {
        return processoEnglobado;
    }

    public void setProcessoEnglobado(ProcessoEnglobado processoEnglobado) {
        this.processoEnglobado = processoEnglobado;
    }

    public void desmarcarTodosGuiaRecebimento() {
        protocolosSelecionadosGuiaRecebimento.clear();
    }

    public boolean hasTodosSelecionadosGuiaRecebimento() {
        return protocolosSelecionadosGuiaRecebimento != null && protocolosSelecionadosGuiaRecebimento.size() - (filtrosRecebimentoMultiplo.getQuantidadeTotal() != null ? filtrosRecebimentoMultiplo.getQuantidadeTotal() : 0) == 0;
    }

    public void selecionarTodosGuiaRecebimento() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(null, null, null, getUsuarioCorrente());
        filtro.setProtocolo(true);
        filtro.setGestor(true);
        filtro.setUnidadeOrganizacional(filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().getUnidadeOrganizacional());
        filtro.setDestinoExterno(filtrosRecebimentoMultiplo.getFiltroGuiaRecebimento().getDestinoExterno());

        protocolosSelecionadosGuiaRecebimento = facade.buscarProcessosProtocoloEncaminhados(filtro, 0, 0);
        filtrosRecebimentoMultiplo.setQuantidadeTotal(protocolosSelecionadosGuiaRecebimento.size());
    }

    public void demarcarProtocoloGuiaRecebimento(VoProcesso processo) {
        protocolosSelecionadosGuiaRecebimento.remove(processo);
    }

    public Boolean isSelecionadoProtocoloGuiaRecebimento(VoProcesso processo) {
        return protocolosSelecionadosGuiaRecebimento.contains(processo);
    }

    public void selecionarProtocoloGuiaRecebimento(VoProcesso processo) {
        protocolosSelecionadosGuiaRecebimento.add(processo);
    }

    public class FiltroGuiaRecebimento {
        private TipoProcessoTramite tipoProcesso;
        private UnidadeOrganizacional unidadeOrganizacional;
        private String destinoExterno;

        public FiltroGuiaRecebimento() {
            tipoProcesso = TipoProcessoTramite.INTERNO;
            destinoExterno = "";
        }

        public TipoProcessoTramite getTipoProcesso() {
            return tipoProcesso;
        }

        public void setTipoProcesso(TipoProcessoTramite tipoProcesso) {
            this.tipoProcesso = tipoProcesso;
        }

        public UnidadeOrganizacional getUnidadeOrganizacional() {
            return unidadeOrganizacional;
        }

        public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
            this.unidadeOrganizacional = unidadeOrganizacional;
        }

        public String getDestinoExterno() {
            return destinoExterno;
        }

        public void setDestinoExterno(String destinoExterno) {
            this.destinoExterno = destinoExterno;
        }

        public boolean isInterno() {
            return TipoProcessoTramite.INTERNO.equals(tipoProcesso);
        }

        public boolean isExterno() {
            return TipoProcessoTramite.EXTERNO.equals(tipoProcesso);
        }

        public String getDestino() {
            if (unidadeOrganizacional != null) {
                return unidadeOrganizacional.toString();
            }
            return destinoExterno;
        }
    }

    public void definirUnidadeDestinoFiltro() {
        if (hierarquiaOrganizacional != null) {
            filtrosTramitesPendentes.getFiltroGuiaRecebimento().setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public FiltrosPesquisaProtocolo getFiltrosGerados() {
        return filtrosGerados;
    }

    public void setFiltrosGerados(FiltrosPesquisaProtocolo filtrosGerados) {
        this.filtrosGerados = filtrosGerados;
    }

    public FiltrosPesquisaProtocolo getFiltrosCancelados() {
        return filtrosCancelados;
    }

    public void setFiltrosCancelados(FiltrosPesquisaProtocolo filtrosCancelados) {
        this.filtrosCancelados = filtrosCancelados;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesPendentes() {
        return filtrosTramitesPendentes;
    }

    public void setFiltrosTramitesPendentes(FiltrosPesquisaProtocolo filtrosTramitesPendentes) {
        this.filtrosTramitesPendentes = filtrosTramitesPendentes;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesAceitos() {
        return filtrosTramitesAceitos;
    }

    public void setFiltrosTramitesAceitos(FiltrosPesquisaProtocolo filtrosTramitesAceitos) {
        this.filtrosTramitesAceitos = filtrosTramitesAceitos;
    }

    public FiltrosPesquisaProtocolo getFiltrosEncaminhados() {
        return filtrosEncaminhados;
    }

    public void setFiltrosEncaminhados(FiltrosPesquisaProtocolo filtrosEncaminhados) {
        this.filtrosEncaminhados = filtrosEncaminhados;
    }

    public FiltrosPesquisaProtocolo getFiltrosRecebimentoMultiplo() {
        return filtrosRecebimentoMultiplo;
    }

    public void setFiltrosRecebimentoMultiplo(FiltrosPesquisaProtocolo filtrosRecebimentoMultiplo) {
        this.filtrosRecebimentoMultiplo = filtrosRecebimentoMultiplo;
    }

    public List<VoProcesso> getProtocolosSelecionadosGuiaRecebimento() {
        return protocolosSelecionadosGuiaRecebimento;
    }

    public void setProtocolosSelecionadosGuiaRecebimento(List<VoProcesso> protocolosSelecionadosGuiaRecebimento) {
        this.protocolosSelecionadosGuiaRecebimento = protocolosSelecionadosGuiaRecebimento;
    }

    public FiltrosPesquisaProtocolo getFiltrosArquivados() {
        return filtrosArquivados;
    }

    public void setFiltrosArquivados(FiltrosPesquisaProtocolo filtrosArquivados) {
        this.filtrosArquivados = filtrosArquivados;
    }

    public FiltrosPesquisaProtocolo getFiltrosFinalizados() {
        return filtrosFinalizados;
    }

    public void setFiltrosFinalizados(FiltrosPesquisaProtocolo filtrosFinalizados) {
        this.filtrosFinalizados = filtrosFinalizados;
    }

    public void exibirRevisao(ProcessoHistorico processoHistorico) {
        Long idRevtype = facade.recuperarIdRevisaoHistoricoProcesso(processoHistorico);
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + processoHistorico.getProcesso().getId() + "/" + Processo.class.getSimpleName());
    }

    public void imprimirProtocolo() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("selecionadoId", selecionado.getId());
            dto.adicionarParametro("MUNICIPIO", "Prefeitura Municipal de Rio Branco");
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio(selecionado.toString());
            dto.setApi("administrativo/protocolo-simples/");

            ReportService.getInstance().gerarRelatorio(getSistemaControlador().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Boolean hasTramitacaoAceita() {
        return selecionado.getTramites().get(0).getAceito().equals(false) &&
            !SituacaoProcesso.CANCELADO.name().equals(selecionado.getSituacao().name());
    }

    public void cancelarProtocolo() {
        try {
            if (!Util.isStringNulaOuVazia(selecionado.getMotivoCancelamento()) && Util.isNotNull(selecionado.getDataCancelamento())) {
                selecionado.setSituacao(SituacaoProcesso.CANCELADO);
                gerarHistoricoProcesso(selecionado, Processo.PROTOCOLO_CANCELADO, selecionado.getHistoricos().get(0).getUnidadeOrganizacional());
                facade.salvarProcessoProtocolo(selecionado);
                FacesUtil.addOperacaoRealizada("Protocolo Cancelado com sucesso");
                FacesUtil.redirecionamentoInterno("/protocolo/listar/");
            } else {
                FacesUtil.addOperacaoNaoRealizada("Campos data de cancelamento e motivo de cancelamento são obrigatórios.");
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possivel cancelar o protocolo, entre em contato com o suporte.");
        }
    }

    public HierarquiaOrganizacional getHierarquiaProtocolo() {
        return hierarquiaProtocolo;
    }

    public void setHierarquiaProtocolo(HierarquiaOrganizacional hierarquiaProtocolo) {
        if (hierarquiaProtocolo != null) {
            selecionado.setUoCadastro(hierarquiaProtocolo.getSubordinada());
        }
        this.hierarquiaProtocolo = hierarquiaProtocolo;
    }

    public Tramite getNovoTramite() {
        return novoTramite;
    }

    public void setNovoTramite(Tramite novoTramite) {
        this.novoTramite = novoTramite;
    }
}
