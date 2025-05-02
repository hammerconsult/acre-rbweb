package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Audited
@Entity
public class ReformaAdministrativa extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private Long numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    private Date data;
    @ManyToOne
    @Etiqueta("Secretaria")
    @Tabelavel
    @Obrigatorio
    private HierarquiaOrganizacional secretaria;
    @Enumerated(EnumType.STRING)
    private TipoHierarquiaOrganizacional tipo;
    @ManyToOne
    @Etiqueta("Usuário")
    @Tabelavel
    @Obrigatorio
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Deferida Em")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date deferidaEm;
    @Etiqueta("Observação")
    private String observacao;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "reformaAdministrativa")
    private List<ReformaAdministrativaUnidade> unidades;
    @Transient
    private List<String> erros;

    public ReformaAdministrativa() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public HierarquiaOrganizacional getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(HierarquiaOrganizacional secretaria) {
        this.secretaria = secretaria;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDeferidaEm() {
        return deferidaEm;
    }

    public void setDeferidaEm(Date deferidaEm) {
        this.deferidaEm = deferidaEm;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ReformaAdministrativaUnidade> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<ReformaAdministrativaUnidade> unidades) {
        this.unidades = unidades;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public TipoHierarquiaOrganizacional getTipo() {
        return tipo;
    }

    public void setTipo(TipoHierarquiaOrganizacional tipo) {
        this.tipo = tipo;
    }

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }

    @Override
    public String toString() {
        return numero + " - " + DataUtil.getDataFormatada(data);
    }
}
