package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.contabil.TipoReformaAdministrativa;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Audited
@Entity
public class ReformaAdministrativaUnidade extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReformaAdministrativa reformaAdministrativa;
    @ManyToOne
    @Etiqueta("Unidade")
    @Tabelavel
    @Obrigatorio
    private HierarquiaOrganizacional unidade;
    @Enumerated(EnumType.STRING)
    private TipoReformaAdministrativa tipo;
    private String codigoNovo;
    private String descricaoNovo;
    private Date inicioVigencia;
    private Date fimVigencia;
    private String erros;
    @ManyToOne
    @Etiqueta("Superior")
    private ReformaAdministrativaUnidade unidadeSuperior;
    @ManyToOne
    private HierarquiaOrganizacional novaHierarquia;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "reformaUnidade")
    private List<ReformaAdministrativaUnidadeUsuario> usuarios;

    public ReformaAdministrativaUnidade() {
        this.usuarios = Lists.newArrayList();
    }

    public ReformaAdministrativaUnidade(HierarquiaOrganizacional filha, ReformaAdministrativaUnidade superior, ReformaAdministrativa reformaAdministrativa) {
        this.reformaAdministrativa = reformaAdministrativa;
        this.unidade = filha;
        this.unidadeSuperior = superior;
        this.tipo = TipoReformaAdministrativa.NAO_ALTERAR;
        this.usuarios = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }

    public TipoReformaAdministrativa getTipo() {
        return tipo;
    }

    public void setTipo(TipoReformaAdministrativa tipo) {
        this.tipo = tipo;
    }

    public String getCodigoNovo() {
        return codigoNovo;
    }

    public void setCodigoNovo(String codigoNovo) {
        this.codigoNovo = codigoNovo;
    }

    public String getDescricaoNovo() {
        return descricaoNovo;
    }

    public void setDescricaoNovo(String descricaoNovo) {
        this.descricaoNovo = descricaoNovo;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public ReformaAdministrativa getReformaAdministrativa() {
        return reformaAdministrativa;
    }

    public void setReformaAdministrativa(ReformaAdministrativa reformaAdministrativa) {
        this.reformaAdministrativa = reformaAdministrativa;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getErros() {
        return erros;
    }

    public void setErros(String erros) {
        this.erros = erros;
    }

    public List<ReformaAdministrativaUnidadeUsuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<ReformaAdministrativaUnidadeUsuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public String toString() {
        if (codigoNovo != null && descricaoNovo != null) {
            return codigoNovo + " - " + descricaoNovo;
        }
        if (unidade != null) {
            return unidade.getCodigo() + " - " + unidade.getDescricao();
        }
        return " - ";
    }

    public ReformaAdministrativaUnidade getUnidadeSuperior() {
        return unidadeSuperior;
    }

    public void setUnidadeSuperior(ReformaAdministrativaUnidade unidadeSuperior) {
        this.unidadeSuperior = unidadeSuperior;
    }

    public HierarquiaOrganizacional getNovaHierarquia() {
        return novaHierarquia;
    }

    public void setNovaHierarquia(HierarquiaOrganizacional novaHierarquia) {
        this.novaHierarquia = novaHierarquia;
    }

    public String getCodigoSuperior() {
        String codigoSuperior = getCodigoSemZerosAoFinal();
        String[] split = codigoSuperior.split("\\.");
        codigoSuperior = codigoSuperior.substring(0, codigoSuperior.indexOf(split[split.length - 1]));
        return codigoSuperior;
    }

    public String getCodigoSemZerosAoFinal() {
        String toReturn = "";
        toReturn = this.codigoNovo;
        int zeroAPartirDe = toReturn.length();
        for (int i = toReturn.length() - 1; i >= 0; i--) {
            if (toReturn.substring(i, i + 1).equals(".")) {
                zeroAPartirDe = i;
            } else if (!toReturn.substring(i, i + 1).equalsIgnoreCase("0")) {
                return toReturn.substring(0, zeroAPartirDe);
            }
        }
        return toReturn;
    }
}
