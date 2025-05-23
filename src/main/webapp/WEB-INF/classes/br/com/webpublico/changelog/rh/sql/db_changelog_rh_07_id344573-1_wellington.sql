create or replace function func_get_orgao(id_unidade in number)
return number
as
    nivel number;
begin
select nivelestrutura(vw.codigo, '.') into nivel from vwhierarquiaadministrativa vw
where vw.SUBORDINADA_ID = id_unidade
  and current_date between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, current_date);

if (nivel < 2) then
       return null;
end if;

   if (nivel = 2) then
       return id_unidade;
end if;

for registro in (with hierarquia (codigo, nivel, id, subordinada_id, superior_id) as (
       select vw.codigo, nivelestrutura(vw.codigo, '.') as nivel, vw.id,
              vw.subordinada_id, vw.superior_id
       from vwhierarquiaadministrativa vw
       where current_date between vw.iniciovigencia
           and coalesce(vw.fimvigencia, current_date)
         and vw.subordinada_id = id_unidade
       union all
       select vw.codigo, nivelestrutura(vw.codigo, '.') as nivel,
              vw.id, vw.subordinada_id, vw.superior_id
       from vwhierarquiaadministrativa vw
                inner join hierarquia filho on filho.superior_id = vw.subordinada_id
       where current_date between vw.iniciovigencia and coalesce(vw.fimvigencia, current_date)
   )
                    select hie.subordinada_id
                    from hierarquia
                             inner join HIERARQUIAORGANIZACIONAL hie on hie.id = hierarquia.id
                    where hierarquia.nivel = 2
                      and current_date between trunc(hie.iniciovigencia)
                        and coalesce(trunc(hie.fimvigencia), current_date)
                    order by hie.codigo)
    loop
       return registro.subordinada_id;
end loop;
return null;
end;
