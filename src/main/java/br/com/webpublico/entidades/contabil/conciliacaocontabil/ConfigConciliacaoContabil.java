package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.ContaContabil;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.conciliacaocontabil.TipoConfigConciliacaoContabil;
import br.com.webpublico.enums.conciliacaocontabil.TipoMovimentoSaldo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Configuração de Conciliação Contábil")
public class ConfigConciliacaoContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Totalizador")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoConfigConciliacaoContabil totalizador;
    @Etiqueta("Buscar por?")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoMovimentoSaldo tipoMovimentoSaldo;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Inicial")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Date dataInicial;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final")
    @Pesquisavel
    @Tabelavel
    private Date dataFinal;
    @Etiqueta("Ordem")
    @Obrigatorio
    private Integer ordem;
    @Etiqueta("Quadro")
    @Obrigatorio
    private Integer quadro;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilConta> contas;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilSubConta> contasFinanceiras;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilTipoContaReceita> tiposDeContaDeReceita;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilNaturezaTipoGrupoMaterial> naturezasTipoGrupoMaterial;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilGrupoBem> gruposBens;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilNaturezaDividaPublica> naturezasDaDividaPublica;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilPassivoAtuarial> passivosAtuariais;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilCategoriaOrcamentaria> categoriasOrcamentarias;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilGrupoMaterial> gruposMateriais;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilCategoriaDividaPublica> categoriasDaDividaPublica;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configConciliacaoContabil")
    private List<ConfigConciliacaoContabilRecPatrimonialXRecOrc> recPatrimoniaisXRecOrcs;

    public ConfigConciliacaoContabil() {
        contas = Lists.newArrayList();
        contasFinanceiras = Lists.newArrayList();
        tiposDeContaDeReceita = Lists.newArrayList();
        naturezasTipoGrupoMaterial = Lists.newArrayList();
        gruposBens = Lists.newArrayList();
        naturezasDaDividaPublica = Lists.newArrayList();
        passivosAtuariais = Lists.newArrayList();
        categoriasOrcamentarias = Lists.newArrayList();
        gruposMateriais = Lists.newArrayList();
        categoriasDaDividaPublica = Lists.newArrayList();
        recPatrimoniaisXRecOrcs = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public TipoConfigConciliacaoContabil getTotalizador() {
        return totalizador;
    }

    public void setTotalizador(TipoConfigConciliacaoContabil tipoConfigConciliacaoContabil) {
        this.totalizador = tipoConfigConciliacaoContabil;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getQuadro() {
        return quadro;
    }

    public void setQuadro(Integer quadro) {
        this.quadro = quadro;
    }

    public List<ConfigConciliacaoContabilConta> getContas() {
        return contas;
    }

    public void setContas(List<ConfigConciliacaoContabilConta> contas) {
        this.contas = contas;
    }

    public List<ConfigConciliacaoContabilConta> getContasContabeis() {
        List<ConfigConciliacaoContabilConta> retorno = Lists.newArrayList();
        for (ConfigConciliacaoContabilConta configConta : contas) {
            if (configConta.getConta() instanceof ContaContabil) {
                retorno.add(configConta);
            }
        }
        return retorno;
    }

    public List<ConfigConciliacaoContabilConta> getContasDeReceita() {
        List<ConfigConciliacaoContabilConta> retorno = Lists.newArrayList();
        for (ConfigConciliacaoContabilConta configConta : contas) {
            if (configConta.getConta() instanceof ContaReceita) {
                retorno.add(configConta);
            }
        }
        return retorno;
    }

    public List<ConfigConciliacaoContabilConta> getContasDeDespesa() {
        List<ConfigConciliacaoContabilConta> retorno = Lists.newArrayList();
        for (ConfigConciliacaoContabilConta configConta : contas) {
            if (configConta.getConta() instanceof ContaDespesa) {
                retorno.add(configConta);
            }
        }
        return retorno;
    }

    public List<ConfigConciliacaoContabilSubConta> getContasFinanceiras() {
        return contasFinanceiras;
    }

    public void setContasFinanceiras(List<ConfigConciliacaoContabilSubConta> contasFinanceiras) {
        this.contasFinanceiras = contasFinanceiras;
    }

    public List<ConfigConciliacaoContabilTipoContaReceita> getTiposDeContaDeReceita() {
        return tiposDeContaDeReceita;
    }

    public void setTiposDeContaDeReceita(List<ConfigConciliacaoContabilTipoContaReceita> tiposDeContaDeReceita) {
        this.tiposDeContaDeReceita = tiposDeContaDeReceita;
    }

    public List<ConfigConciliacaoContabilNaturezaTipoGrupoMaterial> getNaturezasTipoGrupoMaterial() {
        return naturezasTipoGrupoMaterial;
    }

    public void setNaturezasTipoGrupoMaterial(List<ConfigConciliacaoContabilNaturezaTipoGrupoMaterial> naturezasTipoGrupoMaterial) {
        this.naturezasTipoGrupoMaterial = naturezasTipoGrupoMaterial;
    }

    public TipoMovimentoSaldo getTipoMovimentoSaldo() {
        return tipoMovimentoSaldo;
    }

    public void setTipoMovimentoSaldo(TipoMovimentoSaldo tipoMovimentoSaldo) {
        this.tipoMovimentoSaldo = tipoMovimentoSaldo;
    }

    public List<ConfigConciliacaoContabilGrupoBem> getGruposBens() {
        return gruposBens;
    }

    public void setGruposBens(List<ConfigConciliacaoContabilGrupoBem> gruposBensMoveis) {
        this.gruposBens = gruposBensMoveis;
    }

    public List<ConfigConciliacaoContabilGrupoBem> getGruposBensMoveis() {
        List<ConfigConciliacaoContabilGrupoBem> retorno = Lists.newArrayList();
        for (ConfigConciliacaoContabilGrupoBem grupoBem : gruposBens) {
            if (TipoBem.MOVEIS.equals(grupoBem.getGrupoBem().getTipoBem())) {
                retorno.add(grupoBem);
            }
        }
        return retorno;
    }

    public List<ConfigConciliacaoContabilGrupoBem> getGruposBensImoveis() {
        List<ConfigConciliacaoContabilGrupoBem> retorno = Lists.newArrayList();
        for (ConfigConciliacaoContabilGrupoBem grupoBem : gruposBens) {
            if (TipoBem.IMOVEIS.equals(grupoBem.getGrupoBem().getTipoBem())) {
                retorno.add(grupoBem);
            }
        }
        return retorno;
    }

    public List<ConfigConciliacaoContabilNaturezaDividaPublica> getNaturezasDaDividaPublica() {
        return naturezasDaDividaPublica;
    }

    public void setNaturezasDaDividaPublica(List<ConfigConciliacaoContabilNaturezaDividaPublica> naturezasDaDividaPublica) {
        this.naturezasDaDividaPublica = naturezasDaDividaPublica;
    }

    public List<ConfigConciliacaoContabilPassivoAtuarial> getPassivosAtuariais() {
        return passivosAtuariais;
    }

    public void setPassivosAtuariais(List<ConfigConciliacaoContabilPassivoAtuarial> passivosAtuariais) {
        this.passivosAtuariais = passivosAtuariais;
    }

    public List<ConfigConciliacaoContabilCategoriaOrcamentaria> getCategoriasOrcamentarias() {
        return categoriasOrcamentarias;
    }

    public void setCategoriasOrcamentarias(List<ConfigConciliacaoContabilCategoriaOrcamentaria> categoriasOrcamentarias) {
        this.categoriasOrcamentarias = categoriasOrcamentarias;
    }

    public List<ConfigConciliacaoContabilGrupoMaterial> getGruposMateriais() {
        return gruposMateriais;
    }

    public void setGruposMateriais(List<ConfigConciliacaoContabilGrupoMaterial> gruposMateriais) {
        this.gruposMateriais = gruposMateriais;
    }

    public boolean isObrasInstalacoesEAquisicoesDeImoveis() {
        return TipoConfigConciliacaoContabil.OBRAS_E_INSTALACOES_E_AQUISICAO_DE_IMOVEIS.equals(totalizador);
    }

    public List<ConfigConciliacaoContabilCategoriaDividaPublica> getCategoriasDaDividaPublica() {
        return categoriasDaDividaPublica;
    }

    public void setCategoriasDaDividaPublica(List<ConfigConciliacaoContabilCategoriaDividaPublica> categoriasDaDividaPublica) {
        this.categoriasDaDividaPublica = categoriasDaDividaPublica;
    }

    public List<ConfigConciliacaoContabilRecPatrimonialXRecOrc> getRecPatrimoniaisXRecOrcs() {
        return recPatrimoniaisXRecOrcs;
    }

    public void setRecPatrimoniaisXRecOrcs(List<ConfigConciliacaoContabilRecPatrimonialXRecOrc> recPatrimoniaisXRecOrcs) {
        this.recPatrimoniaisXRecOrcs = recPatrimoniaisXRecOrcs;
    }
}
