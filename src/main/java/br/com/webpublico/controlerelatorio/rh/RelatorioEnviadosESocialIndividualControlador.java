/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;


@ManagedBean(name = "relatorioEnviadosESocialIndividualizadoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioEnviadosESocialIndividualizado", pattern = "/relatorio-enviados-esocial-individualizado/", viewId = "/faces/rh/relatorios/relatorioenviadosesocial-individualizado.xhtml")
})
public class RelatorioEnviadosESocialIndividualControlador implements Serializable {
    public static final Logger logger = LoggerFactory.getLogger(RelatorioEnviadosESocialIndividualControlador.class);

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;

    private PessoaFacade pessoaFacade;
    private Exercicio exercicio;
    private TipoClasseESocial eventoESocial;
    private SituacaoESocial situacaoESocial;
    private String tipoVinculo;
    private String filtros;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private VinculoFP vinculoFP;
    private PrestadorServicos prestadorServicos;
    private ConfiguracaoEmpregadorESocial empregador;


    public RelatorioEnviadosESocialIndividualControlador() {
        hierarquiaOrganizacional = null;
        empregador = null;
    }


    @URLAction(mappingId = "relatorioEnviadosESocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        exercicio = null;
        eventoESocial = null;
        empregador = null;
        situacaoESocial = null;
        tipoVinculo = null;
        filtros = null;
        hierarquiaOrganizacional = null;
        vinculoFP = null;
        prestadorServicos = null;
    }

    public void limparVinculoFPEPrestadorServicos() {
        vinculoFP = null;
        prestadorServicos = null;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (eventoESocial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Evento Esocial deve ser informado.");
        }
        if (tipoVinculo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Vinculo deve ser informado.");
        }
        if (tipoVinculo != null && tipoVinculo.equals("SERVIDOR") && vinculoFP == null ||
            (tipoVinculo != null && tipoVinculo.equals("PRESTADOR") && prestadorServicos == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor/Pessoa deve ser informado.");
        }

        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            carregarOrgaoEEmpregador();
            HierarquiaOrganizacional secretaria = sistemaFacade.getSistemaService().getHierarquiAdministrativaCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", sistemaFacade.getSistemaService().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", secretaria.getDescricao().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE CONFÊRENCIA ENVIADOS E-SOCIAL INDIVIDUALIZADO");
            dto.adicionarParametro("ANO", exercicio.getAno());
            dto.adicionarParametro("ORGAO", hierarquiaOrganizacional != null ? hierarquiaOrganizacional.toString() : " ");
            dto.adicionarParametro("STATUS", situacaoESocial != null ? situacaoESocial.name() : null);
            dto.adicionarParametro("STATUSDESCRICAO", situacaoESocial != null ? situacaoESocial.getDescricao() : "Todos");
            dto.adicionarParametro("EVENTO", eventoESocial.name());
            dto.adicionarParametro("EMPREGADOR", empregador != null ? empregador.getId() : null);
            dto.adicionarParametro("EMPREGADORNOME", empregador != null ? empregador.getEntidade().getPessoaJuridica().getCnpj() + " - " + empregador.getEntidade().getNome() : null);
            dto.adicionarParametro("TIPOVINCULO", tipoVinculo);
            if(tipoVinculo.equals("SERVIDOR") ){
                dto.adicionarParametro("VINCULOFPDESCRICAO", vinculoFP.getContratoFP() != null ? vinculoFP.getContratoFP().toString() : vinculoFP.getMatriculaFP().toString());
            } else{
                dto.adicionarParametro("VINCULOFPDESCRICAO", prestadorServicos.toString());
            }
            dto.adicionarParametro("VINCULOFP", vinculoFP != null ?  vinculoFP.getId() : prestadorServicos.getId() );
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("RELATÓRIO-DE-CONFÊRENCIA-ENVIADOS-ESOCIAL-INDIVIDUALIZADO");
            dto.setApi("rh/conferencia-enviados-esocial-individualizado/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getSistemaService().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatório: {} ", ex.getMessage());
            logger.debug("Detalhes do erro ao gerar relatório. ", ex);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível gerar relatório. Detalhes: " + ex.getMessage());
        }
    }

    public void carregarOrgaoEEmpregador() {
        Entidade entidade = null;

        if (tipoVinculo.equals("SERVIDOR")) {
            if (vinculoFP.getContratoFP() != null && vinculoFP.getContratoFP().getUnidadeOrganizacional() != null) {
                hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(vinculoFP.getContratoFP().getUnidadeOrganizacional().getId());
            } else {
                vinculoFP = vinculoFPFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(vinculoFP);
                LotacaoFuncional lotacaoFuncionalVigente = vinculoFP.getLotacaoFuncionalVigente();
                if (lotacaoFuncionalVigente != null) {
                    hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(lotacaoFuncionalVigente.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, lotacaoFuncionalVigente.getInicioVigencia());
                }
            }

            Long idEntidade = entidadeFacade.recuperarEntidadeDoRegistroEsocialPorVinculoFPEAnoETipo(TipoClasseESocial.valueOf(eventoESocial.name()), exercicio.getAno(), vinculoFP.getId());
            if (idEntidade != null) {
                entidade = entidadeFacade.recuperarSimples(idEntidade);
            }
            if (entidade != null) {
                empregador = configuracaoEmpregadorESocialFacade.buscarConfiguracaoPorEntidade(entidade);
            }
        } else {
            if (prestadorServicos != null && prestadorServicos.getLotacao() != null) {
                hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(prestadorServicos.getLotacao().getId());
            }

            Long idEntidade = entidadeFacade.recuperarEntidadeDoRegistroEsocialPorPrestadorServicoEAnoETipo(TipoClasseESocial.valueOf(eventoESocial.name()), exercicio.getAno(), prestadorServicos.getId());
            if (idEntidade != null) {
                entidade = entidadeFacade.recuperarSimples(idEntidade);
            }
            if (entidade != null) {
                empregador = configuracaoEmpregadorESocialFacade.buscarConfiguracaoPorEntidade(entidade);
            }
        }
    }

    public List<SelectItem> buscarEventos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoClasseESocial.S1200, TipoClasseESocial.S1200 + " "));
        toReturn.add(new SelectItem(TipoClasseESocial.S1202, TipoClasseESocial.S1202 + " "));
        toReturn.add(new SelectItem(TipoClasseESocial.S1207, TipoClasseESocial.S1207 + " "));
        toReturn.add(new SelectItem(TipoClasseESocial.S1210, TipoClasseESocial.S1210 + " "));
        return toReturn;
    }

    public static List<SelectItem> buscarSituacoesEsocial() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        retorno.add(new SelectItem(SituacaoESocial.PROCESSADO_COM_SUCESSO, SituacaoESocial.PROCESSADO_COM_SUCESSO.getDescricao()));
        retorno.add(new SelectItem(SituacaoESocial.PROCESSADO_COM_ERRO, SituacaoESocial.PROCESSADO_COM_ERRO.getDescricao()));
        retorno.add(new SelectItem(SituacaoESocial.PROCESSADO_COM_ADVERTENCIA, SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.getDescricao()));
        return retorno;
    }

    public static List<SelectItem> buscarTipoVinculo() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        retorno.add(new SelectItem("PRESTADOR", "Prestador"));
        retorno.add(new SelectItem("SERVIDOR", "Servidor"));
        return retorno;
    }

    public List<VinculoFP> completeVinculosFP(String parte) {
        return vinculoFPFacade.listaTodosVinculos1(parte.trim());
    }

    public List<PrestadorServicos> completePrestadorServicoCPF(String parte) {
        return prestadorServicosFacade.buscaPrestadorServicosNomeCPF(parte);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoClasseESocial getEventoESocial() {
        return eventoESocial;
    }

    public void setEventoESocial(TipoClasseESocial eventoESocial) {
        this.eventoESocial = eventoESocial;
    }

    public SituacaoESocial getSituacaoESocial() {
        return situacaoESocial;
    }

    public void setSituacaoESocial(SituacaoESocial situacaoESocial) {
        this.situacaoESocial = situacaoESocial;
    }

    public String getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(String tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public PrestadorServicos getPrestadorServicos() {
        return prestadorServicos;
    }

    public void setPrestadorServicos(PrestadorServicos prestadorServicos) {
        this.prestadorServicos = prestadorServicos;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

}
