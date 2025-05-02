/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Localizacao;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LocalizacaoFacade;
import br.com.webpublico.negocios.PontoComercialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.util.List;

@ManagedBean(name = "relacaoPontosComerciais")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relacaoPontosComerciais", pattern = "/relacao-de-pontos-comerciais/", viewId = "/faces/tributario/rendaspatrimoniais/relatorio/pontoscomerciais.xhtml")
})
public class RelacaoPontosComerciais extends AbstractReport {

    @Limpavel(Limpavel.NULO)
    private Localizacao localizacao;
    @EJB
    private LocalizacaoFacade localizacaoFacade;
    private Converter converterLocalizacao;
    @Limpavel(Limpavel.NULO)
    private Long codigoInicio;
    @Limpavel(Limpavel.NULO)
    private Long codigoFim;
    @Limpavel(Limpavel.NULO)
    private String inscricaoCadastralInicial;
    @Limpavel(Limpavel.NULO)
    private String inscricaoCadastralFinal;
    private final String arquivoJasper = "RelatorioPontosComerciais.jasper";
    private StringBuilder where;
    private StringBuilder filtros;
    private StringBuilder semDados;
    private SistemaControlador sistemaControlador;
    @EJB
    private PontoComercialFacade pontoComercialFacade;


    public RelacaoPontosComerciais() {
        localizacao = new Localizacao();
        codigoFim = null;
        codigoInicio = null;
        inscricaoCadastralInicial = null;
        inscricaoCadastralFinal = null;
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Long getCodigoFim() {
        return codigoFim;
    }

    public void setCodigoFim(Long codigoFim) {
        this.codigoFim = codigoFim;
    }

    public Long getCodigoInicio() {
        return codigoInicio;
    }

    public void setCodigoInicio(Long codigoInicio) {
        this.codigoInicio = codigoInicio;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public String getInscricaoCadastralInicial() {
        return inscricaoCadastralInicial;
    }

    public void setInscricaoCadastralInicial(String inscricaoCadastralInicial) {
        this.inscricaoCadastralInicial = inscricaoCadastralInicial;
    }

    public String getInscricaoCadastralFinal() {
        return inscricaoCadastralFinal;
    }

    public void setInscricaoCadastralFinal(String inscricaoCadastralFinal) {
        this.inscricaoCadastralFinal = inscricaoCadastralFinal;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Converter getConverterLocalizacao() {
        if (converterLocalizacao == null) {
            converterLocalizacao = new ConverterAutoComplete(Localizacao.class, localizacaoFacade);
        }
        return converterLocalizacao;
    }

    public List<Localizacao> completaLocalizacao(String parte) {
        List<Localizacao> lista = localizacaoFacade.listaLotacaoPorUsuarioCorrente(parte.trim(), "descricao", "codigo", "lotacaoVistoriadora.descricao");
        if (lista == null && parte.trim().equals("")) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "Usuário não tem Lotação Cadastrada!");
        }
        return lista;
    }



    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("tributario/relatorio-relacao-de-pontos-comerciais/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório de Pontos Comerciais : {} ", e);
        }
    }
    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isNull(localizacao)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma localização.");
        }
        ve.lancarException();
    }
    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("Relação de Pontos Comerciais");
        dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANCIAS");
        dto.adicionarParametro("NOMERELATORIO", "Relação de Pontos Comerciais");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("FILTROS", filtros != null ? filtros.toString() : "");
        dto.adicionarParametro("LOCALIZACAO", localizacao.getId());
        dto.adicionarParametro("CODIGOINICIAL", codigoInicio !=null ? codigoInicio.toString(): "");
        dto.adicionarParametro("CODIGOFINAL",codigoFim !=null ? codigoFim.toString(): "");
        dto.adicionarParametro("INSCRICAOCADASTRALINICIAL", inscricaoCadastralInicial);
        dto.adicionarParametro("INSCRICAOCADASTRALFINAL", inscricaoCadastralFinal);

        return dto;
    }


    public void limparCampos() {
        FacesUtil.redirecionamentoInterno("/relacao-de-pontos-comerciais/");
    }

    private void montarCondicao() {
        where = new StringBuilder();
        String juncao = " where ";

        if (localizacao != null) {
            where.append(juncao).append(" loc.id = ").append(localizacao.getId());
            filtros.append("Localização.: ").append(localizacao.getDescricao());
            semDados.append(". Localização: ").append(localizacao.getDescricao());
            juncao = " and ";
        }
        if (codigoInicio != null) {
            where.append(juncao).append(" loc.codigo >= ").append(codigoInicio);
            filtros.append(" Código de Início.: ").append(codigoInicio);
            semDados.append(". Código de início: ").append(codigoInicio);
            juncao = " and ";
        }
        if (codigoFim != null) {
            where.append(juncao).append(" loc.codigo <= ").append(codigoFim);
            filtros.append(" Código de Final.: ").append(codigoFim);
            semDados.append(". Código de fim: ").append(codigoFim);
            juncao = " and ";
        }
        if (inscricaoCadastralInicial != null && !"".equals(inscricaoCadastralInicial.trim())) {
            where.append(juncao).append(" ciPc.inscricaoCadastral >= ").append(inscricaoCadastralInicial);
            filtros.append(" Inscrição Cadastral Inicial.: ").append(inscricaoCadastralInicial);
            semDados.append(". Inscrição Cadastral Inicial: ").append(inscricaoCadastralInicial);
            juncao = " and ";
        }
        if (inscricaoCadastralFinal != null && !"".equals(inscricaoCadastralFinal.trim())) {
            where.append(juncao).append(" ciPc.inscricaoCadastral <= ").append(inscricaoCadastralFinal);
            filtros.append(" Inscrição Cadastral Final.: ").append(inscricaoCadastralFinal);
            semDados.append(". Inscrição Cadastral Final: ").append(inscricaoCadastralFinal);
        }
    }
}
