package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.RelatoriosItemDemonstDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.ItemDemonstrativoFiltrosDTO;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.ApresentacaoRelatorio;

import java.util.List;
import java.util.Objects;

/**
 * Criado por Mateus
 * Data: 28/03/2017.
 */
public class ItemDemonstrativoFiltros {

    private RelatoriosItemDemonst relatorio;
    private UnidadeGestora unidadeGestora;
    private List<ParametrosRelatorios> parametros;
    private String dataInicial;
    private String dataFinal;
    private String dataReferencia;
    private ApresentacaoRelatorio apresentacaoRelatorio;
    private Exercicio exercicio;
    private List<Long> idsUnidades;

    public ItemDemonstrativoFiltros() {
    }

    public ItemDemonstrativoFiltrosDTO toDto() {
        ItemDemonstrativoFiltrosDTO itemDemonstrativoFiltrosDTO = new ItemDemonstrativoFiltrosDTO();
        RelatoriosItemDemonstDTO relatoriosItemDemonstDTO = new RelatoriosItemDemonstDTO();
        relatoriosItemDemonstDTO.setId(relatorio.getId());
        relatoriosItemDemonstDTO.setUsaConta(relatorio.getUsaConta());
        relatoriosItemDemonstDTO.setUsaPrograma(relatorio.getUsaPrograma());
        relatoriosItemDemonstDTO.setUsaAcao(relatorio.getUsaAcao());
        relatoriosItemDemonstDTO.setUsaSubAcao(relatorio.getUsaSubAcao());
        relatoriosItemDemonstDTO.setUsaFuncao(relatorio.getUsaFuncao());
        relatoriosItemDemonstDTO.setUsaSubFuncao(relatorio.getUsaSubFuncao());
        relatoriosItemDemonstDTO.setUsaUnidadeOrganizacional(relatorio.getUsaUnidadeOrganizacional());
        relatoriosItemDemonstDTO.setUsaFonteRecurso(relatorio.getUsaFonteRecurso());
        relatoriosItemDemonstDTO.setUsaTipoDespesa(relatorio.getUsaTipoDespesa());
        relatoriosItemDemonstDTO.setUsaContaFinanceira(relatorio.getUsaContaFinanceira());
        itemDemonstrativoFiltrosDTO.setRelatorio(relatoriosItemDemonstDTO);
        itemDemonstrativoFiltrosDTO.setDataFinal(dataFinal);
        itemDemonstrativoFiltrosDTO.setDataInicial(dataInicial);
        itemDemonstrativoFiltrosDTO.setDataReferencia(dataReferencia);
        if (apresentacaoRelatorio != null) {
            itemDemonstrativoFiltrosDTO.setApresentacaoRelatorio(apresentacaoRelatorio.getToDto());
        }

        if (exercicio != null) {
            itemDemonstrativoFiltrosDTO.setExercicioId(exercicio.getId());
            itemDemonstrativoFiltrosDTO.setExercicioAno(exercicio.getAno());
        }
        itemDemonstrativoFiltrosDTO.setIdsUnidades(idsUnidades);
        if (unidadeGestora != null) {
            itemDemonstrativoFiltrosDTO.setUnidadeGestoraId(unidadeGestora.getId());
        }
        if (parametros != null && !parametros.isEmpty()) {
            itemDemonstrativoFiltrosDTO.setParametros(ParametrosRelatorios.parametrosToDto(parametros));
        }
        return itemDemonstrativoFiltrosDTO;
    }

    public RelatoriosItemDemonst getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(RelatoriosItemDemonst relatorio) {
        this.relatorio = relatorio;
    }

    public Boolean getPesquisouUg() {
        return unidadeGestora != null;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<ParametrosRelatorios> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParametrosRelatorios> parametros) {
        this.parametros = parametros;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(String dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public ApresentacaoRelatorio getApresentacaoRelatorio() {
        return apresentacaoRelatorio;
    }

    public void setApresentacaoRelatorio(ApresentacaoRelatorio apresentacaoRelatorio) {
        this.apresentacaoRelatorio = apresentacaoRelatorio;
    }

    public boolean isApresentacaoUnidadeGestora() {
        return ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacaoRelatorio);
    }

    public boolean isApresentacaoUnidade() {
        return ApresentacaoRelatorio.UNIDADE.equals(apresentacaoRelatorio);
    }

    public boolean isApresentacaoOrgao() {
        return ApresentacaoRelatorio.ORGAO.equals(apresentacaoRelatorio);
    }

    public boolean isApresentacaoConsolidado() {
        return ApresentacaoRelatorio.CONSOLIDADO.equals(apresentacaoRelatorio);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<Long> getIdsUnidades() {
        return idsUnidades;
    }

    public void setIdsUnidades(List<Long> idsUnidades) {
        this.idsUnidades = idsUnidades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDemonstrativoFiltros that = (ItemDemonstrativoFiltros) o;
        return Objects.equals(relatorio, that.relatorio) &&
            Objects.equals(unidadeGestora, that.unidadeGestora) &&
            Objects.equals(parametros, that.parametros) &&
            Objects.equals(dataInicial, that.dataInicial) &&
            Objects.equals(dataFinal, that.dataFinal) &&
            Objects.equals(dataReferencia, that.dataReferencia) &&
            apresentacaoRelatorio == that.apresentacaoRelatorio &&
            Objects.equals(exercicio, that.exercicio) &&
            Objects.equals(idsUnidades, that.idsUnidades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relatorio, unidadeGestora, parametros, dataInicial, dataFinal, dataReferencia, apresentacaoRelatorio, exercicio, idsUnidades);
    }
}
