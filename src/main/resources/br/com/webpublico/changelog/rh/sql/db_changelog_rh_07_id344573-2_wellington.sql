declare
id_orgao numeric(19,0);
begin
for registro in (select vfp.id,
                            lf.UNIDADEORGANIZACIONAL_ID
                     from contratofp cfp
                              inner join vinculofp vfp on vfp.id = cfp.id
                              inner join VWHIERARQUIAADMINISTRATIVA havfp on havfp.SUBORDINADA_ID = vfp.UNIDADEORGANIZACIONAL_ID
                         and current_date between havfp.iniciovigencia and coalesce(havfp.fimvigencia, current_date)
                              inner join matriculafp mfp on mfp.id = vfp.matriculafp_id
                              inner join pessoafisica pf on pf.id = mfp.pessoa_id
                              inner join lotacaofuncional lf on lf.INICIOVIGENCIA = (select max(s.INICIOVIGENCIA)
                                                                                     from lotacaofuncional s
                                                                                     where s.vinculofp_id = vfp.id)
                         and lf.VINCULOFP_ID = vfp.id
                              inner join VWHIERARQUIAADMINISTRATIVA half on half.SUBORDINADA_ID = lf.UNIDADEORGANIZACIONAL_ID
                         and current_date between half.iniciovigencia and coalesce(half.fimvigencia, current_date)
                     where (vfp.finalvigencia is null or vfp.finalvigencia >= to_date('01/01/2018', 'dd/MM/yyyy'))
                       and substr(havfp.codigo, 1, 5) <> substr(half.codigo, 1, 5))
    loop
select func_get_orgao(registro.UNIDADEORGANIZACIONAL_ID) into id_orgao
from dual;
if (id_orgao is not null) then
update vinculofp set unidadeorganizacional_id = id_orgao where id = registro.ID;
end if;
end loop;
end;
