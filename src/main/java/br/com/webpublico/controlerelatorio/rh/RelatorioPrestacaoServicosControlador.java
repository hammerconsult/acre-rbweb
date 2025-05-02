package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ManagedBean(name = "relatorioPrestacaoServicosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioPrestacaoServicos", pattern = "/relatorio-prestacao-servicos/", viewId = "/faces/rh/relatorios/relatorio-prestacao-servicos.xhtml")
})
public class RelatorioPrestacaoServicosControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioPrestacaoServicosControlador.class);

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Entidade empregador;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private Integer mes;
    private Integer ano;
    public List<SelectItem> empregadores;

    @URLAction(mappingId = "relatorioPrestacaoServicos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        empregador = null;
        hierarquiaOrganizacional = null;
        hierarquiasOrganizacionais = Lists.newArrayList();
        empregadores = buscarEmpregadores();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empregador deve ser informado.");
        }
        if (mes != null && ano != null) {
            if (mes < 1 || mes > 12) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O mês informado é inválido!");
            }
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Relatório de Prestação de Serviços");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Prestação de Serviços");
            dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
            dto.adicionarParametro("COMPETENCIA", StringUtil.cortarOuCompletarEsquerda(mes.toString(), 2, "0") + "/" + ano.toString());
            dto.adicionarParametro("EMPREGADOR", empregador.getPessoaJuridica().getCnpj() + " - " + empregador.getNome());
            dto.adicionarParametro("EMPREGADOR", empregador.getPessoaJuridica().getCnpj() + " - " + empregador.getNome());
            dto.adicionarParametro("CONDICAO_EMPREGADOR", montarCondicaoOrgaoEmpregador());
            dto.adicionarParametro("MES", mes);
            dto.adicionarParametro("ANO", ano);
            dto.setApi("rh/relatorio-prestacao-servicos/");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório: " + e);
        }
    }

    public List<SelectItem> buscarEmpregadores() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        Map<Long, Entidade> mapHosUsuario = new HashMap<>();
        List<HierarquiaOrganizacional> hoUsuario = configuracaoEmpregadorESocialFacade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipo(
            sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 1, 2);
        List<ConfiguracaoEmpregadorESocial> empregadores = configuracaoEmpregadorESocialFacade.recuperarConfiguracaoEmpregadorVigente();

        if (empregadores != null) {
            hoUsuario.forEach(ho -> {
                if (ho.getSubordinada().getEntidade() != null) {
                    mapHosUsuario.put(ho.getSubordinada().getEntidade().getId(), ho.getSubordinada().getEntidade());
                }
            });

            for (ConfiguracaoEmpregadorESocial empregador : empregadores) {
                if (mapHosUsuario.containsKey(empregador.getEntidade().getId())) {
                    toReturn.add(new SelectItem(empregador.getEntidade(), empregador.getEntidade() + " "));
                }
            }
        }
        return toReturn;
    }

    public void carregarHierarquiasOrganizacionais() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        hierarquiaOrganizacional = null;
        hierarquiasOrganizacionais = hierarquiaOrganizacionalFacade.buscarHierarquiasDoEmpregadorEsocial(empregador, sistemaFacade.getDataOperacao());
    }

    public String montarCondicaoOrgaoEmpregador() {
        StringBuilder sb = new StringBuilder();
        if (hierarquiaOrganizacional != null) {
            sb.append(" and vw.CODIGO like '").append(hierarquiaOrganizacional.getCodigoSemZerosFinais()).append("%' ");
        } else {
            ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocial = configuracaoEmpregadorESocialFacade.recuperar(configuracaoEmpregadorESocial.getId());
            sb.append(" and (");
            for (ItemConfiguracaoEmpregadorESocial item : configuracaoEmpregadorESocial.getItemConfiguracaoEmpregadorESocial()) {
                HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), item.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
                if (ho != null) {
                    sb.append(" vw.CODIGO like '").append(ho.getCodigoSemZerosFinais()).append("%' or");
                }
            }
            sb = new StringBuilder(sb.substring(0, sb.length() - 3));
            sb.append(") ");
        }
        return sb.toString();
    }

    public List<SelectItem> getHierarquias() {
        return Util.getListSelectItem(hierarquiasOrganizacionais);
    }

    public Entidade getEmpregador() {
        return empregador;
    }

    public void setEmpregador(Entidade empregador) {
        this.empregador = empregador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<SelectItem> getEmpregadores() {
        return empregadores;
    }

    public void setEmpregadores(List<SelectItem> empregadores) {
        this.empregadores = empregadores;
    }
}
