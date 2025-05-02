package br.com.webpublico.viewobjects;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Grafica;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCondicao;
import br.com.webpublico.util.anotacoes.Condicao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author Gustavo
 */
public class FiltroAIDF {

    @Condicao(descricao = "numeroAidf", condicao = TipoCondicao.MAIORIGUAL)
    private Integer numeroAIDFInicial;
    @Condicao(descricao = "numeroAidf", condicao = TipoCondicao.MENORIGUAL)
    private Integer numeroAIDFFinal;
    @Condicao(descricao = "numeroSerie.numeroSerie", condicao = TipoCondicao.MAIORIGUAL)
    private String numeroSerieInicial;
    @Condicao(descricao = "numeroSerie.numeroSerie", condicao = TipoCondicao.MAIORIGUAL)
    private String numeroSerieFinal;
    @Condicao(descricao = "data", condicao = TipoCondicao.MAIORIGUAL)
    private Date dataInicial;
    @Condicao(descricao = "data", condicao = TipoCondicao.MENORIGUAL)
    private Date dataFinal;
    @Condicao(descricao = "cadastroEconomico.inscricaoCadastral", condicao = TipoCondicao.MENORIGUAL)
    private CadastroEconomico cmcInicial;
    @Condicao(descricao = "cadastroEconomico.inscricaoCadastral", condicao = TipoCondicao.MAIORIGUAL)
    private CadastroEconomico cmcFinal;
    @Condicao(descricao = "cadastroEconomico.pessoa", condicao = TipoCondicao.IGUAL)
    private Pessoa pessoa;
    @Condicao(descricao = "grafica", condicao = TipoCondicao.IGUAL)
    private Grafica grafica;

    public FiltroAIDF() {
        numeroAIDFInicial = 1;
        numeroAIDFFinal = 99999999;
    }

    public Integer getNumeroAIDFInicial() {
        return numeroAIDFInicial;
    }

    public void setNumeroAIDFInicial(Integer numeroAIDFInicial) {
        this.numeroAIDFInicial = numeroAIDFInicial;
    }

    public Integer getNumeroAIDFFinal() {
        return numeroAIDFFinal;
    }

    public void setNumeroAIDFFinal(Integer numeroAIDFFinal) {
        this.numeroAIDFFinal = numeroAIDFFinal;
    }

    public String getNumeroSerieInicial() {
        return numeroSerieInicial;
    }

    public void setNumeroSerieInicial(String numeroSerieInicial) {
        this.numeroSerieInicial = numeroSerieInicial;
    }

    public String getNumeroSerieFinal() {
        return numeroSerieFinal;
    }

    public void setNumeroSerieFinal(String numeroSerieFinal) {
        this.numeroSerieFinal = numeroSerieFinal;
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

    public CadastroEconomico getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(CadastroEconomico cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public CadastroEconomico getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(CadastroEconomico cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Grafica getGrafica() {
        return grafica;
    }

    public void setGrafica(Grafica grafica) {
        this.grafica = grafica;
    }

    public static Query montaHql(EntityManager em, FiltroAIDF filtro) throws IllegalArgumentException, IllegalAccessException {
        String juncao = " where ";
        String hql = "select c from CadastroAidf c ";
        for (Field atributo : filtro.getClass().getDeclaredFields()) {
            atributo.setAccessible(true);
            if (atributo.get(filtro) != null && atributo.isAnnotationPresent(Condicao.class)) {
                if (atributo.getClass().equals(String.class) && ((String) atributo.get(filtro)).isEmpty()) {
                    continue;
                }
                Condicao c = (Condicao) atributo.getAnnotation(Condicao.class);
                hql += juncao + "c." + c.descricao() + " " + c.condicao().getDescricao() + " " + ":" + atributo.getName();
                juncao = " and ";
            }
        }
        Query q = em.createQuery(hql);
        for (Field atributo : filtro.getClass().getDeclaredFields()) {
            atributo.setAccessible(true);
            if (atributo.get(filtro) != null && atributo.isAnnotationPresent(Condicao.class)) {
                if (atributo.getClass().equals(String.class) && ((String) atributo.get(filtro)).isEmpty()) {
                    continue;
                }
                q.setParameter(atributo.getName(), atributo.get(filtro));
            }
        }
        //System.out.println("hql  " + hql);
        q.setMaxResults(10);
        return q;
    }
}
