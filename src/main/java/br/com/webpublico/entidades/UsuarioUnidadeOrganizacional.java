package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Fabio
 */
@Etiqueta(value = "Unidades do Usuario")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
@Table(name = "USUARIOUNIDADEORGANIZACIO")

public class UsuarioUnidadeOrganizacional extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    private Boolean gestorProtocolo;
    private Boolean gestorMateriais;
    private Boolean gestorLicitacao;
    private Boolean gestorPatrimonio;
    private Boolean gestorOrcamento;
    private Boolean gestorFinanceiro;
    private Boolean gestorControleInterno;

    public UsuarioUnidadeOrganizacional() {
        gestorProtocolo = Boolean.FALSE;
        gestorMateriais = Boolean.FALSE;
        gestorLicitacao = Boolean.FALSE;
        gestorPatrimonio = Boolean.FALSE;
        gestorOrcamento = Boolean.FALSE;
        gestorFinanceiro = Boolean.FALSE;
        gestorControleInterno = Boolean.FALSE;
    }

    public UsuarioUnidadeOrganizacional(UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeOrganizacional, UsuarioUnidadeOrganizacional uuo) {
        criadoEm = System.nanoTime();
        this.usuarioSistema = usuarioSistema;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.gestorProtocolo = uuo.getGestorProtocolo();
        this.gestorMateriais = uuo.getGestorMateriais();
        this.gestorLicitacao = uuo.getGestorLicitacao();
        this.gestorPatrimonio = uuo.getGestorPatrimonio();
        this.gestorOrcamento = uuo.getGestorOrcamento();
        this.gestorFinanceiro = uuo.getGestorFinanceiro();
        this.gestorControleInterno = uuo.getGestorControleInterno();
    }

    public Boolean getGestorLicitacao() {
        return gestorLicitacao;
    }

    public void setGestorLicitacao(Boolean gestorLicitacao) {
        this.gestorLicitacao = gestorLicitacao;
    }

    public Boolean getGestorMateriais() {
        return gestorMateriais;
    }

    public void setGestorMateriais(Boolean gestorMateriais) {
        this.gestorMateriais = gestorMateriais;
    }

    public Boolean getGestorPatrimonio() {
        return gestorPatrimonio;
    }

    public void setGestorPatrimonio(Boolean gestorPatrimonio) {
        this.gestorPatrimonio = gestorPatrimonio;
    }

    public Boolean getGestorProtocolo() {
        return gestorProtocolo;
    }

    public void setGestorProtocolo(Boolean gestorProtocolo) {
        this.gestorProtocolo = gestorProtocolo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Boolean getGestorOrcamento() {
        return gestorOrcamento;
    }

    public void setGestorOrcamento(Boolean gestorOrcamento) {
        this.gestorOrcamento = gestorOrcamento;
    }

    public Boolean getGestorFinanceiro() {
        return gestorFinanceiro;
    }

    public void setGestorFinanceiro(Boolean gestorFinanceiro) {
        this.gestorFinanceiro = gestorFinanceiro;
    }

    public Boolean getGestorControleInterno() {
        return gestorControleInterno != null ? gestorControleInterno : Boolean.FALSE;
    }

    public void setGestorControleInterno(Boolean gestorControleInterno) {
        this.gestorControleInterno = gestorControleInterno;
    }
}
