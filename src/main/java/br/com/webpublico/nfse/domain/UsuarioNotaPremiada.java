package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Usuário Nota Premiada")
public class UsuarioNotaPremiada extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @Etiqueta("Dados Pessoais")
    private DadosPessoaisNfse dadosPessoais;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário")
    private String login;

    @Etiqueta("Senha")
    private String password;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;

    private String imagem;

    @Etiqueta("Dica da Senha")
    private String dicaSenha;

    private String resetKey;

    @Temporal(TemporalType.TIMESTAMP)
    private Date resetDate;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UsuarioNotaPremiadaPermissao> permissoes;

    @Etiqueta("Participando do Programa?")
    private Boolean participandoPrograma;

    @Transient
    private String senha;
    @Transient
    private String confirmarSenha;


    public UsuarioNotaPremiada() {
        dadosPessoais = new DadosPessoaisNfse();
        ativo = true;
        participandoPrograma = Boolean.TRUE;
        permissoes = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DadosPessoaisNfse getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfse dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imageUrl) {
        this.imagem = imageUrl;
    }

    public String getDicaSenha() {
        return dicaSenha;
    }

    public void setDicaSenha(String dicaSenha) {
        this.dicaSenha = dicaSenha;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate;
    }

    public List<UsuarioNotaPremiadaPermissao> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<UsuarioNotaPremiadaPermissao> permissoes) {
        this.permissoes = permissoes;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public Boolean getParticipandoPrograma() {
        return participandoPrograma != null ? participandoPrograma : Boolean.FALSE;
    }

    public void setParticipandoPrograma(Boolean participandoPrograma) {
        this.participandoPrograma = participandoPrograma;
    }

    @Override
    public void validarCamposObrigatorios() {
        Util.validarCampos(dadosPessoais);
        super.validarCamposObrigatorios();
        ValidacaoException ve = new ValidacaoException();
        if (id == null) {
            if (senha.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a senha do usuário.");
            }
            if (confirmarSenha.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Confirme a senha do usuário.");
            }
            if (!senha.isEmpty() && !confirmarSenha.isEmpty() && !senha.equals(confirmarSenha)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A confirmação da senha não confere.");
            }
        }
        if (id != null) {
            if (!senha.isEmpty()) {
                if (confirmarSenha.isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Confirme a senha do usuário.");
                }
                if (!senha.isEmpty() && !confirmarSenha.isEmpty() && !senha.equals(confirmarSenha)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A confirmação da senha não confere.");
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public String toString() {
        return dadosPessoais.toString();
    }
}
