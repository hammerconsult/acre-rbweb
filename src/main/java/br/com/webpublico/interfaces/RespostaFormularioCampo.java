package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.comum.FormularioCampo;

import java.util.List;

public interface RespostaFormularioCampo {

    public FormularioCampo getFormularioCampo();

    public void setFormularioCampo(FormularioCampo formularioCampo);

    public String getResposta();

    public void setResposta(String resposta);

    public List<String> getRespostaList();

    public void setRespostaList(List<String> respostaList);

}
