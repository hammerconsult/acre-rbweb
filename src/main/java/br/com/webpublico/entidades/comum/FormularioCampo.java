package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.interfaces.RespostaFormularioCampo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Audited
public class FormularioCampo extends SuperEntidade implements Comparable<FormularioCampo>, RespostaFormularioCampo, PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Formulario formulario;
    @ManyToOne
    private FormularioCampo formularioCampo;
    private String resposta;
    @Etiqueta("Título")
    @Obrigatorio
    private String titulo;
    @Etiqueta("Tipo de Componente")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoComponente tipoComponente;
    @Etiqueta("Ordem")
    @Obrigatorio
    private Integer ordem;
    private Boolean obrigatorio;
    @OneToMany(mappedBy = "formularioCampo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormularioCampoOpcao> formularioCampoOpcaoList = Lists.newArrayList();
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Transient
    private List<String> respostaList;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public FormularioCampo getFormularioCampo() {
        return formularioCampo;
    }

    public void setFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoComponente getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(TipoComponente tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public List<FormularioCampoOpcao> getFormularioCampoOpcaoList() {
        return formularioCampoOpcaoList;
    }

    public void setFormularioCampoOpcaoList(List<FormularioCampoOpcao> formularioCampoOpcaoList) {
        this.formularioCampoOpcaoList = formularioCampoOpcaoList;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<String> getRespostaList() {
        if (respostaList == null && !Strings.isNullOrEmpty(resposta)) {
            respostaList = Arrays.asList(resposta.split(";"));
        }
        return respostaList;
    }

    public void setRespostaList(List<String> respostaList) {
        this.respostaList = respostaList;
        if (this.respostaList != null) {
            this.resposta = StringUtils.join(this.respostaList, ";");
        }
    }

    public boolean canAddFilho() {
        return TipoComponente.MENU_OPCAO.equals(tipoComponente) ||
                TipoComponente.BOTAO_SELECAO.equals(tipoComponente) ||
                TipoComponente.CAIXA_SELECAO.equals(tipoComponente);
    }

    public List<String> getHierarquiaOrdem(List<String> ordemList) {
        if (formularioCampo != null) {
            formularioCampo.getHierarquiaOrdem(ordemList);
        }
        ordemList.add(ordem < 10 ? "0"+ordem : ordem+"");
        return ordemList;
    }

    public String getOrdemGeral() {
        List<String> hierarquiaOrdem = Lists.newArrayList();
        return StringUtils.join(getHierarquiaOrdem(hierarquiaOrdem), ".");
    }

    @Override
    public int compareTo(FormularioCampo other) {
        return getOrdemGeral().compareTo(other.getOrdemGeral());
    }

    public enum TipoComponente {
        ETIQUETA("Etiqueta", false),
        TEXTO("Texto", false),
        DATA("Data", false),
        MENU_OPCAO("Menu de Opção", true),
        BOTAO_SELECAO("Botão de Seleção", true),
        CAIXA_SELECAO("Caixa de Seleção", true),
        ARQUIVO("Arquivo", false),
        ARQUIVO_DOWNLOAD("Arquivo Download", false);

        private String descricao;
        private Boolean defineOpcoes;

        TipoComponente(String descricao, Boolean defineOpcoes) {
            this.descricao = descricao;
            this.defineOpcoes = defineOpcoes;
        }

        public String getDescricao() {
            return descricao;
        }

        public Boolean getDefineOpcoes() {
            return defineOpcoes;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
