/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoStatusSolicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Avaliação de Solicitação de Compra")
@Table(name = "AVALIACAOSOLCOMPRA")
public class AvaliacaoSolicitacaoDeCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Avaliação")
    @Temporal(TemporalType.DATE)
    private Date dataAvaliacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoStatusSolicitacao tipoStatusSolicitacao;

    private String motivo;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitação de Compra")
    private SolicitacaoMaterial solicitacao;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    public AvaliacaoSolicitacaoDeCompra() {
        super();
    }

    public AvaliacaoSolicitacaoDeCompra(SolicitacaoMaterial solicitacaoCompra, TipoStatusSolicitacao situcao) {
        this.tipoStatusSolicitacao = situcao;
        this.solicitacao = solicitacaoCompra;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoStatusSolicitacao getTipoStatusSolicitacao() {
        return tipoStatusSolicitacao;
    }

    public void setTipoStatusSolicitacao(TipoStatusSolicitacao tipoStatusSolicitacao) {
        this.tipoStatusSolicitacao = tipoStatusSolicitacao;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public SolicitacaoMaterial getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoMaterial solicitacao) {
        this.solicitacao = solicitacao;
    }

    @Override
    public String toString() {
        return tipoStatusSolicitacao.getDescricao();
    }

    public boolean isSolicitacaoAprovada() {
        if (this.tipoStatusSolicitacao == null) {
            return false;
        }

        return this.tipoStatusSolicitacao.equals(TipoStatusSolicitacao.APROVADA);
    }

    public Boolean isRejeitada() {
        return TipoStatusSolicitacao.REJEITADA.equals(this.getTipoStatusSolicitacao());
    }
}
