/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Representa os possíveis locais de armazenamento de mercadorias permitindo o desdobramento
 * dos locais macro (armazéns, por exemplo) com suas subdivisões (ruas, prateleiras, gavetas, etc.).
 *
 * @author arthur
 */
@Etiqueta("Local de Estoque")
public class LocalEstoque extends SuperEntidade implements Comparable<LocalEstoque> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * O código do LocalEstoque deve ser gerado a partir de uma máscara, da mesma maneira
     * que o código da entidade Conta.
     */
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    /**
     * Auto-relacionamento que possibilita a criação do número desejado de níveis.
     */
    @Tabelavel
    @ManyToOne
    @Etiqueta("Superior")
    private LocalEstoque superior;
    /**
     * UnidadeOrganizacional na qual este LocalEstoque reside. Não deve ser permitido o vínculo
     * de locais desmembrados a UnidadeOrganizacional diferente de seus superiores (hieraquia homogênea).
     */
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeOrganizacional;

    /**
     * Data a partir da qual são permitidos lançamentos de movimentação de estoque neste LocalEstoque
     */
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fechado Em")
    private Date fechadoEm;

    @Tabelavel
    @Etiqueta("Tipo de Estoque")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoEstoque tipoEstoque;

    @Invisivel
    @OneToMany(mappedBy = "localEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocalEstoqueMaterial> listaLocalEstoqueMaterial;

    @Invisivel
    @OneToMany(mappedBy = "localEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GestorLocalEstoque> listaGestorLocalEstoque;

    @Invisivel
    @OneToMany(mappedBy = "localEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocalEstoqueOrcamentario> listaLocalEstoqueOrcamentario;

    /*As reservas de estoque devem ser feitas por local de estoque e não por estoque, pois no momento da avaliação a
    quantidade física em estoque de um almoxarifado é vista de maneira geral, tanto a quantidade do almoxarifado pai,
    quanto a dos seus filhos, porém as requisições são realizadas somente para locais de estoque pai.*/
    @OneToMany(mappedBy = "localEstoque")
    @Invisivel
    private List<ReservaEstoque> reservas;

    private Boolean utilizarPin;

    @ManyToOne
    @Etiqueta("Cadastro Imobiliário")
    private CadastroImobiliario cadastroImobiliario;

    public LocalEstoque() {
        super();
        this.listaGestorLocalEstoque = new ArrayList<>();
        this.listaLocalEstoqueMaterial = new ArrayList<>();
        this.descricao = new String("");
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getFechadoEm() {
        return fechadoEm;
    }

    public void setFechadoEm(Date fechadoEm) {
        this.fechadoEm = fechadoEm;
    }

    public List<LocalEstoqueMaterial> getListaLocalEstoqueMaterial() {
        Collections.sort(listaLocalEstoqueMaterial);
        return listaLocalEstoqueMaterial;
    }

    public void setListaLocalEstoqueMaterial(List<LocalEstoqueMaterial> listaLocalEstoqueMaterial) {
        this.listaLocalEstoqueMaterial = listaLocalEstoqueMaterial;
    }

    public List<GestorLocalEstoque> getListaGestorLocalEstoque() {
        return listaGestorLocalEstoque;
    }

    public void setListaGestorLocalEstoque(List<GestorLocalEstoque> listaGestorLocalEstoque) {
        this.listaGestorLocalEstoque = listaGestorLocalEstoque;
    }

    public Boolean getUtilizarPin() {
        return utilizarPin;
    }

    public void setUtilizarPin(Boolean utilizarPin) {
        this.utilizarPin = utilizarPin;
    }

    public List<ReservaEstoque> getReservas() {
        return reservas;
    }

    public void setReservas(List<ReservaEstoque> reservas) {
        this.reservas = reservas;
    }

    public List<LocalEstoqueOrcamentario> getListaLocalEstoqueOrcamentario() {
        return listaLocalEstoqueOrcamentario;
    }

    public void setListaLocalEstoqueOrcamentario(List<LocalEstoqueOrcamentario> listaLocalEstoqueOrcamentario) {
        this.listaLocalEstoqueOrcamentario = listaLocalEstoqueOrcamentario;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Set<Material> materialSet() {
        Set<Material> retorno = new HashSet<>();
        for (LocalEstoqueMaterial localEstoqueMaterial : listaLocalEstoqueMaterial) {
            retorno.add(localEstoqueMaterial.getMaterial());
        }
        return retorno;
    }

    public Set<Material> materialSet(GrupoMaterial grupoMaterial) {
        Set<Material> retorno = new HashSet<>();
        for (LocalEstoqueMaterial localEstoqueMaterial : listaLocalEstoqueMaterial) {
            if (localEstoqueMaterial.getMaterial().getGrupo().equals(grupoMaterial)) {
                retorno.add(localEstoqueMaterial.getMaterial());
            }
        }
        return retorno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoque getSuperior() {
        return superior;
    }

    public void setSuperior(LocalEstoque superior) {
        this.superior = superior;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String toStringAutoComplete() {
        return this.codigo + " - " + this.descricao + " - " + this.tipoEstoque.getDescricao();
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    @Override
    public int compareTo(LocalEstoque le) {
        return this.id.compareTo(le.getId());
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + descricao + " - " + tipoEstoque.getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getCodigoDescricao() {
        return codigo + " - " + descricao;
    }

    public boolean ehTipoAlmoxarifado() {
        return TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO.equals(this.tipoEstoque);
    }

    public Boolean isDoTipo(TipoEstoque tipoEstoque) {
        return this.tipoEstoque.equals(tipoEstoque);
    }

    public boolean fechadoNaData(Date data) {
        if (fechadoEm == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(sdf.format(data)).after(sdf.parse(sdf.format(fechadoEm)));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro ao processar datas.");
        }
    }

    public String getCodigoSemZerosFinais() {
        //Com o código = "01.02.000.00.000.0000.000.00";
        //vai retornar "01.02.", com o qual faremos um like...;
        boolean tudoZero = true;
        int i = codigo.length();
        int ultimoPonto = codigo.length();
        while (i > 0 && tudoZero) {
            char c = codigo.charAt(i - 1);
            if (c == '.') {
                ultimoPonto = i;
            }
            if (c != '.' && c != '0') {
                tudoZero = false;
            }
            i--;
        }
        return codigo.substring(0, ultimoPonto);
    }

    public String getCodigoDo2NivelDeHierarquia() {
        //Com o código = "01.02.001.00.000.0000.000.00";
        //vai retornar "02", com o qual faremos um like...;
        return codigo.split("\\.")[1];
    }

    public Integer getNivelPorCodigo() {
        //retorna o nivel confer o código
        String[] quebrado = codigo.split("\\.");
        int numero = 0;
        for (String parte : quebrado) {
            if (!parte.replaceAll("0", "").trim().isEmpty()) {
                numero++;
            }
        }
        return numero;
    }

}
