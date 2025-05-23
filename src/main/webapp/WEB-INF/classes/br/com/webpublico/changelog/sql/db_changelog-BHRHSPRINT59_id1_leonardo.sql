create or replace view vwhierarquiacomorgao as with rec (id, subordinado_id, codigo,superior_id, nivel, id_orgao) as (
      select pai.id, pai.subordinada_id, pai.codigo, pai.superior_id, 1 as nivel, cast(null as integer) as orgao_id
        from hierarquiaorganizacional pai
       where pai.superior_id is null  
   UNION ALL
      select filho.id, filho.subordinada_id, filho.codigo, filho.superior_id, rec.nivel + 1, 
             case rec.nivel + 1
                  when 2 
                       then filho.id 
                       else rec.id_orgao 
             end
        from hierarquiaorganizacional filho , rec 
       where filho.superior_id = rec.subordinado_id
) select * from rec order by codigo