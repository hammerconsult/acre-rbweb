package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.WebReportUtil;
import br.com.webpublico.webreportdto.dto.comum.OperacaoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.ParametrosRelatoriosDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-comparativo-debitos-calculados",
        pattern = "/tributario/relatorio/comparativo-debitos-calculados/",
        viewId = "/faces/tributario/relatorio/comparativo-debitos-calculados.xhtml"),
})
public class RelatorioComparativoDebitosCalculadosControlador extends AbstractReport {

    private String filtroUtilizado;
    private TipoCadastroTributario tipoCadastroTributario;
    private Long cadastroInicial;
    private Long cadastroFinal;
    private Calculo.TipoCalculo tipoCalculo;
    private TipoCalculoISS tipoCalculoISS;
    private List<Pessoa> contribuintes;
    private Setor setor;
    private Bairro bairro;
    private Logradouro logradouro;
    private Exercicio anoAtual;
    private Exercicio anoAnterior;
    private TipoRelatorio tipoRelatorio;

    @URLAction(mappingId = "relatorio-comparativo-debitos-calculados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.tipoCadastroTributario = TipoCadastroTributario.IMOBILIARIO;
        this.cadastroInicial = null;
        this.cadastroFinal = null;
        this.tipoCalculo = Calculo.TipoCalculo.IPTU;
        this.tipoCalculoISS = null;
        this.contribuintes = Lists.newArrayList();
        this.setor = null;
        this.bairro = null;
        this.logradouro = null;
        this.anoAtual = getExercicioCorrente();
        this.anoAnterior = getExercicioFacade().getExercicioPorAno(anoAtual.getAno() - 1);
        this.tipoRelatorio = TipoRelatorio.RESUMIDO;
    }

    private RelatorioDTO instanciarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setApi("tributario/relatorio-comparativo-debitos-calculados/");
        dto.setNomeRelatorio("Relatório Comparativo de Lançamento de IPTU");
        dto.adicionarParametro("NOME_MUNICIPIO", "PREFEITURA MUNICIAL DE RIO BRANCO");
        dto.adicionarParametro("USUARIO", getSistemaFacade().obtemLogin());
        dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
        dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.adicionarParametro("SEMDADOS", "SEM DADOS. NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        dto.setNomeParametroBrasao("BRASAO");
        return dto;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            realizarValidacoes();

            List<ParametrosRelatoriosDTO> parametrosRelatorio = (List<ParametrosRelatoriosDTO>) montarParametros().get(0);
            RelatorioDTO dto = instanciarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("FILTROS", filtroUtilizado);
            dto.adicionarParametro("ANOANTERIOR", anoAnterior.getAno());
            dto.adicionarParametro("ANOATUAL", anoAtual.getAno());
            dto.adicionarParametro("TIPOCADASTRO", tipoCadastroTributario.name());
            dto.adicionarParametro("PARAMETROSRELATORIO", parametrosRelatorio);
            if (TipoRelatorioDTO.valueOf(tipoRelatorioExtensao).equals(TipoRelatorioDTO.XLS)) {
                dto.adicionarParametro("FILTROS_EXCEL", montarParametros().get(1));
            }
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception ex) {
            logger.error("ERRO ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getLocalizedMessage());
        }
    }

    public List<SelectItem> getTiposCadastroTributario() {
        return Util.getListSelectItem(Lists.newArrayList(TipoCadastroTributario.ECONOMICO, TipoCadastroTributario.IMOBILIARIO, TipoCadastroTributario.PESSOA));
    }

    public List<SelectItem> getTiposRelatorio() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorio.values());
    }

    public List<SelectItem> getTiposCalculo() {
        if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario))
            return Util.getListSelectItem(Lists.newArrayList(Calculo.TipoCalculo.ALVARA, Calculo.TipoCalculo.ISS));
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario))
            return Util.getListSelectItem(Lists.newArrayList(Calculo.TipoCalculo.IPTU, Calculo.TipoCalculo.ALVARA));
        if (TipoCadastroTributario.PESSOA.equals(tipoCadastroTributario))
            return Util.getListSelectItem(Lists.newArrayList(Calculo.TipoCalculo.ALVARA));

        return Lists.newArrayList();
    }

    public List<SelectItem> getTiposCalculoISS() {
        if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario)
            && tipoCalculo != null && Calculo.TipoCalculo.ISS.equals(tipoCalculo)) {
            return Util.getListSelectItem(Lists.newArrayList(TipoCalculoISS.values()));
        }
        return Lists.newArrayList();
    }

    private void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if ((cadastroInicial == null || cadastroFinal == null) && setor == null && bairro == null && logradouro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Os campos Cadastro Inicial e Final devem ser informados.");
        }
        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cadastro deve ser informado.");
        }
        if (tipoCalculo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Calculo deve ser informado.");
        }
        if (anoAtual == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano Atual deve ser informado.");
        }
        if (anoAnterior == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano Anterior deve ser informado.");
        }
        ve.lancarException();
    }

    private List<Object> montarParametros() {
        List<ParametrosRelatoriosDTO> parametros = Lists.newArrayList();
        List<Object> retorno = Lists.newArrayList();
        Map<String, Map<String, String>> filtrosExcel = Maps.newHashMap();
        String cadastro = null;
        filtroUtilizado = "";

        switch (tipoCadastroTributario) {
            case ECONOMICO:
                cadastro = "ce";
                break;
            case IMOBILIARIO:
                cadastro = "ci";
                break;
        }

        if (cadastro == null) {
            parametros.add(new ParametrosRelatoriosDTO("ce.id", "", null, OperacaoRelatorioDTO.IS_NULL, null, null, 1, false));
            parametros.add(new ParametrosRelatoriosDTO("ci.id", "", null, OperacaoRelatorioDTO.IS_NULL, null, null, 1, false));
        }
        filtroUtilizado += "Tipo de Cadastro: " + tipoCadastroTributario.getDescricao() + "; ";
        WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de Cadastro:", tipoCadastroTributario.getDescricao());
        parametros.add(new ParametrosRelatoriosDTO("calc.tipocalculo", ":tipocalculo", null, OperacaoRelatorioDTO.IGUAL, tipoCalculo.name(), null, 1, false));
        filtroUtilizado += "Tipo de Calculo: " + tipoCalculo.getDescricao() + "; ";
        WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de Calculo:", tipoCalculo.getDescricao());

        if (tipoCalculoISS != null) {
            filtroUtilizado += "Tipo de ISS: " + tipoCalculoISS.getDescricao() + "; ";
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de ISS:", tipoCalculoISS.getDescricao());
            parametros.add(new ParametrosRelatoriosDTO("iss.tipoCalculoISS", ":tipocalculoiss", null, OperacaoRelatorioDTO.IGUAL, tipoCalculoISS.name(), null, 1, false));
        }

        if (tipoCadastroTributario != null && (tipoCadastroTributario.isImobiliario() || tipoCadastroTributario.isEconomico())) {
            if (cadastroInicial != null) {
                parametros.add(new ParametrosRelatoriosDTO(cadastro + ".inscricaoCadastral", ":cadastroinicial", null, OperacaoRelatorioDTO.MAIOR_IGUAL, cadastroInicial.toString(), null, 1, false));
                filtroUtilizado += "Cadastro Inicial: " + cadastroInicial + "; ";
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Cadastro Inicial:", cadastroInicial.toString());
            }
            if (cadastroFinal != null) {
                parametros.add(new ParametrosRelatoriosDTO(cadastro + ".inscricaoCadastral", ":cadastrofinal", null, OperacaoRelatorioDTO.MENOR_IGUAL, cadastroFinal.toString(), null, 1, false));
                filtroUtilizado += "Cadastro Final: " + cadastroFinal + "; ";
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Cadastro Final:", cadastroFinal.toString());
            }
        }
        if (!contribuintes.isEmpty()) {
            parametros.add(new ParametrosRelatoriosDTO("calcp.pessoa_id", ":pessoas", null, OperacaoRelatorioDTO.IN, Util.montarParametroInPeloId(contribuintes), null, 1, false));
            filtroUtilizado += "Contribuintes: ";
            String juncao = "";
            for (Pessoa contribuinte : contribuintes) {
                filtroUtilizado += juncao + contribuinte.getCpf_Cnpj();
                juncao = ", ";
            }
            filtroUtilizado += "; ";
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario)) {
            if (setor != null) {
                parametros.add(new ParametrosRelatoriosDTO("lote.setor_id", ":setor", null, OperacaoRelatorioDTO.IGUAL, setor.getId(), null, 1, false));
                filtroUtilizado += "Setor: " + setor.getNome() + "; ";
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Setor:", setor.getNome());
            }
            if (bairro != null) {
                parametros.add(new ParametrosRelatoriosDTO("bairro.id", ":bairro", null, OperacaoRelatorioDTO.IGUAL, bairro.getId(), null, 1, false));
                filtroUtilizado += "Bairro: " + bairro.getDescricao() + "; ";
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Bairro:", bairro.getDescricao());
            }
            if (logradouro != null) {
                parametros.add(new ParametrosRelatoriosDTO("logradouro.id", ":logradouro", null, OperacaoRelatorioDTO.IGUAL, logradouro.getId(), null, 1, false));
                filtroUtilizado += "Logradouro: " + logradouro.getNome() + "; ";
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Logradouro:", logradouro.getNome());
            }

        }
        retorno.add(parametros);
        retorno.add(filtrosExcel);
        return retorno;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Long getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(Long cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public Long getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(Long cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Calculo.TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public TipoCalculoISS getTipoCalculoISS() {
        return tipoCalculoISS;
    }

    public void setTipoCalculoISS(TipoCalculoISS tipoCalculoISS) {
        this.tipoCalculoISS = tipoCalculoISS;
    }

    public void setTipoCalculo(Calculo.TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public List<Pessoa> getContribuintes() {
        return contribuintes;
    }

    public void setContribuintes(List<Pessoa> contribuintes) {
        this.contribuintes = contribuintes;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Exercicio getAnoAtual() {
        return anoAtual;
    }

    public void setAnoAtual(Exercicio anoAtual) {
        this.anoAtual = anoAtual;
    }

    public Exercicio getAnoAnterior() {
        return anoAnterior;
    }

    public void setAnoAnterior(Exercicio anoAnterior) {
        this.anoAnterior = anoAnterior;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public enum TipoRelatorio {
        RESUMIDO("Resumido"), DETALHADO("Detalhado");

        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
