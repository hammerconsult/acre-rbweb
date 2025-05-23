package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;

import java.util.Date;

public class FiltroConsultaMovimentacaoEstoqueContabil {

    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private GrupoMaterial grupoMaterial;
    private TipoMovimento tipoMovimento;
    private Boolean saidaIntegrada;
    private Boolean notaFiscal;
    private Boolean isSomenteGrupoComInversaoSaldo;
    private Boolean isSomenteDiferencaAConciliar;
    private Boolean isSomenteDiferencaEstoque;
    private Long idMaterial;
    private Long idLocalEstoque;


    public FiltroConsultaMovimentacaoEstoqueContabil() {
        saidaIntegrada = false;
        notaFiscal = false;
        isSomenteGrupoComInversaoSaldo = false;
        isSomenteDiferencaAConciliar = false;
        isSomenteDiferencaEstoque = false;
        tipoMovimento = TipoMovimento.DETALHADO;
    }

    public Boolean getSomenteGrupoComInversaoSaldo() {
        return isSomenteGrupoComInversaoSaldo;
    }

    public void setSomenteGrupoComInversaoSaldo(Boolean somenteGrupoComInversaoSaldo) {
        isSomenteGrupoComInversaoSaldo = somenteGrupoComInversaoSaldo;
    }

    public Boolean getSomenteDiferencaAConciliar() {
        return isSomenteDiferencaAConciliar;
    }

    public void setSomenteDiferencaAConciliar(Boolean somenteDiferencaAConciliar) {
        isSomenteDiferencaAConciliar = somenteDiferencaAConciliar;
    }

    public Boolean getSomenteDiferencaEstoque() {
        return isSomenteDiferencaEstoque;
    }

    public void setSomenteDiferencaEstoque(Boolean somenteDiferencaEstoque) {
        isSomenteDiferencaEstoque = somenteDiferencaEstoque;
    }

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Boolean getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(Boolean notaFiscal) {
        this.notaFiscal = notaFiscal;
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

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Long getIdLocalEstoque() {
        return idLocalEstoque;
    }

    public void setIdLocalEstoque(Long idLocalEstoque) {
        this.idLocalEstoque = idLocalEstoque;
    }

    public Boolean getSaidaIntegrada() {
        return saidaIntegrada;
    }

    public void setSaidaIntegrada(Boolean saidaIntegrada) {
        this.saidaIntegrada = saidaIntegrada;
    }

    public boolean isMateriais(){
        return TipoMovimento.MATERIAIS.equals(tipoMovimento);
    }

    public boolean isContabil(){
        return TipoMovimento.CONTABIL.equals(tipoMovimento);
    }

    public boolean isDetalhado(){
        return TipoMovimento.DETALHADO.equals(tipoMovimento);
    }

    public enum TipoMovimento {
        DETALHADO("Detalhado"),
        MATERIAIS("Materiais"),
        CONTABIL("Contábil");
        private String descricao;

        TipoMovimento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
