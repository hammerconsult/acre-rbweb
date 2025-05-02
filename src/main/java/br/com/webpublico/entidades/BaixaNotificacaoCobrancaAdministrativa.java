package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author octavio
*/
@Entity
@Audited
@Etiqueta("Baixa Notificação de Cobrança Administrativa")
@Table(name = "BAIXANOTIFICACOBRANCAADM")
public class BaixaNotificacaoCobrancaAdministrativa extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Usuário")
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Data Operação")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataOperacao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Programação de Cobrança")
    private ProgramacaoCobranca programacaoCobranca;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Baixado")
    private Boolean baixado;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "baixaNotificacao", orphanRemoval = true)
    private List<BaixaCobrancaAdministrativaItemNotificacao> itensNotificacao;

    public BaixaNotificacaoCobrancaAdministrativa() {
        itensNotificacao = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getBaixado() {
        return baixado;
    }

    public void setBaixado(Boolean baixado) {
        this.baixado = baixado;
    }

    public List<BaixaCobrancaAdministrativaItemNotificacao> getItensNotificacao() {
        return itensNotificacao;
    }

    public void setItensNotificacao(List<BaixaCobrancaAdministrativaItemNotificacao> itensNotificacao) {
        this.itensNotificacao = itensNotificacao;
    }

    public ProgramacaoCobranca getProgramacaoCobranca() {
        return programacaoCobranca;
    }

    public void setProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        this.programacaoCobranca = programacaoCobranca;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }
}
