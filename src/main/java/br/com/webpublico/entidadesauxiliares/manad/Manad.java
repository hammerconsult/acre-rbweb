package br.com.webpublico.entidadesauxiliares.manad;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoEventoContabil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 10/06/14
 * Time: 08:22
 * To change this template use File | Settings | File Templates.
 */
public class Manad implements Serializable {

    private List<ManadRegistro> registros;
    private Entidade prefeitura;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private List<Pessoa> contadores;
    private List<Pessoa> desenvolvedores;
    private String conteudo;
    private Integer quantidadeLinhaBlocoZero;
    private Integer quantidadeLinhaBlocoL;

    public List<TipoEventoContabil> getTipoEventoContabils() {
        List<TipoEventoContabil> tipoEventoContabils = new ArrayList<TipoEventoContabil>();
        tipoEventoContabils.add(TipoEventoContabil.EMPENHO_DESPESA);
        tipoEventoContabils.add(TipoEventoContabil.RESTO_PAGAR);
        tipoEventoContabils.add(TipoEventoContabil.LIQUIDACAO_DESPESA);
        tipoEventoContabils.add(TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR);
        tipoEventoContabils.add(TipoEventoContabil.PAGAMENTO_DESPESA);
        tipoEventoContabils.add(TipoEventoContabil.PAGAMENTO_RESTO_PAGAR);
        tipoEventoContabils.add(TipoEventoContabil.PREVISAO_INICIAL_RECEITA);
        tipoEventoContabils.add(TipoEventoContabil.CREDITO_INICIAL);
        tipoEventoContabils.add(TipoEventoContabil.CREDITO_ADICIONAL);
        tipoEventoContabils.add(TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA);
        return tipoEventoContabils;
    }

    public Manad() {
        contadores = new ArrayList<Pessoa>();
        desenvolvedores = new ArrayList<Pessoa>();
        registros = new ArrayList<ManadRegistro>();
        quantidadeLinhaBlocoL = 0;
        quantidadeLinhaBlocoZero = 0;
    }

    public List<ManadRegistro> getRegistros() {
        return registros;
    }

    public void setRegistros(List<ManadRegistro> registros) {
        this.registros = registros;
    }

    public Entidade getPrefeitura() {
        return prefeitura;
    }

    public void setPrefeitura(Entidade prefeitura) {
        this.prefeitura = prefeitura;
    }

    public List<Pessoa> getContadores() {
        return contadores;
    }

    public void setContadores(List<Pessoa> contadores) {
        this.contadores = contadores;
    }

    public List<Pessoa> getDesenvolvedores() {
        return desenvolvedores;
    }

    public void setDesenvolvedores(List<Pessoa> desenvolvedores) {
        this.desenvolvedores = desenvolvedores;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getQuantidadeLinhaBlocoZero() {
        return quantidadeLinhaBlocoZero;
    }

    public void setQuantidadeLinhaBlocoZero(Integer quantidadeLinhaBlocoZero) {
        this.quantidadeLinhaBlocoZero = quantidadeLinhaBlocoZero;
    }

    public Integer getQuantidadeLinhaBlocoL() {
        return quantidadeLinhaBlocoL;
    }

    public void setQuantidadeLinhaBlocoL(Integer quantidadeLinhaBlocoL) {
        this.quantidadeLinhaBlocoL = quantidadeLinhaBlocoL;
    }
}
