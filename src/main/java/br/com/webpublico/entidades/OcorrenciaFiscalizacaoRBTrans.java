/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.EspecieTransporte;
import br.com.webpublico.enums.GrupoOcorrenciaFiscalizacaoRBTrans;
import br.com.webpublico.enums.TipoGravidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Ocorrência")
@Table(name = "OCORRENCIAFISCALIZARBTRANS")
public class OcorrenciaFiscalizacaoRBTrans extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    @Obrigatorio
    private Long codigo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Transporte")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private EspecieTransporte especieTransporte;
    @Etiqueta("Grupo")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private GrupoOcorrenciaFiscalizacaoRBTrans grupo;
    @Etiqueta("Artigo")
    @Tabelavel
    @Pesquisavel
    private String artigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Conduta")
    @Obrigatorio
    private String conduta;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Etiqueta("Valor (UFM)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, TIPOCAMPO = Tabelavel.TIPOCAMPO.DECIMAL_QUATRODIGITOS)
    @Obrigatorio
    private BigDecimal valor;
    @Etiqueta("Tributo")
    @Obrigatorio
    @ManyToOne
    private Tributo tributo;
    private Integer pontuacao;
    @Enumerated(EnumType.STRING)
    private TipoGravidade gravidade;
    @OneToMany(mappedBy = "ocorrenciaFiscalizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcorrenciaAutuacaoRBTrans> ocorrenciasAutuacaoRBTrans;
    @Etiqueta("Infrator Motorista Auxiliar")
    @Pesquisavel
    @Tabelavel
    private Boolean motorista;
    @Pesquisavel
    @Etiqueta("Infrator Permissionário")
    @Tabelavel
    private Boolean permissionario;
    //Atributo para controlar a seleção dentro das DataTables
    @Transient
    private boolean selecionado;
    @OneToMany(mappedBy = "ocorrenciaFiscalizaRBTrans", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MedidasOcorrencia> ocorrenciaFiscalizacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date vigenciaInicial;
    @Temporal(TemporalType.TIMESTAMP)
    private Date vigenciaFinal;
    private Integer fatorMultReincidencia;

    public List<MedidasOcorrencia> getOcorrenciaFiscalizacao() {
        return ocorrenciaFiscalizacao;
    }

    public void setOcorrenciaFiscalizacao(List<MedidasOcorrencia> ocorrenciaFiscalizacao) {
        this.ocorrenciaFiscalizacao = ocorrenciaFiscalizacao;
    }

    public TipoGravidade getGravidade() {
        return gravidade;
    }

    public void setGravidade(TipoGravidade gravidade) {
        this.gravidade = gravidade;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Boolean getMotorista() {
        return motorista != null ? motorista : false;
    }

    public void setMotorista(Boolean motorista) {
        this.motorista = motorista;
    }

    public Boolean getPermissionario() {
        return permissionario != null ? permissionario : false;
    }

    public void setPermissionario(Boolean permissionario) {
        this.permissionario = permissionario;
    }

    public OcorrenciaFiscalizacaoRBTrans() {
        ocorrenciaFiscalizacao = new ArrayList<>();
        vigenciaInicial = new Date();
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public List<OcorrenciaAutuacaoRBTrans> getOcorrenciasAutuacaoRBTrans() {
        return ocorrenciasAutuacaoRBTrans;
    }

    public void setOcorrenciasAutuacaoRBTrans(List<OcorrenciaAutuacaoRBTrans> ocorrenciasAutuacaoRBTrans) {
        this.ocorrenciasAutuacaoRBTrans = ocorrenciasAutuacaoRBTrans;
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

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Integer getFatorMultReincidencia() {
        return fatorMultReincidencia;
    }

    public void setFatorMultReincidencia(Integer fatorMultReincidencia) {
        this.fatorMultReincidencia = fatorMultReincidencia;
    }

    public EspecieTransporte getEspecieTransporte() {
        return especieTransporte;
    }

    public void setEspecieTransporte(EspecieTransporte especieTransporte) {
        this.especieTransporte = especieTransporte;
    }

    public GrupoOcorrenciaFiscalizacaoRBTrans getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoOcorrenciaFiscalizacaoRBTrans grupo) {
        this.grupo = grupo;
    }

    public String getArtigo() {
        return artigo;
    }

    public void setArtigo(String artigo) {
        this.artigo = artigo;
    }

    public String getConduta() {
        return conduta;
    }

    public void setConduta(String conduta) {
        this.conduta = conduta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcorrenciaFiscalizacaoRBTrans)) {
            return false;
        }
        OcorrenciaFiscalizacaoRBTrans other = (OcorrenciaFiscalizacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return artigo + " - " + conduta + " - " + descricao;
    }
}
