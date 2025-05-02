package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.enums.TipoBem;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoInventarioDeBensMoveis",
        pattern = "/inventario-de-bens-moveis/",
        viewId = "/faces/administrativo/patrimonio/relatorios/inventariodebensmoveis.xhtml")})
public class RelatorioInventarioBemMovelControlador extends SuperRelatorioInventarioBemControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioInventarioBemMovelControlador.class);

    @URLAction(mappingId = "novoInventarioDeBensMoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoInventarioDeBensMoveis() {
        tipoBem = TipoBem.MOVEIS;
        limparCampos();
        setOrdenacaoDisponivel(new ArrayList<Object[]>());
        getOrdenacaoDisponivel().add(new String[]{"Data de Aquisição", "b.DATAAQUISICAO"});
        getOrdenacaoDisponivel().add(new String[]{"Registro Patrimonial", "TO_NUMBER(TRIM(b.identificacao))"});
        getOrdenacaoDisponivel().add(new String[]{"Descrição do Bem", "trim(replace(b.DESCRICAO, '-',''))"});
        getOrdenacaoDisponivel().add(new String[]{"Grupo Patrimonial", "GRUPO.CODIGO"});
        getOrdenacaoDisponivel().add(new String[]{"Tipo de Aquisição", "ESTADO_resultante.TIPOAQUISICAOBEM"});
        getOrdenacaoDisponivel().add(new String[]{"Estado de Conservação", "ESTADO_resultante.ESTADOBEM"});
        getOrdenacaoDisponivel().add(new String[]{"Situação de Conservação", "ESTADO_resultante.SITUACAOCONSERVACAOBEM"});
        getOrdenacaoDisponivel().add(new String[]{"Valor do Bem", "estado_resultante.VALORORIGINAL"});
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        gerarInventarioDeBem(tipoExtensaoRelatorio);
    }
}
