package br.com.webpublico.enums.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author octavio
 */
public enum TipoAuditoriaRH implements EnumComDescricao {

    AFASTAMENTO("Afastamento", Afastamento.class, Boolean.TRUE, "contratofp_id", Boolean.FALSE, null, null, null),
    FICHA_FINANCEIRA("Ficha Financeira", FichaFinanceiraFP.class, Boolean.TRUE, "vinculofp_id", Boolean.FALSE, null, null, "usuariosistema_id"),
    LANCAMENTO_FP("Lançamento FP", LancamentoFP.class, Boolean.TRUE, "vinculofp_id", Boolean.FALSE, null, null, null),
    CONCESSAO_FERIAS("Concessão de Férias", ConcessaoFeriasLicenca.class, Boolean.TRUE, "contratofp_id", Boolean.TRUE, "PERIODOAQUISITIVOFL", "aud.PERIODOAQUISITIVOFL_ID", null),
    CONCESSAO_LICENCA("Concessão de Licença Prêmio", ConcessaoFeriasLicenca.class, Boolean.TRUE, "contratofp_id", Boolean.TRUE, "PERIODOAQUISITIVOFL", "aud.PERIODOAQUISITIVOFL_ID", null),
    ENQUADRAMENTO_FUNCIONAL("Enquadramento Funcional", EnquadramentoFuncional.class, Boolean.TRUE, "contratoservidor_id", Boolean.FALSE, null, null, null),
    CEDENCIA("Cedência", CedenciaContratoFP.class, Boolean.TRUE, "contratofp_id", Boolean.FALSE, null, null, null),
    RETORNO_CEDENCIA("Retorno da Cedência", RetornoCedenciaContratoFP.class, Boolean.TRUE, "contratofp_id", Boolean.FALSE, null, null, null),
    FALTAS("Faltas", Faltas.class, Boolean.TRUE, "contratofp_id", Boolean.FALSE, null, null, null),
    LANCAMENTO_TERCO_FERIAS_AUT("Lançamento de 1/3 de Férias Automático", LancamentoTercoFeriasAut.class, Boolean.TRUE, "contratofp_id", Boolean.FALSE, null, null, null);

    private String descricao;
    private Class classe;
    private Boolean hasJoinVinculo;
    private String nomeColunaVinculo;
    private Boolean hasVinculoOutraTabela;
    private String nomeOutraTabela;
    private String joinVinculoOutraTabela;
    private String nomeColunaUsuario;


    TipoAuditoriaRH(String descricao, Class classe, Boolean hasJoinVinculo, String nomeColunaVinculo, Boolean hasVinculoOutraTabela, String nomeOutraTabela,
                    String joinVinculoOutraTabela, String nomeColunaUsuario) {
        this.descricao = descricao;
        this.classe = classe;
        this.hasJoinVinculo = hasJoinVinculo;
        this.nomeColunaVinculo = nomeColunaVinculo;
        this.hasVinculoOutraTabela = hasVinculoOutraTabela;
        this.nomeOutraTabela = nomeOutraTabela;
        this.joinVinculoOutraTabela = joinVinculoOutraTabela;
        this.nomeColunaUsuario = nomeColunaUsuario;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Class getClasse() {
        return classe;
    }

    public Boolean getHasJoinVinculo() {
        return hasJoinVinculo;
    }

    public void setHasJoinVinculo(Boolean hasJoinVinculo) {
        this.hasJoinVinculo = hasJoinVinculo;
    }

    public void setNomeColunaVinculo(String nomeColunaVinculo) {
        this.nomeColunaVinculo = nomeColunaVinculo;
    }

    public Boolean getHasVinculoOutraTabela() {
        return hasVinculoOutraTabela;
    }

    public void setHasVinculoOutraTabela(Boolean hasVinculoOutraTabela) {
        this.hasVinculoOutraTabela = hasVinculoOutraTabela;
    }

    public String getJoinVinculoOutraTabela() {
        return joinVinculoOutraTabela;
    }

    public void setJoinVinculoOutraTabela(String joinVinculoOutraTabela) {
        this.joinVinculoOutraTabela = joinVinculoOutraTabela;
    }

    public String getNomeColunaVinculo() {
        return nomeColunaVinculo;
    }

    public String getNomeOutraTabela() {
        return nomeOutraTabela;
    }

    public String getNomeColunaUsuario() {
        return nomeColunaUsuario;
    }

    public void setNomeColunaUsuario(String nomeColunaUsuario) {
        this.nomeColunaUsuario = nomeColunaUsuario;
    }
}
