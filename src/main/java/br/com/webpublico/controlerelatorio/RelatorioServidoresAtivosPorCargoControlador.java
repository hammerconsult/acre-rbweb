package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 04/08/14
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-ativos-por-cargo", pattern = "/relatorio/servidores-ativos-por-cargo/", viewId = "/faces/rh/relatorios/relatorioservidoresativosporcargo.xhtml")
})
public class RelatorioServidoresAtivosPorCargoControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private ConverterAutoComplete converterHierarquia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ModalidadeContratoFP modalidadeContratoFP;
    private Boolean todosVinculos, todosItensMarcados;
    private List<Cargo> grupoCargos;
    private String filtro;
    private Date dataInicial, dataFinal;
    private String filtroCargos;

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO",sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("dataInicial", dataInicial);
            dto.adicionarParametro("dataFinal", dataFinal == null ? sistemaControlador.getDataOperacao() : dataFinal);
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO SERVIDORES ATIVOS POR CARGO");
            dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
            dto.adicionarParametro("CODIGOHO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            dto.adicionarParametro("DATAOPERACAO", sistemaControlador.getDataOperacao().getTime());
            dto.adicionarParametro("NOME_ARQUIVO", "RELATORIO-SERVIDORES-ATIVOS-CARGO");
            dto.setNomeRelatorio("RELATÓRIO-SERVIDORES-ATIVOS-POR-CARGO");
            dto.setApi("rh/servidores-ativos-por-cargo/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<ParametrosRelatorios> montarParametros() {
        filtro = "";
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (hierarquiaOrganizacionalSelecionada.getSuperior() != null) {
            parametros.add(new ParametrosRelatorios(" ho.codigo ", ":reccodigo", null, OperacaoRelatorio.LIKE, getCodigo(), null, 1, false));
            filtro += "Órgão: " + hierarquiaOrganizacionalSelecionada.getDescricao() + " - ";
        } else {
            filtro += "Órgão: Todos os órgãos - ";
        }
        parametros.add(new ParametrosRelatorios(" cargo.id ", ":cargo", null, OperacaoRelatorio.IN, getMontaGrupoCargo(), null, 1, false));
        if (!todosVinculos) {
            parametros.add(new ParametrosRelatorios(" mdl.codigo ", ":mdl", null, OperacaoRelatorio.IGUAL, modalidadeContratoFP.getCodigo(), null, 1, false));
            filtro += "Tipo de Vínculo: " + StringUtils.capitalize(modalidadeContratoFP.getDescricao().toLowerCase()) + " - ";
        } else {
            filtro += "Tipo de Vínculo: Todos os vínculos - ";
        }
        if (dataInicial != null) {
            filtro += "Data Inicial: " + DataUtil.getDataFormatada(dataInicial) + " - ";
        }
        if (dataFinal != null) {
            filtro += "Data Final: " + DataUtil.getDataFormatada(dataFinal) + " - ";
        }
        if (dataInicial == null && dataFinal == null) {
            parametros.add(new ParametrosRelatorios(null, ":DATAOPERACAO", null, null, DataUtil.getDataFormatada(sistemaControlador.getDataOperacao()), null, 0, true));
        }
        filtro = filtro.substring(0, filtro.length() - 2);
        return parametros;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo órgão é obrigatório");
        }

        if (todosVinculos == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de cargo é obrigatório");
        }

        boolean flag = false;
        for (Cargo cargo : grupoCargos) {
            if (cargo.isSelecionado()) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o(s) cargo(s) para gerar o relatório");
        }

        if (!todosVinculos && modalidadeContratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cargo é obrigatório");
        }
        if (dataFinal != null && dataInicial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Inicial não pode ficar em branco quando uma Data Final é informada");
        }
        if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final não pode possuir uma data anterior ao campo Data Inicial");
        }
        ve.lancarException();
    }

    private String getCodigo() {
        return hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%";
    }

    private HashSet<Long> getMontaGrupoCargo() {
        HashSet<Long> retorno = new HashSet<>();
        for (Cargo grupo : grupoCargos) {
            if (grupo.isSelecionado()) {
                retorno.add(grupo.getId());
            }
        }
        return retorno;
    }

    @URLAction(mappingId = "relatorio-servidores-ativos-por-cargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
        filtro = "";
        todosVinculos = Boolean.FALSE;
    }

    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        modalidadeContratoFP = null;
        todosVinculos = Boolean.FALSE;
        grupoCargos = Lists.newArrayList();
        todosItensMarcados = false;
        filtroCargos = "";
        dataInicial = sistemaControlador.getDataOperacao();
        dataFinal = sistemaControlador.getDataOperacao();
    }


    public ConverterAutoComplete getConverterHierarquia() {
        if (hierarquiaOrganizacionalSelecionada == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacadeNovo);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public Boolean getTodosVinculos() {
        return todosVinculos;
    }

    public void setTodosVinculos(Boolean todosVinculos) {
        this.todosVinculos = todosVinculos;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public List<Cargo> getGrupoCargos() {
        if (filtroCargos == null || filtroCargos.isEmpty()) {
            return grupoCargos;
        }

        List<Cargo> listRetorno = Lists.newArrayList();
        for (Cargo cargo : grupoCargos) {
            if (cargo.getCodigoDoCargo().contains(filtroCargos) || cargo.getDescricao().trim().toLowerCase().contains(filtroCargos.trim().toLowerCase())) {
                listRetorno.add(cargo);
            }
        }
        return listRetorno;
    }

    public void setGrupoCargos(List<Cargo> grupoCargos) {
        this.grupoCargos = grupoCargos;
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP modalidade : modalidadeContratoFPFacade.modalidadesAtivas()) {
            retorno.add(new SelectItem(modalidade, modalidade.toString()));
        }
        return retorno;
    }

    public void carregarListaDeCargosPorModalidade() {
        if (modalidadeContratoFP != null) {
            if (dataInicial != null && dataFinal != null) {
                grupoCargos = cargoFacade.listaFiltrandoModalidade(modalidadeContratoFP, dataInicial, dataFinal);
            } else {
                grupoCargos = cargoFacade.listaFiltrandoModalidade(modalidadeContratoFP, sistemaControlador.getDataOperacao());
            }
        } else if (todosVinculos) {
            if (dataInicial != null && dataFinal != null) {
                grupoCargos = cargoFacade.filtraCargosVigentesPorUnidadeOrganizacionalAndUsuario(dataInicial, dataFinal);
            } else {
                grupoCargos = cargoFacade.filtraCargosVigentesPorUnidadeOrganizacionalAndUsuario(sistemaControlador.getDataOperacao());
            }
        } else {
            grupoCargos = Lists.newArrayList();
        }
        setTodosItensMarcados(false);
        Collections.sort(grupoCargos, new Comparator<Cargo>() {
            @Override
            public int compare(Cargo o1, Cargo o2) {
                try {
                    return Integer.valueOf(o1.getCodigoDoCargo()).compareTo(Integer.valueOf(o2.getCodigoDoCargo()));
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Boolean getTodosItensMarcados() {
        return todosItensMarcados;
    }

    public void setTodosItensMarcados(Boolean todosItensMarcados) {
        this.todosItensMarcados = todosItensMarcados;
        for (Cargo cargo : grupoCargos) {
            if (Strings.isNullOrEmpty(filtroCargos)) {
                cargo.setSelecionado(todosItensMarcados);
            } else if (cargo.getCodigoDoCargo().contains(filtroCargos) || cargo.getDescricao().trim().toLowerCase().contains(filtroCargos.trim().toLowerCase())) {
                cargo.setSelecionado(todosItensMarcados);
            }
        }
    }

    public String getFiltroCargos() {
        return filtroCargos;
    }

    public void setFiltroCargos(String filtroCargos) {
        this.filtroCargos = filtroCargos;
    }

    public void setItemSelecionado(Cargo cargo, boolean estado) {
        cargo.setSelecionado(estado);
    }
}
