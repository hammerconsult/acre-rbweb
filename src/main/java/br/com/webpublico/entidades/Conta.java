package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.TipoContaContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representa uma conta de um plano de contas. Contas são utilizadas para
 * categorizar lançamentos (contábeis, receitas, despesas, etc).
 * <p/>
 * Contas possuem hierarquia, determinada pelo atributo "superior".
 * <p/>
 * O código das contas é gerado com base na máscara definida no tipo de conta
 * vinculado ao plano de contas juntamente com o nível da conta.
 * <p/>
 * O nível da conta representa a profundidade na hierarquia é calculado a partir
 * do nível da conta pai (nivel + 1, ou 1 se a conta não possuir conta
 * superior).
 * <p/>
 * Apagar uma conta implica em remover (ou mover) todas as suas contas filhas.
 * <p/>
 * Somente contas ativas podem receber lançamentos.
 * <p/>
 * Não é permitido alterar o plano de contas de uma conta pois isto implica nos
 * lançamentos a ela vinculados.
 */
//REMOVIDO POIS FOI CRIADO UM METODO EM SOLICITACAOMATERIALFACADE.buscaContas()
//@NamedNativeQueries({
//    @NamedNativeQuery(name = "conta", query = "with rec (id, superior_id, descricao, codigo, planodecontas_id) as ("
//    + " select pai.id, pai.superior_id, pai.descricao, pai.codigo, pai.planodecontas_id "
//    + " from conta pai "
//    + " where pai.codigo = ? "
//    + " UNION ALL "
//    + " select c.id, c.superior_id, c.descricao, c.codigo, c.planodecontas_id "
//    + " from conta c, rec "
//    + " where c.superior_id = rec.id and c.planodecontas_id = rec.planodecontas_id) "
//    + " select * from conta c "
//    + "inner join rec on c.id = rec.id"
//    + " left join contacontabil cc on cc.id = c.id "
//    + " left join contadespesa cd on cd.id = c.id "
//    + " left join contadedestinacao cdest on cdest.id = c.id "
//    + " left join contaextraorcamentaria cextra on cextra.id = c.id "
//    + " left join contareceita cr on cr.id = c.id ", resultClass = Conta.class),
//    @NamedNativeQuery(name = "contasFilhasPorDescricao", query = "with rec (id, ativa, codigo, dataregistro, descricao, permitirdesdobramento, planodecontas_id, superior_id, rubrica, tipoContaContabil, funcao, exercicio_id) as ("
//    + " select pai.id, pai.ativa, pai.codigo, pai.dataregistro, pai.descricao, pai.permitirdesdobramento, pai.planodecontas_id, pai.superior_id, pai.rubrica, pai.tipoContaContabil, pai.funcao, pai.exercicio_id "
//    + " from conta pai "
//    + " where pai.codigo like ?"
//    + " UNION ALL "
//    + " select c.id, c.ativa, c.codigo, c.dataregistro, c.descricao, c.permitirdesdobramento, c.planodecontas_id, c.superior_id, c.rubrica, c.tipoContaContabil, c.funcao, c.exercicio_id "
//    + " from conta c, rec "
//    + " where c.superior_id = rec.id and c.planodecontas_id = rec.planodecontas_id) "
//    + " select * from conta c "
//    + "inner join rec on c.id = rec.id "
//    + " left join contacontabil cc on cc.id = c.id "
//    + " left join contadespesa cd on cd.id = c.id "
//    + " left join contadedestinacao cdest on cdest.id = c.id "
//    + " left join contaextraorcamentaria cextra on cextra.id = c.id "
//    + " left join contareceita cr on cr.id = c.id "
//    + " where lower(c.descricao) like ? or c.codigo like ?", resultClass = Conta.class)
//})
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Conta extends SuperEntidade implements Comparable<Conta> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Ativa")
    private Boolean ativa;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta(value = "Código")
    private String codigo;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta(value = "Código TCE")
    private String codigoTCE;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta(value = "Código SICONFI")
    private String codigoSICONFI;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta(value = "Descrição")
    private String descricao;
    /**
     * Contas que permitem desdobramento podem ter novas contas filhas
     * vinculadas a ela. Este atributo é usado em planos de contas do TCU, os
     * quais são padronizados até certo nível, mas que permitem novas contas
     * para detalhar os lançamentos.
     */
    @Pesquisavel
    @Etiqueta(value = "Permitir Desdobramento")
    private Boolean permitirDesdobramento;
    @ManyToOne
    private PlanoDeContas planoDeContas;
    @ManyToOne
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Superior")
    private Conta superior;
    private String rubrica;
    @Enumerated(EnumType.STRING)
    private TipoContaContabil tipoContaContabil;
    private String funcao;
    /**
     * Contas ANALITICAS sÃo contas que não possuem contas filhas e que podem
     * receber lançamentos. Contas SINTETICAS são conta que não podem receber
     * lançamentos por possuirem (ou vir a possuir) contas filhas.
     */
    //  @Obrigatorio
    //  @Enumerated(EnumType.STRING)
    //  private CategoriaConta categoria;
    //   private int nivel;
//    @OneToMany(mappedBy = "superior", fetch = FetchType.LAZY, orphanRemoval = true)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private List<Conta> filhos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contaDestino", orphanRemoval = true)
    private List<ContaEquivalente> contasEquivalentes;
    @Invisivel
    @ManyToOne
    private Exercicio exercicio;
    private String dType;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Categoria")
    private CategoriaConta categoria;

    public Conta() {
        super();
//        setFilhos(new ArrayList<Conta>());
        this.contasEquivalentes = new ArrayList<>();
        permitirDesdobramento = false;
        codigo = "";
        dType = "Conta";
    }

    public Conta(String codigo, String descricao, Boolean ativa, Conta superior, List<Conta> filhos, List<ContaEquivalente> contasEquivalentes, PlanoDeContas planoDeContas, Boolean permitirDesdobramento, Date dataRegistro) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.ativa = ativa;
        this.superior = superior;
//        this.filhos = filhos;
        this.contasEquivalentes = contasEquivalentes;
        this.planoDeContas = planoDeContas;
        this.permitirDesdobramento = permitirDesdobramento;
        this.dataRegistro = dataRegistro;
        criadoEm = System.nanoTime();
        dType = "Conta";
    }

    public Conta(Boolean ativa, String codigo, Date dataRegistro, String descricao, Boolean permitirDesdobramento, PlanoDeContas planoDeContas, Conta superior, String rubrica, TipoContaContabil tipoContaContabil, String funcao, List<Conta> filhos, List<ContaEquivalente> contasEquivalentes) {
        this.ativa = ativa;
        this.codigo = codigo;
        this.dataRegistro = dataRegistro;
        this.descricao = descricao;
        this.permitirDesdobramento = permitirDesdobramento;
        this.planoDeContas = planoDeContas;
        this.superior = superior;
        this.rubrica = rubrica;
        this.tipoContaContabil = tipoContaContabil;
        this.funcao = funcao;
//        this.filhos = filhos;
        this.contasEquivalentes = contasEquivalentes;
        criadoEm = System.nanoTime();
        dType = "Conta";
    }

    public String getCodigoSemZerosAoFinal() {
        String toReturn = "";
        toReturn = this.codigo;
        int zeroAPartirDe = toReturn.length();
        for (int i = toReturn.length() - 1; i >= 0; i--) {
            if (toReturn.substring(i, i + 1).equals(".")) {
                zeroAPartirDe = i;
            } else if (!toReturn.substring(i, i + 1).equalsIgnoreCase("0")) {
                return toReturn.substring(0, zeroAPartirDe);
            }
        }
        return toReturn;
    }

    public List<ContaEquivalente> getContasEquivalentes() {
        return contasEquivalentes;
    }

    public void setContasEquivalentes(List<ContaEquivalente> contasEquivalentes) {
        this.contasEquivalentes = contasEquivalentes;
    }

    //    public List<Conta> getFilhos() {
//        return filhos;
//    }
//
//    public void setFilhos(List<Conta> filhos) {
//        this.filhos = filhos;
//    }
    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public CategoriaConta getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaConta categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNivel() {
        return calculaNIvel(this.codigo);
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public Conta getSuperior() {
        return superior;
    }

    public void setSuperior(Conta superior) {
        this.superior = superior;
    }

    public Boolean getPermitirDesdobramento() {
        return permitirDesdobramento;
    }

    public void setPermitirDesdobramento(Boolean permitirDesdobramento) {
        this.permitirDesdobramento = permitirDesdobramento;
    }

    public String getRubrica() {
        return rubrica;
    }

    public void setRubrica(String rubrica) {
        this.rubrica = rubrica;
    }

    public TipoContaContabil getTipoContaContabil() {
        return tipoContaContabil;
    }

    public void setTipoContaContabil(TipoContaContabil tipoContaContabil) {
        this.tipoContaContabil = tipoContaContabil;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    public String getCodigoTCE() {
        return codigoTCE;
    }

    public void setCodigoTCE(String codigoTCE) {
        this.codigoTCE = codigoTCE;
    }

    public String getCodigoSICONFI() {
        return codigoSICONFI;
    }

    public void setCodigoSICONFI(String codigoSICONFI) {
        this.codigoSICONFI = codigoSICONFI;
    }

    public String getCodigoTCEOrCodigoSemPonto() {
        return Strings.isNullOrEmpty(codigoTCE) ? codigo.replace(".", "") : codigoTCE;
    }

    public boolean equalsID(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Conta other = (Conta) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    private int calculaNIvel(String codigo) {
        String str = codigo;
        String[] partes = str.split("\\.");
        int cont = 0;
        int nivelTemp = 0;

        for (int x = 0; x < partes.length; x++) {
            if (partes[x].replace("0", "").length() > 0) {
                nivelTemp = x;
                cont++;
            }
        }
        if ((nivelTemp + 1) > cont) {
            return nivelTemp + 1;
        } else {
            return cont;
        }
    }

    @Override
    public String toString() {
        if (codigo != null && descricao != null) {
            return codigo + " - " + descricao.trim();
        }
        return codigo;
    }

    public String toStringAutoCompleteMaxLength100() {
        String s = toString();
        if (s.length() > 100) {
            return s.substring(0, 100);
        }

        return s;
    }

    public String toStringAutoComplete() {
        String retorno = toString();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }

    @Override
    public int compareTo(Conta o) {
        ////System.out.println("THIS: " + this.codigo);
        ////System.out.println("Conta: " + ((Conta) o).codigo);
        return this.codigo.compareTo(((Conta) o).codigo);
    }

    public void limpaContasEquivalentesEntidade() {
        this.contasEquivalentes = new ArrayList<>();
    }
}
