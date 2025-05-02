package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloFAQ;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 12/07/14
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("FAQ")
public class FAQ extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Pergunta?")
    private String pergunta;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Resposta")
    private String resposta;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Assunto")
    private String assunto;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Módulo")
    @Enumerated(EnumType.STRING)
    private ModuloFAQ modulo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public ModuloFAQ getModulo() {
        return modulo;
    }

    public void setModulo(ModuloFAQ modulo) {
        this.modulo = modulo;
    }
}
