create or replace function func_numerodam_declaracao(p_id_declaracao in number, p_iss_retido in number)
return varchar2
as
    v_return varchar2(100);
begin
select max(dam.numerodam) into v_return
from dam
         inner join itemdam idam on idam.dam_id = dam.id
         inner join parcelavalordivida pvd on pvd.id = idam.parcela_id
         inner join valordivida vd on vd.id = pvd.valordivida_id
         inner join calculo c on c.id = vd.calculo_id
         inner join processocalculo pc on pc.id = c.processocalculo_id
         inner join declaracaomensalservico dms on dms.processocalculo_id = pc.id
         inner join notadeclarada nd on nd.declaracaomensalservico_id = dms.id
where dam.situacao = 'PAGO'
  and nd.declaracaoprestacaoservico_id = p_id_declaracao
  and ((p_iss_retido = 0 and dms.tipomovimento in ('NORMAL', 'INSTITUICAO_FINANCEIRA'))
    or (p_iss_retido = 1 and dms.tipomovimento in ('RETENCAO', 'ISS_RETIDO')));
return v_return;
end;
