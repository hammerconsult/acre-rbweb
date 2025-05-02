package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Wellington on 21/09/20.
 */
@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
public class UserDteCadastroEconomico extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioWeb usuarioWeb;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;

    public UserDteCadastroEconomico() {
        situacao = Situacao.APROVADO;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAprovado() {
        return Situacao.APROVADO.equals(situacao);
    }

    public enum Situacao {
        APROVADO("Aprovado"), PENDENTE("Pendente");
        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
