package br.com.webpublico.ws.model;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.UsuarioSaud;
import br.com.webpublico.entidades.UsuarioSaudDocumento;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.util.ArquivoUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class WsUsuarioSaud implements Serializable {

    private Long id;
    private ArquivoDTO foto;
    private String cpf;
    private String nome;
    private Date dataNascimento;
    private BigDecimal renda;
    private String telefone;
    private String email;
    private String cep;
    private String bairro;
    private String logradouro;
    private String numero;
    private String uf;
    private String localidade;
    private List<WsUsuarioSaudDocumento> documentos;
    private String mensagem;

    public WsUsuarioSaud() {
        this.documentos = Lists.newArrayList();
    }

    public WsUsuarioSaud(UsuarioSaud usuarioSaud, ArquivoFacade arquivoFacade) {
        this();
        this.id = usuarioSaud.getId();
        this.foto = ArquivoUtil.converterArquivoToArquivoDTO(usuarioSaud.getFoto(), arquivoFacade);
        this.cpf = usuarioSaud.getCpf();
        this.nome = usuarioSaud.getNome();
        this.dataNascimento = usuarioSaud.getDataNascimento();
        this.renda = usuarioSaud.getRenda();
        this.telefone = usuarioSaud.getTelefone();
        this.email = usuarioSaud.getEmail();
        this.cep = usuarioSaud.getEndereco().getCep();
        this.bairro = usuarioSaud.getEndereco().getBairro();
        this.logradouro = usuarioSaud.getEndereco().getLogradouro();
        this.numero = usuarioSaud.getEndereco().getNumero();
        this.uf = usuarioSaud.getEndereco().getCidade().getUf().getUf();
        this.localidade = usuarioSaud.getEndereco().getCidade().getNome();
        for (UsuarioSaudDocumento documento : usuarioSaud.getDocumentos()) {
            this.documentos.add(new WsUsuarioSaudDocumento(documento, arquivoFacade));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoDTO getFoto() {
        return foto;
    }

    public void setFoto(ArquivoDTO foto) {
        this.foto = foto;
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

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public List<WsUsuarioSaudDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<WsUsuarioSaudDocumento> documentos) {
        this.documentos = documentos;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
