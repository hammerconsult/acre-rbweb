package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.rh.TempoServicoDTO;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import liquibase.util.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "relatorioTempoDeEfetivoExercioPorOrgaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioTempoDeEfetivoExercioPorOrgao", pattern = "/relatorio/tempo-efetivo-exercicio-orgao/", viewId = "/faces/rh/relatorios/relatoriotempoefetivoexercicioorgao.xhtml")
})
public class RelatorioTempoDeEfetivoExercioPorOrgaoControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private Cargo cargo;
    private List<Cargo> cargos;
    private Sexo sexo;
    private TempoServico tempoServico;
    private Integer quantidadeAnos;

    @URLAction(mappingId = "relatorioTempoDeEfetivoExercioPorOrgao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        cargos = Lists.newArrayList();
        sexo = null;
        tempoServico = null;
        quantidadeAnos = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Servidores e Tempo de Efetivo Exercício por Órgão");
            dto.adicionarParametro("MES", DataUtil.getMes(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("ANO", DataUtil.getAno(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("complementoWhere", complementoWhere());
            dto.adicionarParametro("OPERADOR", tempoServico != null ? tempoServico.getToDto() : null);
            dto.adicionarParametro("TEMPOSERVICO", quantidadeAnos == null ? 0 : quantidadeAnos);
            dto.adicionarParametro("DATAOPERACAO", getSistemaFacade().getDataOperacao());
            dto.adicionarParametro("FILTROS", filtrosUtilizados());
            dto.setNomeRelatorio("Relatório de Servidores e Tempo de Efetivo Exercício por Órgão");
            dto.setApi("rh/tempo-efetivo-exercicio-orgao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getDataOperacao());
    }

    public List<Cargo> completarCargos(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public void adicionarHierarquia() {
        hierarquiasOrganizacionais.add(hierarquiaOrganizacional);
        hierarquiaOrganizacional = null;
    }

    public void removerHierarquia(HierarquiaOrganizacional h) {
        hierarquiasOrganizacionais.remove(h);
    }

    public void adicionarCargo() {
        cargos.add(cargo);
        cargo = null;
    }

    public void removerCargo(Cargo c) {
        cargos.remove(c);
    }

    private String complementoWhere() {
        StringBuilder sb = new StringBuilder("");
        if (!hierarquiasOrganizacionais.isEmpty()) {
            sb.append(" and (ho.codigo like '").append(StringUtils.join(getCodigoHierarquias(), "' or ho.codigo like '")).append("') ");
        }
        if (!cargos.isEmpty()) {
            sb.append(" and cargo.id in (").append(StringUtils.join(getIdCargos(), ",")).append(") ");
        }
        if (sexo != null) {
            sb.append(" and pf.sexo = '").append(sexo.name()).append("'");
        }
        return sb.toString();
    }

    private String filtrosUtilizados() {
        StringBuilder sb = new StringBuilder(" ");
        if (!hierarquiasOrganizacionais.isEmpty()) {
            String orgao = hierarquiasOrganizacionais.size() > 1 ? "Órgãos: " : "Órgão: ";
            sb.append(orgao).append(StringUtils.join(getDescricaoHierarquias(), ", ")).append(";");
        }
        if (!cargos.isEmpty()) {
            String cargo = hierarquiasOrganizacionais.size() > 1 ? " Cargos: " : " Cargo: ";
            sb.append(cargo).append(StringUtils.join(getDescricaoCargos(), ", ")).append(";");
        }
        if (sexo != null) {
            sb.append(" Sexo: ").append(sexo.getDescricao()).append(";");
        }
        if (tempoServico != null && quantidadeAnos != null) {
            sb.append(" Tempo de Serviço: ").append(tempoServico.descricao).append(" ").append(quantidadeAnos).append(" anos").append(".");
        }
        return sb.toString().replaceFirst(".$", "");
    }

    private List<String> getDescricaoHierarquias() {
        List<String> hierarquias = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            hierarquias.add(ho.getDescricao());
        }
        return hierarquias;
    }

    private List<String> getDescricaoCargos() {
        List<String> descricao = Lists.newArrayList();
        for (Cargo car : cargos) {
            descricao.add(StringUtil.removeEspacos(car.toString()));
        }
        return descricao;
    }

    private List<String> getCodigoHierarquias() {
        List<String> hierarquias = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            hierarquias.add(ho.getCodigoSemZerosFinais() + "%");
        }
        return hierarquias;
    }

    private List<String> getIdCargos() {
        List<String> carg = Lists.newArrayList();
        for (Cargo c : cargos) {
            carg.add(c.getId().toString());
        }
        return carg;
    }

    public List<SelectItem> buscarSexo() {
        return Util.getListSelectItem(Sexo.values(), false);
    }

    public List<SelectItem> buscarTempoServico() {
        return Util.getListSelectItem(TempoServico.values(), false);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public TempoServico getTempoServico() {
        return tempoServico;
    }

    public Integer getQuantidadeAnos() {
        return quantidadeAnos;
    }

    public void setQuantidadeAnos(Integer quantidadeAnos) {
        this.quantidadeAnos = quantidadeAnos;
    }

    public void setTempoServico(TempoServico tempoServico) {
        this.tempoServico = tempoServico;
    }

    public enum TempoServico {
        IGUAL("Igual a", TempoServicoDTO.IGUAL),
        MAIOR("Maior que", TempoServicoDTO.MAIOR),
        MENOR("Menor que", TempoServicoDTO.MENOR);
        private String descricao;
        private TempoServicoDTO toDto;

        TempoServico(String descricao, TempoServicoDTO toDto) {
            this.descricao = descricao;
            this.toDto = toDto;
        }

        public String getDescricao() {
            return descricao;
        }

        public TempoServicoDTO getToDto() {
            return toDto;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
