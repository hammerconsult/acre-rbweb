package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoPermissaoRBTrans;

/**
 * Created by AndreGustavo on 08/09/2014.
 */
public class FiltroRenovacaoPermissao {
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    private Integer numeroPermissao;
    private Integer inicioDigitoFinalPermissao;
    private Integer fimDigitoFinalPermissao;

    public FiltroRenovacaoPermissao() {
        tipoPermissaoRBTrans = null;
        numeroPermissao = null;
        inicioDigitoFinalPermissao = null;
        fimDigitoFinalPermissao = null;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public Integer getNumeroPermissao() {
        return numeroPermissao;
    }

    public void setNumeroPermissao(Integer numeroPermissao) {
        this.numeroPermissao = numeroPermissao;
    }

    public Integer getInicioDigitoFinalPermissao() {
        return inicioDigitoFinalPermissao;
    }

    public void setInicioDigitoFinalPermissao(Integer inicioDigitoFinalPermissao) {
        this.inicioDigitoFinalPermissao = inicioDigitoFinalPermissao;
    }

    public Integer getFimDigitoFinalPermissao() {
        return fimDigitoFinalPermissao;
    }

    public void setFimDigitoFinalPermissao(Integer fimDigitoFinalPermissao) {
        this.fimDigitoFinalPermissao = fimDigitoFinalPermissao;
    }

    public String getQueryConsultaPermissoes() {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT distinct pt.numero AS tiponumero, ");
        sb.append("     pt.tipopermissaorbtrans AS tipo, ");
        sb.append("     cepermissionario.inscricaocadastral || ' - ' || coalesce(pf.nome, pj.razaosocial) AS permissionario, ");
        sb.append("     pt.finalvigencia AS vencimento,");
        sb.append("     pt.id ");
        sb.append(" FROM permissaotransporte pt ");
        sb.append(" INNER JOIN permissionario permissionario ON permissionario.permissaotransporte_id = pt.id AND permissionario.finalvigencia IS NULL ");
        sb.append(" INNER JOIN cadastroeconomico cePermissionario ON cePermissionario.id = permissionario.cadastroeconomico_id ");
        sb.append(" INNER JOIN CE_SITUACAOCADASTRAL cesit on cesit.cadastroeconomico_id = cePermissionario.id ");
        sb.append(" INNER JOIN SituacaoCadastroEconomico sit on sit.id = cesit.situacaocadastroeconomico_id ");
        sb.append(" LEFT JOIN pessoafisica pf ON pf.id = cepermissionario.pessoa_id ");
        sb.append(" LEFT JOIN pessoajuridica pj ON pj.id = cepermissionario.pessoa_id ");
        sb.append(" WHERE sit.situacaoCadastral in (:situacaoCadastral) ");
        sb.append("   AND trunc(sit.dataregistro) = (select trunc(max(sc.dataregistro)) from SituacaoCadastroEconomico sc ");
        sb.append("                                   inner join CE_SITUACAOCADASTRAL cesc on cesc.situacaocadastroeconomico_id = sc.id ");
        sb.append("                                   where cesc.cadastroeconomico_id = cePermissionario.id) ");

        if (numeroPermissao != null) {
            sb.append(" and ").append(" pt.numero = :numero ");
        }
        if (tipoPermissaoRBTrans != null) {
            sb.append(" and ").append(" pt.tipopermissaorbtrans = :tipoPermissaoRBTrans");
        }
        if (inicioDigitoFinalPermissao != null) {
            sb.append(" and ").append(" to_number(substr(pt.numero,length(pt.numero), length(pt.numero))) >= :inicioDigitoFinalPermissao ");
        }
        if(fimDigitoFinalPermissao != null){
            sb.append(" and ").append(" to_number(substr(pt.numero,length(pt.numero), length(pt.numero))) <= :fimDigitoFinalPermissao ");
        }

        sb.append(" order by pt.numero");

        return sb.toString();
    }
}
