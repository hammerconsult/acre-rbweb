package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoTransferenciaDeBens;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorioEfetivacaotransfBemMovel", pattern = "/relatorio-efetivacao-transferencia-bem-movel/",
        viewId = "/faces/administrativo/relatorios/relatorio-efetivacao-transf-bem-movel.xhtml")}
)
public class RelatorioEfetivacaoTransfBemMovelControlador {

    public static final String NOME_RELATORIO = "RELATORIO-DE-EFETIVAÇÃO-DE-TRANSFERÊNCIA-DE-BEM-MÓVEL";
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquialOrcamentaria;
    private String numeroSolicitacao;
    private String numeroEfetivacao;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataOperacao;
    private TipoTransferenciaDeBens tipoTransferenciaDeBens;
    private UsuarioSistema usuarioSistema;

    public void limparCampos() {
        hierarquiaAdministrativa = null;
        hierarquialOrcamentaria = null;
        numeroSolicitacao = null;
        numeroEfetivacao = null;
        dataInicial = null;
        dataFinal = null;
        tipoTransferenciaDeBens = null;
        usuarioSistema = sistemaFacade.getUsuarioCorrente();
    }

    @URLAction(mappingId = "relatorioEfetivacaotransfBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte, null,
            sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), null, Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), hierarquiaAdministrativa);
    }

    public List<SelectItem> getTiposDeTransferencia() {
        return Util.getListSelectItem(Arrays.asList(TipoTransferenciaDeBens.values()));
    }


    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        if (tipoTransferenciaDeBens == null){
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Transferência deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data final deve ser posterior a data inicial.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE EFETIVAÇÃO DE TRANSFERÊNCIA DE BEM MÓVEL - " + tipoTransferenciaDeBens.name());
            dto.adicionarParametro("UNIADM", TipoTransferenciaDeBens.CONCEDIDA.equals(tipoTransferenciaDeBens) ? "Unidade Administrativa de Origem: " : "Unidade Administrativa de Destino: ");
            dto.adicionarParametro("UNIADMRESPONSAVEL", TipoTransferenciaDeBens.CONCEDIDA.equals(tipoTransferenciaDeBens) ? "Responsável da Unidade Administrativa de Origem: " : "Responsável da Unidade Administrativa de Destino: ");
            dto.adicionarParametro("SITUACAO", tipoTransferenciaDeBens.getDescricao());
            dto.adicionarParametro("concedida", TipoTransferenciaDeBens.CONCEDIDA.equals(tipoTransferenciaDeBens));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.setNomeRelatorio(NOME_RELATORIO + "-" + tipoTransferenciaDeBens.name());
            dto.setApi("administrativo/efetivacao-transferencia-bem-movel/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex){
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(efet.DATAEFETIVACAO) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        parametros.add(new ParametrosRelatorios(" usuo.usuariosistema_id ", ":idUsuario", null, OperacaoRelatorio.IGUAL, usuarioSistema.getId(), null, 1, false));
        parametros.add(new ParametrosRelatorios(" usuo.gestorpatrimonio ", ":gestor", null, OperacaoRelatorio.IGUAL, 1, null, 1, false));
        if (hierarquiaAdministrativa != null) {
            parametros.add(new ParametrosRelatorios(" vwadm.codigo ", ":codigoAdm", null, OperacaoRelatorio.LIKE, hierarquiaAdministrativa.getCodigoSemZerosFinais() + "%", null, 1, false));
        }
        if (hierarquialOrcamentaria != null) {
            parametros.add(new ParametrosRelatorios(" vworc.codigo ", ":codigoOrc", null, OperacaoRelatorio.LIKE, hierarquialOrcamentaria.getCodigoSemZerosFinais() + "%", null, 1, false));
        }
        if (!Strings.isNullOrEmpty(numeroSolicitacao)){
            parametros.add(new ParametrosRelatorios(" sol.CODIGO ", ":numeroSolicitacao", null, OperacaoRelatorio.IGUAL, numeroSolicitacao.trim(), null, 1, false));
        }
        if (!Strings.isNullOrEmpty(numeroEfetivacao)) {
            parametros.add(new ParametrosRelatorios(" efet.CODIGO ", ":numeroEfetivacao", null, OperacaoRelatorio.IGUAL, numeroEfetivacao.trim(), null, 1, false));
        }
        return parametros;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquialOrcamentaria() {
        return hierarquialOrcamentaria;
    }

    public void setHierarquialOrcamentaria(HierarquiaOrganizacional hierarquialOrcamentaria) {
        this.hierarquialOrcamentaria = hierarquialOrcamentaria;
    }

    public String getNumeroSolicitacao() {
        return numeroSolicitacao;
    }

    public void setNumeroSolicitacao(String numeroSolicitacao) {
        this.numeroSolicitacao = numeroSolicitacao;
    }

    public String getNumeroEfetivacao() {
        return numeroEfetivacao;
    }

    public void setNumeroEfetivacao(String numeroEfetivacao) {
        this.numeroEfetivacao = numeroEfetivacao;
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

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public TipoTransferenciaDeBens getTipoTransferenciaDeBens() {
        return tipoTransferenciaDeBens;
    }

    public void setTipoTransferenciaDeBens(TipoTransferenciaDeBens tipoTransferenciaDeBens) {
        this.tipoTransferenciaDeBens = tipoTransferenciaDeBens;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
