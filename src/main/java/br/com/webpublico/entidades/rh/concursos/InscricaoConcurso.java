package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.RG;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Buzatto on 26/08/2015.
 */
@Entity
@Audited
@Etiqueta(value = "Inscrição do Concurso")
@GrupoDiagrama(nome = "Concursos")
public class InscricaoConcurso extends SuperEntidade implements Serializable, Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Concurso")
    private Concurso concurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data")
    private Date data;

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Número")
    private Integer numero;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Nome")
    private String nome;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data de Nascimento")
    private Date dataNascimento;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Sexo")
    private Sexo sexo;

    @Pesquisavel
    @Tabelavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta(value = "RG")
    private RG rg;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "CPF")
    private String cpf;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Deficiente Físico?")
    private Boolean deficienteFisico;

    @Pesquisavel
    @Tabelavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta(value = "Endereço Correio")
    private EnderecoCorreio enderecoCorreio;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "E Mail")
    private String email;

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Telefone")
    private String telefone;

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Alternativo")
    private String telefoneAlternativo;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta(value = "Cargo")
    private CargoConcurso cargoConcurso;

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Pagamento Efetuado?")
    private Boolean pagamentoEfetuado;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta(value = "Atua em julgamento como jurado?")
    private Boolean jurado;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta(value = "Participa de eleições como mesário?")
    private Boolean mesario;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta(value = "Doador de sangue?")
    private Boolean doador;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta(value = "Já exerceu cargo público?")
    private Boolean cargoPublico;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String nomeMae;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String celular;


    public InscricaoConcurso() {
        super();
        setRg(new RG());
        setEnderecoCorreio(new EnderecoCorreio());
        setDeficienteFisico(Boolean.FALSE);
        setPagamentoEfetuado(Boolean.FALSE);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
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

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public RG getRg() {
        return rg;
    }

    public void setRg(RG rg) {
        this.rg = rg;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefoneAlternativo() {
        return telefoneAlternativo;
    }

    public void setTelefoneAlternativo(String telefoneAlternativo) {
        this.telefoneAlternativo = telefoneAlternativo;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getDeficienteFisico() {
        return deficienteFisico;
    }

    public void setDeficienteFisico(Boolean deficienteFisico) {
        this.deficienteFisico = deficienteFisico;
    }

    public EnderecoCorreio getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(EnderecoCorreio enderecoCorreio) {
        this.enderecoCorreio = enderecoCorreio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public CargoConcurso getCargoConcurso() {
        return cargoConcurso;
    }

    public void setCargoConcurso(CargoConcurso cargoConcurso) {
        this.cargoConcurso = cargoConcurso;
    }

    public Boolean getPagamentoEfetuado() {
        return pagamentoEfetuado;
    }

    public void setPagamentoEfetuado(Boolean pagamentoEfetuado) {
        this.pagamentoEfetuado = pagamentoEfetuado;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Boolean getJurado() {
        if (jurado == null) {
            return Boolean.FALSE;
        }
        return jurado;
    }

    public void setJurado(Boolean jurado) {
        this.jurado = jurado;
    }

    public Boolean getMesario() {
        if (mesario == null) {
            return Boolean.FALSE;
        }
        return mesario;
    }

    public void setMesario(Boolean mesario) {
        this.mesario = mesario;
    }

    public Boolean getDoador() {
        if (doador == null) {
            return Boolean.FALSE;
        }
        return doador;
    }

    public void setDoador(Boolean doador) {
        this.doador = doador;
    }

    public Boolean getCargoPublico() {
        if (cargoPublico == null) {
            return Boolean.FALSE;
        }
        return cargoPublico;
    }

    public void setCargoPublico(Boolean cargoPublico) {
        this.cargoPublico = cargoPublico;
    }

    @Override
    public String toString() {
        return this.numero + " - " + this.nome + " - " + this.cpf;
    }

    @Override
    public int compareTo(Object o) {
        return this.getNumero().compareTo(((InscricaoConcurso) o).getNumero());
    }

    public Telefone getTelefonePrincipalAsEntidadeTelefone() {
        if (getTelefone() == null || getTelefone().trim().isEmpty()) {
            return null;
        }
        Telefone tf = new Telefone();
        tf.setPrincipal(true);
        tf.setDataRegistro(UtilRH.getDataOperacao());
        tf.setTipoFone(TipoTelefone.OUTROS);
        tf.setTelefone(getTelefone());
        return tf;
    }

    public Telefone getTelefoneAsEntidadeTelefone(String telefone, TipoTelefone tipo) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return null;
        }
        Telefone tf = new Telefone();
        tf.setPrincipal(true);
        tf.setDataRegistro(UtilRH.getDataOperacao());
        tf.setTipoFone(tipo);
        tf.setTelefone(telefone);
        return tf;
    }

    public Telefone getTelefoneAlternativoAsEntidadeTelefone() {
        if (getTelefoneAlternativo() == null || getTelefoneAlternativo().trim().isEmpty()) {
            return null;
        }
        Telefone tf = new Telefone();
        tf.setPrincipal(true);
        tf.setDataRegistro(UtilRH.getDataOperacao());
        tf.setTipoFone(TipoTelefone.OUTROS);
        tf.setTelefone(getTelefoneAlternativo());
        return tf;
    }

    public boolean temConcurso() {
        return concurso != null;
    }
}
