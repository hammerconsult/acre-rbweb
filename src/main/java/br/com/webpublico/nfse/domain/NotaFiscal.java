package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.nfse.domain.dtos.PrestadorServicoNfseDTO;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;


/**
 * A NotaFiscal.
 */
@Entity
@Audited
@Etiqueta(value = "Nota Fiscal")
public class NotaFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(cascade = CascadeType.ALL)
    private Rps rps;
    private String emails;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Emissão")
    private Date emissao;
    @Obrigatorio
    @NotNull
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código de Verificação")
    private String codigoVerificacao;
    @Etiqueta("Declaração Prestação de Serviço")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private DeclaracaoPrestacaoServico declaracaoPrestacaoServico;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Prestador")
    private CadastroEconomico prestador;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tomador")
    private TomadorServicoNfse tomador;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descriminação do Serviço")
    private String descriminacaoServico;
    @Etiqueta("Chave de Acesso")
    private String chaveAcesso;
    @Etiqueta("Homologação")
    private Boolean homologacao = false;
    @Etiqueta("Informações Adicionais")
    private String informacoesAdicionais;
    private Boolean substitutoTributario;
    private Boolean enviouPorEmail;

    public NotaFiscal() {
        enviouPorEmail = false;
    }

    public NotaFiscal(Long id, Long numero, Date emissao, String codigoVerificacao, CadastroEconomico prestador,
                      TomadorServicoNfse tomador, String descriminacaoServico) {
        this.id = id;
        this.numero = numero;
        this.emissao = emissao;
        this.codigoVerificacao = codigoVerificacao;
        this.prestador = prestador;
        this.tomador = tomador;
        this.descriminacaoServico = descriminacaoServico;
    }

    public NotaFiscal(Long id) {
        setId(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao != null ? codigoVerificacao.toUpperCase() : " ";
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public DeclaracaoPrestacaoServico getDeclaracaoPrestacaoServico() {
        return declaracaoPrestacaoServico;
    }

    public void setDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        this.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }


    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public Rps getRps() {
        return rps;
    }

    public void setRps(Rps rps) {
        this.rps = rps;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public TomadorServicoNfse getTomador() {
        return tomador;
    }

    public void setTomador(TomadorServicoNfse tomador) {
        this.tomador = tomador;
    }

    public String getDescriminacaoServico() {
        return descriminacaoServico;
    }

    public void setDescriminacaoServico(String descriminacaoServico) {
        this.descriminacaoServico = descriminacaoServico;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(String informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }

    public Boolean getEnviouPorEmail() {
        return enviouPorEmail;
    }

    public void setEnviouPorEmail(Boolean enviouPorEmail) {
        this.enviouPorEmail = enviouPorEmail;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean tomadorJaCadastrado() {
        return this.getDeclaracaoPrestacaoServico() != null &&
            this.getTomador() != null &&
            this.getTomador() != null &&
            this.getTomador().getId() != null;
    }

    public boolean intermediarioJaCadastrado() {
        return this.getDeclaracaoPrestacaoServico() != null &&
            this.getDeclaracaoPrestacaoServico().getIntermediario() != null &&
            this.getDeclaracaoPrestacaoServico().getIntermediario() != null &&
            this.getDeclaracaoPrestacaoServico().getIntermediario().getId() != null;
    }

    public Boolean getSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(Boolean substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public String gerarInformacoesAdicionais(ConfiguracaoNfse configuracaoNfse, Date vencimento) {
        ItemDeclaracaoServico itemDeclaracaoServico = declaracaoPrestacaoServico.getItens().get(0);

        String informacoesAdicionais = "";
        informacoesAdicionais += " - Natureza Operação: " + declaracaoPrestacaoServico.getNaturezaOperacao().getDescricao() + ".\n";
        if (!Exigibilidade.NAO_INCIDENCIA.equals(declaracaoPrestacaoServico.getNaturezaOperacao()) &&
            !Exigibilidade.IMUNIDADE.equals(declaracaoPrestacaoServico.getNaturezaOperacao()) &&
            !Exigibilidade.ISENCAO.equals(declaracaoPrestacaoServico.getNaturezaOperacao())) {
            if (!Exigibilidade.TRIBUTACAO_FORA_MUNICIPIO.equals(declaracaoPrestacaoServico.getNaturezaOperacao()) &&
                !Exigibilidade.EXPORTACAO.equals(declaracaoPrestacaoServico.getNaturezaOperacao()))
                informacoesAdicionais += " - ISS de responsabilidade do: " + responsabilidadeIssqn() + ".\n";
            if (!itemDeclaracaoServico.getServico().getPermiteRecolhimentoFora()) {
                informacoesAdicionais += " - Serviço tributado no município: " + (configuracaoNfse.getCidade() != null ? configuracaoNfse.getCidade().toString() : "") + ".\n";
            } else {
                if (Boolean.TRUE.equals(itemDeclaracaoServico.getPrestadoNoPais())) {
                    informacoesAdicionais += " - Serviço tributado no município: " + (itemDeclaracaoServico.getMunicipio() != null ? itemDeclaracaoServico.getMunicipio().toString() : "") + ".\n";
                } else {
                    informacoesAdicionais += " - Serviço tributado no país: " + (itemDeclaracaoServico.getPais() != null ? itemDeclaracaoServico.getPais().getNome() : "") + ".\n";
                }
            }
            if (!Exigibilidade.TRIBUTACAO_FORA_MUNICIPIO.equals(declaracaoPrestacaoServico.getNaturezaOperacao()) &&
                !Exigibilidade.EXPORTACAO.equals(declaracaoPrestacaoServico.getNaturezaOperacao()) &&
                !Exigibilidade.SIMPLES_NACIONAL.equals(declaracaoPrestacaoServico.getNaturezaOperacao()))
                informacoesAdicionais += " - Data de vencimento do ISS desta NFSE: " + DataUtil.getDataFormatada(vencimento) + ".\n";
        }

        if (TipoIssqn.SIMPLES_NACIONAL.equals(declaracaoPrestacaoServico.getDadosPessoaisPrestador().getTipoIssqn()) && !declaracaoPrestacaoServico.getIssRetido()) {
            informacoesAdicionais += " - ISSQN a ser calculado pela Tabela da LC 123/Simples Nacional e pago na guia DAS/Simples.\n ";
        }

        if (rps != null) {
            informacoesAdicionais += " - RPS n°: " + rps.getNumero() + ".\n";
            informacoesAdicionais += " - Data de Emissão RPS: " + (rps != null ? DataUtil.getDataFormatada(rps.getDataEmissao()) : "") + ".\n";
        }

        informacoesAdicionais += " - Código Verificação: " + codigoVerificacao + ".\n";

        if (TipoIssqn.SIMPLES_NACIONAL.equals(declaracaoPrestacaoServico.getDadosPessoaisPrestador().getTipoIssqn())) {
            informacoesAdicionais += " - DOCUMENTO EMITIDO POR ME OU EPP OPTANTE PELO SIMPLES NACIONAL.\n";
        }

        if (declaracaoPrestacaoServico.getNaturezaOperacao().equals(Exigibilidade.NAO_INCIDENCIA)) {
            informacoesAdicionais += " - Operação não gera valor de ISSQN. Contribuinte Fixo, MEI ou Estimado.\n";
        }

        if (declaracaoPrestacaoServico.getNaturezaOperacao().equals(Exigibilidade.IMUNIDADE)) {
            informacoesAdicionais += " - Operação não gera valor de ISSQN. Contribuinte no regime de recolhimento imune.\n";
        }

        if (declaracaoPrestacaoServico.getNaturezaOperacao().equals(Exigibilidade.ISENCAO)) {
            informacoesAdicionais += " - Operação não gera valor de ISSQN. Contribuinte no regime de recolhimento isento.\n";
        }

        if (declaracaoPrestacaoServico.getNaturezaOperacao().equals(Exigibilidade.RETENCAO) && Boolean.TRUE.equals(substitutoTributario)) {
            informacoesAdicionais += " - Operação com retenção de ISS por Substituição Tributária.\n";
        }

        if (TipoIssqn.SUBLIMITE_ULTRAPASSADO.equals(declaracaoPrestacaoServico.getDadosPessoaisPrestador().getTipoIssqn())) {
            informacoesAdicionais += " - ESTABELECIMENTO IMPEDIDO DE RECOLHER O ICMS/ISS PELO SIMPLES NACIONAL,\n" +
                " NOS TERMOS DO § 1º DO ART. 20 DA LEI COMPLEMENTAR Nº 123, DE 2006.\n";
        }
        return informacoesAdicionais;
    }

    private String responsabilidadeIssqn() {
        if (declaracaoPrestacaoServico.getNaturezaOperacao().equals(Exigibilidade.NAO_INCIDENCIA)) {
            return "Operação não gera valor de ISSQN. Contribuinte MEI, Fixo Anual, Estimado, Imune ou Isento.";
        } else if (Boolean.TRUE.equals(declaracaoPrestacaoServico.getIssRetido())) {
            return "Tomador de Serviço";
        } else {
            return "Prestador de Serviço";
        }
    }

    private PrestadorServicoNfseDTO getPrestadorDTO() {
        PrestadorServicoNfseDTO prestador = new PrestadorServicoNfseDTO();
        prestador.setId(this.prestador.getId());
        prestador.setInscricaoMunicipal(this.prestador.getInscricaoCadastral());
        PessoaNfseDTO pessoaPrestador = new PessoaNfseDTO();
        if (declaracaoPrestacaoServico.getDadosPessoaisPrestador() != null)
            pessoaPrestador.setDadosPessoais(declaracaoPrestacaoServico.getDadosPessoaisPrestador().toNfseDto());
        prestador.setPessoa(pessoaPrestador);
        return prestador;
    }

    @Override
    public String toString() {
        return "Número: " + numero + ", Emissão: " + DataUtil.getDataFormatada(emissao) + ", Prestador: " + (prestador != null ? prestador : " ")+ ", Tomador: " + (tomador != null ? tomador : " ");
    }

    public Boolean getHomologacao() {
        return homologacao != null ? homologacao : false;
    }

    public void setHomologacao(Boolean homologacao) {
        this.homologacao = homologacao;
    }
}
