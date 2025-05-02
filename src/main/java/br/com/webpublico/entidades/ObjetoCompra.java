/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.enums.SituacaoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Romanini
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Solicitação de Cadastro para Objeto de Compra")
public class ObjetoCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Código")
    private Long codigo;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Descrição")
    private String descricao;

    @ManyToOne
    @Etiqueta(value = "Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Tabelavel
    @ManyToOne
    @Etiqueta(value = "Grupo de Objeto de Compra")
    @Obrigatorio
    @Pesquisavel
    private GrupoObjetoCompra grupoObjetoCompra;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Tipo do Objeto de Compra")
    @Enumerated(EnumType.STRING)
    private TipoObjetoCompra tipoObjetoCompra;

    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoObjetoCompra situacaoObjetoCompra;

    @ManyToOne
    @Etiqueta("Efetivação Objeto de Compra")
    private EfetivacaoObjetoCompra efetivacaoObjetoCompra;

    @ManyToOne
    @Etiqueta("Derivação Objeto de Compra")
    private DerivacaoObjetoCompra derivacaoObjetoCompra;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Referencial")
    private Boolean referencial;

    @OneToMany(mappedBy = "objetoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjetoDeCompraEspecificacao> especificacaoes;

    @Transient
    private GrupoContaDespesa grupoContaDespesa;

    public ObjetoCompra() {
        super();
        this.especificacaoes = new ArrayList<>();
        ativo = true;
    }

    public GrupoContaDespesa getGrupoContaDespesa() {
        return grupoContaDespesa;
    }

    public void setGrupoContaDespesa(GrupoContaDespesa grupoContaDespesa) {
        this.grupoContaDespesa = grupoContaDespesa;
    }

    public EfetivacaoObjetoCompra getEfetivacaoObjetoCompra() {
        return efetivacaoObjetoCompra;
    }

    public void setEfetivacaoObjetoCompra(EfetivacaoObjetoCompra efetivacaoObjetoCompra) {
        this.efetivacaoObjetoCompra = efetivacaoObjetoCompra;
    }

    public DerivacaoObjetoCompra getDerivacaoObjetoCompra() {
        return derivacaoObjetoCompra;
    }

    public void setDerivacaoObjetoCompra(DerivacaoObjetoCompra derivacaoObjetoCompra) {
        this.derivacaoObjetoCompra = derivacaoObjetoCompra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public SituacaoObjetoCompra getSituacaoObjetoCompra() {
        return situacaoObjetoCompra;
    }

    public void setSituacaoObjetoCompra(SituacaoObjetoCompra situacaoObjetoCompra) {
        this.situacaoObjetoCompra = situacaoObjetoCompra;
    }

    public List<ObjetoDeCompraEspecificacao> getEspecificacaoesAtivas() {
        List<ObjetoDeCompraEspecificacao> ativas = new ArrayList<>();
        if (especificacaoes != null && especificacaoes.size() > 0) {
            for (ObjetoDeCompraEspecificacao esp : especificacaoes) {
                if (esp.getAtivo()) {
                    ativas.add(esp);
                }
            }
        }
        return ativas;
    }

    public List<ObjetoDeCompraEspecificacao> getEspecificacaoes() {
        return especificacaoes;
    }

    public void setEspecificacaoes(List<ObjetoDeCompraEspecificacao> especificacaoes) {
        this.especificacaoes = especificacaoes;
    }

    public Boolean getReferencial() {
        return referencial;
    }

    public void setReferencial(Boolean referencial) {
        this.referencial = referencial;
    }

    public String getCodigoDescricaoCurta() {
        descricao = descricao.length() > 50 ? descricao.substring(0, 50) + "...": descricao;
        return codigo + " - " + descricao;
    }

    @Override
    public String toString() {
        String retorno = descricao;

        if (codigo == null && descricao == null) {
            return "";
        }

        if (descricao != null && descricao.length() > 90) {
            retorno = descricao.substring(0, 90);
        }

        return codigo + " - " + retorno;
    }

    public boolean isObjetoDerivacao(){
        return derivacaoObjetoCompra !=null;
    }

    public boolean isObjetoCompraTipoConsumo() {
        return TipoObjetoCompra.CONSUMO.equals(this.getTipoObjetoCompra());
    }

    public boolean isObjetoCompraTipoPermanente() {
        return TipoObjetoCompra.PERMANENTE_MOVEL.equals(this.getTipoObjetoCompra()) ||
            TipoObjetoCompra.PERMANENTE_IMOVEL.equals(this.getTipoObjetoCompra());
    }

    public boolean isObjetoValorReferencia() {
        try {
            return grupoObjetoCompra.getTipoCotacao().isValorReferencia();
        }catch (Exception e){
            return false;
        }
    }

    public String getDescricaoCurta() {
        if (descricao.length() > 40) {
            return descricao.substring(0, 40) + "...";
        } else {
            return descricao;
        }
    }
}
