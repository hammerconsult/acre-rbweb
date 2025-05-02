/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.SituacaoAtoLegal;
import br.com.webpublico.enums.TipoAtoLegal;
import br.com.webpublico.enums.TipoAtoLegalUnidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Etiqueta("Ato Legal")
public class AtoLegal extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Propósito Ato Legal")
    private PropositoAtoLegal propositoAtoLegal;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ato Legal")
    private TipoAtoLegal tipoAtoLegal;
    @ManyToOne
    @Etiqueta("Esfera de Governo")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private EsferaGoverno esferaGoverno;
    @Etiqueta("Veículo de Publicação")
    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Número")
    private String numero;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Sanção")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPromulgacao;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data de Publicação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPublicacao;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Nome do Ato Legal")
    private String nome;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Exercício")
    @Pesquisavel
    private Exercicio exercicio;
    @Etiqueta("Data de Validade")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataValidade;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @OneToOne(mappedBy = "atoLegal", cascade = CascadeType.ALL, orphanRemoval = true)
    private AtoLegalORC atoLegalORC;
    @OneToOne(mappedBy = "atoLegal", cascade = CascadeType.ALL, orphanRemoval = true)
    private AtoDeComissao atoDeComissao;
    @ManyToOne
    private AtoLegal superior;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEnvio;
    @Etiqueta("Nº. Publicação ")
    private Integer numeroPublicacao;
    @Etiqueta("Fundamentação Legal")
    private String fundamentacaoLegal;
    @Etiqueta("Descrição Conforme Diário Oficial")
    private String descricaoConformeDo;
    @Etiqueta("Data de Emissão")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataEmissao;
    private String migracaoChave;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Unidade Externa")
    private UnidadeExterna unidadeExterna;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Ato Legal Unidade")
    private TipoAtoLegalUnidade tipoAtoLegalUnidade;
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoAtoLegal situacaoAtoLegal;
    @OneToMany(mappedBy = "atoLegal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtoLegalRepublicacao> republicacoes;

    public AtoLegal() {
        super();
        republicacoes = Lists.newArrayList();
    }

    public AtoLegal(Long id, String numero, String nome) {
        this.id = id;
        this.numero = numero;
        this.nome = nome;
    }

    public AtoLegal(EsferaGoverno esferaGoverno, String numero, Date dataPromulgacao, Date dataPublicacao, Exercicio exercicio, String nome, Arquivo arquivo, PropositoAtoLegal propositoAtoLegal, TipoAtoLegal tipoAtoLegal) {
        super();
        this.esferaGoverno = esferaGoverno;
        this.numero = numero;
        this.dataPromulgacao = dataPromulgacao;
        this.dataPublicacao = dataPublicacao;
        this.exercicio = exercicio;
        this.nome = nome;
        this.arquivo = arquivo;
        this.propositoAtoLegal = propositoAtoLegal;
        this.tipoAtoLegal = tipoAtoLegal;
    }

    public AtoLegal(Exercicio exercicio, Integer numeroPublicacao, String numero, Date dataPromulgacao, Date dataPublicacao, String nome, UnidadeOrganizacional unidadeOrganizacional, EsferaGoverno esferaGoverno, PropositoAtoLegal propositoAtoLegal, TipoAtoLegal tipoAtoLegal, VeiculoDePublicacao veiculoDePublicacao) {
        this.exercicio = exercicio;
        this.numeroPublicacao = numeroPublicacao;
        this.numero = numero;
        this.dataPromulgacao = dataPromulgacao;
        this.dataPublicacao = dataPublicacao;
        this.nome = nome;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.esferaGoverno = esferaGoverno;
        this.propositoAtoLegal = propositoAtoLegal;
        this.tipoAtoLegal = tipoAtoLegal;
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Date getDataPromulgacao() {
        return dataPromulgacao;
    }

    public void setDataPromulgacao(Date dataPromulgacao) {
        this.dataPromulgacao = dataPromulgacao;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public EsferaGoverno getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public String getNomeCortado() {
        if (this.nome != null && this.nome.length() > 45) {
            return this.nome.substring(0, 44);
        }
        return this.nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (numero != null) {
            this.numero = numero.toUpperCase();
        }
        this.numero = numero;
    }

    public PropositoAtoLegal getPropositoAtoLegal() {
        return propositoAtoLegal;
    }

    public void setPropositoAtoLegal(PropositoAtoLegal propositoAtoLegal) {
        this.propositoAtoLegal = propositoAtoLegal;
    }

    public TipoAtoLegal getTipoAtoLegal() {
        return tipoAtoLegal;
    }

    public void setTipoAtoLegal(TipoAtoLegal tipoAtoLegal) {
        this.tipoAtoLegal = tipoAtoLegal;
    }

    public AtoLegalORC getAtoLegalORC() {
        return atoLegalORC;
    }

    public void setAtoLegalORC(AtoLegalORC atoLegalORC) {
        this.atoLegalORC = atoLegalORC;
    }

    public AtoDeComissao getAtoDeComissao() {
        return atoDeComissao;
    }

    public void setAtoDeComissao(AtoDeComissao atoDeComissao) {
        this.atoDeComissao = atoDeComissao;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public AtoLegal getSuperior() {
        return superior;
    }

    public void setSuperior(AtoLegal superior) {
        this.superior = superior;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Integer getNumeroPublicacao() {
        return numeroPublicacao;
    }

    public void setNumeroPublicacao(Integer numeroPublicacao) {
        this.numeroPublicacao = numeroPublicacao;
    }

    public String getFundamentacaoLegal() {
        if (fundamentacaoLegal == null || fundamentacaoLegal.trim().length() <= 0) {
            return "Não foi informada a fundamentação legal para este ato legal.";
        }
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal.toUpperCase();
    }

    public String getDescricaoConformeDo() {
        if (descricaoConformeDo == null || descricaoConformeDo.trim().length() <= 0) {
            return "Não foi informada a descriçao conforme diário oficial para este ato legal.";
        }
        return descricaoConformeDo;
    }

    public void setDescricaoConformeDo(String descricaoConformeDo) {
        this.descricaoConformeDo = descricaoConformeDo.toUpperCase();
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public TipoAtoLegalUnidade getTipoAtoLegalUnidade() {
        return tipoAtoLegalUnidade;
    }

    public void setTipoAtoLegalUnidade(TipoAtoLegalUnidade tipoAtoLegalUnidade) {
        this.tipoAtoLegalUnidade = tipoAtoLegalUnidade;
    }


    public String toString() {
        String s = "";
        SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
        SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");

        if (propositoAtoLegal == null) {
            if (numero != null && numero.equals("")) {
                s += " Nº " + numero;
            }
            if ((nome != null) && (!nome.trim().equals(""))) {
                s += " - " + nome;
            }
        } else if (propositoAtoLegal.equals(PropositoAtoLegal.ATO_DE_PESSOAL) || propositoAtoLegal.equals(PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO)) {
            if (tipoAtoLegal != null && !tipoAtoLegal.getDescricao().equals("")) {
                s += tipoAtoLegal.getDescricao().toUpperCase();
            }
            if (numero != null && !numero.equals("")) {
                s += " Nº " + numero.toUpperCase();
                if (dataEmissao != null) {
                    Integer mes = new Integer(formatarMes.format(dataEmissao));
                    s += " DE " + DataUtil.getDataFormatada(dataEmissao, "dd")
                        + " DE " + DataUtil.getDescricaoMes(mes).toUpperCase()
                        + " DE " + DataUtil.getDataFormatada(dataEmissao, "yyyy");
                }
            }
            if (dataEmissao != null) {
                s += " Emitido em: " + dataFormatada.format(dataEmissao);
            }
            if (dataPromulgacao != null) {
                s += " Promulgado em: " + dataFormatada.format(dataPromulgacao);
            }
            if (dataPublicacao != null) {
                s += ", Publicado em: " + dataFormatada.format(dataPublicacao);
            }
            if (unidadeOrganizacional != null) {
                s += ", Órgão: " + unidadeOrganizacional.getDescricao();
            }
            if (unidadeExterna != null) {
                s += ", Unid. Ext.: " + unidadeExterna.getPessoaJuridica().getNome();
            }
        } else {
            if (numero != null && !numero.equals("")) {
                s += " Nº: " + numero;
            }
            if ((nome != null && !nome.trim().equals(""))) {
                s += " - " + nome;
            }
        }
        return s;
    }

    public String getTipoNumeroAno() {
        String retorno = "";
        if (this.tipoAtoLegal != null) {
            retorno += this.tipoAtoLegal.getDescricao().trim();
        }
        if (!Strings.isNullOrEmpty(this.numero)) {
            retorno += " - " + this.numero;
        }
        if (this.exercicio != null) {
            retorno += "/" + this.exercicio.getAno();
        }
        return retorno;
    }

    public String getTipoNumeroAnoPPA() {
        String retorno = "";
        if (this.tipoAtoLegal != null) {
            if (TipoAtoLegal.LEI_ORDINARIA.equals(this.tipoAtoLegal)) {
                retorno += "L.O. ";
            } else if (TipoAtoLegal.LEI_COMPLEMENTAR.equals(this.tipoAtoLegal)) {
                retorno += "L.C. ";
            } else {
                retorno += this.tipoAtoLegal.getDescricao().trim();
            }
        }
        if (!Strings.isNullOrEmpty(this.numero)) {
            retorno += "Nº " + this.numero;
        }
        if (this.exercicio != null) {
            retorno += "/" + this.exercicio.getAno();
        }
        return retorno;
    }

    public String toStringAtoLegalConvenio() {
        return numero + " - " + nome + ", Data de Publicação " + DataUtil.getDataFormatada(dataPublicacao);
    }

    public boolean isTipoAtoLegalOficio() {
        return TipoAtoLegal.OFICIO.equals(this.getTipoAtoLegal());
    }

    public boolean isPropositoAtoLegalComissao() {
        return PropositoAtoLegal.COMISSAO.equals(propositoAtoLegal);
    }

    public boolean temAtoDeComissao() {
        return atoDeComissao != null;
    }

    public boolean isPropositoAtoLegalAlteracaoOrcamentaria() {
        return PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.equals(propositoAtoLegal);
    }

    public boolean isTipoAtoLegalAlteracaoOrcamentaria() {
        return TipoAtoLegal.DECRETO.equals(tipoAtoLegal);
    }

    public boolean isTipoAtoLegalUnidadeInterna() {
        return TipoAtoLegalUnidade.INTERNA.equals(tipoAtoLegalUnidade);
    }

    public boolean isTipoAtoLegalUnidadeExterna() {
        return TipoAtoLegalUnidade.EXTERNA.equals(tipoAtoLegalUnidade);
    }

    public boolean isPropositoAtoLegalAtoDeCargo() {
        return PropositoAtoLegal.ATO_DE_CARGO.equals(propositoAtoLegal);
    }

    public boolean isPropositoAtoLegalAtoDePessoal() {
        return PropositoAtoLegal.ATO_DE_PESSOAL.equals(propositoAtoLegal);
    }

    public boolean isPropositoAtoLegalConvenio() {
        return PropositoAtoLegal.CONVENIO.equals(propositoAtoLegal);
    }

    public boolean isPropositoAtoLegalOutros() {
        return PropositoAtoLegal.OUTROS.equals(propositoAtoLegal);
    }

    public boolean isTipoAtoLegalProjetoDeLei() {
        return TipoAtoLegal.PROJETO_DE_LEI.equals(tipoAtoLegal);
    }

    public boolean isTipoAtoLegalEmenda() {
        return TipoAtoLegal.EMENDA.equals(tipoAtoLegal);
    }

    public boolean isTipoAtoLegalDecreto() {
        return TipoAtoLegal.DECRETO.equals(tipoAtoLegal);
    }

    public SituacaoAtoLegal getSituacaoAtoLegal() {
        return situacaoAtoLegal;
    }

    public void setSituacaoAtoLegal(SituacaoAtoLegal situacaoAtoLegal) {
        this.situacaoAtoLegal = situacaoAtoLegal;
    }

    public List<AtoLegalRepublicacao> getRepublicacoes() {
        return republicacoes;
    }

    public void setRepublicacoes(List<AtoLegalRepublicacao> republicacoes) {
        this.republicacoes = republicacoes;
    }
}
