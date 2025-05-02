package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.enums.PermissaoUsuarioEmpresaNfse;
import br.com.webpublico.util.anotacoes.Length;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Alex on 23/10/17.
 */
@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
public class UserNfseCadastroEconomico extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioWeb usuarioNfse;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @ElementCollection(targetClass = PermissaoUsuarioEmpresaNfse.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "PERMISSAOCMCNFSE", joinColumns = @JoinColumn(name = "USERNFSECADASTROECONOMICO_ID"))
    @Column(name = "permissao")
    private List<PermissaoUsuarioEmpresaNfse> permissoes;
    private Boolean contador;
    @Length(maximo = 255)
    private String funcao;
    @Enumerated(EnumType.STRING)
    private AdicionadoPor adicionadoPor;
    @ManyToOne
    private UsuarioWeb usuarioResponsavel;
    private Boolean bloqueadoEmissaoNfse;
    private String telefoneDesbloqueio;

    public UserNfseCadastroEconomico() {
        permissoes = Lists.newArrayList();
        situacao = Situacao.APROVADO;
        bloqueadoEmissaoNfse = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioWeb getUsuarioNfse() {
        return usuarioNfse;
    }

    public void setUsuarioNfse(UsuarioWeb usuarioNfse) {
        this.usuarioNfse = usuarioNfse;
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

    public boolean isAprovado() {
        return Situacao.APROVADO.equals(situacao);
    }

    public List<PermissaoUsuarioEmpresaNfse> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<PermissaoUsuarioEmpresaNfse> permissoes) {
        this.permissoes = permissoes;
    }

    public Boolean getContador() {
        return contador != null ? contador : false;
    }

    public void setContador(Boolean contador) {
        this.contador = contador;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public AdicionadoPor getAdicionadoPor() {
        return adicionadoPor;
    }

    public void setAdicionadoPor(AdicionadoPor adicionadoPor) {
        this.adicionadoPor = adicionadoPor;
    }

    public UsuarioWeb getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioWeb usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public Boolean getBloqueadoEmissaoNfse() {
        return bloqueadoEmissaoNfse;
    }

    public void setBloqueadoEmissaoNfse(Boolean bloqueadoEmissaoNfse) {
        this.bloqueadoEmissaoNfse = bloqueadoEmissaoNfse;
    }

    public String getTelefoneDesbloqueio() {
        return telefoneDesbloqueio;
    }

    public void setTelefoneDesbloqueio(String telefoneDesbloqueio) {
        this.telefoneDesbloqueio = telefoneDesbloqueio;
    }

    public void adicionarTodasPermissoes() {
        permissoes = Lists.newArrayList();
        for (PermissaoUsuarioEmpresaNfse permissao : PermissaoUsuarioEmpresaNfse.values()) {
            permissoes.add(permissao);
        }
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

    public enum AdicionadoPor {
        VIA_SISTEMA_SOCIO_ADMINISTRADOR,
        VIA_SISTEMA_CONTADOR
    }
}
