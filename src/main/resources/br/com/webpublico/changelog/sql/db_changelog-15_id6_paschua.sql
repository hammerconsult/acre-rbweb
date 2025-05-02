update subconta s set s.integracao = (
    select 
        case ctaintaga 
          when 'N' then 0 
          when 'S' then 1 
          else null end as integracao 
    from safira.actfnc sub
    where trim(sub.cdgctafnc) = s.codigo
)