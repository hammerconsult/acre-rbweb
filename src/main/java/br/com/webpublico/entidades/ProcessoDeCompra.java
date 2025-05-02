/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoProcessoDeCompra;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Processo de Compra")
public class ProcessoDeCompra extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data do Processo")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataProcesso;

    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private Integer numero;

    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício")
    @Pesquisavel
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo do Processo de Compra")
    @Pesquisavel
    private TipoProcessoDeCompra tipoProcessoDeCompra;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Solicitação de Compra")
    @ManyToOne
    @Pesquisavel
    private SolicitacaoMaterial solicitacaoMaterial;

    @Etiqueta("Lotes")
    @OneToMany(mappedBy = "processoDeCompra", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<LoteProcessoDeCompra> lotesProcessoDeCompra;

    @Tabelavel
    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    @Pesquisavel
    private String descricao;

    @Tabelavel
    @Etiqueta("Unidade Responsável")
    @ManyToOne
    @JoinColumn(name = "UNIDADEORGANIZACIONAL_ID")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo da Solicitação")
    @Pesquisavel
    private TipoSolicitacao tipoSolicitacao;

    @Tabelavel
    @Etiqueta("Responsável")
    @ManyToOne
    @JoinColumn(name = "USUARIOSISTEMA_ID")
    @Obrigatorio
    @Pesquisavel
    private UsuarioSistema responsavel;

    @Invisivel
    @OneToMany(mappedBy = "processoDeCompra")
    private List<Licitacao> licitacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Licitacao> getLicitacoes() {
        return licitacoes;
    }

    public void setLicitacoes(List<Licitacao> licitacoes) {
        this.licitacoes = licitacoes;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataProcesso() {
        return dataProcesso;
    }

    public void setDataProcesso(Date dataProcesso) {
        this.dataProcesso = dataProcesso;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<LoteProcessoDeCompra> getLotesProcessoDeCompra() {
        if (lotesProcessoDeCompra != null) {
            Collections.sort(lotesProcessoDeCompra, new Comparator<LoteProcessoDeCompra>() {
                @Override
                public int compare(LoteProcessoDeCompra o1, LoteProcessoDeCompra o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return lotesProcessoDeCompra;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public void setLotesProcessoDeCompra(List<LoteProcessoDeCompra> lotesProcessoDeCompra) {
        this.lotesProcessoDeCompra = lotesProcessoDeCompra;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public String toString() {
        String retorno = "Nº " + numero + "/" + exercicio + " - " + tipoProcessoDeCompra.getDescricao();
        if (descricao != null) {
            retorno += " - " + getDescricaoReduzida() + "...";
        }
        return retorno;

    }

    public String getNumeroAndExercicio() {
        return "Nº " + numero + "/" + exercicio;
    }

    public String getDescricaoReduzida() {
        if (descricao != null && descricao.length() > 70) {
            return descricao.substring(0, 70);
        }
        return descricao;
    }

    public TipoProcessoDeCompra getTipoProcessoDeCompra() {
        return tipoProcessoDeCompra;
    }

    public void setTipoProcessoDeCompra(TipoProcessoDeCompra tipoProcessoDeCompra) {
        this.tipoProcessoDeCompra = tipoProcessoDeCompra;
    }
}
