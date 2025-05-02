/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author reidocrime
 */
@Audited
@Table(name = "CONFIGURACAOHIERARQUIA")
@Etiqueta("Configuração Unidade Organizacional")
@Entity

public class ConfiguracaoHierarquiaOrganizacional implements Serializable {

    private static final long serialVersionUID = 1L;
    @Invisivel
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Column(name = "MASCARA")
    @Etiqueta("Máscara da Unidade Organizacional")
    private String mascara;
    @Tabelavel
    @Obrigatorio
    @Column(name = "LIST_UNID_NAO_UTIL")
    @Etiqueta("Listar Unidade Organizacional")
    private boolean listarUnidadesOrgNaoUtilizavel;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo Hierarquia Organizacional")
    @Enumerated(EnumType.STRING)
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;

    public ConfiguracaoHierarquiaOrganizacional() {
        inicioVigencia = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isListarUnidadesOrgNaoUtilizavel() {
        return listarUnidadesOrgNaoUtilizavel;
    }

    public void setListarUnidadesOrgNaoUtilizavel(boolean listarUnidadesOrgNaoUtilizavel) {
        this.listarUnidadesOrgNaoUtilizavel = listarUnidadesOrgNaoUtilizavel;
    }

    public TipoHierarquiaOrganizacional getTipoHierarquiaOrganizacional() {
        return tipoHierarquiaOrganizacional;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public Date getFimVigencia() {
        return finalVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.finalVigencia = fimVigencia;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.mascara);
        hash = 73 * hash + (this.listarUnidadesOrgNaoUtilizavel ? 1 : 0);
        hash = 73 * hash + (this.tipoHierarquiaOrganizacional != null ? this.tipoHierarquiaOrganizacional.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfiguracaoHierarquiaOrganizacional other = (ConfiguracaoHierarquiaOrganizacional) obj;
        if (!Objects.equals(this.mascara, other.mascara)) {
            return false;
        }
        if (this.listarUnidadesOrgNaoUtilizavel != other.listarUnidadesOrgNaoUtilizavel) {
            return false;
        }
        if (this.tipoHierarquiaOrganizacional != other.tipoHierarquiaOrganizacional) {
            return false;
        }
        return true;
    }

    public ConfiguracaoHierarquiaOrganizacional cloneConfiguracao(ConfiguracaoHierarquiaOrganizacional conf) {
        ConfiguracaoHierarquiaOrganizacional novaConf = new ConfiguracaoHierarquiaOrganizacional();
        novaConf.setListarUnidadesOrgNaoUtilizavel(conf.isListarUnidadesOrgNaoUtilizavel());
        novaConf.setMascara(conf.getMascara());
        novaConf.setTipoHierarquiaOrganizacional(conf.getTipoHierarquiaOrganizacional());

        return novaConf;
    }
}
