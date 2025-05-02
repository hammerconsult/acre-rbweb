package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.DateUtils;
import br.com.webpublico.StringUtils;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

public class FiltroNotaFiscalNaoEscriturada extends AbstractFiltroNotaFiscal {

    private TipoAgrupamento tipoAgrupamento;

    public FiltroNotaFiscalNaoEscriturada() {
        super();
        tipoAgrupamento = TipoAgrupamento.PRESTADOR;
    }

    public TipoAgrupamento getTipoAgrupamento() {
        return tipoAgrupamento;
    }

    public void setTipoAgrupamento(TipoAgrupamento tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        validarCamposPorEmissaoOrCompetencia(ve);
        ve.lancarException();
    }

    public enum TipoAgrupamento {
        PRESTADOR("Por prestador"),
        TOMADOR("Por tomador");

        String descricao;

        TipoAgrupamento(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public String getWhere() {
        String where = " where func_nota_escriturada(dec.id) = 0 " +
            "  and dec.situacao = '" + SituacaoNota.EMITIDA.name() + "' ";
        if (getDataInicial() != null) {
            where += " and coalesce(nf.emissao, sp.emissao) >= to_date('" +
                DateUtils.getDataFormatada(getDataInicial()) + "', 'dd/mm/yyyy') ";
        }
        if (getDataFinal() != null) {
            where += " and coalesce(nf.emissao, sp.emissao) <= to_date('" +
                DateUtils.getDataFormatada(getDataFinal()) + "', 'dd/mm/yyyy') ";
        }
        if (getExercicioInicial() != null && getMesInicial() != null) {
            where += " and (extract(year from dec.competencia) > " + getExercicioInicial().getAno() + " or " +
                " (extract(year from dec.competencia) = " + getExercicioInicial().getAno() +
                " and extract(month from dec.competencia) >= " + getMesInicial().getNumeroMes() + ")) ";
        }
        if (getExercicioFinal() != null && getMesFinal() != null) {
            where += " and (extract(year from dec.competencia) < " + getExercicioFinal() + " or " +
                " (extract(year from dec.competencia) = " + getExercicioFinal() +
                " and extract(month from dec.competencia) <= " + getMesFinal().getNumeroMes() + ")) ";
        }
        if (getCadastroEconomico() != null) {
            where += " and coalesce(ce_nf.id, ce_sp.id) = " + getCadastroEconomico().getId();
        }
        where += !Strings.isNullOrEmpty(getCpfCnpjTomadorInicial()) ? " and dpt.cpfcnpj >= '" +
            StringUtils.removerMascaraCpfCnpj(getCpfCnpjTomadorInicial()) + "' " : " ";
        where += !Strings.isNullOrEmpty(getCpfCnpjTomadorFinal()) ? " and dpt.cpfcnpj <= '" +
            StringUtils.removerMascaraCpfCnpj(getCpfCnpjTomadorFinal()) + "' " : " ";
        if (getExigibilidades() != null && !getExigibilidades().isEmpty()) {
            List<String> namesExigibilidades = Lists.newArrayList();
            for (Exigibilidade exigibilidade : getExigibilidades()) {
                namesExigibilidades.add("'" + exigibilidade.name() + "'");
            }
            where += " and dec.naturezaoperacao in (" + org.apache.commons.lang.StringUtils.join(namesExigibilidades, ",") + ") ";
        }
        if (getTipoDocumento() != null) {
            where += TipoDocumentoNfse.NFSE.equals(getTipoDocumento()) ? " and nf.id is not null " :
                TipoDocumentoNfse.SERVICO_DECLARADO.equals(getTipoDocumento()) ? " and sp.id is not null " : "";
        }
        return where;
    }

    public String getFiltros() {
        String filtros = " ";
        if (getDataInicial() != null) {
            filtros += " Emissão Inicial: " + DateUtils.getDataFormatada(getDataInicial()) + "; ";
        }
        if (getDataFinal() != null) {
            filtros += " Emissão Final: " + DateUtils.getDataFormatada(getDataFinal()) + "; ";
        }
        if (getExercicioInicial() != null && getMesInicial() != null) {
            filtros += "Competência Inicial: " + getExercicioInicial() + "\\" + getMesInicial().getNumeroMes() + "; ";
        }
        if (getExercicioFinal() != null && getMesFinal() != null) {
            filtros += "Competência Final: " + getExercicioFinal() + "\\" + getMesFinal().getNumeroMes() + "; ";
        }
        if (getCadastroEconomico() != null) {
            filtros += "Cadastro Econômico: " + getCadastroEconomico().toString() + "; ";
        }
        if (!Strings.isNullOrEmpty(getCpfCnpjTomadorInicial())) {
            filtros += "CPF/CNPJ (Tomador) Inicial: " + getCpfCnpjTomadorInicial() + "; ";
        }
        if (!Strings.isNullOrEmpty(getCpfCnpjTomadorFinal())) {
            filtros += "CPF/CNPJ (Tomador) Final: " + getCpfCnpjTomadorFinal() + "; ";
        }
        if (getExigibilidades() != null && !getExigibilidades().isEmpty()) {
            List<String> descricoesExigibilidades = Lists.newArrayList();
            for (Exigibilidade exigibilidade : getExigibilidades()) {
                descricoesExigibilidades.add("'" + exigibilidade.getDescricao() + "'");
            }
            filtros += " Natureza(s) Operação(ões): " + org.apache.commons.lang.StringUtils.join(descricoesExigibilidades, ",") + "; ";
        }
        if (getTipoDocumento() != null) {
            filtros += " Tipo Documento: " + getTipoDocumento().getDescricao() + "; ";
        }
        if (getTipoAgrupamento() != null) {
            filtros += " Tipo Agrupamento: " + getTipoAgrupamento().toString() + "; ";
        }
        return filtros;
    }

    public String getOrderBy() {
        if (getTipoAgrupamento().equals(FiltroNotaFiscalNaoEscriturada.TipoAgrupamento.PRESTADOR)) {
            return " order by dpp.nomerazaosocial ";
        } else {
            return " order by dpt.nomerazaosocial ";
        }
    }
}
