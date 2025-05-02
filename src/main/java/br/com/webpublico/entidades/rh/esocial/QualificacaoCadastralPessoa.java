package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.esocial.SituacaoQualificacaoCadastral;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 17/07/17.
 */
@Entity
@Audited
@Table(name = "QUALIFICACAOCADPESSOA")
public class QualificacaoCadastralPessoa extends SuperEntidade implements Comparable<QualificacaoCadastralPessoa> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private QualificacaoCadastral qualificacaoCadastral;
    @Etiqueta("CPF")
    private String cpf;
    @Etiqueta("NIS")
    private String nis;
    @Etiqueta("Nome do Funcionário")
    private String nomeFuncionario;
    @Etiqueta("Data de Nascimento")
    private Date dataNascimento;
    @Enumerated(EnumType.STRING)
    private SituacaoQualificacaoCadastral processadoRejeitado;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "qualificacaoCadPessoa")
    private List<RetornoQualificacaoCadastral> retornos;
    @ManyToOne
    private Pessoa pessoa;

    public QualificacaoCadastralPessoa() {
        super();
        retornos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QualificacaoCadastral getQualificacaoCadastral() {
        return qualificacaoCadastral;
    }

    public void setQualificacaoCadastral(QualificacaoCadastral qualificacaoCadastral) {
        this.qualificacaoCadastral = qualificacaoCadastral;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCpfComPontuacao() {
        return cpf.length() == 11 ? cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11) : cpf;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeEmpregado) {
        this.nomeFuncionario = nomeEmpregado;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public SituacaoQualificacaoCadastral getProcessadoRejeitado() {
        return processadoRejeitado;
    }

    public void setProcessadoRejeitado(SituacaoQualificacaoCadastral processadoRejeitado) {
        this.processadoRejeitado = processadoRejeitado;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<RetornoQualificacaoCadastral> getRetornos() {
        return retornos;
    }

    public void setRetornos(List<RetornoQualificacaoCadastral> retornos) {
        this.retornos = retornos;
    }

    @Override
    public int compareTo(QualificacaoCadastralPessoa o) {

        return this.nomeFuncionario.compareTo(o.getNomeFuncionario());
    }
}
