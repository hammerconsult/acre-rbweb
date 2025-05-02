package br.com.webpublico.controle.administrativo.obra;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Obra;
import br.com.webpublico.entidadesauxiliares.administrativo.ObraCadastroImobiliarioDTO;
import br.com.webpublico.negocios.ObraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


@ManagedBean(name = "obraMapaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "/dashboard-obras/", pattern = "/dashboard-obras/", viewId = "/faces/dashboard/dashboard-obras.xhtml")
})
public class ObraMapaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ObraMapaControlador.class);

    @EJB
    private ObraFacade facade;
    private List<ObraCadastroImobiliarioDTO> dtos;


    @PostConstruct
    public void iniciar() {

        dtos = Lists.newArrayList();
        List<Obra> obras = facade.buscarTodasObrasComInscricaoCadastral();
        for (Obra obra : obras) {
            ObraCadastroImobiliarioDTO dto = new ObraCadastroImobiliarioDTO();

            dto.setInscricacaoCadastral(obra.getCadastroImobiliario().getInscricaoCadastral());
            dto.setDescricao(obra.getNome());
            dto.setQuadra(obra.getCadastroImobiliario().getLote().getQuadra().getCodigo());
            dto.setLote(obra.getCadastroImobiliario().getLote().getCodigoLote());
            dto.setSetor(obra.getCadastroImobiliario().getLote().getSetor().getCodigo());
            dto.setEndereco(obra.getCadastroImobiliario().getLote().getEndereco());
            dto.setPorcentagem(getPercentualTotalObra(obra));

            dtos.add(dto);

        }


    }

    public BigDecimal getPercentualTotalObra(Obra obra) {
        try {
            BigDecimal valorMedicoes = obra.getValorTotalMedicoes().setScale(4);
            BigDecimal valorObra = obra.getContrato().getValorTotal().setScale(4);
            BigDecimal divisao = valorMedicoes.divide(valorObra, RoundingMode.HALF_DOWN);
            BigDecimal percentual = divisao.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_DOWN);
            return percentual;

        } catch (Exception ex) {
            return new BigDecimal("00.00");

        }
    }

    public void verInscricaoMapa(ObraCadastroImobiliarioDTO dto) {
        FacesUtil.executaJavaScript("criarMapa()");

        int setor = Integer.parseInt(dto.getSetor());
        int quadra = Integer.parseInt(dto.getQuadra());
        int lote = Integer.parseInt(dto.getLote());

        String inscricaoMapa = "1-" + setor + "-" + quadra + "-" + lote;

        FacesUtil.executaJavaScript("getLote('" + inscricaoMapa + "')");
        FacesUtil.executaJavaScript("exibeLote('" + inscricaoMapa + "')");
        FacesUtil.executaJavaScript("centralizar('" + inscricaoMapa + "')");

    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        return new ConverterAutoComplete(CadastroImobiliario.class, facade.getCadastroImobiliarioFacade());
    }

    public List<ObraCadastroImobiliarioDTO> getDtos() {
        return dtos;
    }

    public void setDtos(List<ObraCadastroImobiliarioDTO> dtos) {
        this.dtos = dtos;
    }

}
