package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioCadastroImobiliario;
import br.com.webpublico.entidadesauxiliares.VOAtributo;
import br.com.webpublico.enums.PatrimonioDoLote;
import br.com.webpublico.enums.tributario.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.RelatorioCadastroImobiliarioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@ManagedBean(name = "relatorioCadastroImobiliarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioCI", pattern = "/tributario/cadastromunicipal/relatorio/relatorio-cadastro-imobiliario/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriocadastroimobiliario.xhtml"),
})
public class RelatorioCadastroImobiliarioControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioCadastroImobiliarioControlador.class);

    @EJB
    private RelatorioCadastroImobiliarioFacade facade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private ConverterAutoComplete converterValorPossivel;
    private AssistenteRelatorioCadastroImobiliario assistente;

    public RelatorioCadastroImobiliarioControlador() {
        AssistenteRelatorioCadastroImobiliario assistente = new AssistenteRelatorioCadastroImobiliario();
        assistente.setCadastroInicial("1");
        assistente.setCadastroFinal("99999999999");
        assistente.setSetor(null);
        assistente.setQuadra(null);
        assistente.setLote(null);
    }

    public AssistenteRelatorioCadastroImobiliario getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteRelatorioCadastroImobiliario assistente) {
        this.assistente = assistente;
    }

    @URLAction(mappingId = "novoRelatorioCI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        assistente = new AssistenteRelatorioCadastroImobiliario();
        assistente.limparCampos();
    }

    public void limparCamposCodigoFace() {
        if (assistente != null) {
            assistente.setCodigoFace(null);
            assistente.setCodigoFaceTestaPrincipal(null);
        }
    }

    public ConverterAutoComplete converterValorPossivel() {
        if (converterValorPossivel == null) {
            converterValorPossivel = new ConverterAutoComplete(ValorPossivel.class, facade.getValorPossivelFacade());
        }
        return converterValorPossivel;
    }

    public List<SelectItem> preencherCategorias() {
        List<SelectItem> retorno = Lists.newArrayList();
        List<CategoriaIsencaoIPTU> categorias = facade.getCategoriaIsencaoIPTUFacade().listaDecrescente();
        retorno.add(new SelectItem(null, " "));
        for (CategoriaIsencaoIPTU cat : categorias) {
            retorno.add(new SelectItem(cat, cat.toString()));
        }
        return retorno;
    }

    public List<SelectItem> preencherTiposRelatorio() {
        return Util.getListSelectItem(TipoRelatorioCadastroImobiliario.values());
    }

    public List<SelectItem> preencherSituacoesCadastro() {
        return Util.getListSelectItem(SituacaoCadastroImobiliario.values());
    }

    public List<Pessoa> buscarPessoas(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public List<VOAtributo> buscarTiposConstrucao(String parte) {
        return facade.buscarCaracteristicasConstrucao(parte, "tipo_construcao");
    }

    public List<VOAtributo> buscarTopografia(String parte) {
        return facade.buscarCaracteristicasTerreno(parte, "topografia");
    }

    public List<VOAtributo> buscarPedologia(String parte) {
        return facade.buscarCaracteristicasTerreno(parte, "pedologia");
    }

    public List<VOAtributo> buscarSituacao(String parte) {
        return facade.buscarCaracteristicasTerreno(parte, "situacao");
    }

    public List<VOAtributo> buscarPatrimonio(String parte) {
        return facade.buscarCaracteristicasTerreno(parte, "patrimonio");
    }

    public List<VOAtributo> buscarTipoImovel(String parte) {
        return facade.buscarCaracteristicasConstrucao(parte, "tipo_imovel");
    }

    public List<VOAtributo> buscarQualidadeConstrucao(String parte) {
        return facade.buscarCaracteristicasConstrucao(parte, "qualidade_construcao");
    }

    public List<VOAtributo> buscarComercial(String parte) {
        return facade.buscarCaracteristicasConstrucao(parte, "comercial");
    }

    public List<VOAtributo> buscarPadraoResidencial(String parte) {
        return facade.buscarCaracteristicasConstrucao(parte, "padrao_residencial");
    }

    public List<SelectItem> buscarAliquotas() {
        return Util.getListSelectItem(facade.aliquotasDisponiveis("aliquota"));
    }

    public List<Logradouro> buscarLogradouros(String parte) {
        return facade.getLogradouroFacade().listaLogradourosAtivos(parte.trim());
    }

    public List<Bairro> buscarBairros(String parte) {
        return facade.getBairroFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public List<Loteamento> buscarLoteamentos(String parte) {
        return facade.getLoteamentoFacade().listaFiltrando(parte.trim(), "nome", "codigo");
    }

    public List<Condominio> buscarCondominios(String parte) {
        return facade.getCondominioFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public List<SelectItem> preencherPatrimoniosDoLote() {
        return Util.getListSelectItem(PatrimonioDoLote.values());
    }

    public List<SelectItem> preencherTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTerreno.values());
    }

    public List<SelectItem> preencherOpcoesCodigoFace() {
        return Util.getListSelectItem(CodigoFaceSimNao.values());
    }

    public List<SelectItem> preencherTiposColetaLixo() {
        return Util.getListSelectItem(TipoColetaLixo.values());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorio();
            montarCondicao();
            assistente.setCondicoes(new StringBuilder(assistente.getCondicoes().toString().replaceFirst("and", "where")));
            assistente.setFiltros(new StringBuilder(StringUtils.chop(assistente.getFiltros().toString().trim())));
            montarOrdenacao();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO_RIO_BRANCO");
            adicionarParametros(dto);
            dto.setNomeRelatorio("Relatório de Cadastro Imobiliário");
            dto.setApi("tributario/cadastro-imobiliario/");

            ReportService.getInstance().gerarRelatorio(assistente.getAbstractReport().usuarioLogado(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException ex) {
            ReportService.getInstance().abrirDialogConfirmar(ex);
            logger.error("Erro ao gerar relatorio. ", ex);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatorio. ", e);
        }
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente != null) {
            if (assistente.getTipoRelatorioCadastroImobiliario() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Relatório deve ser informado.");
            }
        }
        ve.lancarException();
    }

    private void adicionarParametros(RelatorioDTO dto) {
        dto.adicionarParametro("USUARIO", assistente.getAbstractReport().usuarioLogado().getNome(), false);
        dto.adicionarParametro("LOGIN", assistente.getAbstractReport().usuarioLogado().getLogin(), false);
        dto.adicionarParametro("FILTROS", assistente.getFiltros().toString());
        dto.adicionarParametro("gerarBci", isBCI());
        dto.adicionarParametro("complementoQuery", assistente.getCondicoes().toString() + assistente.getOrdem().toString());
        dto.adicionarParametro("incluirJoinPessoaProprietario", assistente.getFiltrarProprietariosCpfCnpjInvalido());
        dto.adicionarParametro("incluirJoinPessoaCompromissario", assistente.getFiltrarCompromissariosCpfCnpjInvalido());
        dto.adicionarParametro("dataRevisao", assistente.getDataBCI());
        dto.adicionarParametro("detalhado", assistente.getDetalhado());
        dto.adicionarParametro("URLPORTAL", cadastroImobiliarioFacade.recuperarUrlPortal() + "/autenticidade-de-documentos/");
    }

    public void gerarRelatorioImobiliario(Long id) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            UsuarioSistema usuarioSistema = assistente != null ? assistente.getAbstractReport().usuarioLogado() : Util.getSistemaControlador().getUsuarioCorrente();
            dto.adicionarParametro("USUARIO", usuarioSistema.getNome(), false);
            dto.setNomeParametroBrasao("BRASAO_RIO_BRANCO");
            dto.adicionarParametro("gerarBci", true);
            dto.adicionarParametro("complementoQuery", " where ci.id = " + id);
            dto.adicionarParametro("incluirJoinPessoaProprietario", assistente != null ? assistente.getFiltrarProprietariosCpfCnpjInvalido() : false);
            dto.adicionarParametro("incluirJoinPessoaCompromissario", assistente != null ? assistente.getFiltrarCompromissariosCpfCnpjInvalido() : false);
            dto.setNomeRelatorio("Relatório de Cadastro Imobiliário");
            dto.setApi("tributario/cadastro-imobiliario/");
            ReportService.getInstance().gerarRelatorio(usuarioSistema, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException ex) {
            ReportService.getInstance().abrirDialogConfirmar(ex);
            logger.error("Erro ao gerar relatorio. ", ex);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatorio. ", e);
        }
    }

    public void montarCondicao() {
        assistente.setCondicoes(new StringBuilder());
        assistente.setOrdem(new StringBuilder());
        assistente.setFiltros(new StringBuilder());

        assistente.getFiltros().append("Tipo do Relatório: ").append(assistente.getTipoRelatorioCadastroImobiliario().getDescricao()).append(", ");
        assistente.getFiltros().append("Detalhado: ").append(assistente.getDetalhado() ? "Sim" : "Não").append(", ");

        assistente.getCondicoes().append(assistente.getSituacaoCadastroImobiliario() != null ?
            (SituacaoCadastroImobiliario.ATIVO.equals(assistente.getSituacaoCadastroImobiliario()) ? " and ci.ativo = 1 " : " and ci.ativo = 0") : "");

        assistente.getFiltros().append(assistente.getSituacaoCadastroImobiliario() != null ? ("Situação Cadastro Imobiliário: " +
            (SituacaoCadastroImobiliario.ATIVO.equals(assistente.getSituacaoCadastroImobiliario()) ? "Ativo" : "Inativo")) + ", " : "");

        if (assistente.getImune() != null && assistente.getImune()) {
            assistente.getCondicoes().append(" and exists(select lot.id ")
                .append(" from lote lot")
                .append(" inner join lote_valoratributo atr on atr.lote_id = lot.id ")
                .append(" inner join atributo on atr.atributos_key = atributo.id and atributo.identificacao = 'patrimonio' ")
                .append(" inner join valoratributo va on va.id = atr.atributos_id ")
                .append(" inner join valorpossivel vp on atributo.id = vp.atributo_id and va.valordiscreto_id = vp.id ")
                .append(" where lot.id = l.id ")
                .append(" and lower(vp.valor) <> 'particular' ) ");

            assistente.getFiltros().append("Imune: Sim, ");
        }
        arredondarValores();
        adicionarCondicoes();
    }

    private void arredondarValores() {
        assistente.setMetragemInicialTerreno(assistente.getMetragemInicialTerreno() != null ? (assistente.getMetragemInicialTerreno().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setMetragemFinalTerreno(assistente.getMetragemFinalTerreno() != null ? (assistente.getMetragemFinalTerreno().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setAreaInicialTerreno(assistente.getAreaInicialTerreno() != null ? (assistente.getAreaInicialTerreno().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setAreaFinalTerreno(assistente.getAreaFinalTerreno() != null ? (assistente.getAreaFinalTerreno().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalInicialTerreno(assistente.getValorVenalInicialTerreno() != null ? (assistente.getValorVenalInicialTerreno().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalFinalTerreno(assistente.getValorVenalFinalTerreno() != null ? (assistente.getValorVenalFinalTerreno().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalInicialImovel(assistente.getValorVenalInicialImovel() != null ? (assistente.getValorVenalInicialImovel().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalFinalImovel(assistente.getValorVenalFinalImovel() != null ? (assistente.getValorVenalFinalImovel().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalInicialExcedente(assistente.getValorVenalInicialExcedente() != null ? (assistente.getValorVenalInicialExcedente().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalFinalExcedente(assistente.getValorVenalFinalExcedente() != null ? (assistente.getValorVenalFinalExcedente().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorInicialAreaConstruida(assistente.getValorInicialAreaConstruida() != null ? (assistente.getValorInicialAreaConstruida().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorFinalAreaConstruida(assistente.getValorFinalAreaConstruida() != null ? (assistente.getValorFinalAreaConstruida().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalInicialConstrucao(assistente.getValorVenalInicialConstrucao() != null ? (assistente.getValorVenalInicialConstrucao().setScale(2, RoundingMode.HALF_UP)) : null);
        assistente.setValorVenalFinalConstrucao(assistente.getValorVenalFinalConstrucao() != null ? (assistente.getValorVenalFinalConstrucao().setScale(2, RoundingMode.HALF_UP)) : null);
    }

    private void adicionarCondicoes() {
        adicionarCondicoesInscricaoCadastral();
        adicionarCondicoesLogradouroAndBairroAndTipoCadastro();
        adicionarCondicoesCompromissario();
        adicionarCondicoesIsento();
        adicionarCondicoesProprietario();
        adicionarCondicoesCategoriaAndAliquota();
        adicionarCondicoesEventosCalculo();
        adicionarCondicaoAreaLote();
        adicionarCondicoesCEP();
        adicionarCondicoesSetorAndQuadraAndLote();
        adicionarCondicoesTestada();
        adicionarCondicoesLoteamentoCondominio();
        adicionarCondicoesPatrimonioDoLote();
        adicionarCondicoesServicosUrbanos();
        adicionarCondicoesTipoCadastroPredial();
        adicionarCondicoesColetaLixo();
        adicionarCondicoesCaracteristicasConstrucao();
        adicionarCondicoesCaracteristicaDoTerreno();
        adicionarCondicaoDataBci();
    }

    private void adicionarCondicaoDataBci() {
        if (assistente.getDataBCI() != null) {
            assistente.getCondicoes().append(" and exists (select 1 from cadastroimobiliario_aud aud ");
            assistente.getCondicoes().append(" inner join revisaoauditoria rev on rev.id = aud.rev ");
            assistente.getCondicoes().append(" where aud.id = ci.id ");
            assistente.getCondicoes().append(" and rev.datahora <= to_date('")
                .append(DataUtil.getDataFormatadaDiaHora(DataUtil.getDataUltimaHoraDia(assistente.getDataBCI())))
                .append("', 'dd/MM/yyyy HH24:mi:ss')) ");

            assistente.getFiltros().append("Data do BCI: " + DataUtil.getDataFormatada(assistente.getDataBCI()));
        }
    }

    private void adicionarCondicoesInscricaoCadastral() {
        if (Strings.isNullOrEmpty(assistente.getCadastroInicial())) {
            assistente.setCadastroInicial("1");
        }
        if (Strings.isNullOrEmpty(assistente.getCadastroFinal())) {
            assistente.setCadastroFinal("999999999999999");
        }
        assistente.getCondicoes().append(" and ci.inscricaocadastral between '").append(assistente.getCadastroInicial().trim()).append("' and '").append(assistente.getCadastroFinal().trim()).append("'");
        assistente.getFiltros().append("Cadastro Inicial: ").append(assistente.getCadastroInicial()).append(", ").append("Cadastro Final: ").append(assistente.getCadastroFinal()).append(", ");
    }

    private void adicionarCondicoesCompromissario() {
        if (assistente.getCompromissario() != null) {
            assistente.getCondicoes().append(" and exists (select com.cadastroimobiliario_id from Compromissario  where ci.id = com.cadastroimobiliario_id and com.compromissario_id = ")
                .append(assistente.getCompromissario().getId()).append(")");
            assistente.getFiltros().append("Compromissário: ").append(assistente.getCompromissario().getNomeCpfCnpj()).append(", ");
        }
        assistente.getCondicoes().append(assistente.getImovelSemCompromissario() ? " and com.id is null " : "");
        assistente.getCondicoes().append(assistente.getFiltrarCompromissariosCpfCnpjInvalido() ? " and (com.id is null or sysdate between com.iniciovigencia and coalesce(com.fimvigencia, sysdate)) " : "");
        assistente.getCondicoes().append(assistente.getFiltrarCompromissariosCpfCnpjInvalido() ? adicionarCondicaoCpfCnpjCompromissarioInvalido() : "");

        assistente.getFiltros().append(assistente.getImovelSemCompromissario() ? "Imóvel sem Compromissário: Sim, " : "");
        assistente.getFiltros().append(assistente.getFiltrarCompromissariosCpfCnpjInvalido() ? "CPF/CNPJ do Compromissário Inválido: Sim, " : "");
    }

    private String adicionarCondicaoCpfCnpjCompromissarioInvalido() {
        return " and (com.id is null or" +
            " (select valida_cpf_cnpj(coalesce(pf.cpf, pj.cnpj)) " +
            " from compromissario comp " +
            " inner join pessoa pes on comp.compromissario_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where comp.cadastroimobiliario_id = ci.id " +
            " and sysdate between comp.iniciovigencia " +
            " and coalesce(comp.fimvigencia, sysdate) " +
            " and rownum = 1) = 'N') ";
    }

    private void adicionarCondicoesIsento() {
        if (assistente.getIsento()) {
            assistente.getCondicoes().append(" and exists (select isencao.id ").append(" from isencaocadastroimobiliario isencao ")
                .append(" where current_date between to_date(to_char(isencao.iniciovigencia,'dd/MM/yyyy'),'dd/MM/yyyy') ")
                .append(" and coalesce(to_date(to_char(isencao.finalvigencia,'dd/MM/yyyy'),'dd/MM/yyyy'),current_date) ")
                .append(" and isencao.cadastroimobiliario_id = ci.id) ");

            assistente.getFiltros().append("Isento: Sim, ");
        }
    }

    private void adicionarCondicoesLogradouroAndBairroAndTipoCadastro() {
        if (assistente.getLogradouro() != null) {
            assistente.getCondicoes().append(" and upper(logr.NOME) like '%").append(assistente.getLogradouro().getNome().toUpperCase()).append("%'");
            assistente.getFiltros().append("Logradouro: ").append(assistente.getLogradouro().getNome()).append(", ");
        }
        if (assistente.getImovelSemLogradouro()) {
            assistente.getCondicoes().append(" and logr.id is null ");
            assistente.getFiltros().append("Imóvel sem Logradouro: Sim, ");
        }

        if (assistente.getBairro() != null) {
            assistente.getCondicoes().append(" and upper(b.descricao) like '%").append(assistente.getBairro().getDescricao().toUpperCase()).append("%'");
            assistente.getFiltros().append("Logradouro: ").append(assistente.getBairro().getDescricao()).append(", ");
        }
        if (assistente.getImovelSemBairro()) {
            assistente.getCondicoes().append(" and b.id is null ");
            assistente.getFiltros().append("Imóvel sem Bairro: Sim, ");
        }

        if (assistente.getTipoCadastro() != null) {
            assistente.getCondicoes().append(" and ").append(TipoCadastroTerreno.PREDIAL.equals(assistente.getTipoCadastro()) ? " exists " : " not exists ")
                .append(" (select case when cons.CANCELADA = 0 then 'PREDIAL' else 'TERRITORIAL' end as tipoCadastro from construcao cons " +
                    " where cons.IMOVEL_ID = ci.ID and (cons.CANCELADA = 0 or cons.CANCELADA is null) )");

            assistente.getFiltros().append("Tipo de Cadastro: ").append(assistente.getTipoCadastro().getDescricao()).append(", ");
        }
    }

    private void adicionarCondicoesProprietario() {
        if (assistente.getProprietario() != null) {
            assistente.getCondicoes().append(" and prop.pessoa_id = ").append(assistente.getProprietario().getId());
            assistente.getFiltros().append("Proprietário: ").append(assistente.getProprietario().getNomeCpfCnpj()).append(", ");
        }
        assistente.getCondicoes().append(assistente.getImovelSemProprietario() ? " and prop.id is null " : "");
        assistente.getCondicoes().append(assistente.getFiltrarProprietariosCpfCnpjInvalido() ? " and (prop.id is null or sysdate between prop.iniciovigencia and coalesce(prop.finalvigencia, sysdate)) " : "");
        assistente.getCondicoes().append(assistente.getFiltrarProprietariosCpfCnpjInvalido() ? adicionarCondicaoCpfCnpjProprietarioInvalido() : "");

        assistente.getFiltros().append(assistente.getImovelSemProprietario() ? "Imóvel sem Proprietário: Sim, " : "");
        assistente.getFiltros().append(assistente.getFiltrarProprietariosCpfCnpjInvalido() ? "CPF/CNPJ do Proprietário Inválido: Sim, " : "");
    }

    private String adicionarCondicaoCpfCnpjProprietarioInvalido() {
        return " and (prop.id is null or" +
            " (select valida_cpf_cnpj(coalesce(pf.cpf, pj.cnpj)) " +
            " from propriedade prop " +
            " inner join pessoa pes on prop.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where prop.imovel_id = ci.id " +
            " and sysdate between prop.iniciovigencia " +
            " and coalesce(prop.finalvigencia, sysdate) " +
            " and rownum = 1) = 'N') ";
    }

    private void adicionarCondicoesCategoriaAndAliquota() {
        if (assistente.getCategoria() != null) {
            assistente.getCondicoes().append(" and exists (select categoria.id ")
                .append(" from IsencaoCadastroImobiliario isencao ")
                .append(" inner join processoisencaoiptu processo on isencao.processoisencaoiptu_id = processo.id ")
                .append(" inner join categoriaisencaoiptu categoria on categoria.id = processo.categoriaisencaoiptu_id  ")
                .append(" where ci.id = isencao.cadastroimobiliario_id  and categoria.id = ").append(assistente.getCategoria().getId()).append(")");
            assistente.getFiltros().append("Categoria: ").append(assistente.getCategoria().getDescricao()).append(", ");
        }

        if (assistente.getAliquota() != null || assistente.getImovelSemAliquiota()) {
            if (assistente.getImovelSemAliquiota()) {
                assistente.setAliquota(BigDecimal.ZERO);
                assistente.getFiltros().append("Imóvel sem Alíquota: Sim").append(", ");
            } else {
                BigDecimal aliquota = assistente.getAliquota().multiply(new BigDecimal(100));
                assistente.getFiltros().append("Alíquota: Sim").append(Util.formataPercentual(aliquota)).append(", ");
            }
            String condicao = " and evento.valor * 100 = " + assistente.getAliquota();
            montarCondicaoAtributoGenerico("aliquota", condicao);
        }
    }

    private void adicionarCondicoesEventosCalculo() {
        String condicao = "";

        if (assistente.getMetragemInicialTerreno() != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + assistente.getMetragemInicialTerreno();
            assistente.getFiltros().append("Metragem Inicial Terreno: ").append(assistente.getMetragemInicialTerreno()).append(", ");
        }
        if (assistente.getMetragemFinalTerreno() != null) {
            condicao += " and round(coalesce(evento.valor, 0), 2) <= " + assistente.getMetragemFinalTerreno();
            assistente.getFiltros().append("Metragem Final Terreno: ").append(assistente.getMetragemFinalTerreno()).append(", ");
        }
        if (assistente.getImovelSemMetragemTerreno()) {
            condicao = " and (round(coalesce(evento.valor, 0), 2) is null or round(coalesce(evento.valor, 0), 2) = 0)";
            assistente.getFiltros().append("Imóvel sem Metragem do Terreno: Sim").append(", ");
        }
        montarCondicaoAtributoGenerico("valorM2Terreno", condicao);
        condicao = "";


        if (assistente.getValorVenalInicialTerreno() != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + assistente.getValorVenalInicialTerreno();
            assistente.getFiltros().append("Valor Venal Inicial do Terreno: ").append(assistente.getValorVenalInicialTerreno()).append(", ");
        }
        if (assistente.getValorVenalFinalTerreno() != null) {
            condicao += " and round(coalesce(evento.valor, 0), 2) <= " + assistente.getValorVenalFinalTerreno();
            assistente.getFiltros().append("Valor Venal Final do Terreno: ").append(assistente.getValorVenalFinalTerreno()).append(", ");
        }
        if (assistente.getImovelSemValorVenalTerreno()) {
            condicao = " and (evento.valor is null or evento.valor = 0)";
            assistente.getFiltros().append("Imóvel sem Valor Venal do Terreno: Sim").append(", ");
        }
        montarCondicaoAtributoGenerico("valorVenalTerrenoParaBci", condicao);
        condicao = "";

        if (assistente.getValorVenalInicialImovel() != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + assistente.getValorVenalInicialImovel();
            assistente.getFiltros().append("Valor Venal Inicial do Imóvel: ").append(assistente.getValorVenalInicialImovel()).append(", ");
        }
        if (assistente.getValorVenalFinalImovel() != null) {
            condicao += " and round(coalesce(evento.valor, 0), 2) <= " + assistente.getValorVenalFinalImovel();
            assistente.getFiltros().append("Valor Venal Final do Imóvel: ").append(assistente.getValorVenalFinalImovel()).append(", ");
        }
        if (assistente.getImovelSemValorVenalImovel()) {
            condicao = " and (round(coalesce(evento.valor, 0), 2) is null or round(coalesce(evento.valor, 0), 2) = 0)";
            assistente.getFiltros().append("Imóvel sem Valor Venal: Sim").append(", ");
        }
        montarCondicaoAtributoGenerico("valorVenalImovelBCI", condicao);
        condicao = "";

        if (assistente.getValorVenalInicialExcedente() != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + assistente.getValorVenalInicialExcedente();
            assistente.getFiltros().append("Valor Venal Inicial Excedente: ").append(assistente.getValorVenalInicialExcedente()).append(", ");
        }
        if (assistente.getValorVenalFinalExcedente() != null) {
            condicao += " and round(coalesce(evento.valor, 0), 2) <= " + assistente.getValorVenalFinalExcedente();
            assistente.getFiltros().append("Valor Venal Final Excedente: ").append(assistente.getValorVenalFinalExcedente()).append(", ");
        }
        if (assistente.getImovelSemValorVenalExcedente()) {
            condicao = " and (round(coalesce(evento.valor, 0), 2) is null or round(coalesce(evento.valor, 0), 2) = 0)";
            assistente.getFiltros().append("Imóvel sem Valor Venal Excedente: Sim").append(", ");
        }
        montarCondicaoAtributoGenerico("valorVenalExcedente", condicao);
    }

    private void montarCondicoesEventosCalculoConstrucao(String identificacaoEvento, String condicao) {
        if (!Strings.isNullOrEmpty(condicao)) {
            assistente.getCondicoes().append(" and exists ( select evento.id ").append(" from eventocalculoconstrucao evento ")
                .append(" inner join construcao const on evento.construcao_id = const.id ")
                .append(" inner join eventoconfiguradobci conf on conf.id = evento.eventocalculo_id ")
                .append(" inner join eventocalculo evcalculo on evcalculo.id = conf.eventocalculo_id ")
                .append(" where conf.configuracaotributario_id = ( ")
                .append(" select id from configuracaotributario c where c.vigencia = (")
                .append(" select max(a.vigencia) from ConfiguracaoTributario a)) ")
                .append(" and evCalculo.identificacao = '").append(identificacaoEvento).append("'")
                .append(" and const.imovel_id = ci.id ")
                .append(condicao).append(")");
        }
    }

    private String getString(String condicao, BigDecimal valorVenalInicialTerreno, BigDecimal valorVenalFinalTerreno, String s, Boolean imovelSemValorVenalTerreno) {
        if (valorVenalInicialTerreno != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + valorVenalInicialTerreno;
        }
        if (valorVenalFinalTerreno != null) {
            condicao += s + valorVenalFinalTerreno;
        }
        if (imovelSemValorVenalTerreno) {
            condicao = " and (round(coalesce(evento.valor, 0), 2) is null or round(coalesce(evento.valor, 0), 2) = 0)";
        }
        return condicao;
    }

    private void adicionarCondicaoAreaLote() {
        if (assistente.getAreaInicialTerreno() != null) {
            assistente.getCondicoes().append(" and l.arealote >= ").append(assistente.getAreaInicialTerreno());
            assistente.getFiltros().append("Área Inicial do Terreno: ").append(assistente.getAreaInicialTerreno()).append(", ");
            if (assistente.getAreaFinalTerreno() != null) {
                assistente.getCondicoes().append(" and l.arealote <= ").append(assistente.getAreaFinalTerreno());
                assistente.getFiltros().append("Área Final do Terreno: ").append(assistente.getAreaFinalTerreno()).append(", ");
            }
        }
    }

    private void montarCondicaoAtributoGenerico(String identificacaoEvento, String condicao) {
        if (!Strings.isNullOrEmpty(condicao)) {
            assistente.getCondicoes().append(" and exists ( select evento.id ").append(" from eventocalculobci evento ")
                .append(" inner join eventoconfiguradobci conf on conf.id = evento.eventocalculo_id ")
                .append(" inner join eventocalculo evcalculo on evcalculo.id = conf.eventocalculo_id ")
                .append(" where conf.configuracaotributario_id = ( ")
                .append(" select id from configuracaotributario c where c.vigencia = (")
                .append(" select max(a.vigencia) from ConfiguracaoTributario a)) ")
                .append(" and evCalculo.identificacao = '").append(identificacaoEvento).append("'")
                .append(" and evento.cadastroimobiliario_id = ci.id ")
                .append(condicao).append(")");
        }
    }

    private void adicionarCondicoesCEP() {
        if (!Strings.isNullOrEmpty(assistente.getCep())) {
            assistente.getCondicoes().append(" and lb.cep = '").append(assistente.getCep()).append("'");
            assistente.getFiltros().append("CEP: ").append(assistente.getCep()).append(", ");
        }
        assistente.getCondicoes().append(assistente.getCepInvalido() ? " and not regexp_like(lb.cep, '^\\d{5}-?\\d{3}$') " : "");
        assistente.getCondicoes().append(assistente.getCepNulo() ? " and lb.cep is null " : "");

        assistente.getFiltros().append(assistente.getCepInvalido() ? "CEP Inválido: Sim, " : "");
        assistente.getFiltros().append(assistente.getCepNulo() ? "CEP Nulo: Sim, " : "");
    }

    private void adicionarCondicoesSetorAndQuadraAndLote() {
        if (assistente.getSetor() != null && !assistente.getSetor().trim().isEmpty()) {
            assistente.getCondicoes().append(" and s.codigo like '%").append(assistente.getSetor()).append("%'");
            assistente.getFiltros().append("Setor: ").append(assistente.getSetor()).append(", ");
        }
        if (assistente.getQuadra() != null && !assistente.getQuadra().trim().isEmpty()) {
            assistente.getCondicoes().append(" and q.codigo like '%").append(assistente.getQuadra()).append("%'");
            assistente.getFiltros().append("Quadra: ").append(assistente.getQuadra()).append(", ");
        }
        if (assistente.getLote() != null && !assistente.getLote().trim().isEmpty()) {
            assistente.getCondicoes().append(" and l.codigolote like '%").append(assistente.getLote()).append("%'");
            assistente.getFiltros().append("Lote: ").append(assistente.getLote()).append(", ");
        }
    }

    private void adicionarCondicoesTestada() {
        if (assistente.getLarguraInicial() != null) {
            assistente.getCondicoes().append(" and f.largura >= ").append(assistente.getLarguraInicial());
            assistente.getFiltros().append("Largura Inicial Testada: ").append(assistente.getLarguraInicial()).append(", ");
            if (assistente.getLarguraFinal() != null) {
                assistente.getCondicoes().append(" and f.largura <= ").append(assistente.getLarguraFinal());
                assistente.getFiltros().append("Largura Final Testada: ").append(assistente.getLarguraFinal()).append(", ");
            }
        }
        if (assistente.getMetragemInicialTestada() != null) {
            assistente.getCondicoes().append(" and t.tamanho >= ").append(assistente.getMetragemInicialTestada());
            assistente.getFiltros().append("Metragem Inicial Testada: ").append(assistente.getMetragemInicialTestada()).append(", ");
            if (assistente.getMetragemFinalTestada() != null) {
                assistente.getCondicoes().append(" and t.tamanho <= ").append(assistente.getMetragemFinalTestada());
                assistente.getFiltros().append("Metragem Final Testada: ").append(assistente.getMetragemFinalTestada()).append(", ");
            }
        }

        if (assistente.getCodigoFaceSimNao() != null) {
            if (CodigoFaceSimNao.NAO.equals(assistente.getCodigoFaceSimNao())) {
                assistente.getCondicoes().append(" and f.codigoFace is null ");
                assistente.getFiltros().append("Possúi Face: Não").append(", ");
            } else {
                assistente.getCondicoes().append(" and f.codigoFace is not null ");
                if (!Strings.isNullOrEmpty(assistente.getCodigoFace())) {
                    assistente.getCondicoes().append(" and f.codigoFace = '").append(assistente.getCodigoFace()).append("'");
                    assistente.getFiltros().append("Código Face: ").append(assistente.getCodigoFace()).append(", ");
                }
                if (assistente.getCodigoFaceTestaPrincipal() != null) {
                    Integer principal = CodigoFaceSimNao.NAO.equals(assistente.getCodigoFaceTestaPrincipal()) ? 0 : 1;
                    assistente.getCondicoes().append(" and t.principal = ").append(principal);
                    assistente.getFiltros().append("Face da Testada Principal: ").append(assistente.getCodigoFaceTestaPrincipal().getDescricao()).append(", ");
                }
            }
        }
    }

    private void adicionarCondicoesLoteamentoCondominio() {
        if (assistente.getLoteamento() != null) {
            assistente.getCondicoes().append(" and loteamento.id = ").append(assistente.getLoteamento().getId());
            assistente.getFiltros().append("Loteamento: ").append(assistente.getLoteamento().getCodigo()).append(" - ").append(assistente.getLoteamento().getNome()).append(", ");
        }
        if (assistente.getCondominio() != null) {
            assistente.getCondicoes().append(" and condominio.condominio_id  = ").append(assistente.getCondominio().getId());
            assistente.getFiltros().append("Condomínio: ").append(assistente.getCondominio().getDescricao()).append(", ");
        }
    }

    private void adicionarCondicoesPatrimonioDoLote() {
        if (assistente.getPatrimoniosDoLote() != null && !assistente.getPatrimoniosDoLote().isEmpty()) {
            List<String> valores = Lists.newArrayList();
            for (PatrimonioDoLote patrimonio : assistente.getPatrimoniosDoLote()) {
                valores.add("'" + patrimonio.getDescricao().split("-")[1].toLowerCase() + "'");
            }
            assistente.getCondicoes().append(" and l.id in(select lot.id")
                .append(" from lote lot")
                .append(" inner join lote_valoratributo atr on atr.lote_id = lot.id")
                .append(" inner join atributo on atr.atributos_key = atributo.id and atributo.identificacao = 'patrimonio'")
                .append(" inner join valoratributo va on va.id = atr.atributos_id")
                .append(" inner join valorpossivel vp on atributo.id = vp.atributo_id and va.valordiscreto_id = vp.id")
                .append(" where lot.id = l.id")
                .append(" and lower(vp.valor) in (").append(Joiner.on(",").join(valores)).append("))");
            assistente.getFiltros().append("Patrimônios do Lote: ").append(Joiner.on(",").join(valores)).append(", ");
        }
    }

    private void adicionarCondicoesServicosUrbanos() {
        if (assistente.getPossuiServicoUrbano() != null) {
            String exists = CodigoFaceSimNao.NAO.equals(assistente.getPossuiServicoUrbano()) ? " not " : "";
            assistente.getCondicoes().append(" and ").append(exists).append(" exists ( ")
                .append(" select su.id from servicourbano su ")
                .append(" inner join faceservico fs on fs.servicourbano_id = su.id ")
                .append(" inner join testada on testada.face_id = fs.face_id ")
                .append(" inner join cadastroimobiliario bci on bci.lote_id = testada.lote_id ")
                .append(" where bci.id = ci.id )");

            assistente.getFiltros().append("Possúi Serviço Urbano: ").append(assistente.getPossuiServicoUrbano().getDescricao()).append(", ");
        }
    }

    private void adicionarCondicoesTipoCadastroPredial() {
        String condicao = "";
        if (assistente.getValorVenalInicialConstrucao() != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + assistente.getValorVenalInicialConstrucao();
            assistente.getFiltros().append("Valor Venal Inicial da Construção: ").append(assistente.getValorVenalInicialConstrucao()).append(", ");
        }
        if (assistente.getValorVenalFinalConstrucao() != null) {
            condicao += " and round(coalesce(evento.valor, 0), 2) <= " + assistente.getValorVenalFinalConstrucao();
            assistente.getFiltros().append("Valor Venal Final da Construção: ").append(assistente.getValorVenalFinalConstrucao()).append(", ");
        }
        if (assistente.getImovelSemValorVenalConstrucao()) {
            condicao = " and (round(coalesce(evento.valor, 0), 2) is null or round(coalesce(evento.valor, 0), 2) = 0)";
            assistente.getFiltros().append("Imóvel sem Valor Venal da Construção: Sim").append(", ");
        }
        montarCondicoesEventosCalculoConstrucao("valorVenalConstrucao", condicao);

        condicao = "";

        if (assistente.getValorInicialAreaConstruida() != null) {
            condicao = " and round(coalesce(evento.valor, 0), 2) >= " + assistente.getValorInicialAreaConstruida();
            assistente.getFiltros().append("Valor Venal Inicial da Área Construída: ").append(assistente.getValorInicialAreaConstruida()).append(", ");
        }
        if (assistente.getValorFinalAreaConstruida() != null) {
            condicao += " and round(coalesce(evento.valor, 0), 2) <= " + assistente.getValorFinalAreaConstruida();
            assistente.getFiltros().append("Valor Venal Final da Área Construída: ").append(assistente.getValorFinalAreaConstruida()).append(", ");
        }
        montarCondicoesEventosCalculoConstrucao("valorM2Construcao", condicao);

        if (assistente.getTiposConstrucao() != null && !assistente.getTiposConstrucao().isEmpty()) {
            List<Long> valores = Lists.newArrayList();
            for (ValorPossivel atributo : assistente.getTiposConstrucao()) {
                valores.add(atributo.getId());
            }
            assistente.getCondicoes().append(" and exists( ")
                .append(" select vp.id from construcao_valoratributo const_valor ")
                .append(" inner join valoratributo va on va.id = const_valor.atributos_id ")
                .append(" inner join valorpossivel vp on vp.id = va.valordiscreto_id ")
                .append(" inner join construcao const on const.id = const_valor.construcao_id ")
                .append(" inner join atributo atr on const_valor.atributos_key = atr.id ")
                .append(" where atr.identificacao = 'tipo_construcao' ")
                .append(" and const.imovel_id = ci.id ")
                .append(" and vp.id in (").append(Joiner.on(",").join(valores)).append("))");
            assistente.getFiltros().append("Tipos de Construção: ").append(Joiner.on(",").join(valores)).append(", ");
        }
    }

    private void adicionarCondicoesColetaLixo() {
        if (assistente.getPossuiColetaLixo() != null) {
            assistente.getFiltros().append("Possúi Coleta de Lixo: ").append(assistente.getPossuiColetaLixo().getDescricao()).append(", ");
            List<String> tipos = Lists.newArrayList();
            for (TipoColetaLixo tipo : TipoColetaLixo.values()) {
                tipos.add("'" + tipo.getIdentificacao() + "'");
            }
            String exists = "";

            if (CodigoFaceSimNao.NAO.equals(assistente.getPossuiColetaLixo())) {
                exists = " not ";
            } else {
                if (assistente.getTipoColetaLixo() != null) {
                    String identificacao = !TipoColetaLixo.DIARIA.equals(assistente.getTipoColetaLixo()) ? "'" + TipoColetaLixo.DIARIA.getIdentificacao() + "'" :
                        "'" + TipoColetaLixo.ALTERNADA.getIdentificacao() + "'";
                    tipos.remove(identificacao);
                }
            }
            assistente.getCondicoes().append(" and ").append(exists).append(" exists ( ")
                .append(" select su.nome from servicourbano su ")
                .append(" inner join faceservico fs on fs.servicourbano_id = su.id ")
                .append(" inner join testada on testada.face_id = fs.face_id ")
                .append(" inner join cadastroimobiliario bci on bci.lote_id = testada.lote_id ")
                .append(" where bci.id = ci.id ")
                .append(" and su.identificacao in (").append(Joiner.on(",").join(tipos)).append("))");
            assistente.getFiltros().append("Tipo(s) de Coleta(s): ").append(Joiner.on(",").join(tipos)).append(", ");
        }
    }

    private String existsCaracteristicasConstrucao(String identificacao, Long idValorPadrao) {
        return " and exists( " +
            " select vp.id " +
            " from construcao_valoratributo const_valor " +
            " inner join valoratributo va on va.id = const_valor.atributos_id " +
            " inner join valorpossivel vp on vp.id = va.valordiscreto_id " +
            " inner join construcao const on const.id = const_valor.construcao_id " +
            " inner join atributo atr on const_valor.atributos_key = atr.id " +
            " where atr.identificacao = '" + identificacao + "' and vp.id = " + idValorPadrao + " and const.imovel_id = ci.id )";
    }

    private String existsCaracteristicasTerreno(String identificacao, Long idValorPadrao) {
        return " and exists " +
            " (select vp.id " +
            " from lote_valoratributo lote_valor " +
            " inner join atributo atr on lote_valor.atributos_key = atr.id " +
            " inner join valoratributo va on va.id = lote_valor.atributos_id " +
            " inner join valorpossivel vp on atr.id = vp.atributo_id and va.valordiscreto_id = vp.id " +
            " where lote_valor.lote_id = l.id " +
            " and atr.identificacao = '" + identificacao + "' and vp.id = " + idValorPadrao + " )";
    }

    private void adicionarCondicoesCaracteristicasConstrucao() {
        if (assistente.getTipoImovel() != null) {
            assistente.getCondicoes().append(existsCaracteristicasConstrucao("tipo_imovel", assistente.getTipoImovel().getId()));
            assistente.getFiltros().append("Tipo do Imóvel: ").append(assistente.getTipoImovel().getCodigo()).append(" - ").append(assistente.getTipoImovel().getDescricao()).append(", ");
        }
        if (assistente.getQualidadeConstrucao() != null) {
            assistente.getCondicoes().append(existsCaracteristicasConstrucao("qualidade_construcao", assistente.getQualidadeConstrucao().getId()));
            assistente.getFiltros().append("Qualidade da Construção: ").append(assistente.getQualidadeConstrucao().getCodigo()).append(" - ").append(assistente.getQualidadeConstrucao().getDescricao()).append(", ");
        }
        if (assistente.getComercial() != null) {
            assistente.getCondicoes().append(existsCaracteristicasConstrucao("comercial", assistente.getComercial().getId()));
            assistente.getFiltros().append("Comercial: ").append(assistente.getComercial().getCodigo()).append(" - ").append(assistente.getComercial().getDescricao()).append(", ");
        }
        if (assistente.getPadraoResidencial() != null) {
            assistente.getCondicoes().append(existsCaracteristicasConstrucao("padrao_residencial", assistente.getPadraoResidencial().getId()));
            assistente.getFiltros().append("Padrão Residencial: ").append(assistente.getPadraoResidencial().getCodigo()).append(" - ").append(assistente.getPadraoResidencial().getDescricao()).append(", ");
        }
    }

    private void adicionarCondicoesCaracteristicaDoTerreno() {
        if (assistente.getTopografia() != null) {
            assistente.getCondicoes().append(existsCaracteristicasTerreno("topografia", assistente.getTopografia().getId()));
            assistente.getFiltros().append("Topografia: ").append(assistente.getTopografia().getCodigo()).append(" - ").append(assistente.getTopografia().getDescricao()).append(", ");
        }
        if (assistente.getPedologia() != null) {
            assistente.getCondicoes().append(existsCaracteristicasTerreno("pedologia", assistente.getPedologia().getId()));
            assistente.getFiltros().append("Pedologia: ").append(assistente.getPedologia().getCodigo()).append(" - ").append(assistente.getPedologia().getDescricao()).append(", ");
        }
        if (assistente.getSituacao() != null) {
            assistente.getCondicoes().append(existsCaracteristicasTerreno("situacao", assistente.getSituacao().getId()));
            assistente.getFiltros().append("Situação: ").append(assistente.getSituacao().getCodigo()).append(" - ").append(assistente.getSituacao().getDescricao()).append(", ");
        }
        if (assistente.getPatrimonio() != null) {
            assistente.getCondicoes().append(existsCaracteristicasTerreno("patrimonio", assistente.getPatrimonio().getId()));
            assistente.getFiltros().append("Patrimônio: ").append(assistente.getPatrimonio().getCodigo()).append(" - ").append(assistente.getPatrimonio().getDescricao()).append(", ");
        }
    }

    private void montarOrdenacao() {
        assistente.getOrdem().append(" order by ci.inscricaocadastral asc");
    }

    public boolean isBCI() {
        return assistente != null && TipoRelatorioCadastroImobiliario.BCI.equals(assistente.getTipoRelatorioCadastroImobiliario());
    }

    public boolean isCadastroImobiliario() {
        return assistente != null && TipoRelatorioCadastroImobiliario.CADASTRO_IMOBILIARIO.equals(assistente.getTipoRelatorioCadastroImobiliario());
    }


    public boolean isPredial() {
        return assistente != null && TipoCadastroTerreno.PREDIAL.equals(assistente.getTipoCadastro());
    }

    public boolean hasCodigoFace() {
        return assistente != null && CodigoFaceSimNao.SIM.equals(assistente.getCodigoFaceSimNao());
    }

    public boolean hasColetaLixo() {
        return assistente != null && CodigoFaceSimNao.SIM.equals(assistente.getPossuiColetaLixo());
    }

    public boolean hasBuscarProprietario() {
        if (assistente != null) {
            return assistente.getImovelSemProprietario();
        }
        return false;
    }

    public boolean hasBuscarCompromissario() {
        if (assistente != null) {
            return assistente.getImovelSemCompromissario();
        }
        return false;
    }

    public boolean hasCampoStringInvalido(String campo) {
        if (assistente != null) {
            return !Strings.isNullOrEmpty(campo != null ? campo.trim() : "");
        }
        return false;
    }

    public boolean hasSelecionarAliquota() {
        if (assistente != null) {
            return assistente.getAliquota() != null && assistente.getAliquota().compareTo(BigDecimal.ZERO) > 0;
        }
        return false;
    }

    public void adicionarPatrimonioLote() {
        if (assistente != null && assistente.getPatrimoniosDoLote() != null && assistente.getPatrimonioDoLote() != null) {
            try {
                validarPatrimonioLote();
                assistente.getPatrimoniosDoLote().add(assistente.getPatrimonioDoLote());

            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
            assistente.setPatrimonioDoLote(null);
        }
    }

    private void validarPatrimonioLote() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getPatrimoniosDoLote().contains(assistente.getPatrimonioDoLote())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de patrimônio já foi adicionado.");
        }
        ve.lancarException();
    }

    public void removerPatrimonioLote(PatrimonioDoLote patrimonioDoLote) {
        if (assistente != null && assistente.getPatrimoniosDoLote() != null) {
            assistente.getPatrimoniosDoLote().remove(patrimonioDoLote);
        }
    }

    public void adicionarTipoConstrucao() {
        if (assistente.getTiposConstrucao() != null && assistente.getTipoConstrucao() != null) {
            Util.adicionarObjetoEmLista(assistente.getTiposConstrucao(), assistente.getTipoConstrucao());
        }
        assistente.setTipoConstrucao(null);
    }

    public void removerTipoConstrucao(ValorPossivel tipoConstrucao) {
        if (assistente != null && assistente.getTiposConstrucao() != null) {
            assistente.getTiposConstrucao().remove(tipoConstrucao);
        }
    }

    public void gerarRelatorioImobiliario(CadastroImobiliario cadastroImobiliario) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            UsuarioSistema usuarioSistema = assistente != null ? assistente.getAbstractReport().usuarioLogado() : Util.getSistemaControlador().getUsuarioCorrente();
            dto.adicionarParametro("USUARIO", usuarioSistema.getNome(), false);
            dto.adicionarParametro("LOGIN", usuarioSistema.getLogin(), false);
            dto.setNomeParametroBrasao("BRASAO_RIO_BRANCO");
            dto.adicionarParametro("gerarBci", true);
            dto.adicionarParametro("complementoQuery", " where ci.id = " + cadastroImobiliario.getId());
            dto.adicionarParametro("incluirJoinPessoaProprietario", assistente != null ? assistente.getFiltrarProprietariosCpfCnpjInvalido() : false);
            dto.adicionarParametro("incluirJoinPessoaCompromissario", assistente != null ? assistente.getFiltrarCompromissariosCpfCnpjInvalido() : false);
            dto.setNomeRelatorio("Relatório de Cadastro Imobiliário");
            dto.setApi("tributario/cadastro-imobiliario/");
            dto.adicionarParametro("URLPORTAL", cadastroImobiliarioFacade.recuperarUrlPortal() + "/autenticidade-de-documentos/");
            ReportService.getInstance().gerarRelatorio(usuarioSistema, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException ex) {
            ReportService.getInstance().abrirDialogConfirmar(ex);
            logger.error("Erro ao gerar relatorio. ", ex);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatorio. ", e);
        }
    }
}
