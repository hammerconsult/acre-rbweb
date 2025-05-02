package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Seguranca;
import br.com.webpublico.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WSPessoa implements Serializable {

    private Long id;
    private String nome;
    private String nomeFantasia;
    private String nomeMae;
    private String tipo;
    private Date nascimento;
    private Sexo sexo;
    private String cpfCnpj;
    private String email;
    private String homePage;
    private NivelEscolaridade nivelEscolaridade;
    private String profissao;
    private String rgInscricao;
    private String orgaoEmissor;
    private List<WSTelefoneDaPessoa> telefones;
    private List<WSEnderecoDaPessoa> enderecos;
    private WSUsuario usuario;
    private WSFotoDaPessoa foto;
    private List<WSVinculoFP> vinculos;
    private SituacaoCadastralPessoa situacaoCadastralPessoa;
    private BigDecimal proporcaoSocio;
    private Boolean ott;

    public WSPessoa() {
    }

    public WSPessoa(Pessoa pessoa, String cpfCnpj, String senha) {
        this(pessoa);
        if (pessoa.getUsuariosWeb() != null && !pessoa.getUsuariosWeb().isEmpty()) {
            if (StringUtil.retornaApenasNumeros(cpfCnpj).equals(StringUtil.retornaApenasNumeros(pessoa.getCpf_Cnpj()))) {
                for (UsuarioWeb usuarioWeb : pessoa.getUsuariosWeb()) {
                    String senhaCript = usuarioWeb.getPassword();
                    if (usuarioWeb.isPasswordTemporary())
                        senhaCript = Seguranca.bCryptPasswordEncoder.encode(usuarioWeb.getPassword());
                    if (Seguranca.bCryptPasswordEncoder.matches(senha, senhaCript)) {
                        this.usuario = new WSUsuario(usuarioWeb);
                        this.usuario.setSenha(senhaCript);
                        break;
                    }
                }
            } else {
                for (UsuarioWeb usuarioWeb : pessoa.getUsuariosWeb()) {
                    if (cpfCnpj.equals(usuarioWeb.getEmail())) {
                        this.usuario = new WSUsuario(usuarioWeb);
                        break;
                    }
                }
            }
        }
    }

    public WSPessoa(Pessoa pessoa) {
        this.id = pessoa.getId();
        telefones = Lists.newArrayList();
        enderecos = Lists.newArrayList();
        this.nome = pessoa.getNome();
        this.cpfCnpj = pessoa.getCpf_Cnpj();
        if (pessoa.getUsuariosWeb() != null && !pessoa.getUsuariosWeb().isEmpty()) {
            this.email = pessoa.getUsuariosWeb().get(0).getEmail();
        } else {
            this.email = pessoa.getEmail();
        }

        this.homePage = pessoa.getHomePage();
        this.rgInscricao = pessoa.getRg_InscricaoEstadual();
        this.orgaoEmissor = pessoa.getOrgaoExpedidor();
        if (pessoa instanceof PessoaFisica) {
            tipo = TipoCadastroDoctoOficial.PESSOAFISICA.name();
            PessoaFisica pf = (PessoaFisica) pessoa;
            if (pf.getDataNascimento() != null) {
                this.nascimento = DataUtil.dataSemHorario(pf.getDataNascimento());
            }
            this.sexo = pf.getSexo();
            this.nomeMae = pf.getMae();
            this.nivelEscolaridade = pf.getNivelEscolaridade();
            if (pf.getProfissao() != null) {
                this.profissao = pf.getProfissao().getDescricao();
            }
            if (pf.getArquivo() != null) {
                foto = new WSFotoDaPessoa(pf.getArquivo());
            }
        } else {
            tipo = TipoCadastroDoctoOficial.PESSOAJURIDICA.name();
            nomeFantasia = ((PessoaJuridica) pessoa).getNomeFantasia();
        }
        for (Telefone telefone : pessoa.getTelefones()) {
            WSTelefoneDaPessoa wsTelefoneDaPessoa = new WSTelefoneDaPessoa(telefone);
            if (telefone.equals(pessoa.getTelefonePrincipal())) {
                wsTelefoneDaPessoa.setPrincipal(true);
            }
            telefones.add(wsTelefoneDaPessoa);
        }
        for (EnderecoCorreio endereco : pessoa.getEnderecos()) {
            enderecos.add(new WSEnderecoDaPessoa(endereco));
        }
        if (!pessoa.getUsuariosWeb().isEmpty()) {
            this.usuario = new WSUsuario(pessoa.getUsuariosWeb().get(0));
        }
        this.situacaoCadastralPessoa = pessoa.getSituacaoCadastralPessoa();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public NivelEscolaridade getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(NivelEscolaridade nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getRgInscricao() {
        return rgInscricao;
    }

    public void setRgInscricao(String rgInscricao) {
        this.rgInscricao = rgInscricao;
    }

    public String getOrgaoEmissor() {
        return orgaoEmissor;
    }

    public void setOrgaoEmissor(String orgaoEmissor) {
        this.orgaoEmissor = orgaoEmissor;
    }

    public List<WSTelefoneDaPessoa> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<WSTelefoneDaPessoa> telefones) {
        this.telefones = telefones;
    }

    public List<WSEnderecoDaPessoa> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<WSEnderecoDaPessoa> enderecos) {
        this.enderecos = enderecos;
    }

    public WSUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(WSUsuario usuario) {
        this.usuario = usuario;
    }

    public WSFotoDaPessoa getFoto() {
        return foto;
    }

    public void setFoto(WSFotoDaPessoa foto) {
        this.foto = foto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public List<WSVinculoFP> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<WSVinculoFP> vinculos) {
        this.vinculos = vinculos;
    }

    public SituacaoCadastralPessoa getSituacaoCadastralPessoa() {
        return situacaoCadastralPessoa;
    }

    public void setSituacaoCadastralPessoa(SituacaoCadastralPessoa situacaoCadastralPessoa) {
        this.situacaoCadastralPessoa = situacaoCadastralPessoa;
    }

    public BigDecimal getProporcaoSocio() {
        return proporcaoSocio;
    }

    public void setProporcaoSocio(BigDecimal proporcaoSocio) {
        this.proporcaoSocio = proporcaoSocio;
    }

    public Boolean getOtt() {
        return ott;
    }

    public void setOtt(Boolean ott) {
        this.ott = ott;
    }
}
