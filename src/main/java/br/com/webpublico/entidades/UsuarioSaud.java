package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class UsuarioSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Obrigatorio
    @Etiqueta("CPF")
    private String cpf;
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @Obrigatorio
    @Etiqueta("Renda (R$)")
    private BigDecimal renda;
    @Obrigatorio
    @Etiqueta("Telefone")
    private String telefone;
    @Obrigatorio
    @Etiqueta("Data de Nascimento")
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo foto;
    @OneToOne(cascade = CascadeType.ALL)
    private UsuarioSaudEndereco endereco;
    @OneToMany(mappedBy = "usuarioSaud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioSaudDocumento> documentos;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToOne
    private UsuarioWeb usuarioWeb;

    public UsuarioSaud() {
        super();
        this.status = Status.CADASTRADO;
        this.dataCadastro = new Date();
        this.endereco = new UsuarioSaudEndereco();
        this.documentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getRenda() {
        return renda;
    }

    public void setRenda(BigDecimal renda) {
        this.renda = renda;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Arquivo getFoto() {
        return foto;
    }

    public void setFoto(Arquivo arquivo) {
        this.foto = arquivo;
    }

    public UsuarioSaudEndereco getEndereco() {
        return endereco;
    }

    public void setEndereco(UsuarioSaudEndereco endereco) {
        this.endereco = endereco;
    }

    public List<UsuarioSaudDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<UsuarioSaudDocumento> documentos) {
        this.documentos = documentos;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public boolean hasDocumentoRejeitado(UsuarioSaud usuarioSaud) {
        return usuarioSaud.getDocumentos().stream().anyMatch((doc) -> UsuarioSaudDocumento.Status.REJEITADO.equals(doc.getStatus()));
    }

    public Boolean getDocumentacaoAprovada() {
        if (documentos == null || documentos.isEmpty()) return true;
        return documentos
            .stream()
            .noneMatch((usuarioSaudDocumento -> !usuarioSaudDocumento.getStatus().equals(UsuarioSaudDocumento.Status.APROVADO)));
    }

    public enum Status {
        CADASTRADO("Cadastrado"),
        AGUARDANDO_DOCUMENTACAO("Aguardando Documentação"),
        RETORNO_DOCUMENTACAO("Retorno Documentação"),
        DOCUMENTACAO_APROVADA("Documentação Aprovada"),
        ATIVO("Ativo"),
        SUSPENSO("Suspenso"),
        INATIVO("Inativo");

        private String descricao;

        Status(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (getFoto() == null) {
            throw new ValidacaoException("A Foto deve ser informada.");
        }
        endereco.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (documentos != null && !documentos.isEmpty()) {
            for (UsuarioSaudDocumento usuarioSaudDocumento : documentos) {
                if (usuarioSaudDocumento.getDocumento() == null) {
                    String descricaoDocumento = usuarioSaudDocumento.getParametroSaudDocumento() != null
                        ? usuarioSaudDocumento.getParametroSaudDocumento().getDescricao()
                        : usuarioSaudDocumento.getDescricao();
                    ve.adicionarMensagemDeCampoObrigatorio("O documento " + descricaoDocumento + " deve ser informado.");
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public String toString() {
        return nome + " - (" + cpf + ")";
    }
}
