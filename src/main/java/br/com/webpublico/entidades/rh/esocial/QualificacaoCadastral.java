package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 17/07/17.
 */
@Entity
@Audited
@Etiqueta("Qualificação Cadastral")
public class QualificacaoCadastral extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Envio")
    @Obrigatorio
    private Date dataEnvio;
    private String cnpjMunicipio;
    private String cpfTransmissor;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Responsavel")
    private PessoaFisica responsavel;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Entidade")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "qualificacaoCadastral")
    private List<QualificacaoCadastralPessoa> pessoas;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public QualificacaoCadastral() {
        super();
        pessoas = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getCnpjMunicipio() {
        return cnpjMunicipio;
    }

    public void setCnpjMunicipio(String cnpjMunicipio) {
        this.cnpjMunicipio = cnpjMunicipio;
    }

    public String getCpfTransmissor() {
        return cpfTransmissor;
    }

    public void setCpfTransmissor(String cpfTransmissor) {
        this.cpfTransmissor = cpfTransmissor;
    }


    public List<QualificacaoCadastralPessoa> getPessoas() {
        Collections.sort(pessoas);
        return pessoas;
    }

    public void setPessoas(List<QualificacaoCadastralPessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
