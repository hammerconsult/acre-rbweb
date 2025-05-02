package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.util.Util;

import java.util.Date;

public class FiltroRelatorioExtratoBem {

    private Date dataInicial;
    private Date dataFinal;
    private ApresentacaoRelatorio apresentacaoRelatorio;
    private UsuarioSistema usuarioSistema;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private Bem bem;
    private TipoBem tipoBem;

    public FiltroRelatorioExtratoBem() {
        dataInicial = new Date();
        dataFinal = new Date();
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
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

    public ApresentacaoRelatorio getApresentacaoRelatorio() {
        return apresentacaoRelatorio;
    }

    public void setApresentacaoRelatorio(ApresentacaoRelatorio apresentacaoRelatorio) {
        this.apresentacaoRelatorio = apresentacaoRelatorio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
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

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public String getFiltro() {
        StringBuilder where = new StringBuilder();
        if (getBem() != null) {
            where.append(" AND ev.bem_id  = ").append(getBem().getId());
        }
        if (getHierarquiaAdministrativa() != null) {
            where.append(" AND ADM.CODIGO LIKE '").append(getHierarquiaAdministrativa().getCodigoSemZerosFinais() + "%'");
        }
        if (getHierarquiaOrcamentaria() != null) {
            where.append(" AND est_resultante.detentoraOrcamentaria_id = ").append(getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        return where.toString();
    }

    public String getCriteriosUtilizados() {
        StringBuilder filtro = new StringBuilder("Critérios Utilizados: ");
        if (getBem() != null) {
            filtro.append(" Bem: ").append(getBem().toString().trim()).append("; ");
        }
        if (getHierarquiaAdministrativa() != null) {
            filtro.append(" Unidade Administrativa: ").append(getHierarquiaAdministrativa().getSubordinada()).append("; ");
        }
        if (getHierarquiaOrcamentaria() != null) {
            filtro.append(" Unidade Orçamentária: ").append(getHierarquiaOrcamentaria().getSubordinada()).append("; ");
        }
        filtro.append(" Período: De: ").append(Util.dateToString(getDataInicial())).append(" Até: ").append(Util.dateToString(getDataFinal()));
        return filtro.toString();
    }
}
