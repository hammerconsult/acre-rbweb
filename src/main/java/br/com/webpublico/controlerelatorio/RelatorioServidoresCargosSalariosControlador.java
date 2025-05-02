/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-cargos-salarios", pattern = "/relatorio/servidores-cargos-salarios/", viewId = "/faces/rh/relatorios/relatorioservidorescargosesalarios.xhtml")
})
public class RelatorioServidoresCargosSalariosControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private CargoFacade cargoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Integer mes;
    private Integer ano;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquia;
    private ConverterAutoComplete converterCargo;
    private List<Cargo> listaDeCargos;
    private Boolean agruparOrgao;
    private Boolean ehRaiz;

    public RelatorioServidoresCargosSalariosControlador() {
        this.listaDeCargos = new ArrayList<>();
    }

    @URLAction(mappingId = "relatorio-servidores-cargos-salarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        listaDeCargos = Lists.newArrayList();
        mes = null;
        ano = null;
        setEhRaiz(Boolean.FALSE);
        setAgruparOrgao(Boolean.FALSE);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validaCampos();
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/servidores-cargos-salarios/");
            dto.setNomeRelatorio("servidores-cargos-salarios");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE SERVIDORES CARGOS E SALÁRIOS");
            dto.adicionarParametro("MES", (mes - 1));
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("hieraquiaOrganizacional", hierarquiaOrganizacionalSelecionada.getSuperior() == null
                && !agruparOrgao ? hierarquiaOrganizacionalSelecionada.toString() : "");
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
            }
            ReportService.getInstance().gerarRelatorio(abstractReport.getSistemaFacade().getUsuarioCorrente(), dto);
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
        List<ParametrosRelatorios> listaParametros = new ArrayList<>();
        listaParametros.add(new ParametrosRelatorios(null, ":DATAPESQUISA", null, null, getMes() + "/" + getAno(), null, 0, true));
        listaParametros.add(new ParametrosRelatorios("ficha.mes", ":mes", null, OperacaoRelatorio.IGUAL, getMes() - 1, null, 1, false));
        listaParametros.add(new ParametrosRelatorios("ficha.ano", ":ano", null, OperacaoRelatorio.IGUAL, getAno(), null, 1, false));
        listaParametros.add(new ParametrosRelatorios("ho.codigo", ":codigoho", null, OperacaoRelatorio.LIKE, getHierarquiaOrganizacionalSelecionada().getCodigoSemZerosFinais() + "%", null, 1, false));
        if (listaDeCargos != null && !listaDeCargos.isEmpty()) {
            listaParametros.add(new ParametrosRelatorios("c.id ", ":cargo", null, OperacaoRelatorio.IN, getMontaGrupoCargo(), null, 1, false));
        }
        return listaParametros;
    }

    private HashSet<Long> getMontaGrupoCargo() {
        HashSet<Long> retorno = new HashSet<>();
        if (listaDeCargos != null && !listaDeCargos.isEmpty()) {
            for (Cargo grupo : listaDeCargos) {
                retorno.add(grupo.getId());
            }
        }
        return retorno;
    }

    public void habilitarAgrupamento() {
        if (hierarquiaOrganizacionalSelecionada != null && hierarquiaOrganizacionalSelecionada.getSuperior() == null) {
            setEhRaiz(Boolean.TRUE);
        } else {
            setEhRaiz(Boolean.FALSE);
        }
    }


    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null || (ano != null && ano == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public List<Cargo> completarCargos(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public Converter getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterAutoComplete(Cargo.class, cargoFacade);
        }
        return converterCargo;
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

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<Cargo> getListaDeCargos() {
        return listaDeCargos;
    }

    public void setListaDeCargos(List<Cargo> listaDeCargos) {
        this.listaDeCargos = listaDeCargos;
    }

    public Boolean getAgruparOrgao() {
        return agruparOrgao;
    }

    public void setAgruparOrgao(Boolean agruparOrgao) {
        this.agruparOrgao = agruparOrgao;
    }

    public Boolean getEhRaiz() {
        return ehRaiz;
    }

    public void setEhRaiz(Boolean ehRaiz) {
        this.ehRaiz = ehRaiz;
    }
}
