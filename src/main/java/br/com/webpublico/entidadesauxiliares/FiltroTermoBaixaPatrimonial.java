package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EfetivacaoBaixaPatrimonial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;

import java.util.Date;

public class FiltroTermoBaixaPatrimonial {

    private Date dataBaixaInicial;
    private Date dataBaixaFinal;
    private Date dataOperacao;
    private String numeroSolicitacaoBaixa;
    private String numeroEfetivacaoBaixa;
    private TipoBaixa tipoBaixa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private EfetivacaoBaixaPatrimonial efetivacaoBaixa;
    private UsuarioSistema usuario;

    public FiltroTermoBaixaPatrimonial() {
        numeroSolicitacaoBaixa = null;
        numeroEfetivacaoBaixa = null;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdministrativa() {
        return hierarquiaOrganizacionalAdministrativa;
    }

    public void setHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa) {
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public String getFiltros() {
        StringBuilder filtros = new StringBuilder();
        if (hierarquiaOrganizacionalAdministrativa != null) {
            filtros.append(" and unidadeorigem.codigo = '").append(hierarquiaOrganizacionalAdministrativa.getCodigo()).append("'");
        }
        if (hierarquiaOrganizacionalOrcamentaria != null) {
            filtros.append(" and unidadeorcamentaria.codigo = '").append(hierarquiaOrganizacionalOrcamentaria.getCodigo()).append("'");
        }
        if (efetivacaoBaixa != null) {
            filtros.append(" and eft.id = ").append(efetivacaoBaixa.getId());
        }
        if (!Strings.isNullOrEmpty(numeroSolicitacaoBaixa)) {
            filtros.append(" and solicitacao.codigo = ").append(numeroSolicitacaoBaixa);
        }
        if (!Strings.isNullOrEmpty(numeroEfetivacaoBaixa)) {
            filtros.append(" and eft.codigo = ").append(numeroEfetivacaoBaixa);
        }
        if (dataBaixaInicial != null && dataBaixaFinal != null) {
            filtros.append(" and trunc(eft.dataefetivacao) between to_date('").append(DataUtil.getDataFormatada(dataBaixaInicial))
                .append("','dd/MM/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataBaixaFinal)).append("','dd/MM/yyyy')");
        }
        if (tipoBaixa != null) {
            filtros.append(" and solicitacao.tipobaixa = '").append(tipoBaixa.name()).append("'");
        }
        filtros.append(" and uu.gestorpatrimonio = ").append(1);
        filtros.append(" and uu.usuariosistema_id = ").append(usuario.getId());
        return filtros.toString();
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getNumeroSolicitacaoBaixa() {
        return numeroSolicitacaoBaixa;
    }

    public void setNumeroSolicitacaoBaixa(String numeroSolicitacaoBaixa) {
        this.numeroSolicitacaoBaixa = numeroSolicitacaoBaixa;
    }

    public String getNumeroEfetivacaoBaixa() {
        return numeroEfetivacaoBaixa;
    }

    public void setNumeroEfetivacaoBaixa(String numeroEfetivacaoBaixa) {
        this.numeroEfetivacaoBaixa = numeroEfetivacaoBaixa;
    }

    public TipoBaixa getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(TipoBaixa tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public Date getDataBaixaInicial() {
        return dataBaixaInicial;
    }

    public void setDataBaixaInicial(Date dataBaixaInicial) {
        this.dataBaixaInicial = dataBaixaInicial;
    }

    public Date getDataBaixaFinal() {
        return dataBaixaFinal;
    }

    public void setDataBaixaFinal(Date dataBaixaFinal) {
        this.dataBaixaFinal = dataBaixaFinal;
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixa() {
        return efetivacaoBaixa;
    }

    public void setEfetivacaoBaixa(EfetivacaoBaixaPatrimonial efetivacaoBaixa) {
        this.efetivacaoBaixa = efetivacaoBaixa;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }
}
