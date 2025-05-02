package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */
public class SuperRelatorioInventarioBemControlador extends RelatorioPatrimonioControlador {

    private static final Logger logger = LoggerFactory.getLogger(SuperRelatorioInventarioBemControlador.class);
    private Future<ByteArrayOutputStream> futureBytesRelatorio;

    public void imprimir(String nomeRelatorio) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        try {
            byte[] bytes = futureBytesRelatorio.get().toByteArray();
            if (bytes != null && bytes.length > 0) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "inline; filename=\"" + nomeRelatorio + ".pdf\"");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarInventarioDeBem(String tipoRelatorioExtensao) {
        try {
            validarFiltrosInventarioDeBem();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            String nomeDoRelatorio = "";
            if (this.tipoInventarioBemMovel != null) {
                nomeDoRelatorio = "RELATÓRIO DE INVENTÁRIO DE BENS " + tipoBem.getDescricao().toUpperCase() + " " + tipoInventarioBemMovel.getDescricao().toUpperCase();
            } else {
                nomeDoRelatorio = "RELATÓRIO DE INVENTÁRIO DE BENS " + tipoBem.getDescricao().toUpperCase();
            }
            dto.adicionarParametro("NOMERELATORIO", nomeDoRelatorio);
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), Boolean.FALSE);
            dto.adicionarParametro("dataReferencia", dtReferencia);
            dto.adicionarParametro("complementoQuery", montarWhereAndOrderBy(tipoBem, dto));
            dto.adicionarParametro("DETALHADO", detalhar);
            dto.adicionarParametro("CONSIDERAR_HIERARQUIA_ADM", (detalhar || hierarquiaOrganizacional != null));
            dto.setNomeRelatorio(nomeDoRelatorio);
            dto.setApi("administrativo/inventario-bem/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }


    public Future<ByteArrayOutputStream> getFutureBytesRelatorio() {
        return futureBytesRelatorio;
    }

    public void setFutureBytesRelatorio(Future<ByteArrayOutputStream> futureBytesRelatorio) {
        this.futureBytesRelatorio = futureBytesRelatorio;
    }

    protected String montarNomeArquivoRelatorio() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        if (hierarquiaOrganizacional != null && hierarquiaOrganizacionalOrcamentaria != null) {
            String codigoAdm = hierarquiaOrganizacional.getCodigoSemZerosFinais().
                subSequence(0, hierarquiaOrganizacional.getCodigoSemZerosFinais().length() - 1).toString();
            String codigoOrc = hierarquiaOrganizacionalOrcamentaria.getCodigoSemZerosFinais().
                subSequence(0, hierarquiaOrganizacionalOrcamentaria.getCodigoSemZerosFinais().length() - 1).toString();
            return "RELATORIO-DE-INVENTARIO-BENS-" + tipoBem.name() + "-" + codigoAdm.replace(".", "-") + "-" +
                codigoOrc.replace(".", "-") + "-" + sf.format(dtReferencia);
        } else if (hierarquiaOrganizacional != null) {
            String codigo = hierarquiaOrganizacional.getCodigoSemZerosFinais().
                subSequence(0, hierarquiaOrganizacional.getCodigoSemZerosFinais().length() - 1).toString();
            return "RELATORIO-DE-INVENTARIO-BENS-" + tipoBem.name() + "-" + codigo.replace(".", "-") + "-" +
                sf.format(dtReferencia);
        } else {
            String codigo = hierarquiaOrganizacionalOrcamentaria.getCodigoSemZerosFinais().
                subSequence(0, hierarquiaOrganizacionalOrcamentaria.getCodigoSemZerosFinais().length() - 1).toString();
            return "RELATORIO-DE-INVENTARIO-BENS-" + tipoBem.name() + "-" + codigo.replace(".", "-") +
                "-" + sf.format(dtReferencia);
        }
    }

    private void validarFiltrosInventarioDeBem() {
        ValidacaoException ve = new ValidacaoException();
        if (this.hierarquiaOrganizacional == null && this.hierarquiaOrganizacionalOrcamentaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Unidade Administrativa' ou o campo 'Unidade Orçamentária' deve ser informado.");
        }
        if (this.dtReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Data de Referência' é obrigatório.");
        }

        if (this.dtinicial != null && this.dtReferencia != null && this.dtinicial.compareTo(this.dtReferencia) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Data Inicial' deve ser menor ou igual a 'Data de Referência'.");
        }

        if (this.dtinicial != null && this.dtFinal != null && this.dtinicial.compareTo(this.dtFinal) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Data Inicial' deve ser menor ou igual a 'Data Final'.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public String montarWhereAndOrderBy(TipoBem tipoBem, RelatorioDTO dto) {
        StringBuilder where = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        if (dtReferencia != null) {
            filtros.append("Data de Referência: ").append(DataUtil.getDataFormatada(dtReferencia)).append(". ");
        }

        where.append(" and grupo.tipobem = '").append(tipoBem.name()).append("' ");
        where.append(" and ev.SITUACAOEVENTOBEM <> 'BAIXADO' ");

        filtros.append("Detalhar: ").append(detalhar ? "Sim. " : "Não. ");

        if (hierarquiaOrganizacional != null) {
            where.append(" and vw.codigo like '").append(hierarquiaOrganizacional.getCodigoSemZerosFinais()).append("%' ");
            filtros.append("Unidade Organizacional: ").append(hierarquiaOrganizacional.toString()).append(". ");
        }

        if (hierarquiaOrganizacionalOrcamentaria != null) {
            where.append(" and vworc.subordinada_id = ").append(hierarquiaOrganizacionalOrcamentaria.getSubordinada().getId()).append(" ");
            filtros.append("Unidade Orçametária: ").append(hierarquiaOrganizacionalOrcamentaria.toString()).append(". ");
        }

        if (bem != null && bem.getId() != null) {
            where.append(" and  b.id = ").append(bem.getId());
            filtros.append("Bem: ").append(bem.getDescricaoParaAutoComplete()).append(". ");
        }

        if (grupoBem != null) {
            where.append(" and grupo.codigo = '").append(grupoBem.getCodigo()).append("' ");
            filtros.append("Grupo Patrimonial: ").append(grupoBem.toString()).append(". ");
        }

        if (grupoObjetoCompra != null) {
            where.append("  and grupoobj.codigo = '").append(grupoObjetoCompra.getCodigo()).append("' ");
            filtros.append("Grupo Objeto de Compra: ").append(grupoObjetoCompra.getDescricao()).append(". ");
        }

        if (estadoConservacaoBem != null) {
            where.append(" and estado_resultante.estadobem = '").append(estadoConservacaoBem.name()).append("' ");
            filtros.append("Estado de conservação: ").append(estadoConservacaoBem.getDescricao()).append(". ");
        }

        if (situacaoConservacaoBem != null) {
            where.append(" and estado_resultante.situacaoconservacaobem = '").append(situacaoConservacaoBem.name()).append("' ");
            filtros.append("Situação de conservação: ").append(situacaoConservacaoBem.getDescricao()).append(". ");
        }

        if (tipoGrupo != null) {
            where.append(" and estado_resultante.tipogrupo = '").append(tipoGrupo.name()).append("'");
            filtros.append("Tipo Grupo: ").append(tipoGrupo.getDescricao()).append(". ");
        }

        if (dtinicial == null && dtFinal == null) {
            where.append(" and b.dataaquisicao <= to_date('").append(DataUtil.getDataFormatada(dtReferencia)).append("','dd/MM/yyyy') ");
        }

        if (dtinicial != null) {
            where.append(" and b.dataaquisicao >= to_date('").append(DataUtil.getDataFormatada(dtinicial)).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(dtinicial)).append(". ");
        }

        if (dtFinal != null) {
            where.append(" and b.dataaquisicao <= to_date('").append(DataUtil.getDataFormatada(dtFinal)).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Final: ").append(DataUtil.getDataFormatada(dtFinal)).append(". ");
        }

        if (numeroNotaFiscal != null && !numeroNotaFiscal.trim().isEmpty()) {
            where.append(" and exists " +
                "    (select 1 " +
                "     from bemnotafiscal bnf " +
                "     where bnf.bem_id = b.id ");
            where.append("  and bnf.numeronotaFiscal = '").append(numeroNotaFiscal.trim()).append("' ) ");
            filtros.append("Nº da Nota Fiscal: ").append(numeroNotaFiscal.trim()).append(". ");
        }

        if (numeroEmpenho != null && !numeroEmpenho.trim().isEmpty()) {
            where.append(" and exists " +
                "    (select 1 " +
                "     from bemnotafiscal bnf " +
                "     inner join bemnotafiscalempenho bnfe ON bnfe.bemnotafiscal_id = bnf.id " +
                "     where bnf.bem_id = b.id ");
            where.append("  and bnfe.numeroempenho = '").append(numeroEmpenho.trim()).append("' ) ");
            filtros.append("Nº Empenho: ").append(numeroEmpenho.trim()).append(". ");
        }

        if (localizacao != null && !localizacao.trim().isEmpty()) {
            where.append(" and lower(estado_resultante.localizacao) like '%").append(localizacao.trim().toLowerCase()).append("%' ");
            filtros.append("Localização: ").append(localizacao.trim()).append(". ");
        }

        if (marca != null && !marca.trim().isEmpty()) {
            where.append(" and lower(b.marca) like '%").append(marca.trim().toLowerCase()).append("%' ");
            filtros.append("Marca: ").append(marca.trim()).append(". ");
        }

        if (modelo != null && !modelo.trim().isEmpty()) {
            where.append(" and lower(b.modelo) like '%").append(modelo.trim().toLowerCase()).append("%' ");
            filtros.append("Modelo: ").append(modelo.trim()).append(". ");
        }

        if (tipoAquisicaoBem != null) {
            where.append(" and estado_resultante.tipoAquisicaoBem =  '").append(tipoAquisicaoBem.name()).append("' ");
            filtros.append("Tipo de Aquisição: ").append(tipoAquisicaoBem.getDescricao()).append(". ");
        }

        if (ordenacaoSelecionada != null && ordenacaoSelecionada.length > 0) {
            where.append(detalhar ? " order by vw.codigo, vworc.codigo, " : "order by ");
            filtros.append(" Ordenado por: ");
            List<String> ordernacaoCampo = Lists.newArrayList();
            List<String> ordernacaoDescricao = Lists.newArrayList();
            for (Object[] item : ordenacaoSelecionada) {
                ordernacaoCampo.add((String) item[1]);
                ordernacaoDescricao.add((String) item[0]);
            }
            where.append(StringUtils.join(ordernacaoCampo, ","));
            filtros.append(StringUtils.join(ordernacaoDescricao, "; "));
        } else {
            where.append(detalhar ? "  order by vw.codigo, vworc.codigo, to_number(trim(b.identificacao)) " : " order by to_number(trim(b.identificacao))");
        }
        dto.adicionarParametro("FILTROS", filtros.toString());
        return where.toString();
    }

}
