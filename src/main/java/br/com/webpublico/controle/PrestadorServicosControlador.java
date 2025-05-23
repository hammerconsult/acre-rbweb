/*
 * Codigo gerado automaticamente em Wed Jan 04 15:30:31 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.esocial.TipoUnidadePagamento;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.CategoriaTrabalhadorFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "prestadorServicosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPrestadorServicos", pattern = "/rh/prestador-de-servico/novo/", viewId = "/faces/rh/administracaodepagamento/prestadorservicos/edita.xhtml"),
    @URLMapping(id = "listaPrestadorServicos", pattern = "/rh/prestador-de-servico/listar/", viewId = "/faces/rh/administracaodepagamento/prestadorservicos/lista.xhtml"),
    @URLMapping(id = "verPrestadorServicos", pattern = "/rh/prestador-de-servico/ver/#{prestadorServicosControlador.id}/", viewId = "/faces/rh/administracaodepagamento/prestadorservicos/visualizar.xhtml"),
    @URLMapping(id = "editarPrestadorServicos", pattern = "/rh/prestador-de-servico/editar/#{prestadorServicosControlador.id}/", viewId = "/faces/rh/administracaodepagamento/prestadorservicos/edita.xhtml"),
})
public class PrestadorServicosControlador extends PrettyControlador<PrestadorServicos> implements Serializable, CRUD {

    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private PessoaFisicaFacade prestadorFacade;
    private ConverterAutoComplete converterPrestador;
    @EJB
    private TomadorDeServicoFacade tomadorFacade;
    @EJB
    private CBOFacade cboFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterCbo;
    @EJB
    private RetencaoIRRFFacade retencaoIRRFFacade;
    private ConverterAutoComplete converterRetencaoIRRF;
    @EJB
    private OcorrenciaSEFIPFacade ocorrenciaSEFIPFacade;
    private ConverterAutoComplete converterOcorrenciaSEFIP;
    @EJB
    private CategoriaSEFIPFacade categoriaSEFIPFacade;
    private ConverterAutoComplete converterCategoriaSEFIP;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClassificacaoCredorFacade classificacaoCredorFacade;
    @EJB
    private CategoriaTrabalhadorFacade categoriaTrabalhadorFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private CargoFacade cargoFacade;
    private ConverterAutoComplete converterClassificacaoCredor;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private PrestadorServicoAlteracaoFacade prestadorServicoAlteracaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FichaRPAFacade fichaRPAFacade;
    private String xml;
    private List<OcorrenciaESocialDTO> ocorrencias;

    private List<EventoESocialDTO> eventosFiltrados;


    public PrestadorServicosControlador() {
        super(PrestadorServicos.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return prestadorServicosFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/prestador-de-servico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<PessoaFisica> completaPrestador(String parte) {
        return prestadorFacade.buscarPessoasPorTipoPerfilAndAtiva(parte.trim(), PerfilEnum.PERFIL_PRESTADOR);
    }

    public Converter getConverterPrestador() {
        if (converterPrestador == null) {
            converterPrestador = new ConverterAutoComplete(PessoaFisica.class, prestadorFacade);
        }
        return converterPrestador;
    }

    public List<CBO> completaCBO(String parte) {
        return cboFacade.listaFiltrandoLong(parte.trim(), "descricao");
    }

    public Converter getConverterCBO() {
        if (converterCbo == null) {
            converterCbo = new ConverterAutoComplete(CBO.class, cboFacade);
        }
        return converterCbo;
    }

    public List<RetencaoIRRF> completaRetencaoIRRF(String parte) {
        return retencaoIRRFFacade.listaFiltrandoLong(parte.trim(), "descricao");
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Converter getConverterRetencaoIRRF() {
        if (converterRetencaoIRRF == null) {
            converterRetencaoIRRF = new ConverterAutoComplete(RetencaoIRRF.class, retencaoIRRFFacade);
        }
        return converterRetencaoIRRF;
    }

    public List<OcorrenciaSEFIP> completaOcorrenciaSEFIP(String parte) {
        return ocorrenciaSEFIPFacade.listaFiltrandoLong(parte.trim(), "descricao");
    }

    public Converter getConverterOcorrenciaSEFIP() {
        if (converterOcorrenciaSEFIP == null) {
            converterOcorrenciaSEFIP = new ConverterAutoComplete(OcorrenciaSEFIP.class, ocorrenciaSEFIPFacade);
        }
        return converterOcorrenciaSEFIP;
    }

    public List<CategoriaSEFIP> completaCategoriaSEFIP(String parte) {
        return categoriaSEFIPFacade.listaFiltrandoLong(parte.trim(), "descricao");
    }

    public Converter getConverterCategoriaSEFIP() {
        if (converterCategoriaSEFIP == null) {
            converterCategoriaSEFIP = new ConverterAutoComplete(CategoriaSEFIP.class, categoriaSEFIPFacade);
        }
        return converterCategoriaSEFIP;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPrestador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Prestador é um campo obrigatório!");
        }
        if (selecionado.getCbo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O CBO é um campo obrigatório!");
        }
        if (selecionado.getRetencaoIRRF() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Retenção IRRF é um campo obrigatório!");
        }
        if (selecionado.getClassificacaoCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Classificação do Credor é um campo obrigatório!");
        }
        if (selecionado.getOcorrenciaSEFIP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Ocorrência SEFIP é um campo obrigatório!");
        }
        if (selecionado.getOcorrenciaSEFIP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Categoria SEFIP é um campo obrigatório!");
        }
        if (selecionado.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Categoria do Trabalhador é obrigatório!");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Lotação na aba 'Hierarquia Organizacional' é obrigatório!");
        }
        if (selecionado.getInicioLotacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Início de Vigência na aba 'Hierarquia Organizacional' é obrigatório!");
        }
        if (selecionado.getCargo() == null && selecionado.getCategoriaTrabalhador() != null && (selecionado.getCategoriaTrabalhador().getCodigo() == 901 ||
            selecionado.getCategoriaTrabalhador().getCodigo() == 903 || selecionado.getCategoriaTrabalhador().getCodigo() == 904 ||
            selecionado.getCategoriaTrabalhador().getCodigo() == 905)) {
            ve.adicionarMensagemDeCampoObrigatorio("Para as categorias de trabalhador 901, 903, 904 e 905 o preenchimento do Cargo é obrigatório.");

        }
        if (selecionado.getCategoriaTrabalhador() != null && (selecionado.getCategoriaTrabalhador().getCodigo() == 712 ||
            selecionado.getCategoriaTrabalhador().getCodigo() == 721 || selecionado.getCategoriaTrabalhador().getCodigo() == 305 ||
            selecionado.getCategoriaTrabalhador().getCodigo() == 771)) {
            if (selecionado.getValorTotal() == null || selecionado.getValorParcelaFixa() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Para as categorias de trabalhador 712, 721, 305 e 771 o preenchimento do Valor Total do Contrato e Valor da Parcela Fixa da Remuneração são obrigatórios");
            }
        }
        if (selecionado.getInicioVigenciaContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Início de Vigência na aba 'Contrato de Prestação de Serviços'");
        }
        if (selecionado.getInicioVigenciaContrato() != null && selecionado.getFinalVigenciaContrato() != null) {
            if (selecionado.getInicioVigenciaContrato().after(selecionado.getFinalVigenciaContrato())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Início de Vigência na aba 'Contrato de Prestação de Serviços' não pode ser maior que o Final de Vigência");
            }
        }
        if (selecionado.getInicioLotacao() != null && selecionado.getFinalLotacao() != null) {
            if (selecionado.getInicioLotacao().after(selecionado.getFinalLotacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Início de Vigência na aba 'Hierarquia Organizacional' não pode ser maior que o Final de Vigência'");
            }
        }
        if (selecionado.getValorParcelaFixa() != null && selecionado.getUnidadePagamento() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ao informar o Valor da Parcela Fixa também deve ser informado a Unidade de Pagamento");
        }
        if (selecionado.getValorParcelaFixa() != null) {
            if (selecionado.getUnidadePagamento() != null &&
                (!TipoUnidadePagamento.NA0_APLICAVEL.equals(selecionado.getUnidadePagamento())) && selecionado.getValorParcelaFixa().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para o Tipo de Unidade de Pagamento selecionado deve ser informado o Valor da Parcela Fixa da Remuneração e deve ser maior que Zero");
            }
        }

        if (prestadorServicosFacade.prestadorServicoJaCadastrado(selecionado, hierarquiaOrganizacional)) {
            if (Strings.isNullOrEmpty(selecionado.getNumeroContrato())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe cadastro de prestador com o mesmo CPF, Categoria e Órgão.");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe cadastro de prestador com o mesmo CPF, Categoria, Órgão e Número do Contrato vigente.");
            }
        }

        ve.lancarException();
    }

    @URLAction(mappingId = "novoPrestadorServicos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(prestadorServicosFacade.retornaUltimoCodigo());
        selecionado.setDataCadastro(new Date());
        ocorrencias = Lists.newArrayList();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (operacao == Operacoes.NOVO) {
                Long novoCodigo = prestadorServicosFacade.retornaUltimoCodigo();
                if (!novoCodigo.equals(selecionado.getCodigo())) {
                    FacesUtil.addInfo("", "O Código " + selecionado.getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                    selecionado.setCodigo(novoCodigo);
                }
                selecionado.setDataCadastro(new Date());
            } else {
                PrestadorServicoAlteracao alteracao = new PrestadorServicoAlteracao();
                alteracao.setPrestadorServico(selecionado);
                alteracao.setDataAlteracao(new Date());
                prestadorServicoAlteracaoFacade.salvar(alteracao);
            }
            selecionado.setLotacao(hierarquiaOrganizacional.getSubordinada());
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    @URLAction(mappingId = "editarPrestadorServicos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getLotacao() != null) {
            hierarquiaOrganizacional =
                hierarquiaOrganizacionalFacade.recuperarHierarquiaOrganizacionalPorUnidadeId(selecionado.getLotacao().getId(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, null);
        }
        getEventoPorIdentificador();
        ocorrencias = Lists.newArrayList();
        ordenarEventosPorDataEnvioMaisRecente(selecionado.getEventosEsocial());
    }

    @URLAction(mappingId = "verPrestadorServicos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        getEventoPorIdentificador();
        ocorrencias = Lists.newArrayList();
        ordenarEventosPorDataEnvioMaisRecente(selecionado.getEventosEsocial());
    }

    public List<PessoaFisica> getDependentes() {
        return dependenteFacade.listaDependentesPorResponsavel(getSelecionado().getPrestador());
    }

    public Converter getConverterClassificacaoCredor() {
        if (converterClassificacaoCredor == null) {
            converterClassificacaoCredor = new ConverterAutoComplete(ClassificacaoCredor.class, classificacaoCredorFacade);
        }
        return converterClassificacaoCredor;
    }

    public List<ClassificacaoCredor> completaClassificacaoCredor(String parte) {
        return classificacaoCredorFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void criarNovoDependente(ActionEvent ev) {
        String requestContext = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String pessoaFisicaId = "" + getSelecionado().getPrestador().getId();
        String url = "window.open('" + requestContext + "/dependente/novo/?sessao=true&responsavel=" + pessoaFisicaId + "', '_blank'); ativarOuvidor(ativouAba);";
        RequestContext.getCurrentInstance().execute(url);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getCategoriaTrabalhador() {
        return Util.getListSelectItem(categoriaTrabalhadorFacade.getCategoriaTrabalhadorPrestadorServico());
    }

    public void buscarCBOCargo() {
        if (selecionado.getCargo() != null && selecionado.getCargo().getCbos() != null) {
            CBO cbo = cboFacade.getCBOCargoOrdenadoPorCodigo(selecionado.getCargo());
            if (cbo != null) {
                selecionado.setCbo(cbo);
            }
        }
    }

    public void enviarEventoEsocial() {
        try {
            ConfiguracaoEmpregadorESocial configuracao = configuracaoEmpregadorESocialFacade.buscarEmpregadorPorPrestadorServico(selecionado);
            validarEnvioEventoEsocial(configuracao);
            prestadorServicosFacade.enviarS2300ESocial(configuracao, selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Evento enviado com sucesso! Aguarde seu processamento para receber a situação.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void validarEnvioEventoEsocial(ConfiguracaoEmpregadorESocial configuracao) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracao == null || configuracao.getCertificado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração do empregador do servidor.");
        }
        ve.lancarException();
    }

    private void getEventoPorIdentificador() {
        List<Long> fichasId = fichaRPAFacade.buscarIdsFichaRPAPorPrestador(selecionado);
        selecionado.setEventosEsocial(contratoFPFacade.getEventoPorIdentificador(selecionado.getId(), StringUtil.retornaApenasNumeros(selecionado.getPrestador().getCpf_Cnpj()), fichasId, null, null, null));
    }

    public List<SelectItem> getTipoUnidade() {
        return Util.getListSelectItem(TipoUnidadePagamento.values(), false);
    }

    public List<Cargo> completarCargos(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public List<PrestadorServicos> completaPrestadorServico(String parte) {
        return prestadorServicosFacade.buscaPrestadorServicosNomeCPF(parte.trim());
    }

    public void preencherMatriculaESocial() {
        PessoaFisica prestador = prestadorServicosFacade.getPessoaFisicaFacade()
            .recuperar(selecionado.getPrestador().getId());
        selecionado.setMatriculaESocial(prestador);
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    public List<EventoESocialDTO> getEventosFiltrados() {
        return eventosFiltrados;
    }

    public void setEventosFiltrados(List<EventoESocialDTO> eventosFiltrados) {
        this.eventosFiltrados = eventosFiltrados;
    }

    public void ordenarEventosPorDataEnvioMaisRecente(List<EventoESocialDTO> eventos) {
        if (eventos != null && !eventos.isEmpty()) {
            eventos.sort(Comparator.comparing(EventoESocialDTO::getDataOperacao, Comparator.nullsLast(Comparator.reverseOrder())));
        }
    }
}
