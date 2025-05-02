package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
public class AssuntoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Código")
    private Integer codigo;

    @Obrigatorio
    @Etiqueta("Descrição Reduzida")
    private String descricao;

    @Etiqueta("Descrição Completa")
    private String descricaoCompleta;

    @Obrigatorio
    @Etiqueta("Ativo")
    private Boolean ativo;

    @Obrigatorio
    @Etiqueta("Tributo")
    @ManyToOne
    private Tributo tributo;

    @Etiqueta("Tipo de documento oficial")
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficial;

    private BigDecimal valorLicencaUFM;

    @OneToMany(mappedBy = "assunto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoriaAssuntoLicenciamentoAmbiental> categorias;

    @OneToMany(mappedBy = "assuntoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SituacaoEmissaoDebitoLicenciamentoAmbiental> situacoesEmissaoDebito;

    @OneToMany(mappedBy = "assuntoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SituacaoEmissaoLicencaLicenciamentoAmbiental> situacoesEmissaoLicenca;

    @Obrigatorio
    @Etiqueta("Para gerar débito, precisa de análise?")
    private Boolean geraTaxaExpediente;

    @Obrigatorio
    @Etiqueta("Por padrão, mostrar anexos no portal")
    private Boolean mostrarAnexosPortal;

    public AssuntoLicenciamentoAmbiental() {
        super();
        ativo = Boolean.TRUE;
        valorLicencaUFM = BigDecimal.ZERO;
        mostrarAnexosPortal = Boolean.FALSE;
        geraTaxaExpediente = Boolean.FALSE;
        categorias = Lists.newArrayList();
        situacoesEmissaoDebito = Lists.newArrayList();
        situacoesEmissaoLicenca = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public List<CategoriaAssuntoLicenciamentoAmbiental> getCategorias() {
        if (categorias == null) categorias = Lists.newArrayList();
        return categorias;
    }

    public void setCategorias(List<CategoriaAssuntoLicenciamentoAmbiental> categorias) {
        this.categorias = categorias;
    }

    public List<SituacaoEmissaoDebitoLicenciamentoAmbiental> getSituacoesEmissaoDebito() {
        return situacoesEmissaoDebito;
    }

    public void setSituacoesEmissaoDebito(List<SituacaoEmissaoDebitoLicenciamentoAmbiental> situacoesEmissaoDebito) {
        this.situacoesEmissaoDebito = situacoesEmissaoDebito;
    }

    public List<SituacaoEmissaoLicencaLicenciamentoAmbiental> getSituacoesEmissaoLicenca() {
        return situacoesEmissaoLicenca;
    }

    public void setSituacoesEmissaoLicenca(List<SituacaoEmissaoLicencaLicenciamentoAmbiental> situacoesEmissaoLicenca) {
        this.situacoesEmissaoLicenca = situacoesEmissaoLicenca;
    }

    public Boolean getGeraTaxaExpediente() {
        return geraTaxaExpediente;
    }

    public void setGeraTaxaExpediente(Boolean geraTaxaExpediente) {
        this.geraTaxaExpediente = geraTaxaExpediente;
    }

    public Boolean getMostrarAnexosPortal() {
        return mostrarAnexosPortal;
    }

    public void setMostrarAnexosPortal(Boolean mostrarAnexosPortal) {
        this.mostrarAnexosPortal = mostrarAnexosPortal;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
