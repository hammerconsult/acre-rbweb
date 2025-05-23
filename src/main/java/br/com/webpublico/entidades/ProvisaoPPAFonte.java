/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TreeMap;

/**
 * Detalha o valor de cada despesa de cada ação do PPA de acordo com a origem dos recursos, de
 * forma a determinar sua destinaçào.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("Provisão PPA Fonte")
public class ProvisaoPPAFonte implements Serializable, EntidadeContabil, IManadRegistro, IGeraContaAuxiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Provisão PPA Despesa")
    @ManyToOne
    @Obrigatorio
    private ProvisaoPPADespesa provisaoPPADespesa;
    /**
     * Somente uma conta de fonte de recurso pode ser selecionada! Verificar qual é o plano de contas
     * de destinações de recursos vigente no exercício do PPA para filtrar as contas que poderão ser vinculadas
     * (ver PlanoDecontasExercicio)
     */
    @Tabelavel
    @Etiqueta("Destinação de Recursos")
    @ManyToOne
    @Obrigatorio
    private Conta destinacaoDeRecursos;
    @Tabelavel
    @Monetario
    @Obrigatorio
    private BigDecimal valor;
    @OneToOne
    private ProvisaoPPAFonte origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    @Enumerated(EnumType.STRING)
    private EsferaOrcamentaria esferaOrcamentaria;
    @Transient
    private Long criadoEm;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Extensão Fonte de Recurso")
    private ExtensaoFonteRecurso extensaoFonteRecurso;

    public ProvisaoPPAFonte() {
        somenteLeitura = false;
        valor = new BigDecimal(BigInteger.ZERO);
        criadoEm = System.nanoTime();
    }

    public ProvisaoPPAFonte(ProvisaoPPADespesa provisaoPPADespesa, Conta destinacaoDeRecursos, BigDecimal valor) {
        this.provisaoPPADespesa = provisaoPPADespesa;
        this.destinacaoDeRecursos = destinacaoDeRecursos;
        this.valor = valor;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public ProvisaoPPAFonte getOrigem() {
        return origem;
    }

    public void setOrigem(ProvisaoPPAFonte origem) {
        this.origem = origem;
    }

    public Conta getDestinacaoDeRecursos() {
        return destinacaoDeRecursos;
    }

    public void setDestinacaoDeRecursos(Conta destinacaoDeRecursos) {
        this.destinacaoDeRecursos = destinacaoDeRecursos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProvisaoPPADespesa getProvisaoPPADespesa() {
        return provisaoPPADespesa;
    }

    public void setProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa) {
        this.provisaoPPADespesa = provisaoPPADespesa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public ExtensaoFonteRecurso getExtensaoFonteRecurso() {
        return extensaoFonteRecurso;
    }

    public void setExtensaoFonteRecurso(ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.extensaoFonteRecurso = extensaoFonteRecurso;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getProvisaoPPADespesa() != null) {
            historicoNota += "N°: " + this.getProvisaoPPADespesa().getCodigo() + "/" + this.getProvisaoPPADespesa().getSubAcaoPPA().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getProvisaoPPADespesa() != null) {
            historicoNota += " Funcional Programática: " + this.getProvisaoPPADespesa().getSubAcaoPPA().getExercicio().getAno() + "."
                + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getFuncao().getCodigo() + "."
                + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getSubFuncao().getCodigo() + "."
                + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo() + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo().substring(1, 3) + "."
                + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getCodigo() + "."
                + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getSubFuncao().getCodigo() + "-"
                + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getSubFuncao().getDescricao() + " - "
                + this.getProvisaoPPADespesa().getUnidadeOrganizacional() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getProvisaoPPADespesa().getContaDeDespesa() != null) {
            historicoNota += " Conta de Despesa: " + this.getProvisaoPPADespesa().getContaDeDespesa().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDestinacaoDeRecursos() != null) {
            historicoNota += " Fonte de Recursos: " + ((ContaDeDestinacao) this.getDestinacaoDeRecursos()).getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " ";
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return " Cód: " + provisaoPPADespesa.getCodigo() + " - Fonte de Recurso: " + destinacaoDeRecursos + " valor : " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return provisaoPPADespesa.getCodigo();
    }

    public ContaDeDestinacao getDestinacaoDeRecursosAsContaDeDestinacao() {
        return (ContaDeDestinacao) destinacaoDeRecursos;
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L250;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        Date data = SistemaFacade.getDataCorrente();
        UnidadeOrganizacional unidadeOrganizacional = getProvisaoPPADespesa().getUnidadeOrganizacional();

        HierarquiaOrganizacional unidade = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(data, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(data, unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);

        ContaDeDestinacao conta = (ContaDeDestinacao) this.getDestinacaoDeRecursos();
        Conta contaDeDespesa = this.getProvisaoPPADespesa().getContaDeDespesa();
        SubAcaoPPA subAcaoPPA = this.getProvisaoPPADespesa().getSubAcaoPPA();
        AcaoPPA acaoPPA = subAcaoPPA.getAcaoPPA();
        Funcao funcao = acaoPPA.getFuncao();
        SubFuncao subFuncao = acaoPPA.getSubFuncao();
        AcaoPrincipal acaoPrincipal = acaoPPA.getAcaoPrincipal();
        ProgramaPPA programa = acaoPrincipal.getPrograma();

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L250.name(), conteudo);
        ManadUtil.criaLinha(2, conta.getFonteDeRecursos().getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, orgao.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(4, unidade.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(5, funcao.getCodigo(), conteudo);
        ManadUtil.criaLinha(6, subFuncao.getCodigo(), conteudo);
        ManadUtil.criaLinha(7, programa.getCodigo(), conteudo);
        ManadUtil.criaLinha(8, acaoPrincipal.getCodigo(), conteudo);
        ManadUtil.criaLinha(9, acaoPPA.getCodigo(), conteudo);
        ManadUtil.criaLinha(10, contaDeDespesa.getCodigo(), conteudo);
        ManadUtil.criaLinha(11, "", conteudo);
        ManadUtil.criaLinha(12, "", conteudo);
        ManadUtil.criaLinha(13, ManadUtil.getValor(getValor()), conteudo);
        ManadUtil.criaLinha(14, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(15, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(16, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(17, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(18, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(19, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(20, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(21, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(22, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(23, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(24, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    getDestinacaoDeRecursosAsContaDeDestinacao(),
                    getProvisaoPPADespesa().getContaDeDespesa(),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    getDestinacaoDeRecursosAsContaDeDestinacao(),
                    (getProvisaoPPADespesa().getContaDeDespesa().getCodigoSICONFI() != null ?
                        getProvisaoPPADespesa().getContaDeDespesa().getCodigoSICONFI() :
                        getProvisaoPPADespesa().getContaDeDespesa().getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    public String getCodigoExtensaoFonteRecursoAsString() {
        return getExtensaoFonteRecurso().getCodigo().toString();
    }
}
