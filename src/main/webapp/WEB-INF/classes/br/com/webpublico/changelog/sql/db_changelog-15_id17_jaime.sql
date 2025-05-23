create or replace
function recuperaEntidadeDaOrcViaAdm(p_idDaOrcamentaria in number, v_inicio in date, v_final in date) return number as 
  v_idDaEntidade number;
  v_idDaAdministrativa number;
begin
   select corr.hierarquiaorgadm_id into v_idDaAdministrativa 
     from hierarquiaOrgCorre corr
    where corr.hierarquiaorgorc_id = p_idDaOrcamentaria
      and filhoNaVigencia(corr.dataInicio, corr.dataFim, v_inicio, v_final) = 1;    
   select entidade_id into v_idDaEntidade 
     from vwhierarquiaadministrativa 
    where id = v_idDaAdministrativa;    
  return v_idDaEntidade;
exception 
when NO_DATA_FOUND then
  return null;
end;