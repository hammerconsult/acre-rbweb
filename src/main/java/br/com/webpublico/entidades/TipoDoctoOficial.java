/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoSequenciaDoctoOficial;
import br.com.webpublico.enums.TipoValidacaoDoctoOficial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leonardo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Certidao")
@Etiqueta("Tipo de Documento Oficial")

public class TipoDoctoOficial extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Sequência")
    private TipoSequenciaDoctoOficial tipoSequencia;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Grupo")
    private GrupoDoctoOficial grupoDoctoOficial;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Cadastro")
    private TipoCadastroDoctoOficial tipoCadastroDoctoOficial;
    @ManyToOne
    private Tributo tributo;
    private Integer validadeDam;
    private Integer validadeDocto;
    private BigDecimal valor;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tipoDoctoOficial")
    private List<TipoDoctoFinalidade> tipoDoctoFinalidades;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tipoDoctoOficial")
    private List<AtributoDoctoOficial> listaAtributosDoctoOficial;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tipoDoctoOficial", fetch = FetchType.EAGER)
    private List<UsuarioTipoDocto> listaUsuarioTipoDocto;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tipoDoctoOficial")
    private List<ModeloDoctoOficial> listaModeloDoctoOficial;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tipoDoctoOficial")
    private List<AssinaturaTipoDoctoOficial> assinaturas;
    @Enumerated(EnumType.STRING)
    private TipoValidacaoDoctoOficial tipoValidacaoDoctoOficial;
    private Boolean controleEnvioRecebimento;
    @Etiqueta("Módulo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private ModuloTipoDoctoOficial moduloTipoDoctoOficial;
    private Boolean imprimirDiretoPDF;
    private Boolean disponivelSolicitacaoWeb;
    private Boolean exigirAssinatura;
    private Boolean permitirImpressaoSemAssinatura;

    public TipoDoctoOficial() {
        disponivelSolicitacaoWeb = false;
        tipoDoctoFinalidades = new ArrayList<>();
        listaAtributosDoctoOficial = new ArrayList<>();
        listaUsuarioTipoDocto = new ArrayList<>();
        listaModeloDoctoOficial = new ArrayList<>();
        assinaturas = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public TipoValidacaoDoctoOficial getTipoValidacaoDoctoOficial() {
        return tipoValidacaoDoctoOficial;
    }

    public void setTipoValidacaoDoctoOficial(TipoValidacaoDoctoOficial tipoValidacaoDoctoOficial) {
        this.tipoValidacaoDoctoOficial = tipoValidacaoDoctoOficial;
    }

    public List<UsuarioTipoDocto> getListaUsuarioTipoDocto() {
        return listaUsuarioTipoDocto;
    }

    public void setListaUsuarioTipoDocto(List<UsuarioTipoDocto> listaUsuarioTipoDocto) {
        this.listaUsuarioTipoDocto = listaUsuarioTipoDocto;
    }

    public List<ModeloDoctoOficial> getListaModeloDoctoOficial() {
        return listaModeloDoctoOficial;
    }

    public void setListaModeloDoctoOficial(List<ModeloDoctoOficial> listaModeloDoctoOficial) {
        this.listaModeloDoctoOficial = listaModeloDoctoOficial;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Boolean getControleEnvioRecebimento() {
        return controleEnvioRecebimento;
    }

    public void setControleEnvioRecebimento(Boolean controleEnvioRecebimento) {
        this.controleEnvioRecebimento = controleEnvioRecebimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public GrupoDoctoOficial getGrupoDoctoOficial() {
        return grupoDoctoOficial;
    }

    public void setGrupoDoctoOficial(GrupoDoctoOficial grupoDoctoOficial) {
        this.grupoDoctoOficial = grupoDoctoOficial;
    }

    public TipoCadastroDoctoOficial getTipoCadastroDoctoOficial() {
        return tipoCadastroDoctoOficial;
    }

    public void setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial tipoCadastroDoctoOficial) {
        this.tipoCadastroDoctoOficial = tipoCadastroDoctoOficial;
    }

    public List<TipoDoctoFinalidade> getTipoDoctoFinalidades() {
        return tipoDoctoFinalidades;
    }

    public void setTipoDoctoFinalidades(List<TipoDoctoFinalidade> tipoDoctoFinalidades) {
        this.tipoDoctoFinalidades = tipoDoctoFinalidades;
    }

    public TipoSequenciaDoctoOficial getTipoSequencia() {
        return tipoSequencia;
    }

    public void setTipoSequencia(TipoSequenciaDoctoOficial tipoSequencia) {
        this.tipoSequencia = tipoSequencia;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Integer getValidadeDam() {
        return validadeDam;
    }

    public void setValidadeDam(Integer validadeDam) {
        this.validadeDam = validadeDam;
    }

    public Integer getValidadeDocto() {
        return validadeDocto;
    }

    public void setValidadeDocto(Integer validadeDocto) {
        this.validadeDocto = validadeDocto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<AtributoDoctoOficial> getListaAtributosDoctoOficial() {
        return listaAtributosDoctoOficial;
    }

    public void setListaAtributosDoctoOficial(List<AtributoDoctoOficial> listaAtributosDoctoOficial) {
        this.listaAtributosDoctoOficial = listaAtributosDoctoOficial;
    }

    public List<AssinaturaTipoDoctoOficial> getAssinaturas() {
        return assinaturas;
    }

    public void setAssinaturas(List<AssinaturaTipoDoctoOficial> assinaturas) {
        this.assinaturas = assinaturas;
    }

    public ModuloTipoDoctoOficial getModuloTipoDoctoOficial() {
        return moduloTipoDoctoOficial;
    }

    public void setModuloTipoDoctoOficial(ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        this.moduloTipoDoctoOficial = moduloTipoDoctoOficial;
    }

    public Boolean getImprimirDiretoPDF() {
        return imprimirDiretoPDF;
    }

    public void setImprimirDiretoPDF(Boolean imprimirDiretoPDF) {
        this.imprimirDiretoPDF = imprimirDiretoPDF;
    }

    public Boolean getDisponivelSolicitacaoWeb() {
        return disponivelSolicitacaoWeb;
    }

    public void setDisponivelSolicitacaoWeb(Boolean disponivelSolicitacaoWeb) {
        this.disponivelSolicitacaoWeb = disponivelSolicitacaoWeb;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public void adicionarModeloDoctoOficial(ModeloDoctoOficial modeloDoctoOficial) {
        if (!listaModeloDoctoOficial.contains(modeloDoctoOficial)) {
            listaModeloDoctoOficial.add(modeloDoctoOficial);
        }
    }

    public boolean hasModeloDoctoOficial() {
        return listaModeloDoctoOficial != null && !listaModeloDoctoOficial.isEmpty();
    }

    public Boolean getExigirAssinatura() {
        return exigirAssinatura == null ? Boolean.FALSE : exigirAssinatura;
    }

    public void setExigirAssinatura(Boolean exigirAssinatura) {
        this.exigirAssinatura = exigirAssinatura;
    }

    public Boolean getPermitirImpressaoSemAssinatura() {
        return permitirImpressaoSemAssinatura == null ? Boolean.TRUE : permitirImpressaoSemAssinatura;
    }

    public void setPermitirImpressaoSemAssinatura(Boolean permitirImpressaoSemAssinatura) {
        this.permitirImpressaoSemAssinatura = permitirImpressaoSemAssinatura;
    }
}

