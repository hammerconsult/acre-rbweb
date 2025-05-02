package br.com.webpublico.pncp.service;

import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.EsferaGovernamental;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.pncp.dto.EnteAutorizadoDTO;
import br.com.webpublico.pncp.dto.EntidadePncpDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.Query;
import java.util.List;

@Service
public class EntidadeService extends PncpService {

    private final String urlEntidadePncp = "/api/v1/entidades";

    public void buscarEntidadesCadastradasPncp(List<EntidadePncpDTO> entidades, ValidacaoException ve) {
        for (EntidadePncpDTO entidade : entidades) {
            try {
                restTemplate.getForEntity(getBaseUrl(ve) + urlEntidadePncp.concat("/") + entidade.getCnpj(), EntidadePncpDTO.class);
                entidade.setIntegradoPNCP(true);
            } catch (HttpClientErrorException ex) {
                entidade.setIntegradoPNCP(false);
                tratarErroGenerico(ve, ex);
            }
        }
    }

    public void enviarEntidadesPncp(List<EntidadePncpDTO> entidades, ValidacaoException ve) {
       /* for (EntidadePncpDTO entidade : entidades) {
            if (!entidade.getIntegradoPNCP()) {
                try {
                    restTemplate.postForEntity(getBaseUrl(ve) + urlEntidadePncp, entidade, String.class);
                } catch (HttpClientErrorException ex) {
                    tratarErroGenerico(ve, ex);
                }
            }
        }*/
    }

    public void cadastrarEntidades(List<EntidadePncpDTO> entidades, ValidacaoException ve) {
        EnteAutorizadoDTO entesAutorizados = new EnteAutorizadoDTO();
        for (EntidadePncpDTO entidade : entidades) {
            if (entidade.getIntegradoPNCP()) {
                entesAutorizados.getEntesAutorizados().add(entidade.getCnpj());
            }
        }
        restTemplate.postForEntity(getBaseUrl(ve) + "/api/v1/usuarios/ente-autorizado", entesAutorizados, String.class);
    }

    public List<EntidadePncpDTO> buscarEntidades(List<EntidadePncpDTO> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            String sql = " select distinct ent.id, " +
                "      pj.cnpj           as cnpj, " +
                "      pj.razaosocial    as razao_social, " +
                "      ent.esferadopoder as poder, " +
                "      cast( 'MUNICIPAL' as varchar(10))    as esfera," +
                "     vw.codigo " +
                " from entidade ent " +
                "      inner join unidadeorganizacional unid on unid.entidade_id = ent.id " +
                "      inner join hierarquiaorganizacional vw on vw.subordinada_id = unid.id " +
                "      inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id " +
                " where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                "      and vw.tipohierarquiaorganizacional = :tipoHierarquia " +
                "       and vw.SUPERIOR_ID is null " +
                " order by pj.razaosocial ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
            List<EntidadePncpDTO> response = Lists.newArrayList();
            List<Object[]> list = q.getResultList();
            if (list != null && !list.isEmpty()) {
                for (Object[] obj : list) {
                    EntidadePncpDTO dto = new EntidadePncpDTO();
                    dto.setId(Long.parseLong(obj[0].toString()));
                    dto.setCnpj((String) obj[1]);
                    dto.setRazaoSocial((String) obj[2]);
                    dto.setPoderId(EsferaDoPoder.valueOf((String) obj[3]));
                    dto.setEsferaId(EsferaGovernamental.valueOf((String) obj[4]));
                    dto.setCodigo((String) obj[5]);
                    response.add(dto);
                }
            }
            return response;
        }
        return entidades;
    }
}
