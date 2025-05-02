package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by renato on 30/08/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class PaginaPrefeituraPortal extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Prefeitura")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private PrefeituraPortal prefeitura;
    @Etiqueta("Módulo")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private ModuloPrefeituraPortal modulo;
    @Etiqueta("Tipo")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPaginaPortal tipoPaginaPortal;
    @Etiqueta("Componente")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoComponentePaginaPortal tipoComponente;
    @Obrigatorio
    private String conteudo;
    @Obrigatorio
    private String nome;
    @Obrigatorio
    private String chave;
    private String conteudoHtml;
    private Boolean filtrarTodosRegistros;
    private Boolean paginaPublica;
    private Integer ordem;
    @Etiqueta("Ícone")
    private String icone;
    private String subNome;
    private String chaveSubNome;
    @JsonIgnore
    @Etiqueta("Página de Anexo Geral?")
    private Boolean anexoGeral;

    public PaginaPrefeituraPortal() {
        anexoGeral = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrefeituraPortal getPrefeitura() {
        return prefeitura;
    }

    public void setPrefeitura(PrefeituraPortal prefeitura) {
        this.prefeitura = prefeitura;
    }

    public TipoPaginaPortal getTipoPaginaPortal() {
        return tipoPaginaPortal;
    }

    public void setTipoPaginaPortal(TipoPaginaPortal tipoPaginaPortal) {
        this.tipoPaginaPortal = tipoPaginaPortal;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public TipoComponentePaginaPortal getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(TipoComponentePaginaPortal tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public enum TipoPaginaPortal {
        HTML, SQL, LINK;
    }

    public enum TipoComponentePaginaPortal {
        TABELA, TABELA_UNICA, GRID;
    }

    public String getConteudoHtml() {
        return conteudoHtml;
    }

    public void setConteudoHtml(String conteudoHtml) {
        this.conteudoHtml = conteudoHtml;
    }

    public Boolean getFiltrarTodosRegistros() {
        if (filtrarTodosRegistros == null) {
            filtrarTodosRegistros = Boolean.FALSE;
        }
        return filtrarTodosRegistros;
    }

    public void setFiltrarTodosRegistros(Boolean filtrarTodosRegistros) {
        this.filtrarTodosRegistros = filtrarTodosRegistros;
    }

    public Boolean getPaginaPublica() {
        if (paginaPublica == null) {
            paginaPublica = Boolean.FALSE;
        }
        return paginaPublica;
    }

    public void setPaginaPublica(Boolean paginaPublica) {
        this.paginaPublica = paginaPublica;
    }

    public ModuloPrefeituraPortal getModulo() {
        return modulo;
    }

    public void setModulo(ModuloPrefeituraPortal modulo) {
        this.modulo = modulo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getSubNome() {
        return subNome;
    }

    public void setSubNome(String subNome) {
        this.subNome = subNome;
    }

    public String getChaveSubNome() {
        return chaveSubNome;
    }

    public void setChaveSubNome(String chaveSubNome) {
        this.chaveSubNome = chaveSubNome;
    }

    public Boolean getAnexoGeral() {
        return anexoGeral == null ? Boolean.FALSE : anexoGeral;
    }

    public void setAnexoGeral(Boolean anexoGeral) {
        this.anexoGeral = anexoGeral;
    }

    @Override
    public String toString() {
        return nome;
    }
}
