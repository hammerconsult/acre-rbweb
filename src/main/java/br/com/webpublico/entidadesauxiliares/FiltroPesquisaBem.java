package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import com.google.common.collect.Lists;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FiltroPesquisaBem {

    private Bem bem;
    private TipoBem tipoBem;
    private TipoGrupo tipoGrupo;
    private GrupoBem grupoBem;
    private List<Long> idsBem;
    private EstadoConservacaoBem estadoConservacaoBem;
    private SituacaoConservacaoBem situacaoConservacaoBem;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaAdministrativaFilha;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private UnidadeOrganizacional unidadePesquisaBem;
    private Object selecionado;
    private Long idSelecionado;
    private Date dataOperacao;
    private Date dataReferencia;
    private Operacoes operacao;
    private TipoEventoBem tipoEventoBem;
    private List<Long> idsUnidadesControleSetorial;
    private Integer primeiroRegistro;
    private Integer quantidadeRegistro;
    private Boolean isDepreciacao;
    private Boolean isAlienacao;
    private Boolean isHirarquiaEncerrada;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;

    public FiltroPesquisaBem() {
        this.isDepreciacao = Boolean.FALSE;
        this.isHirarquiaEncerrada = Boolean.TRUE;
        this.idsBem = Lists.newArrayList();
        this.dataReferencia = new Date();
        this.dataOperacao = new Date();
        this.hierarquiasOrcamentarias = new ArrayList<>();
    }

    public FiltroPesquisaBem(TipoBem tipoBem, Object selecionado) {
        this.tipoBem = tipoBem;
        this.idsBem = Lists.newArrayList();
        this.selecionado = selecionado;
        this.dataReferencia = new Date();
        this.dataOperacao = new Date();
        this.idsUnidadesControleSetorial = Lists.newArrayList();
        this.isDepreciacao = Boolean.FALSE;
        this.hierarquiasOrcamentarias = new ArrayList<>();
    }

    public FiltroPesquisaBem(Object selecionado) {
        this.selecionado = selecionado;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public Object getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Object selecionado) {
        this.selecionado = selecionado;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public Long getIdSelecionado() {
        return idSelecionado;
    }

    public void setIdSelecionado(Long idSelecionado) {
        this.idSelecionado = idSelecionado;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public boolean isOperacaoVer() {
        return this.operacao != null && Operacoes.VER.equals(this.operacao);
    }

    public List<Long> getIdsUnidadesControleSetorial() {
        return idsUnidadesControleSetorial;
    }

    public void setIdsUnidadesControleSetorial(List<Long> idsUnidadesControleSetorial) {
        this.idsUnidadesControleSetorial = idsUnidadesControleSetorial;
    }

    public Integer getPrimeiroRegistro() {
        return primeiroRegistro;
    }

    public void setPrimeiroRegistro(Integer primeiroRegistro) {
        this.primeiroRegistro = primeiroRegistro;
    }

    public Integer getQuantidadeRegistro() {
        return quantidadeRegistro;
    }

    public void setQuantidadeRegistro(Integer quantidadeRegistro) {
        this.quantidadeRegistro = quantidadeRegistro;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativaFilha() {
        return hierarquiaAdministrativaFilha;
    }

    public void setHierarquiaAdministrativaFilha(HierarquiaOrganizacional hierarquiaAdministrativaFilha) {
        this.hierarquiaAdministrativaFilha = hierarquiaAdministrativaFilha;
    }

    public UnidadeOrganizacional getUnidadePesquisaBem() {
        return unidadePesquisaBem;
    }

    public void setUnidadePesquisaBem(UnidadeOrganizacional unidadePesquisaBem) {
        this.unidadePesquisaBem = unidadePesquisaBem;
    }

    public Boolean getDepreciacao() {
        return isDepreciacao;
    }

    public void setDepreciacao(Boolean depreciacao) {
        isDepreciacao = depreciacao;
    }

    public Boolean getHirarquiaEncerrada() {
        return isHirarquiaEncerrada;
    }

    public void setHirarquiaEncerrada(Boolean hirarquiaEncerrada) {
        isHirarquiaEncerrada = hirarquiaEncerrada;
    }

    public List<Long> getIdsBem() {
        return idsBem;
    }

    public void setIdsBem(List<Long> idsBem) {
        this.idsBem = idsBem;
    }

    public Boolean getAlienado() {
        return isAlienacao;
    }

    public void setAlienado(Boolean alienado) {
        isAlienacao = alienado;
    }

    public TipoEventoBem getTipoEventoBem() {
        return tipoEventoBem;
    }

    public void setTipoEventoBem(TipoEventoBem tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrcamentarias() {
        return hierarquiasOrcamentarias;
    }

    public void setHierarquiasOrcamentarias(List<HierarquiaOrganizacional> hierarquiasOrcamentarias) {
        this.hierarquiasOrcamentarias = hierarquiasOrcamentarias;
    }

    public String getCondicaoConsultaRelatorioBloqueioBens() {
        StringBuilder filtros = new StringBuilder();
        filtros.append(" and (mov.movimentoum = :bloqueado or mov.movimentodois = :bloqueado or mov.movimentotres = :bloqueado) ");
        if (getHierarquiaAdministrativa() != null) {
            filtros.append(" and vwadm.codigo like '").append(getHierarquiaAdministrativa().getCodigoSemZerosFinais()).append("%'");
        }
        if (getHierarquiaOrcamentaria() != null) {
            filtros.append(" and vworc.subordinada_id = ").append(getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (getGrupoBem() != null) {
            filtros.append(" and gm.id = ").append(getGrupoBem().getId());
        }
        if (getBem() != null) {
            filtros.append(" and bem.id = ").append(getBem().getId());
        }
        if (!getIdsBem().isEmpty()) {
            filtros.append(" and bem.id in :idsBem ");
        }
        if (getAlienado() != null) {
            filtros.append(" and ev.situacaoEventoBem <> '").append(SituacaoEventoBem.ALIENADO.name()).append("'");
        }
        if (getEstadoConservacaoBem() != null) {
            filtros.append(" and est.estadoBem = '").append(getEstadoConservacaoBem().name()).append("'");
        }
        return filtros.toString();
    }

    public static String getCondicaoExistsBloqueioPesquisaBem(ConfigMovimentacaoBem configuracao) {
        String condicao = " and exists (select 1 from movimentobloqueiobem mov  " +
            "      where mov.bem_id = bem.id " +
            "           and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  mov.bem_id = mov2.bem_id) ";
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoUm()) {
            condicao += " and mov.movimentoum  = :bloqueadoMovUm";
            condicao += " and mov.movimentodois = :bloqueadoMovDois";
        }
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoDois()) {
            condicao += " and mov.movimentodois = :bloqueadoMovDois";
        }
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoTres()) {
            condicao += " and mov.movimentotres = :bloqueadoMovTres";
        }
        condicao += "     and rownum = 1 )";
        return condicao;
    }

    public static String getCondicaoBloqueioPesquisaBem(ConfigMovimentacaoBem configuracao) {
        String condicao = "";
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoUm()) {
            condicao += " and mov.movimentoum  = :bloqueadoMovUm";
            condicao += " and mov.movimentodois = :bloqueadoMovDois";
        }
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoDois()) {
            condicao += " and mov.movimentodois = :bloqueadoMovDois";
        }
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoTres()) {
            condicao += " and mov.movimentotres = :bloqueadoMovTres";
        }
        return condicao;
    }

    public static String getCondicaoGeral(FiltroPesquisaBem filtroPesquisa) {
        String condicao = "";
        if (filtroPesquisa.getGrupoBem() != null) {
            condicao += " and gb.id = :idGrupo ";
        }
        if (filtroPesquisa.getBem() != null) {
            condicao += " and bem.id = :idBem ";
        }
        if (filtroPesquisa.getEstadoConservacaoBem() != null) {
            condicao += " and est.estadobem = :estadoConservacao";
        }
        if (filtroPesquisa.getSituacaoConservacaoBem() != null) {
            condicao += " and est.situacaoconservacaobem = :situacaoConservacao";
        }
        return condicao;
    }

    public static void adicionarParametrosConfigMovimentacaoBem(Query q, ConfigMovimentacaoBem configuracao) {
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoUm()) {
            q.setParameter("bloqueadoMovUm", !configuracao.getPesquisa().getMovimentoTipoUm());
            q.setParameter("bloqueadoMovDois", !configuracao.getPesquisa().getMovimentoTipoDois());
        }
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoDois()) {
            q.setParameter("bloqueadoMovDois", !configuracao.getPesquisa().getMovimentoTipoDois());
        }
        if (configuracao.getOperacaoMovimentacaoBem().isOperacaoMovimentoTres()) {
            q.setParameter("bloqueadoMovTres", !configuracao.getPesquisa().getMovimentoTipoTres());
        }
    }

    public static void adicionarParametrosGerais(Query q, FiltroPesquisaBem filtroPesquisa) {
        if (filtroPesquisa.getGrupoBem() != null) {
            q.setParameter("idGrupo", filtroPesquisa.getGrupoBem().getId());
        }
        if (filtroPesquisa.getBem() != null) {
            q.setParameter("idBem", filtroPesquisa.getBem().getId());
        }
        if (filtroPesquisa.getEstadoConservacaoBem() != null) {
            q.setParameter("estadoConservacao", filtroPesquisa.getEstadoConservacaoBem().name());
        }
        if (filtroPesquisa.getSituacaoConservacaoBem() != null) {
            q.setParameter("situacaoConservacao", filtroPesquisa.getSituacaoConservacaoBem().name());
        }
    }

    public List<Long> getIdsHierarquiaOrcamentaria() {
        List<Long> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional ho : hierarquiasOrcamentarias) {
            retorno.add(ho.getSubordinada().getId());
        }
        return retorno;
    }
}
