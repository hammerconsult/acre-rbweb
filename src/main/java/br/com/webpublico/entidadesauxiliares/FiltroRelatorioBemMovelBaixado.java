package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;

import java.util.Date;

public class FiltroRelatorioBemMovelBaixado {

    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquialOrcamentaria;
    private String numeroEfetivacao;
    private GrupoBem grupoPatrimonial;
    private Date dataInicial;
    private Date dataFinal;
    private TipoBaixa tipoBaixa;
    private UsuarioSistema usuarioSistema;

    public FiltroRelatorioBemMovelBaixado() {
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquialOrcamentaria() {
        return hierarquialOrcamentaria;
    }

    public void setHierarquialOrcamentaria(HierarquiaOrganizacional hierarquialOrcamentaria) {
        this.hierarquialOrcamentaria = hierarquialOrcamentaria;
    }

    public String getNumeroEfetivacao() {
        return numeroEfetivacao;
    }

    public void setNumeroEfetivacao(String numeroEfetivacao) {
        this.numeroEfetivacao = numeroEfetivacao;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
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

    public TipoBaixa getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(TipoBaixa tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getCondicao() {
        StringBuilder filtros = new StringBuilder();
        filtros.append(" and usuo.usuariosistema_id = ").append(usuarioSistema.getId()).append(" ");
        if (hierarquiaAdministrativa != null) {
            filtros.append(" and vwadm.codigo like '%").append(hierarquiaAdministrativa.getCodigoSemZerosFinais()).append("%' ");
        }
        if (hierarquialOrcamentaria != null) {
            filtros.append(" and vworc.codigo like '%").append(hierarquialOrcamentaria.getCodigoSemZerosFinais()).append("%' ");
        }
        if (!Strings.isNullOrEmpty(numeroEfetivacao)) {
            filtros.append(" and efet.codigo = ").append(numeroEfetivacao.trim()).append(" ");
        }
        if (grupoPatrimonial != null) {
            filtros.append(" and grupobem.id = ").append(grupoPatrimonial.getId()).append(" ");
        }
        if (tipoBaixa != null) {
            filtros.append(" and sol.tipoBaixa = '").append(tipoBaixa.name()).append("' ");
        }
        return filtros.toString();
    }

    public String getFiltrosUtilizados() {
        StringBuilder filtros = new StringBuilder();
        if (hierarquiaAdministrativa != null) {
            filtros.append("Unidade Administrativa: ").append(hierarquiaAdministrativa.toString()).append(", ");
        }
        if (hierarquialOrcamentaria != null) {
            filtros.append("Unidade Orçamentária: ").append(hierarquialOrcamentaria.toString()).append(", ");
        }
        if (!Strings.isNullOrEmpty(numeroEfetivacao)) {
            filtros.append("Nº da Efetivação: ").append(numeroEfetivacao).append(", ");
        }
        if (grupoPatrimonial != null) {
            filtros.append("Grupo Patrimonial: ").append(grupoPatrimonial.toString()).append(", ");
        }
        filtros.append("Data Baixa Inicial: ").append(DataUtil.getDataFormatada(dataInicial)).append(", ");
        filtros.append("Data Baixa Final: ").append(DataUtil.getDataFormatada(dataFinal)).append(", ");
        if (tipoBaixa != null) {
            filtros.append("Tipo de Baixa: ").append(tipoBaixa.getDescricao()).append(", ");
        }
        return filtros.toString().substring(0, filtros.length() - 2);
    }
}
