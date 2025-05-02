package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistenteConsultaMovimentacaoBemContabil extends AssistenteBarraProgresso {

    public static final String CONTA_CONTABIL_ORIGINAL = "1.2.3.1.1.00.00";
    public static final String CONTA_CONTABIL_AJUSTE = "1.2.3.8.1.01.00";
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private GrupoBem grupoBem;
    private TipoConsulta tipoConsulta;
    private Boolean somenteUnidadeComDiferenca;
    private MovimentacaoBemContabilGrupo movGrupoSelecionado;
    private List<MovimentacaoBemContabilUnidade> movimentacoes;

    public AssistenteConsultaMovimentacaoBemContabil() {
        somenteUnidadeComDiferenca = false;
        tipoConsulta = TipoConsulta.DETALHADO;
        movimentacoes = Lists.newArrayList();
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

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Boolean getSomenteUnidadeComDiferenca() {
        return somenteUnidadeComDiferenca;
    }

    public void setSomenteUnidadeComDiferenca(Boolean somenteUnidadeComDiferenca) {
        this.somenteUnidadeComDiferenca = somenteUnidadeComDiferenca;
    }

    public MovimentacaoBemContabilGrupo getMovGrupoSelecionado() {
        return movGrupoSelecionado;
    }

    public void setMovGrupoSelecionado(MovimentacaoBemContabilGrupo movGrupoSelecionado) {
        this.movGrupoSelecionado = movGrupoSelecionado;
    }

    public List<MovimentacaoBemContabilUnidade> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoBemContabilUnidade> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public enum TipoConsulta {
        DETALHADO("Detalhado"),
        CONTABIL("Contábil"),
        ADMINISTRATIVO("Administrativo");
        private String descricao;

        TipoConsulta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isAdministrativo() {
            return TipoConsulta.ADMINISTRATIVO.equals(this);
        }

        public boolean isContabil() {
            return TipoConsulta.CONTABIL.equals(this);
        }

        public boolean isDetalhado() {
            return TipoConsulta.DETALHADO.equals(this);
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
